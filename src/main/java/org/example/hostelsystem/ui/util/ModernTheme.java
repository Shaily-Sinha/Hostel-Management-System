package org.example.hostelsystem.ui.util;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ModernTheme — dark design system for Hostel Management System.
 * Drop this file into: src/main/java/org/example/hostelsystem/ui/util/
 */
public class ModernTheme {

    // ── Palette ──────────────────────────────────────────────────────────────
    public static final Color BG_DARK        = new Color(9, 13, 20);
    public static final Color BG_CARD        = new Color(17, 24, 39);
    public static final Color BG_INPUT       = new Color(24, 33, 48);
    public static final Color BG_HOVER       = new Color(36, 47, 65);
    public static final Color BORDER_COLOR   = new Color(48, 61, 83);
    public static final Color ACCENT         = new Color(56, 189, 248);
    public static final Color ACCENT_DARK    = new Color(37, 99, 235);
    public static final Color SUCCESS        = new Color(35, 197, 94);
    public static final Color SUCCESS_DARK   = new Color(35, 134, 54);
    public static final Color DANGER         = new Color(248, 81, 73);
    public static final Color DANGER_DARK    = new Color(180, 35, 24);
    public static final Color WARNING        = new Color(210, 153, 34);
    public static final Color TEXT_PRIMARY   = new Color(230, 237, 243);
    public static final Color TEXT_SECONDARY = new Color(139, 148, 158);
    public static final Color TEXT_MUTED     = new Color(88, 96, 105);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEAD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_NAV     = new Font("Segoe UI", Font.BOLD, 12);

    /** Call once from main() before creating any frame. */
    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background",               BG_DARK);
        UIManager.put("OptionPane.background",          BG_CARD);
        UIManager.put("OptionPane.messageForeground",   TEXT_PRIMARY);
        UIManager.put("Button.background",              BG_INPUT);
        UIManager.put("Button.foreground",              TEXT_PRIMARY);
        UIManager.put("TextField.background",           BG_INPUT);
        UIManager.put("TextField.foreground",           TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",      ACCENT);
        UIManager.put("PasswordField.background",       BG_INPUT);
        UIManager.put("PasswordField.foreground",       TEXT_PRIMARY);
        UIManager.put("PasswordField.caretForeground",  ACCENT);
        UIManager.put("ComboBox.background",            BG_INPUT);
        UIManager.put("ComboBox.foreground",            TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground",   ACCENT_DARK);
        UIManager.put("ComboBox.selectionForeground",   Color.WHITE);
        UIManager.put("List.background",                BG_CARD);
        UIManager.put("List.foreground",                TEXT_PRIMARY);
        UIManager.put("ScrollPane.background",          BG_DARK);
        UIManager.put("Viewport.background",            BG_DARK);
        UIManager.put("Table.background",               BG_DARK);
        UIManager.put("Table.foreground",               TEXT_PRIMARY);
        UIManager.put("Table.gridColor",                BORDER_COLOR);
        UIManager.put("Table.selectionBackground",      new Color(31, 111, 235, 60));
        UIManager.put("Table.selectionForeground",      TEXT_PRIMARY);
        UIManager.put("TableHeader.background",         new Color(33, 38, 45));
        UIManager.put("TableHeader.foreground",         TEXT_SECONDARY);
        UIManager.put("Label.foreground",               TEXT_PRIMARY);
        UIManager.put("TabbedPane.background",          BG_DARK);
        UIManager.put("TabbedPane.foreground",          TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected",            BG_CARD);
        UIManager.put("TabbedPane.contentAreaColor",    BG_DARK);
        UIManager.put("TabbedPane.light",               BORDER_COLOR);
        UIManager.put("TabbedPane.darkShadow",          BORDER_COLOR);
        UIManager.put("TabbedPane.shadow",              BG_HOVER);
        UIManager.put("TabbedPane.tabAreaBackground",   BG_CARD);
        UIManager.put("MenuBar.background",             BG_CARD);
        UIManager.put("MenuBar.foreground",             TEXT_PRIMARY);
        UIManager.put("Menu.background",                BG_CARD);
        UIManager.put("Menu.foreground",                TEXT_PRIMARY);
        UIManager.put("MenuItem.background",            BG_CARD);
        UIManager.put("MenuItem.foreground",            TEXT_PRIMARY);
        UIManager.put("MenuItem.selectionBackground",   BG_HOVER);
        UIManager.put("MenuItem.selectionForeground",   TEXT_PRIMARY);
        UIManager.put("PopupMenu.background",           BG_CARD);
        UIManager.put("PopupMenu.border",               BorderFactory.createLineBorder(BORDER_COLOR));
        UIManager.put("Separator.foreground",           BORDER_COLOR);
        UIManager.put("ToolTip.background",             BG_HOVER);
        UIManager.put("ToolTip.foreground",             TEXT_PRIMARY);
        UIManager.put("CheckBox.background",            BG_DARK);
        UIManager.put("CheckBox.foreground",            TEXT_PRIMARY);
        UIManager.put("RadioButton.background",         BG_DARK);
        UIManager.put("RadioButton.foreground",         TEXT_PRIMARY);
        UIManager.put("Spinner.background",             BG_INPUT);
        UIManager.put("Spinner.foreground",             TEXT_PRIMARY);
        UIManager.put("ScrollBar.width",                8);
        UIManager.put("ScrollBar.background",           BG_CARD);
        UIManager.put("ScrollBar.thumb",                BG_HOVER);
        UIManager.put("ScrollBar.track",                BG_CARD);
    }

    // ── Buttons ───────────────────────────────────────────────────────────────
    public static JButton primaryButton(String text) { return styledBtn(text, ACCENT_DARK, Color.WHITE); }
    public static JButton successButton(String text) { return styledBtn(text, SUCCESS_DARK, Color.WHITE); }
    public static JButton dangerButton(String text)  { return styledBtn(text, DANGER_DARK,  Color.WHITE); }
    public static JButton secondaryButton(String text){ return styledBtn(text, BG_HOVER, TEXT_PRIMARY); }

    private static JButton styledBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            private boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited(MouseEvent e)  { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(FONT_SUBHEAD);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 24, 36));
        return btn;
    }

