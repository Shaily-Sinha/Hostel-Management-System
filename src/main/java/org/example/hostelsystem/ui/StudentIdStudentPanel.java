package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.model.StudentId;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.ResidentService;
import org.example.hostelsystem.service.StudentIdService;
import org.example.hostelsystem.ui.util.ModernTheme;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StudentIdStudentPanel extends JPanel {

    private final StudentIdService studentIdService = new StudentIdService();
    private final ResidentService residentService = new ResidentService();

    private JPanel idCardPanel;
    private JLabel nameValueLabel;
    private JLabel idNumberValueLabel;
    private JLabel roomValueLabel;
    private JLabel emailValueLabel;
    private JLabel phoneValueLabel;
    private JLabel courseValueLabel;
    private JLabel issueDateValueLabel;
    private JLabel expiryDateValueLabel;
    private JLabel statusValueLabel;
    private JPanel avatarPanel;

    public StudentIdStudentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadStudentId();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // Create card panel with border and padding
        JPanel cardWrapper = new JPanel(new BorderLayout(10, 10));
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        idCardPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ModernTheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(ModernTheme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        idCardPanel.setOpaque(false);
        idCardPanel.setPreferredSize(new Dimension(500, 400));
        idCardPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Header bar
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(ModernTheme.ACCENT_DARK);
        headerPanel.setBorder(BorderFactory.createMatteBorder(12, 12, 0, 12, ModernTheme.ACCENT_DARK));
        
        JLabel titleLabel = new JLabel("  STUDENT ID CARD", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        idCardPanel.add(headerPanel, BorderLayout.NORTH);

        // Content area
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left side: Avatar
        avatarPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ModernTheme.BG_HOVER);
                g2.fillOval(0, 0, 100, 100);
                g2.setColor(ModernTheme.ACCENT);
                g2.drawOval(0, 0, 100, 100);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(100, 100));
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel avatarLabel = new JLabel("S", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        avatarLabel.setForeground(ModernTheme.ACCENT);
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);

        // Right side: Details
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 8, 3, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        nameValueLabel = createValueLabel("—");
        addDetailRow(detailsPanel, gbc, "Name:", nameValueLabel);
        
        idNumberValueLabel = createValueLabel("—");
        idNumberValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idNumberValueLabel.setForeground(ModernTheme.ACCENT);
        addDetailRow(detailsPanel, gbc, "ID Number:", idNumberValueLabel);
        
        roomValueLabel = createValueLabel("—");
        addDetailRow(detailsPanel, gbc, "Room:", roomValueLabel);
        
        emailValueLabel = createValueLabel("—");
        addDetailRow(detailsPanel, gbc, "Email:", emailValueLabel);
        
        phoneValueLabel = createValueLabel("—");
        addDetailRow(detailsPanel, gbc, "Phone:", phoneValueLabel);
        
        courseValueLabel = createValueLabel("—");
        addDetailRow(detailsPanel, gbc, "Course:", courseValueLabel);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(detailsPanel, BorderLayout.CENTER);

        contentPanel.add(avatarPanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);
        idCardPanel.add(contentPanel, BorderLayout.CENTER);

        // Footer: Status, dates, QR code
        JPanel footerPanel = new JPanel(new BorderLayout(10, 0));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        JPanel datesPanel = new JPanel(new GridBagLayout());
        datesPanel.setOpaque(false);
        GridBagConstraints dgbc = new GridBagConstraints();
        dgbc.insets = new Insets(3, 8, 3, 8);
        dgbc.anchor = GridBagConstraints.WEST;
        dgbc.gridx = 0; dgbc.gridy = 0;

        issueDateValueLabel = createValueLabel("—");
        addDetailRow(datesPanel, dgbc, "Issued:", issueDateValueLabel);
        
        expiryDateValueLabel = createValueLabel("—");
        addDetailRow(datesPanel, dgbc, "Expires:", expiryDateValueLabel);
        
        statusValueLabel = createValueLabel("—");
        statusValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addDetailRow(datesPanel, dgbc, "Status:", statusValueLabel);

        // QR Code placeholder
        JPanel qrPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ModernTheme.BG_INPUT);
                g2.fillRoundRect(0, 0, 80, 80, 6, 6);
                g2.setColor(ModernTheme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, 80, 80, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        qrPanel.setOpaque(false);
        qrPanel.setPreferredSize(new Dimension(80, 80));
        qrPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        JLabel qrLabel = new JLabel("QR", SwingConstants.CENTER);
        qrLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        qrLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        qrPanel.add(qrLabel, BorderLayout.CENTER);

        footerPanel.add(datesPanel, BorderLayout.CENTER);
        footerPanel.add(qrPanel, BorderLayout.EAST);
        idCardPanel.add(footerPanel, BorderLayout.SOUTH);

        cardWrapper.add(idCardPanel, BorderLayout.CENTER);
        add(cardWrapper, BorderLayout.CENTER);

        // Refresh button
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        refreshPanel.setOpaque(false);
        JButton refreshBtn = ModernTheme.secondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadStudentId());
        refreshPanel.add(refreshBtn);
        add(refreshPanel, BorderLayout.SOUTH);
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ModernTheme.FONT_BODY);
        label.setForeground(ModernTheme.TEXT_PRIMARY);
        return label;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, JLabel valueLabel) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(ModernTheme.FONT_SMALL);
        lbl.setForeground(ModernTheme.TEXT_SECONDARY);
        gbc.gridx = 0;
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        panel.add(valueLabel, gbc);
        gbc.gridy++;
    }

    private void loadStudentId() {
        Integer residentId = getStudentResidentId();
        if (residentId == null) {
            resetToError("Profile not linked. Contact admin.");
            return;
        }

        try {
            Resident resident = residentService.getResidentById(residentId);
            if (resident == null) {
                resetToError("Resident not found.");
                return;
            }

            // Update avatar with initials
            String initials = "S";
            String fullName = resident.getFullName();
            if (fullName != null && !fullName.isEmpty()) {
                initials = fullName.substring(0, 1).toUpperCase();
            }
            Component[] avatarComps = avatarPanel.getComponents();
            if (avatarComps.length > 0 && avatarComps[0] instanceof JLabel) {
                ((JLabel) avatarComps[0]).setText(initials);
            }

            // Set student details
            nameValueLabel.setText(resident.getFullName() != null ? resident.getFullName() : "—");
            roomValueLabel.setText(resident.getRoomId() != null ? "Room " + resident.getRoomId() : "—");
            emailValueLabel.setText(resident.getEmail() != null ? resident.getEmail() : "—");
            phoneValueLabel.setText(resident.getPhone() != null ? resident.getPhone() : "—");
            courseValueLabel.setText(resident.getAddress() != null ? resident.getAddress() : "—");

            StudentId sid = studentIdService.getStudentIdByResident(residentId);
            if (sid == null) {
                idNumberValueLabel.setText("No ID assigned");
                idNumberValueLabel.setForeground(ModernTheme.TEXT_SECONDARY);
                issueDateValueLabel.setText("—");
                expiryDateValueLabel.setText("—");
                statusValueLabel.setText("—");
            } else {
                idNumberValueLabel.setText(sid.getStudentIdNumber());
                idNumberValueLabel.setForeground(ModernTheme.ACCENT);
                issueDateValueLabel.setText(sid.getIssueDate() != null ? sid.getIssueDate().toString() : "—");
                expiryDateValueLabel.setText(sid.getExpiryDate() != null ? sid.getExpiryDate().toString() : "—");
                statusValueLabel.setText(sid.getStatus());
                switch (sid.getStatus()) {
                    case "ISSUED" -> statusValueLabel.setForeground(ModernTheme.SUCCESS);
                    case "LOST" -> statusValueLabel.setForeground(ModernTheme.DANGER);
                    case "RENEWED" -> statusValueLabel.setForeground(ModernTheme.ACCENT);
                    case "EXPIRED" -> statusValueLabel.setForeground(ModernTheme.WARNING);
                    default -> statusValueLabel.setForeground(ModernTheme.TEXT_PRIMARY);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading student ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetToError(String message) {
        nameValueLabel.setText(message);
        nameValueLabel.setForeground(ModernTheme.DANGER);
        idNumberValueLabel.setText("—");
        roomValueLabel.setText("—");
        emailValueLabel.setText("—");
        phoneValueLabel.setText("—");
        courseValueLabel.setText("—");
        issueDateValueLabel.setText("—");
        expiryDateValueLabel.setText("—");
        statusValueLabel.setText("—");
    }

    private Integer getStudentResidentId() {
        String email = AuthService.getCurrentUser().getEmail();
        try {
            List<Resident> residents = residentService.getAllResidents();
            for (Resident r : residents) {
                if (r.getEmail() != null && r.getEmail().equalsIgnoreCase(email)) {
                    return r.getId();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
