package com.java.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public final class AppTheme {

    // ===== COLOR PALETTE (Cyberpunk Dark) =====
    public static final Color BG_DARKEST   = new Color(0x0B, 0x0F, 0x19); // #0B0F19
    public static final Color BG_DARK      = new Color(0x11, 0x18, 0x27); // #111827
    public static final Color BG_CARD      = new Color(0x1E, 0x29, 0x3B); // #1E293B
    public static final Color BG_INPUT     = new Color(0x27, 0x3A, 0x4D); // #273A4D

    public static final Color ACCENT_CYAN  = new Color(0x06, 0xB6, 0xD4); // #06B6D4
    public static final Color ACCENT_GREEN = new Color(0x22, 0xC5, 0x5E); // #22C55E
    public static final Color ACCENT_RED   = new Color(0xEF, 0x44, 0x44); // #EF4444
    public static final Color ACCENT_YELLOW= new Color(0xEA, 0xB3, 0x08); // #EAB308
    public static final Color ACCENT_PURPLE= new Color(0xA8, 0x55, 0xF7); // #A855F7

    public static final Color TEXT_PRIMARY   = new Color(0xF1, 0xF5, 0xF9); // #F1F5F9
    public static final Color TEXT_SECONDARY = new Color(0x94, 0xA3, 0xB8); // #94A3B8
    public static final Color TEXT_MUTED     = new Color(0x64, 0x71, 0x7A); // #64717A

    // ===== FONTS (Larger, readable) =====
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBHEAD = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_CODE    = new Font("JetBrains Mono", Font.PLAIN, 14);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 15);

    // ===== BORDERS =====
    public static final Border BORDER_CARD = new LineBorder(new Color(0x33, 0x44, 0x55), 1, true);
    public static final Border BORDER_INPUT = new LineBorder(new Color(0x44, 0x55, 0x66), 1, true);
    public static final Border BORDER_EMPTY_SM = new EmptyBorder(8, 12, 8, 12);
    public static final Border BORDER_EMPTY_MD = new EmptyBorder(16, 20, 16, 20);
    public static final Border BORDER_EMPTY_LG = new EmptyBorder(24, 32, 24, 32);

    // ===== STATUS COLORS =====
    public static Color statusColor(String status) {
        return switch (status) {
            case "AC" -> ACCENT_GREEN;
            case "WA" -> ACCENT_RED;
            case "TLE" -> ACCENT_YELLOW;
            case "RE", "CE", "MLE" -> TEXT_MUTED;
            default -> TEXT_SECONDARY;
        };
    }

    // ===== UI FACTORIES =====
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT_CYAN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 24, 10, 24));
        btn.setMinimumSize(new Dimension(80, 36));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(ACCENT_CYAN.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(ACCENT_CYAN); }
        });
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(BG_CARD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setBorder(new LineBorder(new Color(0x44, 0x55, 0x66), 1, true));
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 24, 10, 24));
        btn.setMinimumSize(new Dimension(80, 36));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(0x33, 0x44, 0x55)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(BG_CARD); }
        });
        return btn;
    }

    public static JButton createAccentButton(String text, Color bg) {
        JButton btn = createPrimaryButton(text);
        btn.setBackground(bg);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel createHeadingLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel createBodyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BORDER_CARD,
                BORDER_EMPTY_MD
        ));
        return panel;
    }

    public static JScrollPane createStyledScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BORDER_CARD);
        sp.getViewport().setBackground(BG_DARK);
        sp.setBackground(BG_DARK);
        return sp;
    }

    public static JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(FONT_CODE);
        ta.setBackground(BG_INPUT);
        ta.setForeground(TEXT_PRIMARY);
        ta.setCaretColor(TEXT_PRIMARY);
        ta.setBorder(BORDER_EMPTY_SM);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setSelectionColor(ACCENT_CYAN.darker());
        ta.setSelectedTextColor(Color.WHITE);
        return ta;
    }

    public static JTextField createStyledTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(FONT_BODY);
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(BORDER_INPUT, BORDER_EMPTY_SM));
        tf.setSelectionColor(ACCENT_CYAN.darker());
        tf.setSelectedTextColor(Color.WHITE);
        return tf;
    }

    public static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_BODY);
        cb.setBackground(BG_INPUT);
        cb.setForeground(TEXT_PRIMARY);
        cb.setBorder(BORDER_EMPTY_SM);
        cb.setPreferredSize(new Dimension(cb.getPreferredSize().width, 38));
        return cb;
    }

    public static void applyGlobalTheme() {
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
            UIManager.put("Panel.background", BG_DARK);
            UIManager.put("OptionPane.background", BG_DARK);
            UIManager.put("TextField.background", BG_INPUT);
            UIManager.put("TextArea.background", BG_INPUT);
            UIManager.put("ComboBox.background", BG_INPUT);
            UIManager.put("Table.background", BG_CARD);
            UIManager.put("Table.foreground", TEXT_PRIMARY);
            UIManager.put("Table.gridColor", new Color(0x33, 0x44, 0x55));
            UIManager.put("Table.selectionBackground", ACCENT_CYAN.darker());
            UIManager.put("Table.selectionForeground", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
