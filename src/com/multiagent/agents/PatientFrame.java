package com.multiagent.agents;

import com.multiagent.model.DiagnosticData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface graphique moderne 100 % Swing (sans dépendance externe).
 */
public class PatientFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Map<String, String[]> CATEGORIES = new LinkedHashMap<>();
    static {
        CATEGORIES.put("🩺   Gastro-Entérologie", new String[]{
                "brûlures_d_estomac", "nausées", "vomissements",
                "douleur_abdominale", "ballonnements", "reflux"});
        CATEGORIES.put("🧠   Neurologie", new String[]{
                "maux_de_tete", "vertiges", "sensibilité_lumière",
                "engourdissement", "perte_d_équilibre", "migraine"});
        CATEGORIES.put("🦴   Orthopédie", new String[]{
                "douleur_articulaire", "gonflement", "mal_de_dos",
                "raideur_nuque", "douleur_genou"});
        CATEGORIES.put("❤️   Cardiologie", new String[]{
                "palpitations", "douleur_thoracique",
                "pression_arterielle_haute", "essoufflement_effort"});
        CATEGORIES.put("🫁   Pneumologie", new String[]{
                "toux_seche", "toux_grasse", "difficulte_respiratoire",
                "sifflement_thoracique", "fievre"});
    }

    private final AgentPatient agent;
    private final Map<String, UiTheme.SymptomChip> chips = new LinkedHashMap<>();
    private final UiTheme.RoundedTextField champPerso =
            new UiTheme.RoundedTextField("ex: fatigue, anxiété, frissons...");
    private final JPanel zoneResultats = new JPanel();
    private final UiTheme.PillLabel badgeStatut =
            new UiTheme.PillLabel("● Prêt", new Color(0xE2E8F0), UiTheme.TEXT_MUTED);
    private final UiTheme.PrimaryButton btnConsulter =
            new UiTheme.PrimaryButton("🔍   Lancer la consultation");
    private final UiTheme.GhostButton btnReset =
            new UiTheme.GhostButton("Réinitialiser");
    private JLabel compteur;

    public PatientFrame(AgentPatient agent) {
        this.agent = agent;
        setTitle("Consultation Médicale — Système Multi-Agents");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(980, 660));
        getContentPane().setBackground(UiTheme.BG);
        buildUI();
        setLocationRelativeTo(null);
        toFront();
    }

    // ==================================================================
    // Construction
    // ==================================================================
    private void buildUI() {
        JPanel racine = new JPanel(new BorderLayout(0, 0));
        racine.setBackground(UiTheme.BG);
        racine.setBorder(new EmptyBorder(18, 22, 18, 22));
        setContentPane(racine);

        racine.add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildPanelSymptomes(),
                buildPanelResultats());
        split.setResizeWeight(0.46);
        split.setBorder(null);
        split.setDividerSize(18);
        split.setOpaque(false);
        split.setContinuousLayout(true);
        racine.add(split, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 4, 18, 4));

        JPanel gauche = new JPanel();
        gauche.setOpaque(false);
        gauche.setLayout(new BoxLayout(gauche, BoxLayout.Y_AXIS));

        JLabel titre = new JLabel("🏥  Consultation Médicale Multi-Agents");
        titre.setFont(UiTheme.F_TITLE);
        titre.setForeground(UiTheme.TEXT);
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sous = new JLabel(
                "Système de diagnostic assisté — analyse par 5 spécialistes en temps réel");
        sous.setFont(UiTheme.F_SUB);
        sous.setForeground(UiTheme.TEXT_MUTED);
        sous.setAlignmentX(Component.LEFT_ALIGNMENT);

        gauche.add(titre);
        gauche.add(Box.createVerticalStrut(4));
        gauche.add(sous);
        header.add(gauche, BorderLayout.WEST);

        JPanel droite = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        droite.setOpaque(false);
        droite.add(badgeStatut);
        header.add(droite, BorderLayout.EAST);

        return header;
    }

    private JPanel buildPanelSymptomes() {
        UiTheme.Card card = new UiTheme.Card(new BorderLayout(0, 12));

        // En-tête
        JPanel entete = new JPanel(new BorderLayout());
        entete.setOpaque(false);
        JLabel titre = new JLabel("Sélectionnez vos symptômes");
        titre.setFont(UiTheme.F_H2);
        titre.setForeground(UiTheme.TEXT);
        compteur = new JLabel("0 sélectionné");
        compteur.setFont(UiTheme.F_SMALL);
        compteur.setForeground(UiTheme.TEXT_MUTED);
        entete.add(titre, BorderLayout.WEST);
        entete.add(compteur, BorderLayout.EAST);
        card.add(entete, BorderLayout.NORTH);

        // Corps défilable
        JPanel corps = new JPanel();
        corps.setOpaque(false);
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));

        for (Map.Entry<String, String[]> e : CATEGORIES.entrySet()) {
            corps.add(buildBlocCategorie(e.getKey(), e.getValue()));
            corps.add(Box.createVerticalStrut(14));
        }
        corps.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(corps);
        UiTheme.applyModernScroll(scroll);
        card.add(scroll, BorderLayout.CENTER);

        // Pied
        JPanel pied = new JPanel();
        pied.setOpaque(false);
        pied.setLayout(new BoxLayout(pied, BoxLayout.Y_AXIS));

        JLabel lblPerso = new JLabel("Autres symptômes (séparés par des virgules)");
        lblPerso.setFont(UiTheme.F_SMALL);
        lblPerso.setForeground(UiTheme.TEXT_MUTED);
        lblPerso.setAlignmentX(Component.LEFT_ALIGNMENT);
        pied.add(lblPerso);
        pied.add(Box.createVerticalStrut(6));

        champPerso.setAlignmentX(Component.LEFT_ALIGNMENT);
        pied.add(champPerso);
        pied.add(Box.createVerticalStrut(12));

        JPanel actions = new JPanel(new BorderLayout(10, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        btnConsulter.addActionListener(e -> lancerConsultation());
        btnReset.addActionListener(e -> reinitialiser());

        actions.add(btnConsulter, BorderLayout.CENTER);
        actions.add(btnReset, BorderLayout.EAST);
        pied.add(actions);

        card.add(pied, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildBlocCategorie(String nom, String[] symptomes) {
        JPanel bloc = new JPanel();
        bloc.setOpaque(false);
        bloc.setLayout(new BoxLayout(bloc, BoxLayout.Y_AXIS));
        bloc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titre = new JLabel(nom);
        titre.setFont(UiTheme.F_BOLD);
        titre.setForeground(UiTheme.TEXT);
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);
        bloc.add(titre);
        bloc.add(Box.createVerticalStrut(8));

        JPanel grille = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        grille.setOpaque(false);
        grille.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String s : symptomes) {
            UiTheme.SymptomChip chip = new UiTheme.SymptomChip(s);
            chip.addActionListener(e -> majCompteur());
            chips.put(s, chip);
            grille.add(chip);
        }
        bloc.add(grille);
        return bloc;
    }

    private JPanel buildPanelResultats() {
        UiTheme.Card card = new UiTheme.Card(new BorderLayout(0, 12));

        JLabel titre = new JLabel("📋   Rapport médical");
        titre.setFont(UiTheme.F_H2);
        titre.setForeground(UiTheme.TEXT);
        card.add(titre, BorderLayout.NORTH);

        zoneResultats.setOpaque(false);
        zoneResultats.setLayout(new BoxLayout(zoneResultats, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(zoneResultats);
        UiTheme.applyModernScroll(scroll);
        card.add(scroll, BorderLayout.CENTER);

        afficherEtatVide();
        return card;
    }

    // ==================================================================
    // États
    // ==================================================================
    private void afficherEtatVide() {
        zoneResultats.removeAll();

        JPanel centre = new JPanel();
        centre.setOpaque(false);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));

        JLabel icone = new JLabel("🩺", SwingConstants.CENTER);
        icone.setFont(new Font(UiTheme.FF, Font.PLAIN, 56));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("Aucune consultation en cours");
        msg.setFont(UiTheme.F_H2);
        msg.setForeground(UiTheme.TEXT);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel aide = new JLabel(
                "<html><div style='text-align:center;color:#64748B;'>"
                + "1. Sélectionnez vos symptômes à gauche<br>"
                + "2. Cliquez sur <b>Lancer la consultation</b><br>"
                + "3. Recevez le diagnostic des spécialistes</div></html>");
        aide.setFont(UiTheme.F_BODY);
        aide.setAlignmentX(Component.CENTER_ALIGNMENT);

        centre.add(Box.createVerticalGlue());
        centre.add(icone);
        centre.add(Box.createVerticalStrut(12));
        centre.add(msg);
        centre.add(Box.createVerticalStrut(10));
        centre.add(aide);
        centre.add(Box.createVerticalGlue());

        zoneResultats.add(centre);
        zoneResultats.revalidate();
        zoneResultats.repaint();
    }

    private void afficherChargement(List<String> symptomes) {
        zoneResultats.removeAll();

        JPanel conteneur = new JPanel();
        conteneur.setOpaque(false);
        conteneur.setLayout(new BoxLayout(conteneur, BoxLayout.Y_AXIS));

        JLabel icone = new JLabel("⏳", SwingConstants.CENTER);
        icone.setFont(new Font(UiTheme.FF, Font.PLAIN, 48));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("Consultation en cours...");
        msg.setFont(UiTheme.F_H2);
        msg.setForeground(UiTheme.TEXT);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Analyse par les spécialistes en temps réel");
        sub.setFont(UiTheme.F_BODY);
        sub.setForeground(UiTheme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setPreferredSize(new Dimension(280, 6));
        progress.setMaximumSize(new Dimension(280, 6));
        progress.setBorderPainted(false);
        progress.setBackground(UiTheme.SOFT_BG);
        progress.setForeground(UiTheme.PRIMARY);
        progress.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.setBorder(new EmptyBorder(22, 20, 0, 20));

        JLabel lblSymp = new JLabel("Symptômes transmis :");
        lblSymp.setFont(UiTheme.F_SMALL);
        lblSymp.setForeground(UiTheme.TEXT_MUTED);
        lblSymp.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(lblSymp);
        box.add(Box.createVerticalStrut(8));

        JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        chipsPanel.setOpaque(false);
        chipsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (String s : symptomes) {
            chipsPanel.add(new UiTheme.PillLabel(s, UiTheme.PRIMARY_LT, UiTheme.PRIMARY_DK));
        }
        box.add(chipsPanel);

        conteneur.add(Box.createVerticalGlue());
        conteneur.add(icone);
        conteneur.add(Box.createVerticalStrut(14));
        conteneur.add(msg);
        conteneur.add(Box.createVerticalStrut(6));
        conteneur.add(sub);
        conteneur.add(Box.createVerticalStrut(20));
        conteneur.add(progress);
        conteneur.add(box);
        conteneur.add(Box.createVerticalGlue());

        zoneResultats.add(conteneur);
        zoneResultats.revalidate();
        zoneResultats.repaint();
    }

    private void afficherRapportVisuel(DiagnosticData r) {
        zoneResultats.removeAll();

        // --- Carte diagnostic retenu ---
        JPanel diagCard = sousCarte(new Color(0xEFF6FF));
        JPanel diagInner = new JPanel();
        diagInner.setOpaque(false);
        diagInner.setLayout(new BoxLayout(diagInner, BoxLayout.Y_AXIS));

        JLabel tag = new JLabel("🎯   DIAGNOSTIC RETENU");
        tag.setFont(UiTheme.F_SMALL);
        tag.setForeground(UiTheme.PRIMARY_DK);
        tag.setAlignmentX(Component.LEFT_ALIGNMENT);
        diagInner.add(tag);
        diagInner.add(Box.createVerticalStrut(8));

        JLabel patho = new JLabel(r.getPathologieRetenue());
        patho.setFont(new Font(UiTheme.FF, Font.BOLD, 22));
        patho.setForeground(UiTheme.TEXT);
        patho.setAlignmentX(Component.LEFT_ALIGNMENT);
        diagInner.add(patho);
        diagInner.add(Box.createVerticalStrut(6));

        Color scoreColor = r.getScoreMax() >= 70 ? UiTheme.SUCCESS
                         : r.getScoreMax() >= 40 ? UiTheme.WARNING : UiTheme.TEXT_MUTED;
        JLabel score = new JLabel(String.format("Confiance : %.1f %%", r.getScoreMax()));
        score.setFont(UiTheme.F_BOLD);
        score.setForeground(scoreColor);
        score.setAlignmentX(Component.LEFT_ALIGNMENT);
        diagInner.add(score);
        diagInner.add(Box.createVerticalStrut(14));

        diagInner.add(UiTheme.separator());
        diagInner.add(Box.createVerticalStrut(12));

        JLabel titreTrait = new JLabel("💊   Traitements préconisés");
        titreTrait.setFont(UiTheme.F_BOLD);
        titreTrait.setForeground(UiTheme.TEXT);
        titreTrait.setAlignmentX(Component.LEFT_ALIGNMENT);
        diagInner.add(titreTrait);
        diagInner.add(Box.createVerticalStrut(8));

        if (r.getTraitements().isEmpty()) {
            JLabel aucun = new JLabel("Aucun traitement spécifique");
            aucun.setFont(UiTheme.F_BODY);
            aucun.setForeground(UiTheme.TEXT_MUTED);
            aucun.setAlignmentX(Component.LEFT_ALIGNMENT);
            diagInner.add(aucun);
        } else {
            int i = 1;
            for (String t : r.getTraitements()) {
                JLabel item = new JLabel("<html><div style='width:340px;'>"
                        + "<b>" + i + ".</b> " + escape(t) + "</div></html>");
                item.setFont(UiTheme.F_BODY);
                item.setForeground(UiTheme.TEXT);
                item.setAlignmentX(Component.LEFT_ALIGNMENT);
                item.setBorder(new EmptyBorder(2, 4, 6, 0));
                diagInner.add(item);
                i++;
            }
        }
        diagCard.add(diagInner);
        zoneResultats.add(diagCard);
        zoneResultats.add(Box.createVerticalStrut(12));

        // --- Carte avis spécialistes ---
        JPanel avisCard = sousCarte(Color.WHITE);
        JPanel avisInner = new JPanel();
        avisInner.setOpaque(false);
        avisInner.setLayout(new BoxLayout(avisInner, BoxLayout.Y_AXIS));

        JLabel titreAvis = new JLabel("🧑‍⚕️   Avis des spécialistes");
        titreAvis.setFont(UiTheme.F_H2);
        titreAvis.setForeground(UiTheme.TEXT);
        titreAvis.setAlignmentX(Component.LEFT_ALIGNMENT);
        avisInner.add(titreAvis);
        avisInner.add(Box.createVerticalStrut(12));

        if (r.getAvisSpecialistes().isEmpty()) {
            JLabel aucun = new JLabel("Aucun avis reçu");
            aucun.setFont(UiTheme.F_BODY);
            aucun.setForeground(UiTheme.TEXT_MUTED);
            aucun.setAlignmentX(Component.LEFT_ALIGNMENT);
            avisInner.add(aucun);
        } else {
            List<Map.Entry<String, Double>> entries =
                    new ArrayList<>(r.getAvisSpecialistes().entrySet());
            entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            for (Map.Entry<String, Double> e : entries) {
                UiTheme.ScoreBar bar = new UiTheme.ScoreBar(e.getKey(), e.getValue());
                bar.setAlignmentX(Component.LEFT_ALIGNMENT);
                avisInner.add(bar);
                avisInner.add(Box.createVerticalStrut(4));
            }
        }
        avisCard.add(avisInner);
        zoneResultats.add(avisCard);
        zoneResultats.add(Box.createVerticalStrut(12));

        // --- Carte symptômes analysés + avertissement ---
        JPanel sympCard = sousCarte(Color.WHITE);
        JPanel sympInner = new JPanel();
        sympInner.setOpaque(false);
        sympInner.setLayout(new BoxLayout(sympInner, BoxLayout.Y_AXIS));

        JLabel titreSymp = new JLabel("📝   Symptômes analysés");
        titreSymp.setFont(UiTheme.F_H2);
        titreSymp.setForeground(UiTheme.TEXT);
        titreSymp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sympInner.add(titreSymp);
        sympInner.add(Box.createVerticalStrut(10));

        JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        chipsPanel.setOpaque(false);
        chipsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String s : r.getSymptomes()) {
            chipsPanel.add(new UiTheme.PillLabel(s, UiTheme.SOFT_BG, UiTheme.TEXT));
        }
        sympInner.add(chipsPanel);
        sympInner.add(Box.createVerticalStrut(14));

        JLabel avert = new JLabel(
                "<html><div style='width:380px;color:#92400E;'>"
                + "⚠️ <b>Avertissement</b> — Ces informations sont indicatives et "
                + "ne remplacent en aucun cas l'avis d'un professionnel de santé.</div></html>");
        avert.setFont(UiTheme.F_SMALL);
        avert.setAlignmentX(Component.LEFT_ALIGNMENT);
        avert.setBorder(new EmptyBorder(10, 12, 10, 12));
        avert.setOpaque(true);
        avert.setBackground(new Color(0xFEF3C7));
        sympInner.add(avert);

        sympCard.add(sympInner);
        zoneResultats.add(sympCard);
        zoneResultats.add(Box.createVerticalStrut(6));

        zoneResultats.revalidate();
        zoneResultats.repaint();
    }

    private JPanel sousCarte(Color bg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(true);
        p.setBackground(bg);
        p.setBorder(BorderFactory.createCompoundBorder(
                new UiTheme.RoundedBorder(UiTheme.BORDER, 12),
                new EmptyBorder(14, 16, 14, 16)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return p;
    }

    // ==================================================================
    // Actions
    // ==================================================================
    private void lancerConsultation() {
        List<String> symptomes = new ArrayList<>();
        for (Map.Entry<String, UiTheme.SymptomChip> e : chips.entrySet()) {
            if (e.getValue().isSelected()) symptomes.add(e.getKey());
        }
        String brut = champPerso.getText().trim();
        if (!brut.isEmpty()) {
            for (String s : brut.split(",")) {
                String v = s.trim().replace(' ', '_').toLowerCase();
                if (!v.isEmpty() && !symptomes.contains(v)) symptomes.add(v);
            }
        }
        if (symptomes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner au moins un symptôme.",
                    "Aucun symptôme", JOptionPane.WARNING_MESSAGE);
            return;
        }
        btnConsulter.setEnabled(false);
        majBadge("● Consultation en cours...", new Color(0xFEF3C7), new Color(0x92400E));
        afficherChargement(symptomes);
        agent.envoyerSymptomes(symptomes);
    }

    private void reinitialiser() {
        for (UiTheme.SymptomChip c : chips.values()) c.setSelected(false);
        champPerso.setText("");
        majCompteur();
        afficherEtatVide();
        majBadge("● Prêt", new Color(0xE2E8F0), UiTheme.TEXT_MUTED);
    }

    private void majCompteur() {
        int n = 0;
        for (UiTheme.SymptomChip c : chips.values()) if (c.isSelected()) n++;
        compteur.setText(n + " sélectionné" + (n > 1 ? "s" : ""));
    }

    private void majBadge(String txt, Color bg, Color fg) {
        badgeStatut.setText(txt);
        badgeStatut.setColors(bg, fg);
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ==================================================================
    // API appelée par l'agent (thread-safe)
    // ==================================================================
    public void afficherStatut(String statut) {
        SwingUtilities.invokeLater(() -> badgeStatut.setText("● " + statut));
    }

    public void afficherErreur(String message) {
        SwingUtilities.invokeLater(() -> {
            btnConsulter.setEnabled(true);
            majBadge("● Erreur", new Color(0xFEE2E2), UiTheme.DANGER);
            JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
        });
    }

    public void afficherRapport(DiagnosticData rapport) {
        SwingUtilities.invokeLater(() -> {
            afficherRapportVisuel(rapport);
            btnConsulter.setEnabled(true);
            majBadge("● Consultation terminée", new Color(0xD1FAE5), new Color(0x065F46));
        });
    }
}
