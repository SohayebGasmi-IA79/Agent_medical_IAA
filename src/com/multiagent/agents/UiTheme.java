package com.multiagent.agents;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Thème visuel 100% Swing pur (aucune dépendance externe).
 * Fournit : palette, typographie et composants personnalisés
 * (Card, PrimaryButton, GhostButton, SymptomChip, ScoreBar,
 * PillLabel, RoundedTextField, ModernScrollBarUI).
 */
public final class UiTheme {

    // ---------------- Palette (inspirée Tailwind) ----------------
    public static final Color BG         = new Color(0xF8FAFC);
    public static final Color CARD       = Color.WHITE;
    public static final Color PRIMARY    = new Color(0x2563EB);
    public static final Color PRIMARY_DK = new Color(0x1D4ED8);
    public static final Color PRIMARY_LT = new Color(0xDBEAFE);
    public static final Color SUCCESS    = new Color(0x10B981);
    public static final Color WARNING    = new Color(0xF59E0B);
    public static final Color DANGER     = new Color(0xEF4444);
    public static final Color TEXT       = new Color(0x0F172A);
    public static final Color TEXT_MUTED = new Color(0x64748B);
    public static final Color BORDER     = new Color(0xE2E8F0);
    public static final Color SOFT_BG    = new Color(0xF1F5F9);

    // ---------------- Typographie ----------------
    public static final String FF = choisirPolice();
    public static final Font F_TITLE = new Font(FF, Font.BOLD, 22);
    public static final Font F_SUB   = new Font(FF, Font.PLAIN, 13);
    public static final Font F_H2    = new Font(FF, Font.BOLD, 15);
    public static final Font F_BODY  = new Font(FF, Font.PLAIN, 13);
    public static final Font F_SMALL = new Font(FF, Font.PLAIN, 12);
    public static final Font F_BOLD  = new Font(FF, Font.BOLD, 13);
    public static final Font F_BIG   = new Font(FF, Font.BOLD, 26);

    private UiTheme() {}

