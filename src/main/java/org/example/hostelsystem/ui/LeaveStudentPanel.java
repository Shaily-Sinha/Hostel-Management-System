package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.LeaveRequest;
import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.LeaveRequestService;
import org.example.hostelsystem.service.ResidentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class LeaveStudentPanel extends JPanel {

    private final LeaveRequestService leaveRequestService = new LeaveRequestService();
    private final ResidentService residentService = new ResidentService();

    private JTable leaveTable;
    private DefaultTableModel tableModel;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JTextArea reasonArea;

    public LeaveStudentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadStudentLeaves();
    }

    private void initComponents() {
        // Top: Application Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Apply for Leave"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        reasonArea = new JTextArea(3, 25);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(startDateSpinner, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 3;
        formPanel.add(endDateSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(new JScrollPane(reasonArea), gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton applyBtn = new JButton("Submit Application");
        applyBtn.setBackground(new Color(60, 179, 113));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setOpaque(true);
        applyBtn.setBorderPainted(false);
        applyBtn.addActionListener(e -> applyForLeave());
        btnPanel.add(applyBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Center: Leave History Table
        String[] cols = {"Start Date", "End Date", "Reason", "Status", "Applied On"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        leaveTable = new JTable(tableModel);
        leaveTable.setRowHeight(25);
        leaveTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        add(new JScrollPane(leaveTable), BorderLayout.CENTER);

        // South: Refresh button
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshBtn = new JButton("Refresh History");
        refreshBtn.addActionListener(e -> loadStudentLeaves());
        refreshPanel.add(refreshBtn);
        add(refreshPanel, BorderLayout.SOUTH);
    }

    private void applyForLeave() {
        Integer residentId = getStudentResidentId();
        if (residentId == null) {
            JOptionPane.showMessageDialog(this, "Your resident profile is not linked. Contact admin.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date startDate = new Date(((java.util.Date) startDateSpinner.getValue()).getTime());
        Date endDate = new Date(((java.util.Date) endDateSpinner.getValue()).getTime());
        String reason = reasonArea.getText().trim();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a reason for leave.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (endDate.before(startDate)) {
            JOptionPane.showMessageDialog(this, "End date cannot be before start date.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LeaveRequest request = new LeaveRequest();
            request.setResidentId(residentId);
            request.setStartDate(startDate);
            request.setEndDate(endDate);
            request.setReason(reason);
            leaveRequestService.applyForLeave(request);
            loadStudentLeaves();
            reasonArea.setText("");
            JOptionPane.showMessageDialog(this, "Leave application submitted successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentLeaves() {
        tableModel.setRowCount(0);
        Integer residentId = getStudentResidentId();
        if (residentId == null) {
            tableModel.addRow(new Object[]{"—", "—", "Profile not linked", "—", "—"});
            return;
        }
        try {
            List<LeaveRequest> requests = leaveRequestService.getLeaveRequestsByResident(residentId);
            if (requests.isEmpty()) {
                tableModel.addRow(new Object[]{"—", "—", "No leave history", "—", "—"});
            } else {
                for (LeaveRequest r : requests) {
                    tableModel.addRow(new Object[]{
                        r.getStartDate(), r.getEndDate(), r.getReason(),
                        r.getStatus(), r.getCreatedAt()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading leave history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
