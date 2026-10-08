package com.multiagent.agents;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent spécialiste en Cardiologie (pression artérielle / circulation).
 */
public class AgentCardiologue extends AbstractSpecialistAgent {

    private static final long serialVersionUID = 1L;

    @Override
    protected String getSpecialite() {
        return "Cardiologie";
    }

    @Override
    protected Map<String, Map<String, Double>> construireKnowledgeBase() {
        Map<String, Map<String, Double>> kb = new LinkedHashMap<>();

        Map<String, Double> hta = new LinkedHashMap<>();
        hta.put("pression_arterielle_haute", 3.5);
        hta.put("maux_de_tete", 1.5);
        hta.put("essoufflement_effort", 1.5);
        hta.put("palpitations", 1.0);
        kb.put("Hypertension artérielle", hta);

        Map<String, Double> arythmie = new LinkedHashMap<>();
        arythmie.put("palpitations", 3.5);
        arythmie.put("essoufflement_effort", 2.0);
        arythmie.put("douleur_thoracique", 1.5);
        kb.put("Arythmie cardiaque", arythmie);

        Map<String, Double> angine = new LinkedHashMap<>();
        angine.put("douleur_thoracique", 3.5);
        angine.put("essoufflement_effort", 3.0);
        angine.put("palpitations", 1.5);
        kb.put("Angine de poitrine", angine);

        return kb;
    }

    @Override
    protected Map<String, List<String>> construireTraitements() {
        Map<String, List<String>> t = new LinkedHashMap<>();
        t.put("Hypertension artérielle", Arrays.asList(
                "Bilan cardiologique complet (ECG, MAPA)",
                "Régime hyposodé et activité physique régulière",
                "Antihypertenseur (IEC, ARA2 ou diurétique) selon avis médical"
        ));
        t.put("Arythmie cardiaque", Arrays.asList(
                "ECG et Holter ECG en urgence",
                "Éviter caféine, alcool et stimulants",
                "Traitement antiarythmique selon étiologie"
        ));
        t.put("Angine de poitrine", Arrays.asList(
                "URGENCE : appel au 15 si douleur persistante",
                "Repos strict et trinitrine sublinguale",
                "Bilan coronarographique rapide"
        ));
        return t;
    }
}
