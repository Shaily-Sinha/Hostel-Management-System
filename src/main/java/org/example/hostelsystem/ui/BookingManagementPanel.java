package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.Booking;
import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.model.Room;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.BookingService;
import org.example.hostelsystem.service.ResidentService;
import org.example.hostelsystem.service.RoomService;
import org.example.hostelsystem.ui.util.ModernTheme;
import org.example.hostelsystem.ui.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class BookingManagementPanel extends JPanel {

    private final BookingService bookingService = new BookingService();
    private final ResidentService residentService = new ResidentService();
    private final RoomService roomService = new RoomService();
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> residentCombo, roomCombo, statusCombo;
    private JSpinner bookingDateSpinner, checkInSpinner, checkOutSpinner;
    private JTextField amountField, notesField;
    private int selectedBookingId = -1;

    public BookingManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initTable();
        initForm();
        loadBookings();
    }

    private void initTable() {
        String[] columns = {"ID", "Resident", "Room", "Booking Date", "Check-In", "Check-Out", "Status", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookingTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        bookingTable.setRowSorter(sorter);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernTheme.styleTable(bookingTable);
        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookingTable.getSelectedRow() != -1) {
                populateFormFromSelection();
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout());
        JTextField searchField =
                ModernTheme.searchField("🔍 Search bookings...");
        searchField.setPreferredSize(
                new Dimension(220, 36)
        );
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
            }
        });
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(ModernTheme.scrollPane(bookingTable), BorderLayout.CENTER);
        tablePanel.setPreferredSize(new Dimension(0, 280));
        add(tablePanel, BorderLayout.NORTH);
    }

    private void initForm() {
        JPanel formPanel = ModernTheme.card();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(20, 20, 20, 20),
                        BorderFactory.createTitledBorder("Booking Details")
                )
        );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        residentCombo = new JComboBox<>();
        roomCombo = new JComboBox<>();
        statusCombo = new JComboBox<>(new String[]{"PENDING", "CONFIRMED", "CANCELLED"});
        bookingDateSpinner = new JSpinner(new SpinnerDateModel());
        bookingDateSpinner.setEditor(new JSpinner.DateEditor(bookingDateSpinner, "yyyy-MM-dd"));
        checkInSpinner = new JSpinner(new SpinnerDateModel());
        checkInSpinner.setEditor(new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd"));
        checkOutSpinner = new JSpinner(new SpinnerDateModel());
        checkOutSpinner.setEditor(new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd"));
        amountField = new JTextField(10);
        notesField = new JTextField(20);

        refreshCombos();

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Resident:"), gbc);
        gbc.gridx = 1;
        formPanel.add(residentCombo, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Room:"), gbc);
        gbc.gridx = 3;
        formPanel.add(roomCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Booking Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(bookingDateSpinner, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        formPanel.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Check-In:"), gbc);
        gbc.gridx = 1;
        formPanel.add(checkInSpinner, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Check-Out:"), gbc);
        gbc.gridx = 3;
        formPanel.add(checkOutSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 3;
        formPanel.add(notesField, gbc);

        boolean canEdit = !AuthService.isWarden();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addBtn =
                ModernTheme.successButton("＋ Create Booking");

        JButton updateBtn =
                ModernTheme.primaryButton("✎ Update");

        JButton confirmBtn =
                ModernTheme.primaryButton("✓ Confirm");

        JButton cancelBtn =
                ModernTheme.dangerButton("✕ Cancel");

        JButton clearBtn =
                ModernTheme.secondaryButton("Clear");



        addBtn.addActionListener(this::addBooking);
        updateBtn.addActionListener(this::updateBooking);
        confirmBtn.addActionListener(this::confirmBooking);
        cancelBtn.addActionListener(this::cancelBooking);
        clearBtn.addActionListener(e -> clearForm());

        if (canEdit) buttonPanel.add(addBtn);
        if (canEdit) buttonPanel.add(updateBtn);
        if (canEdit) buttonPanel.add(confirmBtn);
        if (canEdit) buttonPanel.add(cancelBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void refreshCombos() {
        residentCombo.removeAllItems();
        roomCombo.removeAllItems();
        try {
            List<Resident> residents = residentService.getAllResidents();
            for (Resident r : residents) {
                residentCombo.addItem(r.getId() + " - " + r.getFullName());
            }
            List<Room> rooms = roomService.getAllRooms();
            for (Room room : rooms) {
                int occupancy = roomService.getCurrentOccupancy(room.getId());
                String item = room.getId() + " - " + room.getRoomNumber() +
                              " (" + occupancy + "/" + room.getCapacity() + ")";
                roomCombo.addItem(item);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error refreshing combos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isRoomFull(Integer roomId, Integer excludeBookingId) {
        if (roomId == null) return false;
        try {
            Room room = roomService.getRoomById(roomId);
            if (room == null) return false;
            int occupancy = roomService.getCurrentOccupancy(room.getId());
            // If updating same booking with same room, it is not full for this booking
            if (excludeBookingId != null) {
                Booking current = bookingService.getBookingById(excludeBookingId);
                if (current != null && roomId.equals(current.getRoomId())) {
                    return false;
                }
            }
            if (occupancy >= room.getCapacity()) {
                JOptionPane.showMessageDialog(this,
                    "Room " + room.getRoomNumber() + " is already at full capacity (" +
                    occupancy + "/" + room.getCapacity() + ").",
                    "Room Full", JOptionPane.WARNING_MESSAGE);
                return true;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking room capacity: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }


    private void loadBookings() {
        tableModel.setRowCount(0);
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            for (Booking b : bookings) {
                tableModel.addRow(new Object[]{
                    b.getId(), b.getResidentName(), b.getRoomNumber(),
                    b.getBookingDate(), b.getCheckInDate(), b.getCheckOutDate(),
                    b.getStatus(), b.getTotalAmount()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromSelection() {
        int row = bookingTable.getSelectedRow();
        selectedBookingId = (int) tableModel.getValueAt(row, 0);
        try {
            Booking b = bookingService.getBookingById(selectedBookingId);
            if (b != null) {
                for (int i = 0; i < residentCombo.getItemCount(); i++) {
                    if (residentCombo.getItemAt(i).startsWith(b.getResidentId() + " -")) {
                        residentCombo.setSelectedIndex(i); break;
                    }
                }
                for (int i = 0; i < roomCombo.getItemCount(); i++) {
                    if (roomCombo.getItemAt(i).startsWith(b.getRoomId() + " -")) {
                        roomCombo.setSelectedIndex(i); break;
                    }
                }
                statusCombo.setSelectedItem(b.getStatus());
                if (b.getBookingDate() != null) bookingDateSpinner.setValue(new java.util.Date(b.getBookingDate().getTime()));
                if (b.getCheckInDate() != null) checkInSpinner.setValue(new java.util.Date(b.getCheckInDate().getTime()));
                if (b.getCheckOutDate() != null) checkOutSpinner.setValue(new java.util.Date(b.getCheckOutDate().getTime()));
                amountField.setText(b.getTotalAmount() != null ? b.getTotalAmount().toString() : "");
                notesField.setText(b.getNotes() != null ? b.getNotes() : "");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading booking details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBooking(ActionEvent e) {
        if (!ValidationUtil.requireComboSelection(this, "Resident", residentCombo)) return;
        if (!ValidationUtil.requireComboSelection(this, "Room", roomCombo)) return;

        Booking b = buildBookingFromForm();
        if (!ValidationUtil.validateDateRange(this, b.getCheckInDate(), b.getCheckOutDate())) return;
        if (isRoomFull(b.getRoomId(), null)) return;

        try {
            bookingService.addBooking(b);
            loadBookings();
            clearForm();
            JOptionPane.showMessageDialog(this, "Booking created successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBooking(ActionEvent e) {
        if (selectedBookingId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!ValidationUtil.requireComboSelection(this, "Resident", residentCombo)) return;
        if (!ValidationUtil.requireComboSelection(this, "Room", roomCombo)) return;

        Booking b = buildBookingFromForm();
        b.setId(selectedBookingId);
        if (!ValidationUtil.validateDateRange(this, b.getCheckInDate(), b.getCheckOutDate())) return;
        if (isRoomFull(b.getRoomId(), selectedBookingId)) return;

        try {
            bookingService.updateBooking(b);
            loadBookings();
            clearForm();
            JOptionPane.showMessageDialog(this, "Booking updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmBooking(ActionEvent e) {
        if (selectedBookingId == -1) return;
        try {
            bookingService.confirmBooking(selectedBookingId);
            loadBookings();
            JOptionPane.showMessageDialog(this, "Booking confirmed!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking(ActionEvent e) {
        if (selectedBookingId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookingService.cancelBooking(selectedBookingId);
                loadBookings();
                JOptionPane.showMessageDialog(this, "Booking cancelled!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Booking buildBookingFromForm() {
        Booking b = new Booking();
        String resStr = (String) residentCombo.getSelectedItem();
        String roomStr = (String) roomCombo.getSelectedItem();
        if (resStr != null && !resStr.isEmpty()) {
            b.setResidentId(Integer.parseInt(resStr.split(" - ")[0].trim()));
        }
        if (roomStr != null && !roomStr.isEmpty()) {
            b.setRoomId(Integer.parseInt(roomStr.split(" - ")[0].trim()));
        }
        b.setStatus((String) statusCombo.getSelectedItem());
        b.setBookingDate(new Date(((java.util.Date) bookingDateSpinner.getValue()).getTime()));
        b.setCheckInDate(new Date(((java.util.Date) checkInSpinner.getValue()).getTime()));
        b.setCheckOutDate(new Date(((java.util.Date) checkOutSpinner.getValue()).getTime()));
        if (!amountField.getText().trim().isEmpty()) {
            b.setTotalAmount(new BigDecimal(amountField.getText().trim()));
        }
        b.setNotes(notesField.getText().trim());
        return b;
    }

    private void clearForm() {
        selectedBookingId = -1;
        statusCombo.setSelectedIndex(0);
        amountField.setText("");
        notesField.setText("");
        bookingDateSpinner.setValue(new java.util.Date());
        checkInSpinner.setValue(new java.util.Date());
        checkOutSpinner.setValue(new java.util.Date());
        bookingTable.clearSelection();
        refreshCombos();
    }
}
