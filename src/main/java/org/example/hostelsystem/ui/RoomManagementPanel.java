package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.Room;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.RoomService;
import org.example.hostelsystem.ui.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class RoomManagementPanel extends JPanel {

    private final RoomService roomService = new RoomService();
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField roomNumberField, roomTypeField, capacityField, priceField, floorField, descField;
    private JComboBox<String> statusCombo;
    private int selectedRoomId = -1;

    public RoomManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initTable();
        initForm();
        loadRooms();
    }

    private void initTable() {
        String[] columns = {"ID", "Room No", "Type", "Capacity", "Price/Month", "Status", "Floor", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        roomTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        roomTable.setRowSorter(sorter);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        roomTable.setRowHeight(25);
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && roomTable.getSelectedRow() != -1) {
                populateFormFromSelection();
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField(20);
        searchField.setToolTipText("Search by room number or type...");
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
        tablePanel.add(new JScrollPane(roomTable), BorderLayout.CENTER);
        tablePanel.setPreferredSize(new Dimension(0, 280));
        add(tablePanel, BorderLayout.NORTH);
    }

    private void initForm() {
        boolean canEdit = !AuthService.isWarden();

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Room Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        roomNumberField = new JTextField(12);
        roomTypeField = new JTextField(12);
        capacityField = new JTextField(12);
        priceField = new JTextField(12);
        floorField = new JTextField(12);
        descField = new JTextField(20);
        statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE"});

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Room Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roomNumberField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Room Type:"), gbc);
        gbc.gridx = 3;
        formPanel.add(roomTypeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        formPanel.add(capacityField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Price/Month:"), gbc);
        gbc.gridx = 3;
        formPanel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Floor:"), gbc);
        gbc.gridx = 1;
        formPanel.add(floorField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        formPanel.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(descField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addBtn = new JButton("Add Room");
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

        addBtn.addActionListener(this::addRoom);
        updateBtn.addActionListener(this::updateRoom);
        deleteBtn.addActionListener(this::deleteRoom);
        clearBtn.addActionListener(e -> clearForm());

        if (canEdit) buttonPanel.add(addBtn);
        if (canEdit) buttonPanel.add(updateBtn);
        if (canEdit) buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void loadRooms() {
        tableModel.setRowCount(0);
        try {
            List<Room> rooms = roomService.getAllRooms();
            for (Room room : rooms) {
                tableModel.addRow(new Object[]{
                    room.getId(), room.getRoomNumber(), room.getRoomType(),
                    room.getCapacity(), room.getPricePerMonth(), room.getStatus(),
                    room.getFloor(), room.getDescription()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromSelection() {
        int row = roomTable.getSelectedRow();
        selectedRoomId = (int) tableModel.getValueAt(row, 0);
        roomNumberField.setText((String) tableModel.getValueAt(row, 1));
        roomTypeField.setText((String) tableModel.getValueAt(row, 2));
        capacityField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        priceField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        statusCombo.setSelectedItem(tableModel.getValueAt(row, 5));
        floorField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        descField.setText((String) tableModel.getValueAt(row, 7));
    }

    private void addRoom(ActionEvent e) {
        String roomNumber = roomNumberField.getText().trim();
        String roomType = roomTypeField.getText().trim();
        String capacity = capacityField.getText().trim();
        String price = priceField.getText().trim();
        String floor = floorField.getText().trim();

        if (!ValidationUtil.requireNonEmpty(this, "Room Number", roomNumber)) return;
        if (!ValidationUtil.requireNonEmpty(this, "Room Type", roomType)) return;
        if (!ValidationUtil.requirePositiveInt(this, "Capacity", capacity)) return;
        if (!ValidationUtil.requirePositiveDecimal(this, "Price", price)) return;
        if (!ValidationUtil.requirePositiveInt(this, "Floor", floor)) return;

        try {
            Room room = new Room();
            room.setRoomNumber(roomNumber);
            room.setRoomType(roomType);
            room.setCapacity(Integer.parseInt(capacity));
            room.setPricePerMonth(new BigDecimal(price));
            room.setFloor(Integer.parseInt(floor));
            room.setStatus((String) statusCombo.getSelectedItem());
            room.setDescription(descField.getText().trim());
            roomService.addRoom(room);
            loadRooms();
            clearForm();
            JOptionPane.showMessageDialog(this, "Room added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRoom(ActionEvent e) {
        if (selectedRoomId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String roomNumber = roomNumberField.getText().trim();
        String roomType = roomTypeField.getText().trim();
        String capacity = capacityField.getText().trim();
        String price = priceField.getText().trim();
        String floor = floorField.getText().trim();

        if (!ValidationUtil.requireNonEmpty(this, "Room Number", roomNumber)) return;
        if (!ValidationUtil.requireNonEmpty(this, "Room Type", roomType)) return;
        if (!ValidationUtil.requirePositiveInt(this, "Capacity", capacity)) return;
        if (!ValidationUtil.requirePositiveDecimal(this, "Price", price)) return;
        if (!ValidationUtil.requirePositiveInt(this, "Floor", floor)) return;

        try {
            Room room = new Room();
            room.setId(selectedRoomId);
            room.setRoomNumber(roomNumber);
            room.setRoomType(roomType);
            room.setCapacity(Integer.parseInt(capacity));
            room.setPricePerMonth(new BigDecimal(price));
            room.setFloor(Integer.parseInt(floor));
            room.setStatus((String) statusCombo.getSelectedItem());
            room.setDescription(descField.getText().trim());
            roomService.updateRoom(room);
            loadRooms();
            clearForm();
            JOptionPane.showMessageDialog(this, "Room updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRoom(ActionEvent e) {
        if (selectedRoomId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this room?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                roomService.deleteRoom(selectedRoomId);
                loadRooms();
                clearForm();
                JOptionPane.showMessageDialog(this, "Room deleted successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedRoomId = -1;
        roomNumberField.setText("");
        roomTypeField.setText("");
        capacityField.setText("");
        priceField.setText("");
        floorField.setText("");
        descField.setText("");
        statusCombo.setSelectedIndex(0);
        roomTable.clearSelection();
    }
}
