package com.multiagent.agents;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent spécialiste en Neurologie.
 */
public class AgentNeurologue extends AbstractSpecialistAgent {

    private static final long serialVersionUID = 1L;

    @Override
    protected String getSpecialite() {
        return "Neurologie";
    }

    @Override
    protected Map<String, Map<String, Double>> construireKnowledgeBase() {
        Map<String, Map<String, Double>> kb = new LinkedHashMap<>();

        Map<String, Double> migraineOphtalmique = new LinkedHashMap<>();
        migraineOphtalmique.put("migraine", 3.5);
        migraineOphtalmique.put("sensibilité_lumière", 3.0);
        migraineOphtalmique.put("maux_de_tete", 2.5);
        migraineOphtalmique.put("nausées", 1.5);
        kb.put("Migraine ophtalmique", migraineOphtalmique);

        Map<String, Double> cephaleeTension = new LinkedHashMap<>();
        cephaleeTension.put("maux_de_tete", 3.5);
        cephaleeTension.put("raideur_nuque", 2.0);
        cephaleeTension.put("vertiges", 1.5);
        cephaleeTension.put("engourdissement", 1.0);
        kb.put("Céphalée de tension", cephaleeTension);

        Map<String, Double> nevralgie = new LinkedHashMap<>();
        nevralgie.put("engourdissement", 3.0);
        nevralgie.put("maux_de_tete", 2.0);
        nevralgie.put("sensibilité_lumière", 1.0);
        kb.put("Névralgie", nevralgie);

        Map<String, Double> vertigePositionnel = new LinkedHashMap<>();
        vertigePositionnel.put("vertiges", 3.5);
        vertigePositionnel.put("perte_d_équilibre", 3.0);
        vertigePositionnel.put("nausées", 1.5);
        kb.put("Vertige positionnel bénin", vertigePositionnel);

        return kb;
    }

    @Override
    protected Map<String, List<String>> construireTraitements() {
        Map<String, List<String>> t = new LinkedHashMap<>();
        t.put("Migraine ophtalmique", Arrays.asList(
                "Repos dans une pièce sombre et calme",
                "Antalgique (paracétamol 1 g) ou AINS",
                "Triptan en cas de crise sévère",
                "Consultation neurologique de suivi"
        ));
        t.put("Céphalée de tension", Arrays.asList(
                "Antalgiques simples (paracétamol)",
                "Gestion du stress, relaxation",
                "Hygiène du sommeil"
        ));
        t.put("Névralgie", Arrays.asList(
                "Antalgiques et anti-inflammatoires",
                "Consultation spécialisée pour bilan étiologique",
                "Éviter les efforts physiques intenses"
        ));
        t.put("Vertige positionnel bénin", Arrays.asList(
                "Manœuvres d'Epley (kinésithérapie vestibulaire)",
                "Éviter les changements brusques de position",
                "Bétahistine si nécessaire"
        ));
        return t;
    }
}
