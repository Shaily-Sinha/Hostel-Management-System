package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.LeaveRequest;
import org.example.hostelsystem.service.LeaveRequestService;
import org.example.hostelsystem.ui.util.ModernTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class LeaveManagementPanel extends JPanel {

    private final LeaveRequestService leaveRequestService = new LeaveRequestService();
    private JTable leaveTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> filterCombo;

    public LeaveManagementPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(ModernTheme.BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initComponents();
        loadLeaveRequests();
    }

    private void initComponents() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 14, 0));
        header.add(ModernTheme.panelHeader("Leave Requests", "Review and manage student leave"), BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        JLabel filterLabel = ModernTheme.label("Filter");
        filterCombo = ModernTheme.comboBox(new String[]{"All", "Pending", "Approved", "Rejected"});
        filterCombo.setPreferredSize(new Dimension(150, 36));
        filterCombo.addActionListener(e -> loadLeaveRequests());

        JButton refreshBtn = ModernTheme.secondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(98, 36));
        refreshBtn.addActionListener(e -> loadLeaveRequests());

        controls.add(filterLabel);
        controls.add(filterCombo);
        controls.add(refreshBtn);
        header.add(controls, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTable() {
        String[] cols = {"ID", "Resident", "Start Date", "End Date", "Reason", "Status", "Applied On"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        leaveTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        leaveTable.setRowSorter(sorter);
        leaveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernTheme.styleTable(leaveTable);
        leaveTable.getColumnModel().getColumn(5).setCellRenderer(ModernTheme.statusRenderer());

        int[] widths = {60, 210, 145, 145, 430, 130, 220};
        for (int i = 0; i < widths.length; i++) {
            leaveTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = ModernTheme.scrollPane(leaveTable);
        scrollPane.setPreferredSize(new Dimension(0, 430));

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setOpaque(false);
        tableWrap.add(scrollPane, BorderLayout.CENTER);
        return tableWrap;
    }

    private JPanel buildActions() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel actions = ModernTheme.card();
        actions.setLayout(new BorderLayout());
        actions.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel hint = ModernTheme.label("Select one request, then choose an action");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        JButton approveBtn = ModernTheme.successButton("Approve");
        JButton rejectBtn = ModernTheme.secondaryButton("Reject");
        JButton deleteBtn = ModernTheme.dangerButton("Delete");
        approveBtn.addActionListener(this::approveLeave);
        rejectBtn.addActionListener(this::rejectLeave);
        deleteBtn.addActionListener(this::deleteLeave);

        buttons.add(approveBtn);
        buttons.add(rejectBtn);
        buttons.add(deleteBtn);

        actions.add(hint, BorderLayout.WEST);
        actions.add(buttons, BorderLayout.EAST);
        outer.add(actions, BorderLayout.CENTER);
        return outer;
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
        int modelRow = leaveTable.convertRowIndexToModel(row);
        return (int) tableModel.getValueAt(modelRow, 0);
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
}
