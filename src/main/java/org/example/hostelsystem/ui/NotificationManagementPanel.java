package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.model.Notification;
import org.example.hostelsystem.model.User;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.NotificationService;
import org.example.hostelsystem.service.ResidentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class NotificationManagementPanel extends JPanel {

    private final NotificationService notificationService = new NotificationService();
    private final ResidentService residentService = new ResidentService();

    private JTable notificationTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeCombo;
    private JComboBox<ResidentComboItem> recipientCombo;
    private JTextField titleField;
    private JTextArea messageArea;

    public NotificationManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadNotifications();
    }

    private void initComponents() {
        // Send Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Send Notification"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        typeCombo = new JComboBox<>(new String[]{"BROADCAST", "PERSONAL"});
        typeCombo.addActionListener(e -> toggleRecipient());
        recipientCombo = new JComboBox<>();
        loadResidents();
        recipientCombo.setEnabled(false);
        titleField = new JTextField(20);
        messageArea = new JTextArea(4, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Recipient:"), gbc);
        gbc.gridx = 3;
        formPanel.add(recipientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Message:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(new JScrollPane(messageArea), gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton sendBtn = new JButton("Send Notification");
        sendBtn.setBackground(new Color(60, 179, 113));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setOpaque(true);
        sendBtn.setBorderPainted(false);
        sendBtn.addActionListener(this::sendNotification);
        btnPanel.add(sendBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Sent Notifications Table
        String[] cols = {"ID", "Type", "Recipient", "Title", "Message", "Status", "Sent On"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        notificationTable = new JTable(tableModel);
        notificationTable.setRowHeight(25);
        notificationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        notificationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(notificationTable), BorderLayout.CENTER);

        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshBtn = new JButton("Refresh");
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(220, 20, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setOpaque(true);
        deleteBtn.setBorderPainted(false);
        refreshBtn.addActionListener(e -> loadNotifications());
        deleteBtn.addActionListener(this::deleteNotification);
        refreshPanel.add(refreshBtn);
        refreshPanel.add(deleteBtn);
        add(refreshPanel, BorderLayout.SOUTH);
    }

    private void toggleRecipient() {
        boolean isPersonal = "PERSONAL".equals(typeCombo.getSelectedItem());
        recipientCombo.setEnabled(isPersonal);
    }

    private void loadResidents() {
        recipientCombo.removeAllItems();
        recipientCombo.addItem(new ResidentComboItem(0, "-- Select Resident --"));
        try {
            List<Resident> residents = residentService.getAllResidents();
            for (Resident r : residents) {
                recipientCombo.addItem(new ResidentComboItem(r.getId(), r.getFullName()));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading residents: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNotifications() {
        tableModel.setRowCount(0);
        try {
            List<Notification> list = notificationService.getAllNotifications();
            for (Notification n : list) {
                tableModel.addRow(new Object[]{
                    n.getId(), n.getType(),
                    n.getResidentName() != null ? n.getResidentName() : "All Residents",
                    n.getTitle(), n.getMessage(), n.getStatus(), n.getCreatedAt()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendNotification(ActionEvent e) {
        String title = titleField.getText().trim();
        String message = messageArea.getText().trim();
        String type = (String) typeCombo.getSelectedItem();

        if (title.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in title and message.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User currentUser = AuthService.getCurrentUser();
            if ("BROADCAST".equals(type)) {
                notificationService.sendBroadcast(currentUser.getId(), title, message);
            } else {
                ResidentComboItem item = (ResidentComboItem) recipientCombo.getSelectedItem();
                if (item == null || item.id == 0) {
                    JOptionPane.showMessageDialog(this, "Please select a recipient.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Notification note = new Notification();
                note.setSenderId(currentUser.getId());
                note.setResidentId(item.id);
                note.setTitle(title);
                note.setMessage(message);
                note.setType("PERSONAL");
                notificationService.sendNotification(note);
            }
            loadNotifications();
            titleField.setText("");
            messageArea.setText("");
            JOptionPane.showMessageDialog(this, "Notification sent successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteNotification(ActionEvent e) {
        int row = notificationTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a notification to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this notification?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                notificationService.deleteNotification(id);
                loadNotifications();
                JOptionPane.showMessageDialog(this, "Notification deleted!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class ResidentComboItem {
        int id;
        String name;
        ResidentComboItem(int id, String name) { this.id = id; this.name = name; }
        @Override
        public String toString() { return name; }
    }
}