    // ── Text fields ───────────────────────────────────────────────────────────
    public static JTextField textField() {
        JTextField tf = new JTextField();
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT);
        tf.setFont(FONT_BODY);
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        tf.setPreferredSize(new Dimension(tf.getPreferredSize().width, 34));
        return tf;
    }

    public static JPasswordField passwordField() {
        JPasswordField pf = new JPasswordField();
        pf.setBackground(BG_INPUT);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(ACCENT);
        pf.setFont(FONT_BODY);
        pf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        pf.setPreferredSize(new Dimension(pf.getPreferredSize().width, 34));
        return pf;
    }

    public static JTextField searchField(String placeholder) {
        JTextField tf = textField();
        tf.setText(placeholder);
        tf.setForeground(TEXT_MUTED);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) { tf.setText(""); tf.setForeground(TEXT_PRIMARY); }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) { tf.setText(placeholder); tf.setForeground(TEXT_MUTED); }
            }
        });
        return tf;
    }

    // ── ComboBox ─────────────────────────────────────────────────────────────
    public static <T> JComboBox<T> comboBox(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setBackground(BG_INPUT);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setBorder(new LineBorder(BORDER_COLOR, 1));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT_DARK : BG_INPUT);
                setForeground(TEXT_PRIMARY);
                setBorder(new EmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
        return cb;
    }

    // ── Labels ────────────────────────────────────────────────────────────────
    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_SECONDARY);
        l.setFont(FONT_LABEL);
        return l;
    }

    // ── Card panel ───────────────────────────────────────────────────────────
    public static JPanel card() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    // ── Table styling ─────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setBackground(new Color(17, 24, 39));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(53);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(31, 42, 61));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(37, 99, 235, 95));
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        if (table.getRowSorter() == null) {
            table.setAutoCreateRowSorter(true);
        }
        table.setFocusable(false);
        table.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(15, 23, 42));
        header.setForeground(new Color(226, 232, 240));
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        header.setPreferredSize(new Dimension(header.getWidth(), 55));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBackground(new Color(15, 23, 42));
                label.setForeground(new Color(226, 232, 240));
                label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                label.setOpaque(true);
                label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(203, 213, 225)));
                return label;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(isSelected
                    ? new Color(37, 99, 235, 100)
                    : (row % 2 == 0 ? new Color(17, 24, 39) : new Color(21, 30, 46)));
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.PLAIN, 15));
                setBorder(new EmptyBorder(0, 16, 0, 16));
                setOpaque(true);
                return this;
            }
        });

        applyStatusRenderer(table);
    }

    private static void applyStatusRenderer(JTable table) {
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            if (table.getColumnName(i).toLowerCase().contains("status")) {
                table.getColumnModel().getColumn(i).setCellRenderer(statusRenderer());
            }
        }
    }

    // ── ScrollPane ───────────────────────────────────────────────────────────
    public static JScrollPane scrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBackground(new Color(17, 24, 39));
        sp.getViewport().setBackground(new Color(17, 24, 39));
        sp.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)));
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(9, 0));
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 9));
        return sp;
    }

    // ── Status badge renderer ─────────────────────────────────────────────────
    public static TableCellRenderer statusRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel badge = new JLabel(String.valueOf(value), SwingConstants.CENTER);
                badge.setForeground(badgeColor(String.valueOf(value)));
                badge.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                badge.setOpaque(true);
                badge.setBackground(isSelected
                    ? new Color(37, 99, 235, 100)
                    : new Color(8, 13, 22));
                return badge;
            }
        };
    }

    private static Color badgeColor(String status) {
        if (status == null) return TEXT_MUTED;
        return switch (status.toUpperCase()) {
            case "AVAILABLE", "ACTIVE", "APPROVED", "PRESENT", "CONFIRMED", "PAID",
                 "ISSUED", "RENEWED", "READ", "SENT", "ACKNOWLEDGED", "EXCUSED" -> SUCCESS;
            case "OCCUPIED", "INACTIVE", "REJECTED", "ABSENT", "CANCELLED", "CHECKED_OUT",
                 "LOST", "EXPIRED", "DUE", "UNREAD" -> DANGER;
            case "RESERVED", "PENDING", "LATE", "MAINTENANCE", "PARTIAL", "NOTIFIED" -> WARNING;
            default -> TEXT_SECONDARY;
        };
    }

    // ── Panel header ─────────────────────────────────────────────────────────
    public static JPanel panelHeader(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel t = new JLabel(title);
        t.setFont(FONT_TITLE);
        t.setForeground(TEXT_PRIMARY);

        JLabel s = new JLabel(subtitle);
        s.setFont(FONT_LABEL);
        s.setForeground(TEXT_MUTED);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(t);
        if (subtitle != null && !subtitle.isEmpty()) {
            left.add(Box.createHorizontalStrut(10));
            left.add(s);
        }
        p.add(left, BorderLayout.WEST);
        return p;
    }
}
