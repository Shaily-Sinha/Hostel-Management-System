package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.LeaveRequest;
import org.example.hostelsystem.service.LeaveRequestService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class LeaveManagementPanel extends JPanel {

    private final LeaveRequestService leaveRequestService = new LeaveRequestService();
    private JTable leaveTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    public LeaveManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadLeaveRequests();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterCombo = new JComboBox<>(new String[]{"All", "Pending", "Approved", "Rejected"});
        filterCombo.addActionListener(e -> loadLeaveRequests());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadLeaveRequests());
        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterCombo);
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Resident", "Start Date", "End Date", "Reason", "Status", "Applied On"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        leaveTable = new JTable(tableModel);
        leaveTable.setRowHeight(25);
        leaveTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        leaveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(leaveTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");
        JButton deleteBtn = new JButton("Delete");

        styleButton(approveBtn, new Color(60, 179, 113));
        styleButton(rejectBtn, new Color(255, 140, 0));
        styleButton(deleteBtn, new Color(220, 20, 60));

        approveBtn.addActionListener(this::approveLeave);
        rejectBtn.addActionListener(this::rejectLeave);
        deleteBtn.addActionListener(this::deleteLeave);

        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadLeaveRequests() {
        tableModel.setRowCount(0);
        String filter = (String) filterCombo.getSelectedItem();
        try {
            java.util.List<LeaveRequest> requests;
            if ("Pending".equals(filter)) {
                requests = leaveRequestService.getPendingLeaveRequests();
            } else if ("Approved".equals(filter)) {
                requests = leaveRequestService.getAllLeaveRequests().stream()
                    .filter(r -> "APPROVED".equals(r.getStatus())).toList();
            } else if ("Rejected".equals(filter)) {
                requests = leaveRequestService.getAllLeaveRequests().stream()
                    .filter(r -> "REJECTED".equals(r.getStatus())).toList();
            } else {
                requests = leaveRequestService.getAllLeaveRequests();
            }

            for (LeaveRequest r : requests) {
                tableModel.addRow(new Object[]{
                    r.getId(), r.getResidentName(), r.getStartDate(),
                    r.getEndDate(), r.getReason(), r.getStatus(), r.getCreatedAt()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading leave requests: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getSelectedLeaveId() {
        int row = leaveTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a leave request.", "Warning", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void approveLeave(ActionEvent e) {
        int id = getSelectedLeaveId();
        if (id == -1) return;
        try {
            leaveRequestService.approveLeave(id);
            loadLeaveRequests();
            JOptionPane.showMessageDialog(this, "Leave request approved!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejectLeave(ActionEvent e) {
        int id = getSelectedLeaveId();
        if (id == -1) return;
        try {
            leaveRequestService.rejectLeave(id);
            loadLeaveRequests();
            JOptionPane.showMessageDialog(this, "Leave request rejected!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLeave(ActionEvent e) {
        int id = getSelectedLeaveId();
        if (id == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this leave request?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                leaveRequestService.deleteLeaveRequest(id);
                loadLeaveRequests();
                JOptionPane.showMessageDialog(this, "Leave request deleted!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }
}
