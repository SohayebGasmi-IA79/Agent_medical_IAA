package com.multiagent.agents;

import com.multiagent.model.DiagnosticData;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Agent Coordinateur — cœur du système.
 *
 * <p>Responsabilités :</p>
 * <ol>
 *   <li>Recevoir la demande du patient ({@code ACLMessage.REQUEST}).</li>
 *   <li>Rechercher dynamiquement les spécialistes via le Directory Facilitator.</li>
 *   <li>Solliciter chaque spécialiste et collecter les avis.</li>
 *   <li>Sélectionner le diagnostic au score le plus élevé.</li>
 *   <li>Renvoyer le rapport médical complet au patient via {@code ACLMessage.INFORM}.</li>
 * </ol>
 */
public class AgentCoordinateur extends Agent {

    private static final long serialVersionUID = 1L;

    /** Type de service DF utilisé par les spécialistes. */
    private static final String SERVICE_SPECIALISTE = "diagnostic-medical";

    /** Type de service DF utilisé par le coordinateur lui-même. */
    private static final String SERVICE_COORDINATEUR = "coordination-medicale";

    /** Délai maximal d'attente des réponses spécialistes (ms). */
    private static final long DELAI_REPONSE_MS = 8_000L;

    // ------------------------------------------------------------------
    // Cycle de vie JADE
    // ------------------------------------------------------------------
    @Override
    protected void setup() {
        System.out.println("[Coordinateur] Démarrage de l'agent Coordinateur.");
        enregistrerDansDF();

        final MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        addBehaviour(new CyclicBehaviour(this) {
            private static final long serialVersionUID = 1L;

            @Override
            public void action() {
                ACLMessage msg = myAgent.receive(template);
                if (msg == null) {
                    // Aucune demande : libération du thread de comportement.
                    block();
                    return;
                }
                traiterDemande(msg);
            }
        });
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println("[Coordinateur] Désenregistré du DF.");
        } catch (FIPAException e) {
            System.err.println("[Coordinateur] Erreur de désenregistrement : " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Enregistrement DF
    // ------------------------------------------------------------------
    private void enregistrerDansDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(SERVICE_COORDINATEUR);
        sd.setName("Coordinateur médical");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("[Coordinateur] Enregistré au DF (service '" + SERVICE_COORDINATEUR + "').");
        } catch (FIPAException e) {
            System.err.println("[Coordinateur] Échec d'enregistrement DF : " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Traitement d'une demande patient
    // ------------------------------------------------------------------
    private void traiterDemande(ACLMessage msg) {
        try {
            Object contenu = msg.getContentObject();
            if (!(contenu instanceof DiagnosticData)) {
                System.err.println("[Coordinateur] Contenu inattendu : " + contenu);
                return;
            }

            DiagnosticData demande = (DiagnosticData) contenu;
            System.out.println("[Coordinateur] Demande reçue de " + msg.getSender().getLocalName()
                    + " — symptômes : " + demande.getSymptomes());

            List<AID> specialistes = rechercherSpecialistes();
            System.out.println("[Coordinateur] " + specialistes.size() + " spécialiste(s) trouvé(s) via le DF.");

            DiagnosticData rapport = consulterSpecialistes(demande, specialistes);

            ACLMessage reponse = msg.createReply();
            reponse.setPerformative(ACLMessage.INFORM);
            reponse.setContentObject(rapport);
            send(reponse);

            System.out.println("[Coordinateur] Rapport envoyé à " + msg.getSender().getLocalName());
        } catch (UnreadableException e) {
            System.err.println("[Coordinateur] Contenu illisible : " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[Coordinateur] Erreur de sérialisation : " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Recherche DF des spécialistes
    // ------------------------------------------------------------------
    private List<AID> rechercherSpecialistes() {
        List<AID> resultat = new ArrayList<>();

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(SERVICE_SPECIALISTE);
        template.addServices(sd);

        try {
            DFAgentDescription[] trouves = DFService.search(this, template);
            for (DFAgentDescription d : trouves) {
                resultat.add(d.getName());
            }
        } catch (FIPAException e) {
            System.err.println("[Coordinateur] Erreur de recherche DF : " + e.getMessage());
        }
        return resultat;
    }

    // ------------------------------------------------------------------
    // Consultation des spécialistes
    // ------------------------------------------------------------------
    private DiagnosticData consulterSpecialistes(DiagnosticData demande, List<AID> specialistes) {
        DiagnosticData rapport = new DiagnosticData(demande.getSymptomes());
        rapport.setPathologieRetenue("Aucun diagnostic");
        rapport.setScoreMax(0.0);

        if (specialistes.isEmpty()) {
            return rapport;
        }

        // Identifiant de conversation partagé par toutes les requêtes de cette consultation.
        String convId = "consult-" + System.currentTimeMillis() + "-" + System.nanoTime();

        // 1) Envoi des requêtes (contenu fraîchement sérialisé pour chaque spécialiste).
        for (AID aid : specialistes) {
            ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
            req.addReceiver(aid);
            req.setConversationId(convId);
            req.setReplyWith("rw-" + System.nanoTime());
            try {
                req.setContentObject(new DiagnosticData(demande.getSymptomes()));
                send(req);
            } catch (IOException e) {
                System.err.println("[Coordinateur] Impossible de sérialiser la requête pour "
                        + aid.getLocalName() + " : " + e.getMessage());
            }
        }

        // 2) Collecte des réponses avec timeout global.
        long fin = System.currentTimeMillis() + DELAI_REPONSE_MS;
        int recus = 0;
        int attendus = specialistes.size();

        MessageTemplate mtReponses = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchConversationId(convId)
        );

        while (recus < attendus) {
            long reste = fin - System.currentTimeMillis();
            if (reste <= 0) {
                System.out.println("[Coordinateur] Timeout — " + recus + "/" + attendus + " réponses reçues.");
                break;
            }

            ACLMessage rep = blockingReceive(mtReponses, reste);
            if (rep == null) {
                System.out.println("[Coordinateur] Timeout — " + recus + "/" + attendus + " réponses reçues.");
                break;
            }
            recus++;

            try {
                Object c = rep.getContentObject();
                if (!(c instanceof DiagnosticData)) {
                    continue;
                }
                DiagnosticData dr = (DiagnosticData) c;

                for (Map.Entry<String, Double> e : dr.getAvisSpecialistes().entrySet()) {
                    rapport.addAvis(e.getKey(), e.getValue());
                    if (e.getValue() > rapport.getScoreMax()) {
                        rapport.setScoreMax(e.getValue());
                        rapport.setPathologieRetenue(dr.getPathologieRetenue());
                        rapport.setTraitements(dr.getTraitements());
                    }
                }
            } catch (UnreadableException e) {
                System.err.println("[Coordinateur] Réponse illisible de "
                        + rep.getSender().getLocalName() + " : " + e.getMessage());
            }
        }

        System.out.println("[Coordinateur] Diagnostic retenu : " + rapport.getPathologieRetenue()
                + " (score = " + String.format("%.2f", rapport.getScoreMax()) + ")");
        return rapport;
    }
}
