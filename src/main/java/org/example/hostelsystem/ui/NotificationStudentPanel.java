package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.Notification;
import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.NotificationService;
import org.example.hostelsystem.service.ResidentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class NotificationStudentPanel extends JPanel {

    private final NotificationService notificationService = new NotificationService();
    private final ResidentService residentService = new ResidentService();

    private JTable notificationTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    public NotificationStudentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadNotifications();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterCombo = new JComboBox<>(new String[]{"All", "Unread", "Read"});
        filterCombo.addActionListener(e -> loadNotifications());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadNotifications());
        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterCombo);
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "From", "Type", "Title", "Message", "Status", "Received On"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        notificationTable = new JTable(tableModel);
        notificationTable.setRowHeight(25);
        notificationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        notificationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(notificationTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton readBtn = new JButton("Mark as Read");
        JButton viewBtn = new JButton("View Details");
        readBtn.setBackground(new Color(60, 179, 113));
        readBtn.setForeground(Color.WHITE);
        readBtn.setOpaque(true);
        readBtn.setBorderPainted(false);
        readBtn.addActionListener(this::markAsRead);
        viewBtn.addActionListener(this::viewDetails);
        btnPanel.add(viewBtn);
        btnPanel.add(readBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadNotifications() {
        tableModel.setRowCount(0);
        Integer residentId = getStudentResidentId();
        if (residentId == null) {
            tableModel.addRow(new Object[]{"—", "—", "—", "Profile not linked", "Contact admin", "—", "—"});
            return;
        }
        try {
            String filter = (String) filterCombo.getSelectedItem();
            List<Notification> list;
            if ("Unread".equals(filter)) {
                list = notificationService.getUnreadNotificationsForResident(residentId);
            } else if ("Read".equals(filter)) {
                list = notificationService.getNotificationsForResident(residentId).stream()
                    .filter(n -> "READ".equals(n.getStatus())).toList();
            } else {
                list = notificationService.getNotificationsForResident(residentId);
            }

            if (list.isEmpty()) {
                tableModel.addRow(new Object[]{"—", "—", "—", "No notifications", "—", "—", "—"});
            } else {
                for (Notification n : list) {
                    tableModel.addRow(new Object[]{
                        n.getId(), n.getSenderName(), n.getType(),
                        n.getTitle(), n.getMessage(), n.getStatus(), n.getCreatedAt()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markAsRead(ActionEvent e) {
        int row = notificationTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a notification.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            notificationService.markAsRead(id);
            loadNotifications();
            JOptionPane.showMessageDialog(this, "Marked as read!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewDetails(ActionEvent e) {
        int row = notificationTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a notification.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String title = (String) tableModel.getValueAt(row, 3);
        String message = (String) tableModel.getValueAt(row, 4);
        String from = (String) tableModel.getValueAt(row, 1);
        String type = (String) tableModel.getValueAt(row, 2);
        String status = (String) tableModel.getValueAt(row, 5);
        Object date = tableModel.getValueAt(row, 6);

        JTextArea textArea = new JTextArea(8, 40);
        textArea.setText(
            "From: " + from + "\n" +
            "Type: " + type + "\n" +
            "Status: " + status + "\n" +
            "Date: " + date + "\n\n" +
            "Title: " + title + "\n\n" +
            "Message:\n" + message
        );
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Notification Details", JOptionPane.INFORMATION_MESSAGE);
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
