package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.AttendanceRecord;
import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.service.AttendanceService;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.ResidentService;
import org.example.hostelsystem.attendance.AttendanceServer;
import org.example.hostelsystem.ui.util.ModernTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class AttendancePanel extends JPanel {

    private static final AttendanceService attendanceService = new AttendanceService();
    private final ResidentService residentService = new ResidentService();
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> residentCombo;
    private JSpinner dateSpinner;
    private static AttendanceServer attendanceServer;
    private SwingWorker<Void, Void> pollingWorker;

    public AttendancePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initServer();
        initTable();
        if (AuthService.isStudent()) {
            initStudentForm();
        } else {
            initForm();
        }
        loadAttendance();
    }

    private void initServer() {
        if (attendanceServer != null) {
            return;
        }
        try {
            attendanceServer = new AttendanceServer(8765, attendanceService);
            attendanceServer.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to start attendance server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initTable() {
        String[] columns;
        if (AuthService.isStudent()) {
            columns = new String[]{"Date", "Time", "Location Verified", "Biometric Verified", "Status"};
        } else {
            columns = new String[]{"ID", "Resident", "Date", "Time", "Location Verified", "Biometric Verified", "Status"};
        }
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        attendanceTable = new JTable(tableModel);
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernTheme.styleTable(attendanceTable);

        JScrollPane scrollPane = ModernTheme.scrollPane(attendanceTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        add(scrollPane, BorderLayout.NORTH);
    }

    private void initForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Mark Attendance"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        residentCombo = new JComboBox<>();
        refreshResidentCombo();

        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Resident:"), gbc);
        gbc.gridx = 1;
        formPanel.add(residentCombo, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 3;
        formPanel.add(dateSpinner, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton markBtn = new JButton("Mark Attendance (Browser)");
        markBtn.setBackground(new Color(60, 179, 113));
        markBtn.setForeground(Color.WHITE);
        markBtn.setOpaque(true);
        markBtn.setBorderPainted(false);
        markBtn.setFont(new Font("Arial", Font.BOLD, 14));
        markBtn.setFocusPainted(false);
        markBtn.addActionListener(this::markAttendance);

        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.addActionListener(e -> {
            refreshResidentCombo();
            loadAttendance();
        });

        buttonPanel.add(markBtn);
        buttonPanel.add(refreshBtn);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        JLabel infoLabel = new JLabel("<html><center>This will open your browser to verify location and biometric authentication.</center></html>", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.DARK_GRAY);
        gbc.gridy = 2;
        formPanel.add(infoLabel, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void initStudentForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Mark My Attendance"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateSpinner, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton markBtn = new JButton("Mark My Attendance (Browser)");
        markBtn.setBackground(new Color(60, 179, 113));
        markBtn.setForeground(Color.WHITE);
        markBtn.setOpaque(true);
        markBtn.setBorderPainted(false);
        markBtn.setFont(new Font("Arial", Font.BOLD, 14));
        markBtn.setFocusPainted(false);
        markBtn.addActionListener(this::markAttendance);

        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.addActionListener(e -> loadAttendance());

        buttonPanel.add(markBtn);
        buttonPanel.add(refreshBtn);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        JLabel infoLabel = new JLabel("<html><center>This will open your browser to verify your location and biometric.</center></html>", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.DARK_GRAY);
        gbc.gridy = 2;
        formPanel.add(infoLabel, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void refreshResidentCombo() {
        residentCombo.removeAllItems();
        try {
            List<Resident> residents = residentService.getActiveResidents();
            for (Resident r : residents) {
                residentCombo.addItem(r.getId() + " - " + r.getFullName());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading residents: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAttendance() {
        tableModel.setRowCount(0);
        try {
            if (AuthService.isStudent()) {
                Integer residentId = getStudentResidentId();
                if (residentId != null) {
                    List<AttendanceRecord> records = attendanceService.getAttendanceByResident(residentId);
                    for (AttendanceRecord r : records) {
                        tableModel.addRow(new Object[]{
                            r.getAttendanceDate(), r.getCheckInTime(),
                            r.isLocationVerified() ? "Yes" : "No",
                            r.isBiometricVerified() ? "Yes" : "No",
                            r.getStatus()
                        });
                    }
                }
            } else {
                List<AttendanceRecord> records = attendanceService.getAllAttendance();
                for (AttendanceRecord r : records) {
                    tableModel.addRow(new Object[]{
                        r.getId(), r.getResidentName(), r.getAttendanceDate(),
                        r.getCheckInTime(), r.isLocationVerified() ? "Yes" : "No",
                        r.isBiometricVerified() ? "Yes" : "No", r.getStatus()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading attendance: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void markAttendance(ActionEvent e) {
        if (attendanceServer == null) {
            JOptionPane.showMessageDialog(this, "Attendance server is not running. Please restart the application.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int residentId;
        if (AuthService.isStudent()) {
            Integer id = getStudentResidentId();
            if (id == null) {
                JOptionPane.showMessageDialog(this, "Your resident profile is not linked. Contact admin.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            residentId = id;
        } else {
            if (residentCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select a resident", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String residentStr = (String) residentCombo.getSelectedItem();
            residentId = Integer.parseInt(residentStr.split(" - ")[0]);
        }

        try {
            Date selectedDate = new Date(((java.util.Date) dateSpinner.getValue()).getTime());
            if (attendanceService.isAttendanceMarked(residentId, selectedDate)) {
                JOptionPane.showMessageDialog(this, "Attendance already marked for this resident on selected date!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking attendance: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date selectedDate = new Date(((java.util.Date) dateSpinner.getValue()).getTime());
        attendanceServer.setPendingResidentId(residentId, selectedDate);

        try {
            Desktop.getDesktop().browse(URI.create("http://localhost:8765/attendance"));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not open browser. Please manually visit: http://localhost:8765/attendance", "Browser Error", JOptionPane.WARNING_MESSAGE);
        }

        if (pollingWorker != null && !pollingWorker.isDone()) {
            pollingWorker.cancel(true);
        }

        pollingWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                for (int i = 0; i < 120; i++) {
                    if (isCancelled()) return null;
                    try {
                        Thread.sleep(1000);
                        if (attendanceServer != null && attendanceServer.isAttendanceProcessed()) {
                            break;
                        }
                    } catch (InterruptedException ignored) {}
                }
                return null;
            }

            @Override
            protected void done() {
                if (isCancelled()) return;
                loadAttendance();
                if (attendanceServer != null && attendanceServer.isAttendanceProcessed()) {
                    String message = attendanceServer.getLastAttendanceMessage();
                    int msgType = (message != null && message.contains("successfully"))
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.WARNING_MESSAGE;
                    JOptionPane.showMessageDialog(AttendancePanel.this, message, "Attendance Result", msgType);
                } else {
                    JOptionPane.showMessageDialog(AttendancePanel.this, "Attendance not marked yet. Please complete the browser verification or try again.", "Pending", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };
        pollingWorker.execute();
    }
}
