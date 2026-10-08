package com.multiagent.containers;

import com.multiagent.agents.AgentCardiologue;
import com.multiagent.agents.AgentCoordinateur;
import com.multiagent.agents.AgentGastro;
import com.multiagent.agents.AgentNeurologue;
import com.multiagent.agents.AgentOrthopediste;
import com.multiagent.agents.AgentPatient;
import com.multiagent.agents.AgentPneumologue;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.concurrent.CountDownLatch;

/**
 * Lanceur unifié du système multi-agents médical.
 *
 * <p>Démarre dans un SEUL JVM et un SEUL terminal :</p>
 * <ol>
 *   <li>Le <b>Main Container</b> JADE (sans GUI RMA) + l'Agent Coordinateur</li>
 *   <li>Le <b>SpecialistsContainer</b> + les 5 agents spécialistes</li>
 *   <li>Le <b>PatientContainer</b> + l'Agent Patient (ouvre l'IHM Swing)</li>
 * </ol>
 *
 * <p>Aucune GUI JADE n'est affichée : seule votre application Swing apparaît.
 * Fermer la fenêtre Patient termine automatiquement l'ensemble du JVM.</p>
 */
public class UnifiedLauncher {

    // ------------------------------------------------------------------
    // Paramètres de la plateforme
    // ------------------------------------------------------------------
    private static final String HOST        = "127.0.0.1";
    private static final int    PORT        = 1099;
    private static final String PLATFORM_ID = "medical-platform";

    // Délais de synchronisation (ms) entre démarrages de containers.
    private static final long DELAI_ENTRE_CONTAINERS = 800L;

    // ==================================================================
    // MAIN
    // ==================================================================
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  Système Multi-Agents Médical — Démarrage unifié         ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        // Swing doit pouvoir démarrer (pas de mode headless).
        System.setProperty("java.awt.headless", "false");

        // ------------------------------------------------------------------
        // 1) Récupération du Runtime JADE (singleton)
        // ------------------------------------------------------------------
        Runtime runtime = Runtime.instance();
        runtime.setCloseVM(true);   // ferme le JVM quand tous les containers sont arrêtés

        // ------------------------------------------------------------------
        // 2) Main Container (sans GUI JADE) + Agent Coordinateur
        // ------------------------------------------------------------------
        Profile mainProfile = new ProfileImpl();
        mainProfile.setParameter(Profile.GUI, "false");          // ← pas de RMA
        mainProfile.setParameter(Profile.LOCAL_HOST, HOST);
        mainProfile.setParameter(Profile.LOCAL_PORT, String.valueOf(PORT));
        mainProfile.setParameter(Profile.PLATFORM_ID, PLATFORM_ID);

        AgentContainer mainContainer = runtime.createMainContainer(mainProfile);
        System.out.println("[UnifiedLauncher] Main Container démarré sur " + HOST + ":" + PORT);

        demarrerAgent(mainContainer, "Coordinateur", AgentCoordinateur.class);
        attendre(DELAI_ENTRE_CONTAINERS);

        // ------------------------------------------------------------------
        // 3) Container des spécialistes + 5 agents médecins
        // ------------------------------------------------------------------
        Profile specProfile = new ProfileImpl();
        specProfile.setParameter(Profile.MAIN_HOST, HOST);
        specProfile.setParameter(Profile.MAIN_PORT, String.valueOf(PORT));
        specProfile.setParameter(Profile.CONTAINER_NAME, "SpecialistsContainer");
        specProfile.setParameter(Profile.PLATFORM_ID, PLATFORM_ID);

        AgentContainer specContainer = runtime.createAgentContainer(specProfile);
        System.out.println("[UnifiedLauncher] SpecialistsContainer créé.");

        demarrerAgent(specContainer, "Gastro",       AgentGastro.class);
        demarrerAgent(specContainer, "Neurologue",   AgentNeurologue.class);
        demarrerAgent(specContainer, "Orthopediste", AgentOrthopediste.class);
        demarrerAgent(specContainer, "Cardiologue",  AgentCardiologue.class);
        demarrerAgent(specContainer, "Pneumologue",  AgentPneumologue.class);
        attendre(DELAI_ENTRE_CONTAINERS);

        // ------------------------------------------------------------------
        // 4) Container Patient + ouverture de l'IHM Swing
        // ------------------------------------------------------------------
        Profile patProfile = new ProfileImpl();
        patProfile.setParameter(Profile.MAIN_HOST, HOST);
        patProfile.setParameter(Profile.MAIN_PORT, String.valueOf(PORT));
        patProfile.setParameter(Profile.CONTAINER_NAME, "PatientContainer");
        patProfile.setParameter(Profile.PLATFORM_ID, PLATFORM_ID);

        AgentContainer patContainer = runtime.createAgentContainer(patProfile);
        System.out.println("[UnifiedLauncher] PatientContainer créé.");

        demarrerAgent(patContainer, "Patient", AgentPatient.class);

        System.out.println("[UnifiedLauncher] ✓ Système prêt — l'IHM Swing va s'ouvrir.");
        System.out.println("[UnifiedLauncher] Fermez la fenêtre Patient pour arrêter tout le système.");

        // ------------------------------------------------------------------
        // 5) Maintien du thread principal en vie
        // ------------------------------------------------------------------
        // La fermeture de la JFrame (EXIT_ON_CLOSE) termine le JVM.
        // Le latch sert uniquement à garder main() bloqué proprement.
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ==================================================================
    // Helpers
    // ==================================================================

    /** Démarre un agent dans un container donné, avec gestion d'erreur. */
    private static void demarrerAgent(AgentContainer container, String nom, Class<?> classe) {
        try {
            AgentController ac = container.createNewAgent(nom, classe.getName(), new Object[]{});
            ac.start();
            System.out.println("[UnifiedLauncher]   → Agent " + nom + " démarré.");
        } catch (StaleProxyException e) {
            System.err.println("[UnifiedLauncher]   ✗ Échec du démarrage de " + nom
                    + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Petite pause pour laisser le temps aux services JADE de s'initialiser. */
    private static void attendre(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
