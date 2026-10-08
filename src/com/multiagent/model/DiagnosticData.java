package com.multiagent.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Objet de transfert (DTO) sérialisable transportant l'ensemble des
 * informations médicales échangées entre les agents (Patient, Coordinateur,
 * Spécialistes).
 *
 * <p>Cette classe est le seul véhicule de données du système : elle est
 * transmise via {@code ACLMessage.setContentObject()} et donc
 * obligatoirement {@link Serializable}.</p>
 */
public class DiagnosticData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Symptômes déclarés par le patient. */
    private List<String> symptomes;

    /** Carte des avis des spécialistes : clé = "Spécialité : Pathologie", valeur = score (0-100). */
    private Map<String, Double> avisSpecialistes;

    /** Pathologie retenue à l'issue de la délibération. */
    private String pathologieRetenue;

    /** Score de confiance maximal associé à la pathologie retenue. */
    private double scoreMax;

    /** Traitements préconisés pour la pathologie retenue. */
    private List<String> traitements;

    public DiagnosticData() {
        this.symptomes = new ArrayList<>();
        this.avisSpecialistes = new LinkedHashMap<>();
        this.pathologieRetenue = "";
        this.scoreMax = 0.0;
        this.traitements = new ArrayList<>();
    }

    public DiagnosticData(List<String> symptomes) {
        this();
        if (symptomes != null) {
            this.symptomes.addAll(symptomes);
        }
    }

    // ------------------------------------------------------------------
    // Symptômes
    // ------------------------------------------------------------------
    public List<String> getSymptomes() {
        return symptomes;
    }

    public void setSymptomes(List<String> symptomes) {
        this.symptomes = new ArrayList<>();
        if (symptomes != null) {
            this.symptomes.addAll(symptomes);
        }
    }

    public void addSymptome(String symptome) {
        if (symptome != null) {
            String s = symptome.trim();
            if (!s.isEmpty() && !symptomes.contains(s)) {
                symptomes.add(s);
            }
        }
    }

    // ------------------------------------------------------------------
    // Avis des spécialistes
    // ------------------------------------------------------------------
    public Map<String, Double> getAvisSpecialistes() {
        return avisSpecialistes;
    }

    public void setAvisSpecialistes(Map<String, Double> avis) {
        this.avisSpecialistes = new LinkedHashMap<>();
        if (avis != null) {
            this.avisSpecialistes.putAll(avis);
        }
    }

    public void addAvis(String specialite, double score) {
        if (specialite != null) {
            this.avisSpecialistes.put(specialite, score);
        }
    }

    // ------------------------------------------------------------------
    // Pathologie retenue
    // ------------------------------------------------------------------
    public String getPathologieRetenue() {
        return pathologieRetenue;
    }

    public void setPathologieRetenue(String pathologieRetenue) {
        this.pathologieRetenue = (pathologieRetenue == null) ? "" : pathologieRetenue;
    }

    // ------------------------------------------------------------------
    // Score
    // ------------------------------------------------------------------
    public double getScoreMax() {
        return scoreMax;
    }

    public void setScoreMax(double scoreMax) {
        this.scoreMax = scoreMax;
    }

    // ------------------------------------------------------------------
    // Traitements
    // ------------------------------------------------------------------
    public List<String> getTraitements() {
        return traitements;
    }

    public void setTraitements(List<String> traitements) {
        this.traitements = new ArrayList<>();
        if (traitements != null) {
            this.traitements.addAll(traitements);
        }
    }

    public void addTraitement(String traitement) {
        if (traitement != null && !traitement.trim().isEmpty()) {
            this.traitements.add(traitement.trim());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DiagnosticData{");
        sb.append("symptomes=").append(symptomes);
        sb.append(", avisSpecialistes=").append(avisSpecialistes);
        sb.append(", pathologieRetenue='").append(pathologieRetenue).append('\'');
        sb.append(", scoreMax=").append(String.format("%.2f", scoreMax));
        sb.append(", traitements=").append(traitements);
        sb.append('}');
        return sb.toString();
    }
}
