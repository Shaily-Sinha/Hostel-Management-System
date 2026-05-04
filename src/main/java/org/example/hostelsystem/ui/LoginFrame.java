package org.example.hostelsystem.ui;

import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.ui.util.ModernTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Hostel Management System");
        setSize(460, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 460, 540, 20, 20));
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                    setShape(null);
                } else {
                    setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                }
            }
        });

        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ModernTheme.BG_DARK, 0, getHeight(), new Color(8, 12, 18));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(88, 166, 255, 15));
                g2.fillOval(-80, -80, 320, 220);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1));
        setContentPane(root);

        enableWindowDragging(root);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(new EmptyBorder(12, 20, 0, 12));

        JLabel appLabel = new JLabel("HMS");
        appLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        appLabel.setForeground(ModernTheme.ACCENT);

        JButton minBtn = winBtn("MIN", "Minimize");
        JButton maxBtn = winBtn("MAX", "Full screen");
        JButton closeBtn = winBtn("CLOSE", "Close");

        minBtn.addActionListener(e -> setState(JFrame.ICONIFIED));
        maxBtn.addActionListener(e -> {
            if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            } else {
                setShape(null);
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
        closeBtn.addActionListener(e -> System.exit(0));

        JPanel windowButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        windowButtons.setOpaque(false);
        windowButtons.add(minBtn);
        windowButtons.add(maxBtn);
        windowButtons.add(closeBtn);

        titleBar.add(appLabel, BorderLayout.WEST);
        titleBar.add(windowButtons, BorderLayout.EAST);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        centerWrap.setBorder(new EmptyBorder(0, 36, 0, 36));

        JPanel card = ModernTheme.card();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(36, 32, 36, 32));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.weightx = 1.0;

        JLabel icon = new JLabel("HMS", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 30));
        icon.setForeground(ModernTheme.ACCENT);
        icon.setBorder(new EmptyBorder(0, 0, 16, 0));
        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 0, 0);
        card.add(icon, gc);

        JLabel titleLbl = new JLabel("Welcome back", SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 21));
        titleLbl.setForeground(ModernTheme.TEXT_PRIMARY);
        gc.gridy = 1;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(titleLbl, gc);

        JLabel subLbl = new JLabel("Sign in to Hostel Management System", SwingConstants.CENTER);
        subLbl.setFont(ModernTheme.FONT_LABEL);
        subLbl.setForeground(ModernTheme.TEXT_SECONDARY);
        gc.gridy = 2;
        gc.insets = new Insets(0, 0, 28, 0);
        card.add(subLbl, gc);

        gc.gridy = 3;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(ModernTheme.label("Username"), gc);
        usernameField = ModernTheme.textField();
        usernameField.setPreferredSize(new Dimension(0, 38));
        gc.gridy = 4;
        gc.insets = new Insets(0, 0, 14, 0);
        card.add(usernameField, gc);

        gc.gridy = 5;
        gc.insets = new Insets(0, 0, 6, 0);
        card.add(ModernTheme.label("Password"), gc);
        passwordField = ModernTheme.passwordField();
        passwordField.setPreferredSize(new Dimension(0, 38));
        gc.gridy = 6;
        gc.insets = new Insets(0, 0, 24, 0);
        card.add(passwordField, gc);

        JButton loginButton = new JButton("Sign In") {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                });
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = hover ? new Color(50, 130, 255) : ModernTheme.ACCENT_DARK;
                Color c2 = hover ? ModernTheme.ACCENT_DARK : new Color(20, 80, 180);
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(0, 42));
        loginButton.addActionListener(this::performLogin);
        gc.gridy = 7;
        gc.insets = new Insets(0, 0, 16, 0);
        card.add(loginButton, gc);

        JLabel hint = new JLabel("Default: admin / admin123", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(ModernTheme.TEXT_MUTED);
        gc.gridy = 8;
        gc.insets = new Insets(0, 0, 0, 0);
        card.add(hint, gc);

        centerWrap.add(card, new GridBagConstraints());

        JLabel footer = new JLabel("Copyright 2026 Hostel Management System", SwingConstants.CENTER);
        footer.setFont(ModernTheme.FONT_SMALL);
        footer.setForeground(ModernTheme.TEXT_MUTED);
        footer.setBorder(new EmptyBorder(0, 0, 16, 0));

        root.add(titleBar, BorderLayout.NORTH);
        root.add(centerWrap, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(loginButton);
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) loginButton.doClick();
            }
        });
    }

    private void enableWindowDragging(JPanel root) {
        final Point[] drag = {null};
        root.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { drag[0] = e.getPoint(); }
            public void mouseReleased(MouseEvent e) { drag[0] = null; }
        });
        root.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (drag[0] != null && getExtendedState() == JFrame.NORMAL) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - drag[0].x, loc.y + e.getY() - drag[0].y);
                }
            }
        });
    }

    private JButton winBtn(String type, String tooltip) {
        JButton b = new JButton(new WindowIcon(type));
        b.setBackground(new Color(22, 27, 34));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setToolTipText(tooltip);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(36, 30));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBackground("CLOSE".equals(type) ? ModernTheme.DANGER : ModernTheme.BG_HOVER);
            }

            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(22, 27, 34));
            }
        });
        return b;
    }

    private void performLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authService.login(username, password)) {
            dispose();
            new MainDashboard().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private static class WindowIcon implements Icon {
        private final String type;

        WindowIcon(String type) {
            this.type = type;
        }

        @Override public int getIconWidth() { return 17; }
        @Override public int getIconHeight() { return 17; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(Color.WHITE);

            if ("MIN".equals(type)) {
                g2.drawLine(x + 3, y + 12, x + 14, y + 12);
            } else if ("MAX".equals(type)) {
                g2.drawRect(x + 4, y + 4, 9, 9);
            } else {
                g2.drawLine(x + 4, y + 4, x + 13, y + 13);
                g2.drawLine(x + 13, y + 4, x + 4, y + 13);
            }

            g2.dispose();
        }
    }
}
