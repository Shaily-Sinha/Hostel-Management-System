package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.LateArrivalIntimation;
import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.LateArrivalIntimationService;
import org.example.hostelsystem.service.ResidentService;
import org.example.hostelsystem.ui.util.ModernTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class LateArrivalStudentPanel extends JPanel {

    private final LateArrivalIntimationService intimationService = new LateArrivalIntimationService();
    private final ResidentService residentService = new ResidentService();

    private JTable intimationTable;
    private DefaultTableModel tableModel;
    private JSpinner arrivalDateSpinner;
    private JTextField timeField;
    private JTextArea reasonArea;

    public LateArrivalStudentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadStudentIntimations();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Notify Late Arrival"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        arrivalDateSpinner = new JSpinner(new SpinnerDateModel());
        arrivalDateSpinner.setEditor(new JSpinner.DateEditor(arrivalDateSpinner, "yyyy-MM-dd"));
        timeField = new JTextField("22:00", 12);
        reasonArea = new JTextArea(3, 25);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Arrival Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(arrivalDateSpinner, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Expected Time (HH:mm):"), gbc);
        gbc.gridx = 3;
        formPanel.add(timeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(new JScrollPane(reasonArea), gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton submitBtn = new JButton("Submit Intimation");
        submitBtn.setBackground(new Color(60, 179, 113));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setOpaque(true);
        submitBtn.setBorderPainted(false);
        submitBtn.addActionListener(e -> submitIntimation());
        btnPanel.add(submitBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        String[] cols = {"Arrival Date", "Expected Time", "Reason", "Status", "Notified On"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        intimationTable = new JTable(tableModel);
        ModernTheme.styleTable(intimationTable);
        add(ModernTheme.scrollPane(intimationTable), BorderLayout.CENTER);

        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshBtn = new JButton("Refresh History");
        refreshBtn.addActionListener(e -> loadStudentIntimations());
        refreshPanel.add(refreshBtn);
        add(refreshPanel, BorderLayout.SOUTH);
    }

    private void submitIntimation() {
        Integer residentId = getStudentResidentId();
        if (residentId == null) {
            JOptionPane.showMessageDialog(this, "Your resident profile is not linked. Contact admin.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date arrivalDate = new Date(((java.util.Date) arrivalDateSpinner.getValue()).getTime());
        String expectedTime = timeField.getText().trim();
        String reason = reasonArea.getText().trim();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a reason.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (expectedTime.isEmpty() || !expectedTime.matches("\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Expected time must be in HH:mm format.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LateArrivalIntimation item = new LateArrivalIntimation();
            item.setResidentId(residentId);
            item.setArrivalDate(arrivalDate);
            item.setExpectedTime(expectedTime);
            item.setReason(reason);
            intimationService.submitIntimation(item);
            loadStudentIntimations();
            reasonArea.setText("");
            timeField.setText("22:00");
            JOptionPane.showMessageDialog(this, "Late arrival intimation submitted successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentIntimations() {
        tableModel.setRowCount(0);
        Integer residentId = getStudentResidentId();
        if (residentId == null) {
            tableModel.addRow(new Object[]{"—", "—", "Profile not linked", "—", "—"});
            return;
        }
        try {
            List<LateArrivalIntimation> list = intimationService.getIntimationsByResident(residentId);
            if (list.isEmpty()) {
                tableModel.addRow(new Object[]{"—", "—", "No intimations yet", "—", "—"});
            } else {
                for (LateArrivalIntimation i : list) {
                    tableModel.addRow(new Object[]{
                        i.getArrivalDate(), i.getExpectedTime(), i.getReason(),
                        i.getStatus(), i.getCreatedAt()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
