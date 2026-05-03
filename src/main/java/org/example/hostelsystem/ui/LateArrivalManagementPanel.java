package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.LateArrivalIntimation;
import org.example.hostelsystem.service.LateArrivalIntimationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class LateArrivalManagementPanel extends JPanel {

    private final LateArrivalIntimationService intimationService = new LateArrivalIntimationService();
    private JTable intimationTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    public LateArrivalManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadIntimations();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterCombo = new JComboBox<>(new String[]{"All", "Notified", "Acknowledged", "Excused"});
        filterCombo.addActionListener(e -> loadIntimations());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadIntimations());
        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterCombo);
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Resident", "Arrival Date", "Expected Time", "Reason", "Status", "Notified On"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        intimationTable = new JTable(tableModel);
        intimationTable.setRowHeight(25);
        intimationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        intimationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(intimationTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton ackBtn = new JButton("Acknowledge");
        JButton excuseBtn = new JButton("Excuse");
        JButton deleteBtn = new JButton("Delete");

        styleButton(ackBtn, new Color(60, 179, 113));
        styleButton(excuseBtn, new Color(70, 130, 180));
        styleButton(deleteBtn, new Color(220, 20, 60));

        ackBtn.addActionListener(this::acknowledgeIntimation);
        excuseBtn.addActionListener(this::excuseIntimation);
        deleteBtn.addActionListener(this::deleteIntimation);

        btnPanel.add(ackBtn);
        btnPanel.add(excuseBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadIntimations() {
        tableModel.setRowCount(0);
        String filter = (String) filterCombo.getSelectedItem();
        try {
            java.util.List<LateArrivalIntimation> list;
            if ("Notified".equals(filter)) {
                list = intimationService.getNotifiedIntimations();
            } else if ("Acknowledged".equals(filter)) {
                list = intimationService.getAllIntimations().stream()
                    .filter(i -> "ACKNOWLEDGED".equals(i.getStatus())).toList();
            } else if ("Excused".equals(filter)) {
                list = intimationService.getAllIntimations().stream()
                    .filter(i -> "EXCUSED".equals(i.getStatus())).toList();
            } else {
                list = intimationService.getAllIntimations();
            }

            for (LateArrivalIntimation i : list) {
                tableModel.addRow(new Object[]{
                    i.getId(), i.getResidentName(), i.getArrivalDate(),
                    i.getExpectedTime(), i.getReason(), i.getStatus(), i.getCreatedAt()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading intimations: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getSelectedId() {
        int row = intimationTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an intimation.", "Warning", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void acknowledgeIntimation(ActionEvent e) {
        int id = getSelectedId();
        if (id == -1) return;
        try {
            intimationService.acknowledgeIntimation(id);
            loadIntimations();
            JOptionPane.showMessageDialog(this, "Late arrival acknowledged!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excuseIntimation(ActionEvent e) {
        int id = getSelectedId();
        if (id == -1) return;
        try {
            intimationService.excuseIntimation(id);
            loadIntimations();
            JOptionPane.showMessageDialog(this, "Late arrival excused!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteIntimation(ActionEvent e) {
        int id = getSelectedId();
        if (id == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this intimation?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                intimationService.deleteIntimation(id);
                loadIntimations();
                JOptionPane.showMessageDialog(this, "Intimation deleted!");
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
