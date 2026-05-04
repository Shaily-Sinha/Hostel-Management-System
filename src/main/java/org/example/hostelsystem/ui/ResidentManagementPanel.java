package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.model.Room;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.ResidentService;
import org.example.hostelsystem.service.RoomService;
import org.example.hostelsystem.ui.util.ModernTheme;
import org.example.hostelsystem.ui.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setLayout(new BorderLayout(0, 0));
        setBackground(ModernTheme.BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildFormCard(), BorderLayout.SOUTH);
        loadResidents();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 14, 0));
        header.add(ModernTheme.panelHeader("Residents", "Clean data entry for student records"), BorderLayout.WEST);

        JTextField searchField = ModernTheme.searchField("Search residents...");
        searchField.setPreferredSize(new Dimension(260, 36));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(searchField); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(searchField); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(searchField); }
        });

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(searchField);
        header.add(searchWrap, BorderLayout.EAST);
        return header;
    }

    private void filter(JTextField searchField) {
        String text = searchField.getText().trim();
        if (text.isEmpty() || text.equals("Search residents...")) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2, 3, 4, 6));
        }
    }

    private JPanel buildTable() {
        String[] columns = {"ID", "Name", "Email", "Phone", "Room", "Check-In", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        residentTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        residentTable.setRowSorter(sorter);
        residentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernTheme.styleTable(residentTable);
        residentTable.getColumnModel().getColumn(6).setCellRenderer(ModernTheme.statusRenderer());

        int[] widths = {58, 190, 240, 140, 90, 120, 120};
        for (int i = 0; i < widths.length; i++) {
            residentTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        residentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && residentTable.getSelectedRow() != -1) {
                populateFormFromSelection();
            }
        });
        residentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && residentTable.getSelectedRow() != -1) {
                    int row = residentTable.convertRowIndexToModel(residentTable.getSelectedRow());
                    int id = (int) tableModel.getValueAt(row, 0);
                    String name = (String) tableModel.getValueAt(row, 1);
                    new ResidentHistoryDialog((Frame) SwingUtilities.getWindowAncestor(ResidentManagementPanel.this), id, name).setVisible(true);
                }
            }
        });

        JScrollPane sp = ModernTheme.scrollPane(residentTable);
        sp.setPreferredSize(new Dimension(0, 320));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(sp, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildFormCard() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel card = ModernTheme.card();
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel section = new JLabel("Resident Details");
        section.setFont(ModernTheme.FONT_SUBHEAD);
        section.setForeground(ModernTheme.TEXT_SECONDARY);

        JPanel fields = new JPanel(new GridLayout(3, 4, 14, 12));
        fields.setOpaque(false);

        nameField = addTextField(fields, "Full Name");
        emailField = addTextField(fields, "Email");
        phoneField = addTextField(fields, "Phone");
        genderCombo = addCombo(fields, "Gender", new String[]{"Male", "Female", "Other"});
        dobSpinner = addDateSpinner(fields, "Date of Birth");
        roomCombo = addCombo(fields, "Room", new String[]{"None"});
        statusCombo = addCombo(fields, "Status", new String[]{"ACTIVE", "INACTIVE", "CHECKED_OUT"});
        emergencyField = addTextField(fields, "Emergency Contact");
        addressField = addTextField(fields, "Address");
        idProofField = addTextField(fields, "ID Proof");

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        fields.add(spacer);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        boolean canEdit = !AuthService.isWarden();

        if (canEdit) {
            JButton addBtn = ModernTheme.successButton("Add Resident");
            JButton updateBtn = ModernTheme.primaryButton("Update");
            JButton deleteBtn = ModernTheme.dangerButton("Delete");
            addBtn.addActionListener(this::addResident);
            updateBtn.addActionListener(this::updateResident);
            deleteBtn.addActionListener(this::deleteResident);
            buttonPanel.add(addBtn);
            buttonPanel.add(updateBtn);
            buttonPanel.add(deleteBtn);
        }

        JButton clearBtn = ModernTheme.secondaryButton("Clear");
        clearBtn.addActionListener(e -> clearForm());
        buttonPanel.add(clearBtn);

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.add(fields, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        card.add(section, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        outer.add(card, BorderLayout.CENTER);
        refreshRoomCombo();
        return outer;
    }

    private JTextField addTextField(JPanel parent, String label) {
        JPanel wrap = new JPanel(new BorderLayout(0, 5));
        wrap.setOpaque(false);
        wrap.add(ModernTheme.label(label), BorderLayout.NORTH);
        JTextField field = ModernTheme.textField();
        wrap.add(field, BorderLayout.CENTER);
        parent.add(wrap);
        return field;
    }

    private JComboBox<String> addCombo(JPanel parent, String label, String[] items) {
        JPanel wrap = new JPanel(new BorderLayout(0, 5));
        wrap.setOpaque(false);
        wrap.add(ModernTheme.label(label), BorderLayout.NORTH);
        JComboBox<String> combo = ModernTheme.comboBox(items);
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 36));
        wrap.add(combo, BorderLayout.CENTER);
        parent.add(wrap);
        return combo;
    }

    private JSpinner addDateSpinner(JPanel parent, String label) {
        JPanel wrap = new JPanel(new BorderLayout(0, 5));
        wrap.setOpaque(false);
        wrap.add(ModernTheme.label(label), BorderLayout.NORTH);
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        spinner.setFont(ModernTheme.FONT_BODY);
        spinner.setPreferredSize(new Dimension(spinner.getPreferredSize().width, 36));
        wrap.add(spinner, BorderLayout.CENTER);
        parent.add(wrap);
        return spinner;
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
            if (excludeResidentId != null) {
                Resident current = residentService.getResidentById(excludeResidentId);
                if (current != null && current.getRoomId() != null && current.getRoomId().equals(roomId)) {
                    return true;
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
        int row = residentTable.convertRowIndexToModel(residentTable.getSelectedRow());
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

            String registerUrl = "http://localhost:8765/register?id=" + r.getId();
            JEditorPane ep = new JEditorPane("text/html",
                "<html><body style='font-family:sans-serif;'>" +
                "Resident added successfully!<br><br>" +
                "To register their fingerprint, ask the student to open this URL on their phone:<br>" +
                "<a href=\"" + registerUrl + "\">" + registerUrl + "</a>" +
                "</body></html>");
            ep.addHyperlinkListener(ev -> {
                if (ev.getEventType().equals(javax.swing.event.HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        java.awt.Desktop.getDesktop().browse(ev.getURL().toURI());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            ep.setEditable(false);
            ep.setBackground(new JLabel().getBackground());
            ep.setBorder(null);
            JOptionPane.showMessageDialog(this, ep, "Fingerprint Registration", JOptionPane.INFORMATION_MESSAGE);
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
