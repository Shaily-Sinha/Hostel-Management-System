package ui;

import theme.ModernTheme;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class ResidentsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    // Form fields
    private JTextField fName, fEmail, fPhone, fDob, fAddress, fEmergency, fIdProof;
    private JComboBox<String> fGender, fRoom, fStatus;

    private static final String[] COLS = {"ID","Name","Email","Phone","Room","Check-In","Status"};
    private static final Object[][] SAMPLE_DATA = {
        {1,  "Aarav Sharma",  "aarav@email.com",   "9876543210", "1", "2026-04-01", "ACTIVE"},
        {13, "Abhishek",      "atyagibdn@gmail.com","9259450800", "9", "2026-05-03", "ACTIVE"},
        {4,  "Aditya Singh",  "aditya@email.com",  "9876543213", "4", "2026-04-02", "ACTIVE"},
        {7,  "Arjun Nair",    "arjun@email.com",   "9876543216", "5", "2026-04-04", "ACTIVE"},
        {10, "Ayaan Khan",    "ayaan@email.com",   "9876543219", "8", "2026-04-05", "ACTIVE"},
        {8,  "Dhruv Kumar",   "dhruv@email.com",   "9876543217", "6", "2026-04-04", "ACTIVE"},
        {3,  "Ishaan Gupta",  "ishaan@email.com",  "9876543212", "3", "2026-04-02", "ACTIVE"},
        {5,  "Krishna Rao",   "krishna@email.com", "9876543214", "4", "2026-04-03", "ACTIVE"},
    };

    public ResidentsPanel() {
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
        JLabel title = new JLabel("Residents");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ModernTheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("  Manage hostel residents");
        subtitle.setFont(ModernTheme.FONT_LABEL);
        subtitle.setForeground(ModernTheme.TEXT_MUTED);
        left.add(title); left.add(subtitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        searchField = ModernTheme.searchField("🔍  Search residents...");
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

        // Status badge renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                JLabel badge = ModernTheme.statusBadge(String.valueOf(value));
                badge.setBackground(sel ? new Color(31,111,235,80) : (row%2==0 ? ModernTheme.BG_DARK : new Color(22,27,34)));
                badge.setOpaque(true);
                return badge;
            }
        });

        int[] widths = {50, 150, 180, 120, 60, 110, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) populateForm();
        });

        JScrollPane sp = ModernTheme.scrollPane(table);
        sp.setPreferredSize(new Dimension(0, 240));

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

        JLabel sectionTitle = new JLabel("Resident Details");
        sectionTitle.setFont(ModernTheme.FONT_SUBHEAD);
        sectionTitle.setForeground(ModernTheme.TEXT_SECONDARY);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel fields = new JPanel(new GridLayout(3, 4, 14, 10));
        fields.setOpaque(false);

        fName      = addField(fields, "Full Name");
        fEmail     = addField(fields, "Email");
        fPhone     = addField(fields, "Phone");

        // Gender combo
        JPanel gWrap = new JPanel(new BorderLayout(0, 4));
        gWrap.setOpaque(false);
        gWrap.add(ModernTheme.label("Gender"), BorderLayout.NORTH);
        fGender = ModernTheme.createComboBox(new String[]{"Male","Female","Other"});
        gWrap.add(fGender, BorderLayout.CENTER);
        fields.add(gWrap);

        fDob       = addField(fields, "Date of Birth");
        fAddress   = addField(fields, "Address");
        fEmergency = addField(fields, "Emergency Contact");
        fIdProof   = addField(fields, "ID Proof");

        // Room combo
        JPanel rWrap = new JPanel(new BorderLayout(0, 4));
        rWrap.setOpaque(false);
        rWrap.add(ModernTheme.label("Room"), BorderLayout.NORTH);
        fRoom = ModernTheme.createComboBox(new String[]{"None","1","2","3","4","5","6","7","8","9"});
        rWrap.add(fRoom, BorderLayout.CENTER);
        fields.add(rWrap);

        // Status combo
        JPanel sWrap = new JPanel(new BorderLayout(0, 4));
        sWrap.setOpaque(false);
        sWrap.add(ModernTheme.label("Status"), BorderLayout.NORTH);
        fStatus = ModernTheme.createComboBox(new String[]{"ACTIVE","INACTIVE","PENDING"});
        sWrap.add(fStatus, BorderLayout.CENTER);
        fields.add(sWrap);

        fields.add(new JLabel()); // spacer
        fields.add(new JLabel()); // spacer

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);
        btns.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton addBtn    = ModernTheme.successButton("＋  Add Resident");
        JButton updateBtn = ModernTheme.primaryButton("✎  Update");
        JButton deleteBtn = ModernTheme.dangerButton("✕  Delete");
        JButton clearBtn  = ModernTheme.secondaryButton("Clear");

        addBtn.addActionListener(e -> addResident());
        updateBtn.addActionListener(e -> updateResident());
        deleteBtn.addActionListener(e -> deleteResident());
        clearBtn.addActionListener(e -> clearForm());

        btns.add(addBtn); btns.add(updateBtn); btns.add(deleteBtn); btns.add(clearBtn);

        JPanel formContent = new JPanel(new BorderLayout(0, 10));
        formContent.setOpaque(false);
        formContent.add(fields, BorderLayout.CENTER);
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
        fName.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        fEmail.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        fPhone.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        fRoom.setSelectedItem(tableModel.getValueAt(row, 4));
        fStatus.setSelectedItem(tableModel.getValueAt(row, 6));
    }

    private void addResident() {
        if (fName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{id, fName.getText(), fEmail.getText(),
            fPhone.getText(), fRoom.getSelectedItem(), java.time.LocalDate.now().toString(),
            fStatus.getSelectedItem()});
        clearForm();
    }

    private void updateResident() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a resident first."); return; }
        tableModel.setValueAt(fName.getText(), row, 1);
        tableModel.setValueAt(fEmail.getText(), row, 2);
        tableModel.setValueAt(fPhone.getText(), row, 3);
        tableModel.setValueAt(fRoom.getSelectedItem(), row, 4);
        tableModel.setValueAt(fStatus.getSelectedItem(), row, 6);
    }

    private void deleteResident() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a resident first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete resident " + tableModel.getValueAt(row, 1) + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) { tableModel.removeRow(row); clearForm(); }
    }

    private void clearForm() {
        fName.setText(""); fEmail.setText(""); fPhone.setText("");
        fDob.setText(""); fAddress.setText(""); fEmergency.setText(""); fIdProof.setText("");
        fGender.setSelectedIndex(0); fRoom.setSelectedIndex(0); fStatus.setSelectedIndex(0);
        table.clearSelection();
    }

    private void filterTable() {
        String query = searchField.getText().toLowerCase();
        if (query.equals("🔍  search residents...")) query = "";
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        final String q = query;
        sorter.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?i)" + q));
    }
}
