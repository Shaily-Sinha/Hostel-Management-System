package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.AttendanceRecord;
import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.model.Room;
import org.example.hostelsystem.service.AttendanceService;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.ResidentService;
import org.example.hostelsystem.service.RoomService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StudentProfilePanel extends JPanel {

    private final ResidentService residentService = new ResidentService();
    private final RoomService roomService = new RoomService();
    private final AttendanceService attendanceService = new AttendanceService();
    private JLabel nameLabel, emailLabel, phoneLabel, roomLabel, statusLabel, checkInLabel;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private Resident currentResident;

    public StudentProfilePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initProfile();
        initAttendanceTable();
        loadMyData();
    }

    private void initProfile() {
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBorder(BorderFactory.createTitledBorder("My Profile"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        nameLabel = createValueLabel();
        emailLabel = createValueLabel();
        phoneLabel = createValueLabel();
        roomLabel = createValueLabel();
        statusLabel = createValueLabel();
        checkInLabel = createValueLabel();

        gbc.gridx = 0; gbc.gridy = 0;
        profilePanel.add(createFieldLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        profilePanel.add(nameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        profilePanel.add(createFieldLabel("Email:"), gbc);
        gbc.gridx = 1;
        profilePanel.add(emailLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        profilePanel.add(createFieldLabel("Phone:"), gbc);
        gbc.gridx = 1;
        profilePanel.add(phoneLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        profilePanel.add(createFieldLabel("Room:"), gbc);
        gbc.gridx = 1;
        profilePanel.add(roomLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        profilePanel.add(createFieldLabel("Status:"), gbc);
        gbc.gridx = 1;
        profilePanel.add(statusLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        profilePanel.add(createFieldLabel("Check-In Date:"), gbc);
        gbc.gridx = 1;
        profilePanel.add(checkInLabel, gbc);

        add(profilePanel, BorderLayout.NORTH);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private void initAttendanceTable() {
        String[] columns = {"Date", "Time", "Location Verified", "Biometric Verified", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(25);
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JPanel attendancePanel = new JPanel(new BorderLayout());
        attendancePanel.setBorder(BorderFactory.createTitledBorder("My Attendance History"));
        attendancePanel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        add(attendancePanel, BorderLayout.CENTER);
    }

    private void loadMyData() {
        String email = AuthService.getCurrentUser().getEmail();
        try {
            List<Resident> residents = residentService.getAllResidents();
            currentResident = null;
            for (Resident r : residents) {
                if (r.getEmail() != null && r.getEmail().equalsIgnoreCase(email)) {
                    currentResident = r;
                    break;
                }
            }

            if (currentResident != null) {
                nameLabel.setText(currentResident.getFullName());
                emailLabel.setText(currentResident.getEmail());
                phoneLabel.setText(currentResident.getPhone());
                statusLabel.setText(currentResident.getStatus());
                checkInLabel.setText(String.valueOf(currentResident.getCheckInDate()));

                if (currentResident.getRoomId() != null) {
                    Room room = roomService.getRoomById(currentResident.getRoomId());
                    roomLabel.setText(room != null ? room.getRoomNumber() : "N/A");
                } else {
                    roomLabel.setText("Not Assigned");
                }

                loadAttendanceHistory(currentResident.getId());
            } else {
                nameLabel.setText("Profile not linked. Contact admin.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAttendanceHistory(int residentId) {
        tableModel.setRowCount(0);
        try {
            List<AttendanceRecord> records = attendanceService.getAttendanceByResident(residentId);
            for (AttendanceRecord r : records) {
                tableModel.addRow(new Object[]{
                    r.getAttendanceDate(), r.getCheckInTime(),
                    r.isLocationVerified() ? "Yes" : "No",
                    r.isBiometricVerified() ? "Yes" : "No",
                    r.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading attendance: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Integer getResidentId() {
        return currentResident != null ? currentResident.getId() : null;
    }
}
