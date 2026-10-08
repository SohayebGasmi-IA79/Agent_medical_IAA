package com.multiagent.agents;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent spécialiste en Pneumologie (système respiratoire).
 */
public class AgentPneumologue extends AbstractSpecialistAgent {

    private static final long serialVersionUID = 1L;

    @Override
    protected String getSpecialite() {
        return "Pneumologie";
    }

    @Override
    protected Map<String, Map<String, Double>> construireKnowledgeBase() {
        Map<String, Map<String, Double>> kb = new LinkedHashMap<>();

        Map<String, Double> bronchite = new LinkedHashMap<>();
        bronchite.put("toux_grasse", 3.5);
        bronchite.put("fievre", 2.0);
        bronchite.put("difficulte_respiratoire", 2.0);
        bronchite.put("sifflement_thoracique", 1.5);
        kb.put("Bronchite aiguë", bronchite);

        Map<String, Double> asthme = new LinkedHashMap<>();
        asthme.put("sifflement_thoracique", 3.5);
        asthme.put("difficulte_respiratoire", 3.0);
        asthme.put("toux_seche", 2.0);
        kb.put("Crise d'asthme", asthme);

        Map<String, Double> infectionRespiratoire = new LinkedHashMap<>();
        infectionRespiratoire.put("fievre", 3.0);
        infectionRespiratoire.put("toux_seche", 2.5);
        infectionRespiratoire.put("toux_grasse", 2.0);
        infectionRespiratoire.put("difficulte_respiratoire", 1.5);
        kb.put("Infection respiratoire", infectionRespiratoire);

        return kb;
    }

    @Override
    protected Map<String, List<String>> construireTraitements() {
        Map<String, List<String>> t = new LinkedHashMap<>();
        t.put("Bronchite aiguë", Arrays.asList(
                "Repos, hydratation abondante",
                "Antipyrétique en cas de fièvre (paracétamol)",
                "Kinésithérapie respiratoire si encombrement"
        ));
        t.put("Crise d'asthme", Arrays.asList(
                "Bronchodilatateur en inhalation (salbutamol)",
                "Corticothérapie orale courte si crise sévère",
                "Consulter en urgence en cas d'absence d'amélioration"
        ));
        t.put("Infection respiratoire", Arrays.asList(
                "Repos et hydratation",
                "Antipyrétique et antalgique",
                "Bilan microbiologique si fièvre persistante > 3 jours"
        ));
        return t;
    }
}
