package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.model.StudentId;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.ResidentService;
import org.example.hostelsystem.service.StudentIdService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StudentIdStudentPanel extends JPanel {

    private final StudentIdService studentIdService = new StudentIdService();
    private final ResidentService residentService = new ResidentService();

    private JLabel idNumberLabel;
    private JLabel issueDateLabel;
    private JLabel expiryDateLabel;
    private JLabel statusLabel;

    public StudentIdStudentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initComponents();
        loadStudentId();
    }

    private void initComponents() {
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createTitledBorder("My Student ID Card"));
        cardPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        idNumberLabel = new JLabel("—");
        idNumberLabel.setFont(new Font("Arial", Font.BOLD, 14));
        issueDateLabel = new JLabel("—");
        expiryDateLabel = new JLabel("—");
        statusLabel = new JLabel("—");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.gridx = 0; gbc.gridy = 0;
        cardPanel.add(new JLabel("Student ID Number:"), gbc);
        gbc.gridx = 1;
        cardPanel.add(idNumberLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        cardPanel.add(new JLabel("Issue Date:"), gbc);
        gbc.gridx = 1;
        cardPanel.add(issueDateLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        cardPanel.add(new JLabel("Expiry Date:"), gbc);
        gbc.gridx = 1;
        cardPanel.add(expiryDateLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        cardPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        cardPanel.add(statusLabel, gbc);

        add(cardPanel, BorderLayout.CENTER);

        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadStudentId());
        refreshPanel.add(refreshBtn);
        add(refreshPanel, BorderLayout.SOUTH);
    }

    private void loadStudentId() {
        Integer residentId = getStudentResidentId();
        if (residentId == null) {
            idNumberLabel.setText("Profile not linked. Contact admin.");
            idNumberLabel.setForeground(Color.RED);
            issueDateLabel.setText("—");
            expiryDateLabel.setText("—");
            statusLabel.setText("—");
            return;
        }

        try {
            StudentId sid = studentIdService.getStudentIdByResident(residentId);
            if (sid == null) {
                idNumberLabel.setText("No student ID assigned yet.");
                idNumberLabel.setForeground(Color.GRAY);
                issueDateLabel.setText("—");
                expiryDateLabel.setText("—");
                statusLabel.setText("—");
            } else {
                idNumberLabel.setText(sid.getStudentIdNumber());
                idNumberLabel.setForeground(new Color(0, 100, 0));
                issueDateLabel.setText(sid.getIssueDate() != null ? sid.getIssueDate().toString() : "—");
                expiryDateLabel.setText(sid.getExpiryDate() != null ? sid.getExpiryDate().toString() : "—");
                statusLabel.setText(sid.getStatus());
                switch (sid.getStatus()) {
                    case "ISSUED" -> statusLabel.setForeground(new Color(60, 179, 113));
                    case "LOST" -> statusLabel.setForeground(Color.RED);
                    case "RENEWED" -> statusLabel.setForeground(new Color(70, 130, 180));
                    case "EXPIRED" -> statusLabel.setForeground(Color.ORANGE);
                    default -> statusLabel.setForeground(Color.BLACK);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading student ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
