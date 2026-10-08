package com.multiagent.agents;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent spécialiste en Orthopédie / Traumatologie.
 */
public class AgentOrthopediste extends AbstractSpecialistAgent {

    private static final long serialVersionUID = 1L;

    @Override
    protected String getSpecialite() {
        return "Orthopédie";
    }

    @Override
    protected Map<String, Map<String, Double>> construireKnowledgeBase() {
        Map<String, Map<String, Double>> kb = new LinkedHashMap<>();

        Map<String, Double> lumbago = new LinkedHashMap<>();
        lumbago.put("mal_de_dos", 3.5);
        lumbago.put("raideur_nuque", 1.5);
        lumbago.put("douleur_articulaire", 1.5);
        kb.put("Lumbago", lumbago);

        Map<String, Double> entorse = new LinkedHashMap<>();
        entorse.put("gonflement", 3.0);
        entorse.put("douleur_articulaire", 2.5);
        entorse.put("douleur_genou", 2.0);
        kb.put("Entorse", entorse);

        Map<String, Double> arthrose = new LinkedHashMap<>();
        arthrose.put("douleur_articulaire", 3.0);
        arthrose.put("douleur_genou", 2.5);
        arthrose.put("raideur_nuque", 1.5);
        arthrose.put("gonflement", 1.5);
        kb.put("Arthrose", arthrose);

        Map<String, Double> tendinite = new LinkedHashMap<>();
        tendinite.put("douleur_articulaire", 2.5);
        tendinite.put("douleur_genou", 2.0);
        tendinite.put("gonflement", 1.5);
        kb.put("Tendinite", tendinite);

        return kb;
    }

    @Override
    protected Map<String, List<String>> construireTraitements() {
        Map<String, List<String>> t = new LinkedHashMap<>();
        t.put("Lumbago", Arrays.asList(
                "Repos relatif 48h, éviter l'alitement prolongé",
                "Antalgiques et myorelaxants",
                "Kinésithérapie et renforcement lombaire"
        ));
        t.put("Entorse", Arrays.asList(
                "Protocole GREC : Glace, Repos, Élévation, Compression",
                "Immobilisation par orthèse",
                "Rééducation proprioceptive"
        ));
        t.put("Arthrose", Arrays.asList(
                "Antalgiques et AINS par cure courte",
                "Kinésithérapie douce et maintien d'une activité adaptée",
                "Perte de poids si nécessaire"
        ));
        t.put("Tendinite", Arrays.asList(
                "Glace et repos de l'articulation concernée",
                "AINS en gel local ou par voie orale",
                "Étirements et renforcement progressif"
        ));
        return t;
    }
}
