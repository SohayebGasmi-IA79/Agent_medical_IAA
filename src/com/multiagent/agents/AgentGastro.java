package com.multiagent.agents;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent spécialiste en Gastro-Entérologie.
 */
public class AgentGastro extends AbstractSpecialistAgent {

    private static final long serialVersionUID = 1L;

    @Override
    protected String getSpecialite() {
        return "Gastro-Entérologie";
    }

    @Override
    protected Map<String, Map<String, Double>> construireKnowledgeBase() {
        Map<String, Map<String, Double>> kb = new LinkedHashMap<>();

        Map<String, Double> gastrite = new LinkedHashMap<>();
        gastrite.put("brûlures_d_estomac", 3.0);
        gastrite.put("nausées", 2.0);
        gastrite.put("vomissements", 2.0);
        gastrite.put("douleur_abdominale", 2.5);
        kb.put("Gastrite aiguë", gastrite);

        Map<String, Double> rgo = new LinkedHashMap<>();
        rgo.put("reflux", 3.5);
        rgo.put("brûlures_d_estomac", 3.0);
        rgo.put("nausées", 1.5);
        rgo.put("ballonnements", 1.5);
        kb.put("Reflux Gastro-Œsophagien (RGO)", rgo);

        Map<String, Double> ulcere = new LinkedHashMap<>();
        ulcere.put("douleur_abdominale", 3.0);
        ulcere.put("brûlures_d_estomac", 2.5);
        ulcere.put("vomissements", 2.0);
        ulcere.put("nausées", 1.5);
        kb.put("Ulcère gastrique", ulcere);

        Map<String, Double> indigestion = new LinkedHashMap<>();
        indigestion.put("ballonnements", 3.0);
        indigestion.put("nausées", 2.0);
        indigestion.put("douleur_abdominale", 1.5);
        indigestion.put("reflux", 1.0);
        kb.put("Indigestion", indigestion);

        return kb;
    }

    @Override
    protected Map<String, List<String>> construireTraitements() {
        Map<String, List<String>> t = new LinkedHashMap<>();
        t.put("Gastrite aiguë", Arrays.asList(
                "Oméprazole 20 mg le matin à jeun pendant 14 jours",
                "Éviter alcool, tabac et épices",
                "Repos digestif et hydratation abondante"
        ));
        t.put("Reflux Gastro-Œsophagien (RGO)", Arrays.asList(
                "Inhibiteur de la pompe à protons (Oméprazole 20 mg/j)",
                "Surélévation de la tête du lit de 15 cm",
                "Repas légers, éviter café, alcool et repas tardifs"
        ));
        t.put("Ulcère gastrique", Arrays.asList(
                "Recherche et éradication d'Helicobacter pylori",
                "Oméprazole 40 mg/j pendant 4 à 8 semaines",
                "Contrôle endoscopique de cicatrisation"
        ));
        t.put("Indigestion", Arrays.asList(
                "Antiacides après les repas",
                "Fractionnement des repas",
                "Hydratation et marche légère"
        ));
        return t;
    }
}
