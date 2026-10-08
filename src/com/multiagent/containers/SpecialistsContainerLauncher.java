package com.multiagent.containers;

import com.multiagent.agents.AgentCardiologue;
import com.multiagent.agents.AgentGastro;
import com.multiagent.agents.AgentNeurologue;
import com.multiagent.agents.AgentOrthopediste;
import com.multiagent.agents.AgentPneumologue;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * Lance un Container périphérique et y déploie tous les agents spécialistes.
 *
 * <p>À exécuter APRÈS le Main Container, dans un JVM dédié.</p>
 */
public class SpecialistsContainerLauncher {

    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        runtime.setCloseVM(false);

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, MainContainerLauncher.MAIN_HOST);
        profile.setParameter(Profile.MAIN_PORT, String.valueOf(MainContainerLauncher.MAIN_PORT));
        profile.setParameter(Profile.CONTAINER_NAME, "SpecialistsContainer");
        profile.setParameter(Profile.PLATFORM_ID, MainContainerLauncher.PLATFORM_ID);

        AgentContainer container = runtime.createAgentContainer(profile);

        lancer(container, "Gastro",        AgentGastro.class);
        lancer(container, "Neurologue",    AgentNeurologue.class);
        lancer(container, "Orthopediste",  AgentOrthopediste.class);
        lancer(container, "Cardiologue",   AgentCardiologue.class);
        lancer(container, "Pneumologue",   AgentPneumologue.class);

        System.out.println("[SpecialistsContainer] Tous les agents spécialistes sont déployés et enregistrés au DF.");
    }

    private static void lancer(AgentContainer container, String nom, Class<?> classe) {
        try {
            AgentController ac = container.createNewAgent(nom, classe.getName(), new Object[]{});
            ac.start();
            System.out.println("[SpecialistsContainer] Agent " + nom + " démarré.");
        } catch (StaleProxyException e) {
            System.err.println("[SpecialistsContainer] Échec du démarrage de " + nom + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
