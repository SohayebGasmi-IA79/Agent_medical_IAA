package com.multiagent.agents;

import com.multiagent.model.DiagnosticData;
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
import java.util.List;
import java.util.Map;

/**
 * Classe abstraite dont héritent tous les agents spécialistes.
 *
 * <p>Elle mutualise :</p>
 * <ul>
 *   <li>l'enregistrement automatique dans le Directory Facilitator (DF) ;</li>
 *   <li>la boucle de réception asynchrone ({@link CyclicBehaviour} + {@code block()}) ;</li>
 *   <li>le moteur de scoring pondéré appliqué à la base de connaissances.</li>
 * </ul>
 *
 * <p>Chaque agent concret n'a plus qu'à fournir sa base de connaissances
 * ({@link #getKnowledgeBase()}) et sa table de traitements
 * ({@link #getTraitements()}).</p>
 */
public abstract class AbstractSpecialistAgent extends Agent {

    private static final long serialVersionUID = 1L;

    /** Type de service utilisé dans le DF pour retrouver tous les spécialistes. */
    public static final String SERVICE_TYPE = "diagnostic-medical";

    /** Base de connaissances : pathologie -> (symptôme -> poids). */
    private Map<String, Map<String, Double>> knowledgeBase;

    /** Table des traitements : pathologie -> liste de traitements. */
    private Map<String, List<String>> traitements;

    // ------------------------------------------------------------------
    // À implémenter par les sous-classes
    // ------------------------------------------------------------------
    protected abstract String getSpecialite();

    protected abstract Map<String, Map<String, Double>> construireKnowledgeBase();

    protected abstract Map<String, List<String>> construireTraitements();

    // ------------------------------------------------------------------
    // Cycle de vie JADE
    // ------------------------------------------------------------------
    @Override
    protected void setup() {
        this.knowledgeBase = construireKnowledgeBase();
        this.traitements = construireTraitements();

        System.out.println("[" + getLocalName() + "] Démarrage — spécialité : " + getSpecialite());
        enregistrerDansDF();

        final MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        addBehaviour(new CyclicBehaviour(this) {
            private static final long serialVersionUID = 1L;

            @Override
            public void action() {
                ACLMessage msg = myAgent.receive(template);
                if (msg == null) {
                    // Aucun message : on libère le thread de comportement (0% CPU).
                    block();
                    return;
                }
                traiterRequete(msg);
            }
        });
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println("[" + getLocalName() + "] Désenregistré du DF.");
        } catch (FIPAException e) {
            System.err.println("[" + getLocalName() + "] Erreur lors du désenregistrement : " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Enregistrement DF
    // ------------------------------------------------------------------
    private void enregistrerDansDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(SERVICE_TYPE);
        sd.setName(getSpecialite());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("[" + getLocalName() + "] Enregistré au DF sous le service '" + getSpecialite() + "'.");
        } catch (FIPAException e) {
            System.err.println("[" + getLocalName() + "] Échec d'enregistrement DF : " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Traitement d'une requête entrante
    // ------------------------------------------------------------------
    private void traiterRequete(ACLMessage msg) {
        try {
            Object contenu = msg.getContentObject();
            if (!(contenu instanceof DiagnosticData)) {
                System.err.println("[" + getLocalName() + "] Contenu inattendu : " + contenu);
                return;
            }

            DiagnosticData demande = (DiagnosticData) contenu;
            DiagnosticData reponse = evaluer(demande);

            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContentObject(reponse);
            send(reply);

            System.out.println("[" + getLocalName() + "] Réponse envoyée : " + reponse);
        } catch (UnreadableException e) {
            System.err.println("[" + getLocalName() + "] Contenu illisible : " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[" + getLocalName() + "] Erreur de sérialisation : " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Moteur de diagnostic
    // ------------------------------------------------------------------
    /**
     * Évalue toutes les pathologies de la base de connaissances et retourne
     * la meilleure avec son score, sa pathologie et ses traitements.
     */
    protected DiagnosticData evaluer(DiagnosticData demande) {
        List<String> symptomes = demande.getSymptomes();
        DiagnosticData reponse = new DiagnosticData(symptomes);

        String meilleurePatho = null;
        double meilleurScore = 0.0;

        for (Map.Entry<String, Map<String, Double>> entree : knowledgeBase.entrySet()) {
            double score = calculerScore(symptomes, entree.getValue());
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleurePatho = entree.getKey();
            }
        }

        if (meilleurePatho != null && meilleurScore > 0.0) {
            reponse.addAvis(getSpecialite() + " : " + meilleurePatho, meilleurScore);
            reponse.setPathologieRetenue(meilleurePatho);
            reponse.setScoreMax(meilleurScore);

            List<String> tr = traitements.get(meilleurePatho);
            if (tr != null) {
                for (String t : tr) {
                    reponse.addTraitement(t);
                }
            }
        }
        return reponse;
    }

    /**
     * Calcule un score pondéré (0..100) : rapport entre le poids des
     * symptômes effectivement présents et le poids total de la pathologie.
     */
    protected double calculerScore(List<String> symptomesPatient, Map<String, Double> regles) {
        if (symptomesPatient == null || symptomesPatient.isEmpty() || regles == null || regles.isEmpty()) {
            return 0.0;
        }
        double poidsTotal = 0.0;
        double poidsMatches = 0.0;

        for (Map.Entry<String, Double> regle : regles.entrySet()) {
            poidsTotal += regle.getValue();
            if (symptomesPatient.contains(regle.getKey())) {
                poidsMatches += regle.getValue();
            }
        }
        if (poidsTotal == 0.0) {
            return 0.0;
        }
        return (poidsMatches / poidsTotal) * 100.0;
    }
}
