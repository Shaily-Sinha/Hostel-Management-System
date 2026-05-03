package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.model.Room;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.ResidentService;
import org.example.hostelsystem.service.RoomService;
import org.example.hostelsystem.ui.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ResidentManagementPanel extends JPanel {

    private final ResidentService residentService = new ResidentService();
    private final RoomService roomService = new RoomService();
    private JTable residentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField nameField, emailField, phoneField, addressField, emergencyField, idProofField;
    private JComboBox<String> genderCombo, statusCombo, roomCombo;
    private JSpinner dobSpinner;
    private int selectedResidentId = -1;

    public ResidentManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initTable();
        initForm();
        loadResidents();
    }

    private void initTable() {
        String[] columns = {"ID", "Name", "Email", "Phone", "Room", "Check-In", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        residentTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        residentTable.setRowSorter(sorter);
        residentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        residentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        residentTable.setRowHeight(25);
        residentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && residentTable.getSelectedRow() != -1) {
                populateFormFromSelection();
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField(20);
        searchField.setToolTipText("Search by name, email or phone...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2, 3));
            }
        });
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(residentTable), BorderLayout.CENTER);
        tablePanel.setPreferredSize(new Dimension(0, 250));
        add(tablePanel, BorderLayout.NORTH);
    }

    private void initForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Resident Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(12);
        emailField = new JTextField(12);
        phoneField = new JTextField(12);
        addressField = new JTextField(15);
        emergencyField = new JTextField(12);
        idProofField = new JTextField(12);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "CHECKED_OUT"});
        roomCombo = new JComboBox<>();
        dobSpinner = new JSpinner(new SpinnerDateModel());
        dobSpinner.setEditor(new JSpinner.DateEditor(dobSpinner, "yyyy-MM-dd"));

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 3;
        formPanel.add(genderCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dobSpinner, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Room:"), gbc);
        gbc.gridx = 3;
        formPanel.add(roomCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(addressField, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Emergency Contact:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emergencyField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("ID Proof:"), gbc);
        gbc.gridx = 3;
        formPanel.add(idProofField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);

        boolean canEdit = !AuthService.isWarden();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addBtn = new JButton("Add Resident");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");

        addBtn.setBackground(new Color(60, 179, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setOpaque(true);
        addBtn.setBorderPainted(false);
        updateBtn.setBackground(new Color(70, 130, 180));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setOpaque(true);
        updateBtn.setBorderPainted(false);
        deleteBtn.setBackground(new Color(220, 20, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setOpaque(true);
        deleteBtn.setBorderPainted(false);

        addBtn.addActionListener(this::addResident);
        updateBtn.addActionListener(this::updateResident);
        deleteBtn.addActionListener(this::deleteResident);
        clearBtn.addActionListener(e -> clearForm());

        if (canEdit) buttonPanel.add(addBtn);
        if (canEdit) buttonPanel.add(updateBtn);
        if (canEdit) buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);
        refreshRoomCombo();
    }

    private void refreshRoomCombo() {
        roomCombo.removeAllItems();
        roomCombo.addItem("None");
        try {
            List<Room> rooms = roomService.getAllRooms();
            for (Room room : rooms) {
                int occupancy = roomService.getCurrentOccupancy(room.getId());
                String item = room.getId() + " - " + room.getRoomNumber() +
                              " (" + occupancy + "/" + room.getCapacity() + ")";
                roomCombo.addItem(item);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isRoomCapacityAvailable(Integer roomId, Integer excludeResidentId) {
        if (roomId == null) return true;
        try {
            Room room = roomService.getRoomById(roomId);
            if (room == null) return true;
            int occupancy = roomService.getCurrentOccupancy(room.getId());
            // If updating same resident in same room, don't count them
            if (excludeResidentId != null) {
                Resident current = residentService.getResidentById(excludeResidentId);
                if (current != null && current.getRoomId() != null && current.getRoomId().equals(roomId)) {
                    return true; // already in this room
                }
            }
            if (occupancy >= room.getCapacity()) {
                JOptionPane.showMessageDialog(this,
                    "Room " + room.getRoomNumber() + " is already at full capacity (" +
                    occupancy + "/" + room.getCapacity() + ").",
                    "Room Full", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking room capacity: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void loadResidents() {
        tableModel.setRowCount(0);
        try {
            List<Resident> residents = residentService.getAllResidents();
            for (Resident r : residents) {
                String roomInfo = r.getRoomId() != null ? String.valueOf(r.getRoomId()) : "N/A";
                tableModel.addRow(new Object[]{
                    r.getId(), r.getFullName(), r.getEmail(), r.getPhone(),
                    roomInfo, r.getCheckInDate(), r.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading residents: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromSelection() {
        int row = residentTable.getSelectedRow();
        selectedResidentId = (int) tableModel.getValueAt(row, 0);
        try {
            Resident r = residentService.getResidentById(selectedResidentId);
            if (r != null) {
                nameField.setText(r.getFullName());
                emailField.setText(r.getEmail());
                phoneField.setText(r.getPhone());
                addressField.setText(r.getAddress());
                emergencyField.setText(r.getEmergencyContact());
                idProofField.setText(r.getIdProof());
                genderCombo.setSelectedItem(r.getGender());
                statusCombo.setSelectedItem(r.getStatus());
                if (r.getDateOfBirth() != null) {
                    dobSpinner.setValue(new java.util.Date(r.getDateOfBirth().getTime()));
                }
                if (r.getRoomId() != null) {
                    for (int i = 0; i < roomCombo.getItemCount(); i++) {
                        if (roomCombo.getItemAt(i).startsWith(r.getRoomId() + " -")) {
                            roomCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    roomCombo.setSelectedIndex(0);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addResident(ActionEvent e) {
        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (!ValidationUtil.requireNonEmpty(this, "Full Name", fullName)) return;
        if (!ValidationUtil.validateEmail(this, email)) return;

        try {
            Resident r = buildResidentFromForm();
            if (!isRoomCapacityAvailable(r.getRoomId(), null)) return;
            r.setCheckInDate(new Date(System.currentTimeMillis()));
            residentService.addResident(r);
            loadResidents();
            clearForm();
            JOptionPane.showMessageDialog(this, "Resident added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateResident(ActionEvent e) {
        if (selectedResidentId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resident to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (!ValidationUtil.requireNonEmpty(this, "Full Name", fullName)) return;
        if (!ValidationUtil.validateEmail(this, email)) return;

        try {
            Resident r = buildResidentFromForm();
            if (!isRoomCapacityAvailable(r.getRoomId(), selectedResidentId)) return;
            r.setId(selectedResidentId);
            residentService.updateResident(r);
            loadResidents();
            clearForm();
            JOptionPane.showMessageDialog(this, "Resident updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteResident(ActionEvent e) {
        if (selectedResidentId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resident to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this resident?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                residentService.deleteResident(selectedResidentId);
                loadResidents();
                clearForm();
                JOptionPane.showMessageDialog(this, "Resident deleted successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Resident buildResidentFromForm() {
        Resident r = new Resident();
        r.setFullName(nameField.getText().trim());
        r.setEmail(emailField.getText().trim());
        r.setPhone(phoneField.getText().trim());
        r.setAddress(addressField.getText().trim());
        r.setEmergencyContact(emergencyField.getText().trim());
        r.setIdProof(idProofField.getText().trim());
        r.setGender((String) genderCombo.getSelectedItem());
        r.setStatus((String) statusCombo.getSelectedItem());
        java.util.Date dob = (java.util.Date) dobSpinner.getValue();
        r.setDateOfBirth(new Date(dob.getTime()));
        String roomStr = (String) roomCombo.getSelectedItem();
        if (roomStr != null && !roomStr.equals("None")) {
            r.setRoomId(Integer.parseInt(roomStr.split(" - ")[0]));
        }
        return r;
    }

    private void clearForm() {
        selectedResidentId = -1;
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        emergencyField.setText("");
        idProofField.setText("");
        genderCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        roomCombo.setSelectedIndex(0);
        dobSpinner.setValue(new java.util.Date());
        residentTable.clearSelection();
        refreshRoomCombo();
    }
}
