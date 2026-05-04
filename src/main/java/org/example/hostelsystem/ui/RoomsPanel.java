package ui;

import theme.ModernTheme;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class RoomsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    // Form fields
    private JTextField fRoomNo, fType, fCapacity, fPrice, fFloor, fDesc;
    private JComboBox<String> fStatus;

    private static final String[] COLS = {"ID","Room No","Type","Capacity","Price/Month","Status","Floor","Description"};
    private static final Object[][] SAMPLE_DATA = {
        {1,"101","Single",1,"9,000","AVAILABLE",1,"AC, WiFi, Personal Washroom, Study Table"},
        {2,"102","Single",1,"9,000","AVAILABLE",1,"AC, WiFi, Personal Washroom, Study Table"},
        {3,"103","Single",1,"7,000","AVAILABLE",1,"Non-AC, WiFi, Shared Washroom, Study Table"},
        {4,"104","Double",2,"8,000","AVAILABLE",1,"AC, WiFi, Personal Washroom, 2 Study Tables"},
        {5,"105","Double",2,"6,500","AVAILABLE",1,"Non-AC, WiFi, Shared Washroom, 2 Study Tables"},
        {6,"106","Triple",3,"5,500","AVAILABLE",1,"Non-AC, WiFi, Shared Washroom, 3 Beds"},
        {7,"107","Triple",3,"7,000","AVAILABLE",1,"AC, WiFi, Shared Washroom, 3 Study Tables"},
        {8,"108","Single",1,"9,500","AVAILABLE",1,"Premium AC, WiFi, Personal Washroom"},
        {9,"201","Single",1,"9,000","AVAILABLE",2,"AC, WiFi, Personal Washroom, Study Table"},
    };

    public RoomsPanel() {
        setLayout(new BorderLayout());
        setBackground(ModernTheme.BG_DARK);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        JLabel title = new JLabel("Rooms");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("  Manage hostel rooms");
        subtitle.setFont(ModernTheme.FONT_LABEL);
        subtitle.setForeground(ModernTheme.TEXT_MUTED);
        left.add(title); left.add(subtitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        searchField = ModernTheme.searchField("🔍  Search rooms...");
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        right.add(searchField);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildCenter() {
        tableModel = new DefaultTableModel(SAMPLE_DATA, COLS) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        ModernTheme.styleTable(table);

        // Status column with badge renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                JLabel badge = ModernTheme.statusBadge(String.valueOf(value));
                badge.setBackground(sel ? new Color(31,111,235,80) : (row%2==0 ? ModernTheme.BG_DARK : new Color(22,27,34)));
                badge.setOpaque(true);
                return badge;
            }
        });

        // Column widths
        int[] widths = {50, 80, 80, 70, 100, 110, 60, 300};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) populateForm();
        });

        JScrollPane sp = ModernTheme.scrollPane(table);
        sp.setPreferredSize(new Dimension(0, 280));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(sp);
        return wrap;
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel card = ModernTheme.card();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel sectionTitle = new JLabel("Room Details");
        sectionTitle.setFont(ModernTheme.FONT_SUBHEAD);
        sectionTitle.setForeground(ModernTheme.TEXT_SECONDARY);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel fields = new JPanel(new GridLayout(3, 4, 14, 10));
        fields.setOpaque(false);

        fRoomNo   = addField(fields, "Room Number");
        fType     = addField(fields, "Room Type");
        fCapacity = addField(fields, "Capacity");
        fPrice    = addField(fields, "Price / Month");
        fFloor    = addField(fields, "Floor");

        // Status combo
        JPanel statusWrap = new JPanel(new BorderLayout(0, 4));
        statusWrap.setOpaque(false);
        JLabel sl = ModernTheme.label("Status");
        fStatus = ModernTheme.createComboBox(new String[]{"AVAILABLE","OCCUPIED","RESERVED","MAINTENANCE"});
        fStatus.setPreferredSize(new Dimension(0, 36));
        statusWrap.add(sl, BorderLayout.NORTH);
        statusWrap.add(fStatus, BorderLayout.CENTER);
        fields.add(statusWrap);

        // Description spans full width
        JPanel descRow = new JPanel(new GridLayout(1, 2, 14, 0));
        descRow.setOpaque(false);
        JPanel descWrap = new JPanel(new BorderLayout(0, 4));
        descWrap.setOpaque(false);
        descWrap.add(ModernTheme.label("Description"), BorderLayout.NORTH);
        fDesc = ModernTheme.createTextField();
        descWrap.add(fDesc, BorderLayout.CENTER);
        descRow.add(descWrap);
        descRow.add(new JLabel()); // spacer

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);
        btns.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton addBtn    = ModernTheme.successButton("＋  Add Room");
        JButton updateBtn = ModernTheme.primaryButton("✎  Update");
        JButton deleteBtn = ModernTheme.dangerButton("✕  Delete");
        JButton clearBtn  = ModernTheme.secondaryButton("Clear");

        addBtn.addActionListener(e -> addRoom());
        updateBtn.addActionListener(e -> updateRoom());
        deleteBtn.addActionListener(e -> deleteRoom());
        clearBtn.addActionListener(e -> clearForm());

        btns.add(addBtn); btns.add(updateBtn); btns.add(deleteBtn); btns.add(clearBtn);

        JPanel formContent = new JPanel(new BorderLayout(0, 10));
        formContent.setOpaque(false);
        formContent.add(fields, BorderLayout.NORTH);
        formContent.add(descRow, BorderLayout.CENTER);
        formContent.add(btns, BorderLayout.SOUTH);

        card.add(sectionTitle, BorderLayout.NORTH);
        card.add(formContent, BorderLayout.CENTER);
        outer.add(card);
        return outer;
    }

    private JTextField addField(JPanel parent, String labelText) {
        JPanel wrap = new JPanel(new BorderLayout(0, 4));
        wrap.setOpaque(false);
        wrap.add(ModernTheme.label(labelText), BorderLayout.NORTH);
        JTextField tf = ModernTheme.createTextField();
        wrap.add(tf, BorderLayout.CENTER);
        parent.add(wrap);
        return tf;
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        fRoomNo.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        fType.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        fCapacity.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        fPrice.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        fStatus.setSelectedItem(tableModel.getValueAt(row, 5));
        fFloor.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        fDesc.setText(String.valueOf(tableModel.getValueAt(row, 7)));
    }

    private void addRoom() {
        if (fRoomNo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room number is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{id, fRoomNo.getText(), fType.getText(),
            fCapacity.getText(), fPrice.getText(), fStatus.getSelectedItem(),
            fFloor.getText(), fDesc.getText()});
        clearForm();
    }

    private void updateRoom() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        tableModel.setValueAt(fRoomNo.getText(), row, 1);
        tableModel.setValueAt(fType.getText(), row, 2);
        tableModel.setValueAt(fCapacity.getText(), row, 3);
        tableModel.setValueAt(fPrice.getText(), row, 4);
        tableModel.setValueAt(fStatus.getSelectedItem(), row, 5);
        tableModel.setValueAt(fFloor.getText(), row, 6);
        tableModel.setValueAt(fDesc.getText(), row, 7);
    }

    private void deleteRoom() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete room " + tableModel.getValueAt(row, 1) + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) { tableModel.removeRow(row); clearForm(); }
    }

    private void clearForm() {
        fRoomNo.setText(""); fType.setText(""); fCapacity.setText("");
        fPrice.setText(""); fFloor.setText(""); fDesc.setText("");
        fStatus.setSelectedIndex(0);
        table.clearSelection();
    }

    private void filterTable() {
        String query = searchField.getText().toLowerCase();
        if (query.equals("🔍  search rooms...")) query = "";
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        final String q = query;
        sorter.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + q));
    }
}
