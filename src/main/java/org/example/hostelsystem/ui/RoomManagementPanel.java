package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.Room;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.RoomService;
import org.example.hostelsystem.ui.util.ModernTheme;
import org.example.hostelsystem.ui.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
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
        setLayout(new BorderLayout(0, 0));
        setBackground(ModernTheme.BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildTable(),     BorderLayout.CENTER);
        add(buildFormCard(),  BorderLayout.SOUTH);

        loadRooms();
    }

    // ── Header ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(ModernTheme.BG_DARK);
        p.setBorder(new EmptyBorder(0, 0, 14, 0));

        p.add(ModernTheme.panelHeader("Rooms", "Manage hostel rooms"), BorderLayout.WEST);

        // search field on right
        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightBar.setOpaque(false);
        JTextField searchField = ModernTheme.searchField("🔍  Search rooms...");
        searchField.setPreferredSize(new Dimension(220, 34));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filter(searchField); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filter(searchField); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(searchField); }
        });
        rightBar.add(searchField);
        p.add(rightBar, BorderLayout.EAST);
        return p;
    }

    private void filter(JTextField searchField) {
        String text = searchField.getText().trim();
        boolean isPlaceholder = text.equals("🔍  Search rooms...");
        if (text.isEmpty() || isPlaceholder) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
    }

    // ── Table ──────────────────────────────────────────────────────────────
    private JPanel buildTable() {
        String[] columns = {"ID", "Room No", "Type", "Capacity", "Price/Month", "Status", "Floor", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        roomTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        roomTable.setRowSorter(sorter);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernTheme.styleTable(roomTable);

        // Status badge on column 5
        roomTable.getColumnModel().getColumn(5).setCellRenderer(ModernTheme.statusRenderer());

        // Column widths
        int[] widths = {50, 80, 80, 70, 110, 110, 60, 0};
        for (int i = 0; i < widths.length; i++)
            if (widths[i] > 0) roomTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && roomTable.getSelectedRow() != -1)
                populateFormFromSelection();
        });

        JScrollPane sp = ModernTheme.scrollPane(roomTable);
        sp.setPreferredSize(new Dimension(0, 270));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(ModernTheme.BG_DARK);
        wrap.add(sp);
        return wrap;
    }

    // ── Form card ──────────────────────────────────────────────────────────
    private JPanel buildFormCard() {
        boolean canEdit = !AuthService.isWarden();

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(ModernTheme.BG_DARK);
        outer.setBorder(new EmptyBorder(14, 0, 0, 0));

        JPanel card = ModernTheme.card();
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel sectionLbl = new JLabel("Room Details");
        sectionLbl.setFont(ModernTheme.FONT_SUBHEAD);
        sectionLbl.setForeground(ModernTheme.TEXT_SECONDARY);

        // fields grid
        JPanel fields = new JPanel(new GridLayout(2, 4, 12, 10));
        fields.setOpaque(false);

        roomNumberField = addFieldTo(fields, "Room Number");
        roomTypeField   = addFieldTo(fields, "Room Type");
        capacityField   = addFieldTo(fields, "Capacity");
        priceField      = addFieldTo(fields, "Price / Month");
        floorField      = addFieldTo(fields, "Floor");

        // status combo
        JPanel statusWrap = labeledCombo("Status");
        statusCombo = ModernTheme.comboBox(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE"});
        statusWrap.add(statusCombo, BorderLayout.CENTER);
        fields.add(statusWrap);

        // description (spans across)
        JPanel descRow = new JPanel(new GridLayout(1, 2, 12, 0));
        descRow.setOpaque(false);
        JPanel descWrap = new JPanel(new BorderLayout(0, 4));
        descWrap.setOpaque(false);
        descWrap.add(ModernTheme.label("Description"), BorderLayout.NORTH);
        descField = ModernTheme.textField();
        descWrap.add(descField, BorderLayout.CENTER);
        descRow.add(descWrap);
        descRow.add(new JLabel()); // spacer

        // buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        if (canEdit) {
            JButton addBtn    = ModernTheme.successButton("＋  Add Room");
            JButton updateBtn = ModernTheme.primaryButton("✎  Update");
            JButton deleteBtn = ModernTheme.dangerButton("✕  Delete");
            addBtn.addActionListener(this::addRoom);
            updateBtn.addActionListener(this::updateRoom);
            deleteBtn.addActionListener(this::deleteRoom);
            btnRow.add(addBtn); btnRow.add(updateBtn); btnRow.add(deleteBtn);
        }
        JButton clearBtn = ModernTheme.secondaryButton("Clear");
        clearBtn.addActionListener(e -> clearForm());
        btnRow.add(clearBtn);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);
        content.add(fields,  BorderLayout.NORTH);
        content.add(descRow, BorderLayout.CENTER);
        content.add(btnRow,  BorderLayout.SOUTH);

        card.add(sectionLbl, BorderLayout.NORTH);
        card.add(content,    BorderLayout.CENTER);
        outer.add(card);
        return outer;
    }

    private JTextField addFieldTo(JPanel parent, String labelText) {
        JPanel wrap = new JPanel(new BorderLayout(0, 4));
        wrap.setOpaque(false);
        wrap.add(ModernTheme.label(labelText), BorderLayout.NORTH);
        JTextField tf = ModernTheme.textField();
        wrap.add(tf, BorderLayout.CENTER);
        parent.add(wrap);
        return tf;
    }

    private JPanel labeledCombo(String labelText) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(ModernTheme.label(labelText), BorderLayout.NORTH);
        return p;
    }

    // ── Data ops (unchanged logic) ────────────────────────────────────────
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
        roomNumberField.setText((String)  tableModel.getValueAt(row, 1));
        roomTypeField.setText((String)    tableModel.getValueAt(row, 2));
        capacityField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        priceField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        statusCombo.setSelectedItem(      tableModel.getValueAt(row, 5));
        floorField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        descField.setText((String)        tableModel.getValueAt(row, 7));
    }

    private void addRoom(ActionEvent e) {
        String roomNumber = roomNumberField.getText().trim();
        String roomType   = roomTypeField.getText().trim();
        String capacity   = capacityField.getText().trim();
        String price      = priceField.getText().trim();
        String floor      = floorField.getText().trim();

        if (!ValidationUtil.requireNonEmpty(this, "Room Number", roomNumber)) return;
        if (!ValidationUtil.requireNonEmpty(this, "Room Type",   roomType))   return;
        if (!ValidationUtil.requirePositiveInt(this, "Capacity", capacity))   return;
        if (!ValidationUtil.requirePositiveDecimal(this, "Price", price))     return;
        if (!ValidationUtil.requirePositiveInt(this, "Floor",    floor))      return;

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
        String roomType   = roomTypeField.getText().trim();
        String capacity   = capacityField.getText().trim();
        String price      = priceField.getText().trim();
        String floor      = floorField.getText().trim();

        if (!ValidationUtil.requireNonEmpty(this, "Room Number", roomNumber)) return;
        if (!ValidationUtil.requireNonEmpty(this, "Room Type",   roomType))   return;
        if (!ValidationUtil.requirePositiveInt(this, "Capacity", capacity))   return;
        if (!ValidationUtil.requirePositiveDecimal(this, "Price", price))     return;
        if (!ValidationUtil.requirePositiveInt(this, "Floor",    floor))      return;

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
        roomNumberField.setText(""); roomTypeField.setText(""); capacityField.setText("");
        priceField.setText(""); floorField.setText(""); descField.setText("");
        statusCombo.setSelectedIndex(0);
        roomTable.clearSelection();
    }
}
