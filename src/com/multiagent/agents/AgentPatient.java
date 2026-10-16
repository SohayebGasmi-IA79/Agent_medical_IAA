package com.multiagent.agents;

import com.multiagent.model.DiagnosticData;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.util.List;

/**
 * Agent Patient — point d'entrée utilisateur du système multi-agents médical.
 *
 * <p>La réception asynchrone est assurée par un {@link CyclicBehaviour} JADE
 * standard (et NON par un thread séparé appelant {@code blockingReceive()}).
 * Cette approche évite tout conflit avec le scheduler JADE, qui gère aussi
 * les échanges internes du Directory Facilitator.</p>
 *
 * <p>L'EDT de Swing n'est jamais bloqué : les mises à jour de l'IHM sont
 * effectuées via {@code SwingUtilities.invokeLater()}.</p>
 */
public class AgentPatient extends Agent {

    private static final long serialVersionUID = 1L;

    /** Type de service DF utilisé par le Coordinateur pour s'enregistrer. */
    private static final String SERVICE_COORDINATEUR = "coordination-medicale";

    /** Fenêtre Swing principale. */
    private PatientFrame frame;

    // ==================================================================
    // Cycle de vie JADE
    // ==================================================================
    @Override
    protected void setup() {
        System.out.println("[Patient] Démarrage de l'agent Patient.");

        // Création de l'IHM dans l'EDT Swing.
        SwingUtilities.invokeLater(() -> {
            UiTheme.install();
            frame = new PatientFrame(this);
            frame.setVisible(true);
            frame.toFront();
            frame.requestFocus();
            frame.setState(java.awt.Frame.NORMAL);
        });

        // Réception asynchrone via un CyclicBehaviour JADE standard.
        // ⚠ On NE crée PAS de thread séparé : le scheduler JADE exécute
        // ce comportement de manière coopérative, ce qui garantit que
        // les messages du DF restent accessibles à DFService.search().
        addBehaviour(new CyclicBehaviour(this) {
            private static final long serialVersionUID = 1L;

            @Override
            public void action() {
                // On ne s'intéresse qu'aux INFORM (rapports médicaux).
                ACLMessage msg = myAgent.receive(
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM));

                if (msg == null) {
                    // Aucun message : on libère le thread du scheduler (0 % CPU).
                    block();
                    return;
                }

                // Certains INFORM internes (DF, AMS) ne contiennent pas
                // d'objet Java sérialisé → on les ignore silencieusement.
                Object contenu;
                try {
                    contenu = msg.getContentObject();
                } catch (UnreadableException e) {
                    return;
                }

                if (contenu instanceof DiagnosticData) {
                    final DiagnosticData rapport = (DiagnosticData) contenu;
                    System.out.println("[Patient] Rapport reçu : "
                            + rapport.getPathologieRetenue()
                            + " (score = " + String.format("%.2f", rapport.getScoreMax()) + ")");
                    if (frame != null) {
                        SwingUtilities.invokeLater(() -> frame.afficherRapport(rapport));
                    }
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        if (frame != null) {
            SwingUtilities.invokeLater(frame::dispose);
        }
        System.out.println("[Patient] Agent arrêté.");
    }

    // ==================================================================
    // API appelée par l'interface Swing
    // ==================================================================
    /**
     * Envoie les symptômes saisis au Coordinateur via un {@link OneShotBehaviour}.
     * Cette méthode est appelée depuis l'EDT Swing mais délègue l'envoi ACL
     * au thread du scheduler JADE (via {@code addBehaviour}), ce qui évite
     * toute opération réseau dans l'EDT.
     *
     * @param symptomes liste des symptômes sélectionnés par l'utilisateur
     */
    public void envoyerSymptomes(final List<String> symptomes) {
        addBehaviour(new OneShotBehaviour(this) {
            private static final long serialVersionUID = 1L;

            @Override
            public void action() {
                AID coord = rechercherCoordinateur();
                if (coord == null) {
                    if (frame != null) {
                        frame.afficherErreur(
                                "Agent Coordinateur introuvable dans le DF.\n\n"
                              + "Vérifiez que le Main Container JADE est bien démarré\n"
                              + "et que l'agent Coordinateur s'est enregistré\n"
                              + "auprès du Directory Facilitator.");
                    }
                    return;
                }

                DiagnosticData data = new DiagnosticData(symptomes);
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(coord);
                msg.setConversationId("patient-" + System.currentTimeMillis());
                msg.setReplyWith("rw-" + System.nanoTime());

                try {
                    msg.setContentObject(data);
                    send(msg);
                    System.out.println("[Patient] Demande envoyée au Coordinateur ("
                            + coord.getLocalName() + ") — symptômes : " + symptomes);
                    if (frame != null) {
                        frame.afficherStatut("Demande transmise — attente des avis...");
                    }
                } catch (IOException e) {
                    System.err.println("[Patient] Erreur de sérialisation : " + e.getMessage());
                    if (frame != null) {
                        frame.afficherErreur("Erreur technique lors de l'envoi : " + e.getMessage());
                    }
                }
            }
        });
    }

    // ==================================================================
    // Recherche DF du Coordinateur (avec retry)
    // ==================================================================
    /**
     * Recherche le Coordinateur dans le Directory Facilitator.
     * Effectue jusqu'à 3 tentatives espacées de 500 ms pour absorber
     * d'éventuels délais d'initialisation du DF.
     */
    private AID rechercherCoordinateur() {
        final int maxTentatives = 3;
        final long delaiMs = 500L;

        for (int i = 1; i <= maxTentatives; i++) {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(SERVICE_COORDINATEUR);
            template.addServices(sd);

            try {
                DFAgentDescription[] res = DFService.search(this, template);
                if (res.length > 0) {
                    System.out.println("[Patient] Coordinateur trouvé : "
                            + res[0].getName().getLocalName()
                            + " (tentative " + i + "/" + maxTentatives + ")");
                    return res[0].getName();
                }
            } catch (FIPAException e) {
                System.err.println("[Patient] Tentative " + i + "/" + maxTentatives
                        + " — erreur DF : " + e.getMessage());
            }

            if (i < maxTentatives) {
                try {
                    Thread.sleep(delaiMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        System.err.println("[Patient] Coordinateur introuvable après "
                + maxTentatives + " tentatives.");
        return null;
    }
}