    private static String choisirPolice() {
        String[] pref = {"Segoe UI", "SF Pro Text", "Helvetica Neue", "Roboto", "Inter", "SansSerif"};
        String[] dispo = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String p : pref) {
            for (String d : dispo) if (d.equalsIgnoreCase(p)) return p;
        }
        return "SansSerif";
    }

    /** Ajustements UIManager globaux (sans Look & Feel externe). */
    public static void install() {
        UIManager.put("Panel.background", BG);
        UIManager.put("OptionPane.background", CARD);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.buttonFont", F_BOLD);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("TextField.caretForeground", PRIMARY);
        UIManager.put("TextField.selectionBackground", PRIMARY_LT);
        UIManager.put("TextField.selectionForeground", TEXT);
        UIManager.put("ToolTip.background", TEXT);
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("ToolTip.font", F_SMALL);
        System.out.println("[UiTheme] Thème Swing pur installé.");
    }

    // ==================================================================
    // Bordure arrondie réutilisable
    // ==================================================================
    public static class RoundedBorder extends AbstractBorder {
        private final Color color; private final int radius; private final int thickness;
        public RoundedBorder(Color c, int r) { this(c, r, 1); }
        public RoundedBorder(Color c, int r, int t) { color = c; radius = r; thickness = t; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
    }

    // ==================================================================
    // Carte blanche arrondie avec ombre douce
    // ==================================================================
    public static class Card extends JPanel {
        private final int radius;
        public Card(LayoutManager layout) { this(layout, 14); }
        public Card(LayoutManager layout, int radius) {
            super(layout);
            this.radius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(18, 20, 18, 20));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth() - 4, h = getHeight() - 4;
            for (int i = 3; i >= 1; i--) {
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fillRoundRect(i, i + 1, w, h, radius, radius);
            }
            g2.setColor(CARD);
            g2.fillRoundRect(0, 0, w, h, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==================================================================
    // Bouton principal (bleu plein arrondi)
    // ==================================================================
    public static class PrimaryButton extends JButton {
        public PrimaryButton(String t) {
            super(t);
            setFont(F_BOLD);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorder(new EmptyBorder(11, 22, 11, 22));
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg = !isEnabled() ? new Color(0x94A3B8)
                    : getModel().isPressed()  ? PRIMARY_DK
                    : getModel().isRollover() ? PRIMARY_DK : PRIMARY;
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==================================================================
    // Bouton fantôme (contour discret)
    // ==================================================================
    public static class GhostButton extends JButton {
        public GhostButton(String t) {
            super(t);
            setFont(F_BODY);
            setForeground(TEXT);
            setFocusPainted(false);
            setBorder(new EmptyBorder(10, 18, 10, 18));
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            if (getModel().isRollover()) {
                g2.setColor(SOFT_BG);
                g2.fillRoundRect(0, 0, w, h, 10, 10);
            } else {
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==================================================================
    // Chip cliquable (toggle arrondi)
    // ==================================================================
    public static class SymptomChip extends JToggleButton {
        public SymptomChip(String t) {
            super(t);
            setFont(F_BODY);
            setForeground(TEXT);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(7, 14, 7, 14));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), arc = h;
            if (isSelected()) {
                g2.setColor(PRIMARY_LT);
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                setForeground(PRIMARY_DK);
                g2.setColor(PRIMARY);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, arc, arc);
            } else if (getModel().isRollover()) {
                g2.setColor(SOFT_BG);
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                setForeground(TEXT);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, arc, arc);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
                setForeground(TEXT);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==================================================================
    // Barre de score horizontale
    // ==================================================================
    public static class ScoreBar extends JComponent {
        private final String label; private final double score; private final Color color;
        public ScoreBar(String l, double s) { this(l, s, s >= 70 ? SUCCESS : s >= 40 ? WARNING : TEXT_MUTED); }
        public ScoreBar(String l, double s, Color c) {
            label = l; score = Math.max(0, Math.min(100, s)); color = c;
            setPreferredSize(new Dimension(240, 44));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setMinimumSize(new Dimension(140, 44));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            g2.setFont(F_BODY); g2.setColor(TEXT);
            FontMetrics fm = g2.getFontMetrics();
            String nom = label;
            int maxW = w - 60;
            if (fm.stringWidth(nom) > maxW) {
                while (nom.length() > 3 && fm.stringWidth(nom + "…") > maxW) nom = nom.substring(0, nom.length() - 1);
                nom += "…";
            }
            g2.drawString(nom, 0, 14);
            String pct = String.format("%.0f%%", score);
            g2.setFont(F_BOLD); FontMetrics fm2 = g2.getFontMetrics();
            g2.setColor(color);
            g2.drawString(pct, w - fm2.stringWidth(pct), 14);
            int y = 24, h = 8;
            g2.setColor(SOFT_BG);
            g2.fillRoundRect(0, y, w, h, h, h);
            int fw = (int) Math.round((score / 100.0) * w);
            if (fw > 0) { g2.setColor(color); g2.fillRoundRect(0, y, fw, h, h, h); }
            g2.dispose();
        }
    }

    // ==================================================================
    // Badge "pill" arrondi
    // ==================================================================
    public static class PillLabel extends JLabel {
        private Color bg, fg;
        public PillLabel(String t, Color bg, Color fg) {
            super(t, SwingConstants.CENTER);
            this.bg = bg; this.fg = fg;
            setFont(F_SMALL);
            setForeground(fg);
            setBorder(new EmptyBorder(4, 12, 4, 12));
            setOpaque(false);
        }
        public void setColors(Color bg, Color fg) { this.bg = bg; this.fg = fg; setForeground(fg); repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==================================================================
    // Champ texte arrondi avec placeholder (sans FlatLaf)
    // ==================================================================
    public static class RoundedTextField extends JPanel {
        private final JTextField field;
        private final String placeholder;
        private final Color normalFg = TEXT;
        private final Color phFg = TEXT_MUTED;
        private boolean showingPh = false;

        public RoundedTextField(String placeholder) {
            this.placeholder = (placeholder == null) ? "" : placeholder;
            setLayout(new BorderLayout());
            setOpaque(false);
            field = new JTextField();
            field.setFont(F_BODY);
            field.setOpaque(false);
            field.setBorder(new EmptyBorder(8, 12, 8, 12));
            field.setCaretColor(PRIMARY);
            field.setSelectionColor(PRIMARY_LT);
            field.setSelectedTextColor(TEXT);
            add(field, BorderLayout.CENTER);
            setPreferredSize(new Dimension(200, 38));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            installerPlaceholder();
            field.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }
                @Override public void focusLost(FocusEvent e)   { repaint(); }
            });
        }

        private void installerPlaceholder() {
            afficherPlaceholder();
            field.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (showingPh) {
                        field.setText("");
                        field.setForeground(normalFg);
                        showingPh = false;
                    }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (field.getText().isEmpty()) afficherPlaceholder();
                }
            });
        }

        private void afficherPlaceholder() {
            if (placeholder.isEmpty()) return;
            showingPh = true;
            field.setForeground(phFg);
            field.setText(placeholder);
        }

        public String getText() { return showingPh ? "" : field.getText(); }
        public void setText(String t) {
            if (t == null || t.isEmpty()) { afficherPlaceholder(); return; }
            showingPh = false;
            field.setForeground(normalFg);
            field.setText(t);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.setColor(field.hasFocus() ? PRIMARY : BORDER);
            g2.setStroke(new BasicStroke(field.hasFocus() ? 1.6f : 1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==================================================================
    // Scrollbar fine et moderne
    // ==================================================================
    public static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
        @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
        private JButton zeroBtn() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(new Color(0xF1F5F9));
            g.fillRect(r.x, r.y, r.width, r.height);
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.width <= 0 || r.height <= 0) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x94A3B8));
            g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 6, 6);
            g2.dispose();
        }
    }

    /** Applique une scrollbar fine à un JScrollPane. */
    public static void applyModernScroll(JScrollPane sp) {
        sp.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
    }

    /** Séparateur horizontal discret. */
    public static JSeparator separator() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        s.setBackground(BORDER);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }
}
