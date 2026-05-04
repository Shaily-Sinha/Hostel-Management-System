package org.example.hostelsystem.ui;

import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.ui.util.ModernTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MainDashboard extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private final Map<String, Supplier<JPanel>> panelFactories = new LinkedHashMap<>();

    public MainDashboard() {
        setTitle("Hostel Management System");
        setSize(1280, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 680));
        setUndecorated(true);
        initComponents();
    }

    private void initComponents() {
        registerPanels();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(8, 15, 28));
        setContentPane(root);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);

        contentPanel.setBackground(new Color(8, 15, 28));
        contentPanel.add(buildHomeDashboard(), "Dashboard");
        for (String key : panelFactories.keySet()) {
            contentPanel.add(panelFactories.get(key).get(), key);
        }
        root.add(contentPanel, BorderLayout.CENTER);

        showPage("Dashboard");
    }

    private void registerPanels() {
        if (AuthService.isStudent()) {
            panelFactories.put("My Profile", StudentProfilePanel::new);
            panelFactories.put("Attendance", AttendancePanel::new);
            panelFactories.put("Mess & Food", MessStudentPanel::new);
            panelFactories.put("Leave", LeaveStudentPanel::new);
            panelFactories.put("Late Arrival", LateArrivalStudentPanel::new);
            panelFactories.put("Student ID", StudentIdStudentPanel::new);
            panelFactories.put("Notifications", NotificationStudentPanel::new);
            return;
        }

        panelFactories.put("Rooms", RoomManagementPanel::new);
        panelFactories.put("Residents", ResidentManagementPanel::new);
        panelFactories.put("Bookings", BookingManagementPanel::new);
        panelFactories.put("Attendance", AttendancePanel::new);
        panelFactories.put("Mess & Food", MessManagementPanel::new);
        panelFactories.put("Leave", LeaveManagementPanel::new);
        panelFactories.put("Late Arrival", LateArrivalManagementPanel::new);
        panelFactories.put("Student ID", StudentIdManagementPanel::new);
        panelFactories.put("Notifications", NotificationManagementPanel::new);
        if (AuthService.isAdmin()) {
            panelFactories.put("Users", UserManagementPanel::new);
        }
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(24, 39, 60));
        topBar.setPreferredSize(new Dimension(0, 58));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(42, 58, 82)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        left.setOpaque(false);

        JLabel logo = new JLabel("HMS");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logo.setForeground(Color.WHITE);
        logo.setOpaque(true);
        logo.setBackground(new Color(79, 70, 229));
        logo.setBorder(new EmptyBorder(6, 9, 6, 9));

        JLabel title = new JLabel("Hostel Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(129, 140, 248));

        left.add(logo);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        right.setOpaque(false);

        String user = AuthService.getCurrentUser() != null ? AuthService.getCurrentUser().getFullName() : "";
        if (user == null || user.isBlank()) user = "User";

        JLabel userLabel = new JLabel(user);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(new Color(203, 213, 225));

        JButton logout = new JButton("Logout");
        logout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logout.setForeground(Color.WHITE);
        logout.setBackground(new Color(239, 68, 68));
        logout.setBorder(new EmptyBorder(8, 18, 8, 18));
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(this::logout);

        JButton minBtn = winBtn("MIN", "Minimize");
        JButton maxBtn = winBtn("MAX", "Full screen");
        JButton closeBtn = winBtn("CLOSE", "Close");

        minBtn.addActionListener(e -> setState(JFrame.ICONIFIED));
        maxBtn.addActionListener(e -> {
            if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
        closeBtn.addActionListener(e -> System.exit(0));

        right.add(userLabel);
        right.add(logout);
        right.add(minBtn);
        right.add(maxBtn);
        right.add(closeBtn);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);
        enableWindowDragging(topBar);
        return topBar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(25, 41, 62));
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(new EmptyBorder(22, 0, 20, 0));

        JLabel navTitle = new JLabel("MAIN NAVIGATION");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navTitle.setForeground(new Color(100, 116, 139));
        navTitle.setBorder(new EmptyBorder(0, 18, 16, 0));
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navTitle);

        addNavButton(sidebar, "Dashboard");
        for (String key : panelFactories.keySet()) {
            addNavButton(sidebar, key);
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private void addNavButton(JPanel sidebar, String page) {
        JButton button = new JButton(page);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(new Color(148, 163, 184));
        button.setBackground(new Color(25, 41, 62));
        button.setBorder(new EmptyBorder(12, 24, 12, 18));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setIcon(new NavIcon(page));
        button.setIconTextGap(12);
        button.addActionListener(e -> showPage(page));
        navButtons.put(page, button);
        sidebar.add(button);
    }

    private JPanel buildHomeDashboard() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(new Color(8, 15, 28));
        page.setBorder(new EmptyBorder(58, 58, 58, 58));

        String name = AuthService.getCurrentUser() != null ? AuthService.getCurrentUser().getFullName() : "";
        if (name == null || name.isBlank()) name = "there";

        JLabel title = new JLabel("Welcome back, " + name + "!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Everything looks good. Here are your quick actions.");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        subtitle.setForeground(new Color(148, 163, 184));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 8));
        header.setOpaque(false);
        header.add(title);
        header.add(subtitle);

        JPanel cards = new JPanel(new GridLayout(0, 3, 28, 28));
        cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(44, 0, 0, 0));

        for (String pageName : panelFactories.keySet()) {
            cards.add(actionCard(pageName, cardSubtitle(pageName)));
        }

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(header, BorderLayout.NORTH);
        content.add(cards, BorderLayout.CENTER);

        page.add(content, BorderLayout.CENTER);
        return page;
    }

    private JPanel actionCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(18, 0));
        card.setBackground(new Color(30, 47, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(54, 71, 100)),
            new EmptyBorder(22, 24, 22, 24)
        ));
        card.setPreferredSize(new Dimension(300, 126));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(new DashboardIcon(title));
        icon.setPreferredSize(new Dimension(58, 58));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setVerticalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subLabel.setForeground(new Color(148, 163, 184));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        text.add(titleLabel);
        text.add(subLabel);

        card.add(icon, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showPage(title); }
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(42, 61, 88));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(99, 102, 241)),
                    new EmptyBorder(22, 24, 22, 24)
                ));
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(30, 47, 70));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(54, 71, 100)),
                    new EmptyBorder(22, 24, 22, 24)
                ));
            }
        });

        return card;
    }

    private String cardSubtitle(String page) {
        return switch (page) {
            case "Rooms" -> "Manage hostel rooms";
            case "Residents" -> "Student records";
            case "Bookings" -> "Room bookings";
            case "Attendance" -> "Daily attendance";
            case "Mess & Food" -> "Food and bills";
            case "Leave" -> "Leave approvals";
            case "Late Arrival" -> "Late requests";
            case "Student ID" -> "Identity cards";
            case "Notifications" -> "Messages";
            case "Users" -> "Permissions";
            case "My Profile" -> "Your details";
            default -> "Open section";
        };
    }

    private void showPage(String page) {
        cardLayout.show(contentPanel, page);
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean selected = entry.getKey().equals(page);
            JButton btn = entry.getValue();
            btn.setForeground(selected ? new Color(129, 140, 248) : new Color(148, 163, 184));
            btn.setBackground(selected ? new Color(30, 47, 70) : new Color(25, 41, 62));
        }
    }

    private JButton winBtn(String type, String tooltip) {
        JButton b = new JButton(new WindowIcon(type));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(24, 39, 60));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setToolTipText(tooltip);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(42, 34));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBackground("CLOSE".equals(type) ? new Color(220, 38, 38) : new Color(51, 65, 85));
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(new Color(24, 39, 60));
            }
        });
        return b;
    }

    private static class WindowIcon implements Icon {
        private final String type;

        WindowIcon(String type) {
            this.type = type;
        }

        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(Color.WHITE);

            if ("MIN".equals(type)) {
                g2.drawLine(x + 3, y + 12, x + 15, y + 12);
            } else if ("MAX".equals(type)) {
                g2.drawRect(x + 4, y + 4, 10, 10);
            } else {
                g2.drawLine(x + 4, y + 4, x + 14, y + 14);
                g2.drawLine(x + 14, y + 4, x + 4, y + 14);
            }

            g2.dispose();
        }
    }

    private static class NavIcon implements Icon {
        private final String page;

        NavIcon(String page) {
            this.page = page;
        }

        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(129, 140, 248));
            drawSymbol(g2, page, x, y, 18);
            g2.dispose();
        }
    }

    private static class DashboardIcon implements Icon {
        private final String page;

        DashboardIcon(String page) {
            this.page = page;
        }

        @Override public int getIconWidth() { return 54; }
        @Override public int getIconHeight() { return 54; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawColorIcon(g2, page, x, y);
            g2.dispose();
        }
    }

    private static void drawColorIcon(Graphics2D g2, String page, int x, int y) {
        String key = page.toLowerCase();

        if (key.contains("room")) {
            drawBoxIcon(g2, x, y);
        } else if (key.contains("resident") || key.contains("user") || key.contains("profile")) {
            drawPeopleIcon(g2, x, y);
        } else if (key.contains("booking")) {
            drawCartIcon(g2, x, y);
        } else if (key.contains("attendance") || key.contains("leave") || key.contains("late")) {
            drawCalendarIcon(g2, x, y);
        } else if (key.contains("mess")) {
            drawFoodMessIcon(g2, x, y);
        } else if (key.contains("student id")) {
            drawIdCardIcon(g2, x, y);
        } else if (key.contains("notification")) {
            drawDocumentIcon(g2, x, y);
        } else {
            drawChartIcon(g2, x, y);
        }
    }

    private static void drawBoxIcon(Graphics2D g2, int x, int y) {
        Polygon top = new Polygon(
            new int[]{x + 16, x + 31, x + 45, x + 29},
            new int[]{y + 12, y + 6, y + 15, y + 23},
            4
        );
        Polygon left = new Polygon(
            new int[]{x + 16, x + 29, x + 29, x + 16},
            new int[]{y + 12, y + 23, y + 42, y + 30},
            4
        );
        Polygon right = new Polygon(
            new int[]{x + 29, x + 45, x + 45, x + 29},
            new int[]{y + 23, y + 15, y + 34, y + 42},
            4
        );
        g2.setColor(new Color(245, 158, 66));
        g2.fillPolygon(top);
        g2.setColor(new Color(194, 120, 48));
        g2.fillPolygon(left);
        g2.setColor(new Color(226, 145, 65));
        g2.fillPolygon(right);
        g2.setColor(new Color(255, 206, 128));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(x + 24, y + 9, x + 38, y + 18);
    }

    private static void drawBuildingIcon(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(241, 245, 249));
        g2.fillRect(x + 12, y + 18, 32, 28);
        g2.setColor(new Color(239, 68, 68));
        for (int i = 0; i < 3; i++) {
            int sx = x + 15 + i * 10;
            g2.fillRect(sx, y + 10, 3, 8);
            g2.setColor(new Color(250, 204, 21));
            g2.fillOval(sx - 1, y + 7, 5, 5);
            g2.setColor(new Color(239, 68, 68));
        }
        g2.setColor(new Color(96, 165, 250));
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 3; c++) {
                g2.fillRect(x + 17 + c * 8, y + 23 + r * 8, 4, 4);
            }
        }
        g2.setColor(new Color(71, 85, 105));
        g2.drawRect(x + 12, y + 18, 32, 28);
    }

    private static void drawFoodMessIcon(Graphics2D g2, int x, int y) {
        g2.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Tray
        g2.setColor(new Color(236, 254, 255));
        g2.fillRoundRect(x + 8, y + 33, 40, 13, 6, 6);
        g2.setColor(new Color(15, 23, 42));
        g2.drawRoundRect(x + 8, y + 33, 40, 13, 6, 6);

        // Food pieces
        g2.setColor(new Color(34, 197, 94));
        g2.fillOval(x + 10, y + 25, 14, 13);
        g2.setColor(new Color(249, 115, 22));
        g2.fillArc(x + 18, y + 24, 15, 16, 0, 180);
        g2.setColor(new Color(239, 68, 68));
        g2.fillOval(x + 25, y + 23, 15, 15);
        g2.setColor(new Color(253, 224, 71));
        g2.fillArc(x + 35, y + 26, 12, 13, 0, 180);

        // Leaves
        g2.setColor(new Color(22, 163, 74));
        g2.drawLine(x + 15, y + 31, x + 8, y + 21);
        g2.drawLine(x + 15, y + 31, x + 22, y + 20);
        g2.drawLine(x + 15, y + 31, x + 6, y + 31);
        g2.drawLine(x + 15, y + 31, x + 24, y + 32);

        // Spoon handle
        g2.setColor(new Color(125, 211, 252));
        g2.setStroke(new BasicStroke(4.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 8, y + 8, x + 25, y + 25);
        g2.setColor(new Color(15, 23, 42));
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 8, y + 8, x + 25, y + 25);

        // Drink carton
        Polygon carton = new Polygon(
            new int[]{x + 36, x + 50, x + 44, x + 30},
            new int[]{y + 13, y + 23, y + 34, y + 24},
            4
        );
        g2.setColor(new Color(187, 247, 208));
        g2.fillPolygon(carton);
        g2.setColor(new Color(15, 23, 42));
        g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawPolygon(carton);
        g2.setColor(new Color(134, 239, 172));
        g2.drawLine(x + 35, y + 23, x + 47, y + 18);

        // Straw
        g2.setColor(new Color(125, 211, 252));
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 34, y + 21, x + 47, y + 8);
        g2.setColor(new Color(15, 23, 42));
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 34, y + 21, x + 47, y + 8);
    }

    private static void drawCartIcon(Graphics2D g2, int x, int y) {
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(148, 163, 184));
        g2.drawLine(x + 10, y + 15, x + 17, y + 15);
        g2.drawLine(x + 17, y + 15, x + 22, y + 35);
        g2.drawLine(x + 22, y + 35, x + 42, y + 35);
        g2.drawLine(x + 20, y + 21, x + 45, y + 18);
        g2.drawLine(x + 22, y + 28, x + 42, y + 26);
        g2.setColor(new Color(96, 165, 250));
        g2.fillOval(x + 22, y + 39, 7, 7);
        g2.fillOval(x + 38, y + 39, 7, 7);
    }

    private static void drawChartIcon(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(241, 245, 249));
        g2.fillRect(x + 14, y + 10, 30, 36);
        g2.setColor(new Color(59, 130, 246));
        g2.fillRect(x + 18, y + 28, 6, 14);
        g2.setColor(new Color(34, 197, 94));
        g2.fillRect(x + 27, y + 20, 6, 22);
        g2.setColor(new Color(239, 68, 68));
        g2.fillRect(x + 36, y + 14, 6, 28);
        g2.setColor(new Color(71, 85, 105));
        g2.drawRect(x + 14, y + 10, 30, 36);
    }

    private static void drawDocumentIcon(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(241, 245, 249));
        g2.fillRect(x + 14, y + 9, 28, 36);
        Polygon fold = new Polygon(
            new int[]{x + 34, x + 42, x + 34},
            new int[]{y + 9, y + 17, y + 17},
            3
        );
        g2.setColor(new Color(203, 213, 225));
        g2.fillPolygon(fold);
        g2.setColor(new Color(96, 165, 250));
        g2.fillRect(x + 19, y + 22, 16, 4);
        g2.fillRect(x + 19, y + 30, 18, 4);
        g2.setColor(new Color(148, 163, 184));
        g2.drawRect(x + 14, y + 9, 28, 36);
    }

    private static void drawPeopleIcon(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(56, 189, 248));
        g2.fillOval(x + 20, y + 8, 17, 17);
        g2.fillRoundRect(x + 14, y + 27, 30, 20, 12, 12);
        g2.setColor(new Color(125, 211, 252, 180));
        g2.fillOval(x + 10, y + 18, 11, 11);
        g2.fillRoundRect(x + 6, y + 32, 18, 13, 9, 9);
        g2.fillOval(x + 38, y + 18, 11, 11);
        g2.fillRoundRect(x + 34, y + 32, 18, 13, 9, 9);
    }

    private static void drawCalendarIcon(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(241, 245, 249));
        g2.fillRoundRect(x + 10, y + 12, 36, 32, 5, 5);
        g2.setColor(new Color(239, 68, 68));
        g2.fillRoundRect(x + 10, y + 12, 36, 9, 5, 5);
        g2.setColor(new Color(59, 130, 246));
        g2.fillOval(x + 17, y + 27, 6, 6);
        g2.setColor(new Color(34, 197, 94));
        g2.fillOval(x + 27, y + 27, 6, 6);
        g2.setColor(new Color(250, 204, 21));
        g2.fillOval(x + 37, y + 27, 6, 6);
        g2.setColor(new Color(71, 85, 105));
        g2.drawRoundRect(x + 10, y + 12, 36, 32, 5, 5);
    }

    private static void drawIdCardIcon(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(241, 245, 249));
        g2.fillRoundRect(x + 8, y + 14, 40, 28, 5, 5);
        g2.setColor(new Color(56, 189, 248));
        g2.fillOval(x + 14, y + 21, 10, 10);
        g2.fillRoundRect(x + 12, y + 32, 14, 6, 5, 5);
        g2.setColor(new Color(99, 102, 241));
        g2.fillRect(x + 30, y + 23, 13, 3);
        g2.fillRect(x + 30, y + 31, 10, 3);
        g2.setColor(new Color(71, 85, 105));
        g2.drawRoundRect(x + 8, y + 14, 40, 28, 5, 5);
    }

    private static void drawSymbol(Graphics2D g2, String page, int x, int y, int s) {
        String key = page.toLowerCase();
        if (key.contains("room") || key.contains("profile")) {
            g2.drawRoundRect(x, y + s / 3, s, s / 2, 4, 4);
            g2.drawLine(x + 3, y + s / 3, x + 3, y + s / 5);
            g2.drawLine(x, y + s - 2, x, y + s);
            g2.drawLine(x + s, y + s - 2, x + s, y + s);
        } else if (key.contains("resident") || key.contains("user")) {
            g2.drawOval(x + s / 8, y + 1, s / 4, s / 4);
            g2.drawOval(x + s / 2, y + 1, s / 4, s / 4);
            g2.drawArc(x, y + s / 3, s / 2, s / 2, 0, 180);
            g2.drawArc(x + s / 3, y + s / 3, s / 2, s / 2, 0, 180);
        } else if (key.contains("booking") || key.contains("attendance") || key.contains("leave")) {
            g2.drawRoundRect(x + 2, y + 2, s - 4, s - 4, 4, 4);
            g2.drawLine(x + 2, y + s / 3, x + s - 2, y + s / 3);
            g2.drawLine(x + s / 4, y + s / 2, x + s / 2, y + s - 7);
            g2.drawLine(x + s / 2, y + s - 7, x + s - 6, y + s / 2);
        } else if (key.contains("mess")) {
            g2.drawLine(x + s / 3, y + 2, x + s / 3, y + s - 2);
            g2.drawLine(x + s / 3 - 5, y + 2, x + s / 3 - 5, y + s / 3);
            g2.drawLine(x + s / 3 + 5, y + 2, x + s / 3 + 5, y + s / 3);
            g2.drawArc(x + s / 2, y + 2, s / 3, s - 4, 90, 180);
        } else if (key.contains("late")) {
            g2.drawOval(x + 2, y + 2, s - 4, s - 4);
            g2.drawLine(x + s / 2, y + s / 2, x + s / 2, y + 7);
            g2.drawLine(x + s / 2, y + s / 2, x + s - 8, y + s / 2);
        } else if (key.contains("student id")) {
            g2.drawRoundRect(x + 1, y + 5, s - 2, s - 10, 4, 4);
            g2.drawOval(x + 6, y + 10, s / 4, s / 4);
            g2.drawLine(x + s / 2, y + 12, x + s - 6, y + 12);
            g2.drawLine(x + s / 2, y + 20, x + s - 6, y + 20);
        } else if (key.contains("notification")) {
            g2.drawArc(x + 5, y + 5, s - 10, s - 7, 25, 130);
            g2.drawLine(x + 8, y + s - 8, x + s - 8, y + s - 8);
            g2.drawOval(x + s / 2 - 2, y + s - 6, 4, 4);
        } else {
            g2.drawRoundRect(x + 2, y + 2, s - 4, s - 4, 6, 6);
            g2.drawLine(x + 8, y + s / 2, x + s - 8, y + s / 2);
        }
    }

    private void enableWindowDragging(JPanel titleBar) {
        final Point[] drag = {null};
        titleBar.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { drag[0] = e.getPoint(); }
            @Override public void mouseReleased(MouseEvent e) { drag[0] = null; }
        });
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (drag[0] != null && getExtendedState() == JFrame.NORMAL) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - drag[0].x, loc.y + e.getY() - drag[0].y);
                }
            }
        });
    }

    private void logout(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new AuthService().logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
