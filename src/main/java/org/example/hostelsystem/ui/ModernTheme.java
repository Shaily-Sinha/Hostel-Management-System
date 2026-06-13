package theme;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class ModernTheme {

    // ─── Color Palette ───────────────────────────────────────────────────────
    public static final Color BG_DARK       = new Color(13, 17, 23);
    public static final Color BG_CARD       = new Color(22, 27, 34);
    public static final Color BG_INPUT      = new Color(33, 38, 45);
    public static final Color BG_HOVER      = new Color(48, 54, 61);
    public static final Color BORDER_COLOR  = new Color(48, 54, 61);
    public static final Color ACCENT        = new Color(88, 166, 255);
    public static final Color ACCENT_DARK   = new Color(31, 111, 235);
    public static final Color SUCCESS       = new Color(35, 197, 94);
    public static final Color DANGER        = new Color(248, 81, 73);
    public static final Color WARNING       = new Color(210, 153, 34);
    public static final Color TEXT_PRIMARY  = new Color(230, 237, 243);
    public static final Color TEXT_SECONDARY= new Color(139, 148, 158);
    public static final Color TEXT_MUTED    = new Color(88, 96, 105);
    public static final Color TABLE_ROW_ALT = new Color(22, 27, 34);
    public static final Color TABLE_SELECT  = new Color(31, 111, 235, 60);
    public static final Color TABLE_HEADER  = new Color(33, 38, 45);

    // ─── Fonts ────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEAD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_NAV     = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);

    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background", BG_DARK);
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Button.background", BG_INPUT);
        UIManager.put("Button.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.background", BG_INPUT);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", ACCENT);
        UIManager.put("PasswordField.background", BG_INPUT);
        UIManager.put("PasswordField.foreground", TEXT_PRIMARY);
        UIManager.put("PasswordField.caretForeground", ACCENT);
        UIManager.put("ComboBox.background", BG_INPUT);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", ACCENT_DARK);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("List.background", BG_CARD);
        UIManager.put("List.foreground", TEXT_PRIMARY);
        UIManager.put("ScrollBar.background", BG_CARD);
        UIManager.put("ScrollBar.thumb", BG_HOVER);
        UIManager.put("ScrollBar.track", BG_CARD);
        UIManager.put("ScrollPane.background", BG_DARK);
        UIManager.put("Viewport.background", BG_DARK);
        UIManager.put("Table.background", BG_DARK);
        UIManager.put("Table.foreground", TEXT_PRIMARY);
        UIManager.put("Table.gridColor", BORDER_COLOR);
        UIManager.put("Table.selectionBackground", TABLE_SELECT);
        UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
        UIManager.put("TableHeader.background", TABLE_HEADER);
        UIManager.put("TableHeader.foreground", TEXT_SECONDARY);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("TabbedPane.background", BG_DARK);
        UIManager.put("TabbedPane.foreground", TEXT_PRIMARY);
        UIManager.put("MenuBar.background", BG_CARD);
        UIManager.put("MenuBar.foreground", TEXT_PRIMARY);
        UIManager.put("Menu.background", BG_CARD);
        UIManager.put("Menu.foreground", TEXT_PRIMARY);
        UIManager.put("MenuItem.background", BG_CARD);
        UIManager.put("MenuItem.foreground", TEXT_PRIMARY);
        UIManager.put("PopupMenu.background", BG_CARD);
        UIManager.put("Separator.foreground", BORDER_COLOR);
        UIManager.put("ToolTip.background", BG_HOVER);
        UIManager.put("ToolTip.foreground", TEXT_PRIMARY);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(BORDER_COLOR));
        UIManager.put("ScrollBar.width", 8);
    }

    // ─── Factory: Styled Button ───────────────────────────────────────────────
    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color drawColor;

                if (hovered) {
                    drawColor = bg.brighter();
                } else {
                    drawColor = bg;
                }
                g2.setColor(drawColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
            { // init block
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
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

    public static JButton primaryButton(String text) {
        return createButton(text, ACCENT_DARK, Color.WHITE);
    }
    public static JButton successButton(String text) {
        return createButton(text, new Color(35, 134, 54), Color.WHITE);
    }
    public static JButton dangerButton(String text) {
        return createButton(text, new Color(180, 35, 24), Color.WHITE);
    }
    public static JButton secondaryButton(String text) {
        return createButton(text, BG_HOVER, TEXT_PRIMARY);
    }

    // ─── Factory: Styled TextField ───────────────────────────────────────────
    public static JTextField createTextField() {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT);
        tf.setFont(FONT_INPUT);
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setPreferredSize(new Dimension(tf.getPreferredSize().width, 36));
        tf.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                tf.setBorder(
                        new CompoundBorder(
                                new LineBorder(ACCENT, 1, true),
                                new EmptyBorder(6, 10, 6, 10)
                        )
                );
            }

            @Override
            public void focusLost(FocusEvent e) {
                tf.setBorder(
                        new CompoundBorder(
                                new LineBorder(BORDER_COLOR, 1, true),
                                new EmptyBorder(6, 10, 6, 10)
                        )
                );
            }
        });
        return tf;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pf.setOpaque(false);
        pf.setBackground(BG_INPUT);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(ACCENT);
        pf.setFont(FONT_INPUT);
        pf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        pf.setPreferredSize(new Dimension(pf.getPreferredSize().width, 36));
        return pf;
    }

    // ─── Factory: Styled ComboBox ────────────────────────────────────────────
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setBackground(BG_INPUT);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_INPUT);
        cb.setBorder(new LineBorder(BORDER_COLOR, 1, true));
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

    // ─── Factory: Styled Label ───────────────────────────────────────────────
    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_SECONDARY);
        l.setFont(FONT_LABEL);
        return l;
    }

    public static JLabel titleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_PRIMARY);
        l.setFont(FONT_TITLE);
        return l;
    }

    // ─── Factory: Card Panel ─────────────────────────────────────────────────
    public static JPanel card() {

        JPanel p = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                // Shadow
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillRoundRect(
                        4,
                        4,
                        getWidth() - 5,
                        getHeight() - 5,
                        12,
                        12
                );

                // Card
                g2.setColor(BG_CARD);
                g2.fillRoundRect(
                        0,
                        0,
                        getWidth() - 6,
                        getHeight() - 6,
                        12,
                        12
                );

                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(
                        0,
                        0,
                        getWidth() - 6,
                        getHeight() - 6,
                        12,
                        12
                );

                g2.dispose();
            }
        };

        p.setOpaque(false);

        return p;
    }

    // ─── Factory: Styled Table ───────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setBackground(BG_DARK);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(46);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(31, 111, 235, 80));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.setGridColor(BORDER_COLOR);
        table.setShowHorizontalLines(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT_SECONDARY);
        header.setFont(FONT_SUBHEAD);
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(
                new Dimension(header.getWidth(), 50)
        );

        // Alternating rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setBackground(isSelected ? new Color(31, 111, 235, 80)
                    : (row % 2 == 0 ? BG_DARK : new Color(22, 27, 34)));
                setForeground(TEXT_PRIMARY);
                setFont(FONT_BODY);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        });
    }

    // ─── Factory: Styled ScrollPane ──────────────────────────────────────────
    public static JScrollPane scrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBackground(BG_DARK);
        sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        sp.getVerticalScrollBar().setBackground(BG_CARD);
        sp.getHorizontalScrollBar().setBackground(BG_CARD);
        // Thin scrollbar
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 6));
        return sp;
    }

    // ─── Search Field ────────────────────────────────────────────────────────
    public static JTextField searchField(String placeholder) {
        JTextField tf = createTextField();
        tf.setText(placeholder);
        tf.setForeground(TEXT_MUTED);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(TEXT_MUTED);
                }
            }
        });
        return tf;
    }

    // ─── Status Badge ────────────────────────────────────────────────────────
    public static JLabel statusBadge(String status) {
        JLabel badge = new JLabel(status, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getBadgeColor(status);
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(bg);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setForeground(getBadgeColor(status));
        badge.setFont(FONT_SMALL);
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        badge.setOpaque(false);
        return badge;
    }

    private static Color getBadgeColor(String status) {
        if (status == null) return TEXT_MUTED;
        return switch (status.toUpperCase()) {
            case "AVAILABLE", "ACTIVE" -> SUCCESS;
            case "OCCUPIED", "INACTIVE" -> DANGER;
            case "RESERVED", "PENDING" -> WARNING;
            default -> TEXT_SECONDARY;
        };
    }

    // ─── Section Header ──────────────────────────────────────────────────────
    public static JPanel sectionHeader(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel l = new JLabel(title);
        l.setFont(FONT_HEADING);
        l.setForeground(TEXT_PRIMARY);
        JLabel line = new JLabel();
        line.setOpaque(true);
        line.setBackground(BORDER_COLOR);
        line.setPreferredSize(new Dimension(0, 1));
        p.add(l, BorderLayout.NORTH);
        p.add(line, BorderLayout.SOUTH);
        return p;
    }
    public static class StatusRenderer
            implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            return statusBadge(
                    value == null
                            ? ""
                            : value.toString()
            );
        }
    }
}
