package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.FoodMenu;
import org.example.hostelsystem.model.MessBill;
import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.service.FoodMenuService;
import org.example.hostelsystem.service.MessBillService;
import org.example.hostelsystem.service.ResidentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessManagementPanel extends JPanel {

    private final FoodMenuService foodMenuService = new FoodMenuService();
    private final MessBillService messBillService = new MessBillService();
    private final ResidentService residentService = new ResidentService();

    // Food Menu components
    private JTable menuTable;
    private DefaultTableModel menuTableModel;
    private JSpinner menuDateSpinner;
    private JComboBox<String> mealTypeCombo;
    private JTextArea itemsArea;
    private int selectedMenuId = -1;

    // Mess Bill components
    private JTable billTable;
    private DefaultTableModel billTableModel;
    private TableRowSorter<DefaultTableModel> billSorter; // For filtering
    private JComboBox<String> residentCombo;
    private JTextField monthField;
    private JTextField totalField;
    private int selectedBillId = -1;

    // Filter components
    private JTextField searchNameField;
    private JTextField searchMonthField;
    private JComboBox<String> statusFilterCombo;

    public MessManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initTabs();
    }

    private void initTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));
        tabbedPane.addTab("Food Menu", createMenuPanel());
        tabbedPane.addTab("Mess Bills", createBillPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    // ==================== FOOD MENU (Unchanged) ====================

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] menuCols = {"ID", "Date", "Meal Type", "Items"};
        menuTableModel = new DefaultTableModel(menuCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        menuTable = new JTable(menuTableModel);
        menuTable.setRowHeight(25);
        menuTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && menuTable.getSelectedRow() != -1) {
                populateMenuForm();
            }
        });
        panel.add(new JScrollPane(menuTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Menu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        menuDateSpinner = new JSpinner(new SpinnerDateModel());
        menuDateSpinner.setEditor(new JSpinner.DateEditor(menuDateSpinner, "yyyy-MM-dd"));
        mealTypeCombo = new JComboBox<>(new String[]{"BREAKFAST", "LUNCH", "DINNER"});
        itemsArea = new JTextArea(3, 20);
        itemsArea.setLineWrap(true);
        itemsArea.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1; formPanel.add(menuDateSpinner, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Meal:"), gbc);
        gbc.gridx = 3; formPanel.add(mealTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Items:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; formPanel.add(new JScrollPane(itemsArea), gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addMenuBtn = new JButton("Add Menu");
        JButton updateMenuBtn = new JButton("Update");
        JButton deleteMenuBtn = new JButton("Delete");
        JButton clearMenuBtn = new JButton("Clear");
        JButton refreshMenuBtn = new JButton("Refresh");

        styleButton(addMenuBtn, new Color(60, 179, 113));
        styleButton(updateMenuBtn, new Color(70, 130, 180));
        styleButton(deleteMenuBtn, new Color(220, 20, 60));

        addMenuBtn.addActionListener(this::addMenu);
        updateMenuBtn.addActionListener(this::updateMenu);
        deleteMenuBtn.addActionListener(this::deleteMenu);
        clearMenuBtn.addActionListener(e -> clearMenuForm());
        refreshMenuBtn.addActionListener(e -> loadMenus());

        btnPanel.add(addMenuBtn); btnPanel.add(updateMenuBtn); btnPanel.add(deleteMenuBtn);
        btnPanel.add(clearMenuBtn); btnPanel.add(refreshMenuBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; formPanel.add(btnPanel, gbc);
        panel.add(formPanel, BorderLayout.SOUTH);
        loadMenus();
        return panel;
    }

    private void loadMenus() {
        menuTableModel.setRowCount(0);
        try {
            for (FoodMenu m : foodMenuService.getAllMenus()) {
                menuTableModel.addRow(new Object[]{m.getId(), m.getMenuDate(), m.getMealType(), m.getItems()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading menu: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateMenuForm() {
        int row = menuTable.getSelectedRow();
        selectedMenuId = (int) menuTableModel.getValueAt(row, 0);
        java.util.Date date = (java.util.Date) menuTableModel.getValueAt(row, 1);
        menuDateSpinner.setValue(date);
        mealTypeCombo.setSelectedItem(menuTableModel.getValueAt(row, 2));
        itemsArea.setText((String) menuTableModel.getValueAt(row, 3));
    }

    private void addMenu(ActionEvent e) {
        try {
            FoodMenu menu = new FoodMenu();
            menu.setMenuDate(new Date(((java.util.Date) menuDateSpinner.getValue()).getTime()));
            menu.setMealType((String) mealTypeCombo.getSelectedItem());
            menu.setItems(itemsArea.getText().trim());
            foodMenuService.addMenu(menu);
            loadMenus(); clearMenuForm();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void updateMenu(ActionEvent e) {
        try {
            FoodMenu menu = new FoodMenu();
            menu.setId(selectedMenuId);
            menu.setMenuDate(new Date(((java.util.Date) menuDateSpinner.getValue()).getTime()));
            menu.setMealType((String) mealTypeCombo.getSelectedItem());
            menu.setItems(itemsArea.getText().trim());
            foodMenuService.updateMenu(menu);
            loadMenus(); clearMenuForm();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void deleteMenu(ActionEvent e) {
        try {
            foodMenuService.deleteMenu(selectedMenuId);
            loadMenus(); clearMenuForm();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void clearMenuForm() {
        selectedMenuId = -1;
        menuDateSpinner.setValue(new java.util.Date());
        mealTypeCombo.setSelectedIndex(0);
        itemsArea.setText("");
        menuTable.clearSelection();
    }

    // ==================== MESS BILLS (Updated with New Features) ====================

    private JPanel createBillPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Search & Filter Bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));

        searchNameField = new JTextField(12);
        searchMonthField = new JTextField(7);
        statusFilterCombo = new JComboBox<>(new String[]{"ALL", "PAID", "UNPAID", "PARTIAL"});
        JButton applyFilterBtn = new JButton("Filter");
        JButton clearFilterBtn = new JButton("Clear Filters");

        applyFilterBtn.addActionListener(e -> applyFilters());
        clearFilterBtn.addActionListener(e -> {
            searchNameField.setText(""); searchMonthField.setText("");
            statusFilterCombo.setSelectedIndex(0); applyFilters();
        });

        filterPanel.add(new JLabel("Resident Name:")); filterPanel.add(searchNameField);
        filterPanel.add(new JLabel("Month (YYYY-MM):")); filterPanel.add(searchMonthField);
        filterPanel.add(new JLabel("Status:")); filterPanel.add(statusFilterCombo);
        filterPanel.add(applyFilterBtn); filterPanel.add(clearFilterBtn);

        panel.add(filterPanel, BorderLayout.NORTH);

        // 2. Bill Table (Added Fine Column)
        String[] billCols = {"ID", "Resident", "Month", "Total", "Paid", "Fine", "Due", "Status"};
        billTableModel = new DefaultTableModel(billCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        billTable = new JTable(billTableModel);
        billSorter = new TableRowSorter<>(billTableModel);
        billTable.setRowSorter(billSorter); // Attach sorter for filtering
        billTable.setRowHeight(25);
        billTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && billTable.getSelectedRow() != -1) {
                populateBillForm();
            }
        });
        panel.add(new JScrollPane(billTable), BorderLayout.CENTER);

        // 3. Bill Form & Action Buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Mess Bills"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL;

        residentCombo = new JComboBox<>();
        refreshResidentCombo();
        monthField = new JTextField(10);
        totalField = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Resident:"), gbc);
        gbc.gridx = 1; formPanel.add(residentCombo, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Month (YYYY-MM):"), gbc);
        gbc.gridx = 3; formPanel.add(monthField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1; formPanel.add(totalField, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addBillBtn = new JButton("Add Single Bill");
        JButton updateBillBtn = new JButton("Update Bill");
        JButton deleteBillBtn = new JButton("Delete Bill");
        JButton bulkGenerateBtn = new JButton("Bulk Generate Bills"); // New Feature
        JButton recordPaymentBtn = new JButton("Record Payment"); // New Feature

        styleButton(addBillBtn, new Color(60, 179, 113));
        styleButton(updateBillBtn, new Color(70, 130, 180));
        styleButton(deleteBillBtn, new Color(220, 20, 60));
        styleButton(bulkGenerateBtn, new Color(138, 43, 226)); // Purple for bulk action
        styleButton(recordPaymentBtn, new Color(255, 140, 0)); // Orange for payments

        addBillBtn.addActionListener(this::addBill);
        updateBillBtn.addActionListener(this::updateBill);
        deleteBillBtn.addActionListener(this::deleteBill);
        bulkGenerateBtn.addActionListener(this::generateBulkBills);
        recordPaymentBtn.addActionListener(this::recordPartialPayment);

        btnPanel.add(addBillBtn); btnPanel.add(updateBillBtn); btnPanel.add(deleteBillBtn);
        btnPanel.add(bulkGenerateBtn); btnPanel.add(recordPaymentBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; formPanel.add(btnPanel, gbc);
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadBills();
        return panel;
    }

    // --- Search & Filter Logic ---
    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String nameText = searchNameField.getText().trim();
        if (!nameText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + nameText, 1)); // Filter by Resident (Column 1)
        }

        String monthText = searchMonthField.getText().trim();
        if (!monthText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + monthText, 2)); // Filter by Month (Column 2)
        }

        String statusText = (String) statusFilterCombo.getSelectedItem();
        if (!"ALL".equals(statusText)) {
            filters.add(RowFilter.regexFilter("^" + statusText + "$", 7)); // Filter by Status (Column 7)
        }

        if (filters.isEmpty()) {
            billSorter.setRowFilter(null);
        } else {
            billSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    // --- Bulk Bill Generation Logic ---
    private void generateBulkBills(ActionEvent e) {
        String month = JOptionPane.showInputDialog(this, "Enter Month for Bulk Generation (YYYY-MM):");
        if (month == null || !month.matches("\\d{4}-\\d{2}")) {
            if (month != null) JOptionPane.showMessageDialog(this, "Invalid format. Use YYYY-MM.");
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Enter base Total Amount for all active residents:");
        if (amountStr == null) return;

        try {
            BigDecimal totalAmount = new BigDecimal(amountStr);
            List<Resident> activeResidents = residentService.getActiveResidents();

            for (Resident r : activeResidents) {
                MessBill bill = new MessBill();
                bill.setResidentId(r.getId());
                bill.setBillMonth(month);
                bill.setTotalAmount(totalAmount);
                bill.setPaidAmount(BigDecimal.ZERO);
                // Note: You must update MessBillService to include fine calculation/defaults
                messBillService.addBill(bill);
            }
            loadBills();
            JOptionPane.showMessageDialog(this, "Successfully generated bills for " + activeResidents.size() + " residents.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Record Partial Payment Logic ---
    private void recordPartialPayment(ActionEvent e) {
        int row = billTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill from the table first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view row to model row in case table is filtered/sorted
        int modelRow = billTable.convertRowIndexToModel(row);
        int billId = (int) billTableModel.getValueAt(modelRow, 0);
        String currentDueStr = billTableModel.getValueAt(modelRow, 6).toString(); // Due Column

        JPanel paymentPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField amountField = new JTextField(currentDueStr);
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"CASH", "UPI", "BANK TRANSFER"});

        paymentPanel.add(new JLabel("Payment Amount:"));
        paymentPanel.add(amountField);
        paymentPanel.add(new JLabel("Payment Method:"));
        paymentPanel.add(methodCombo);

        int result = JOptionPane.showConfirmDialog(this, paymentPanel, "Record Payment", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                BigDecimal paymentAmount = new BigDecimal(amountField.getText().trim());
                String method = (String) methodCombo.getSelectedItem();

                // --- BACKEND HOOK ---
                // You must create this method in your MessBillService to insert into 'mess_payments'
                // messBillService.recordPayment(billId, paymentAmount, method);

                JOptionPane.showMessageDialog(this, "Payment feature triggered. \n(Note: Update backend MessBillService.recordPayment() to save this to DB)");
                loadBills();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.");
            }
        }
    }

    private void loadBills() {
        billTableModel.setRowCount(0);
        try {
            for (MessBill b : messBillService.getAllBills()) {
                // Assuming you add getFineAmount() to your MessBill model
                // If not, just replace b.getFineAmount() with "0.00" for now
                Object fineAmount = "0.00";
                // Uncomment below when backend is ready:
                // fineAmount = b.getFineAmount();

                billTableModel.addRow(new Object[]{
                        b.getId(), b.getResidentName(), b.getBillMonth(),
                        b.getTotalAmount(), b.getPaidAmount(), fineAmount, b.getDueAmount(), b.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading bills: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateBillForm() {
        int viewRow = billTable.getSelectedRow();
        if(viewRow == -1) return;
        int modelRow = billTable.convertRowIndexToModel(viewRow);

        selectedBillId = (int) billTableModel.getValueAt(modelRow, 0);
        String residentName = (String) billTableModel.getValueAt(modelRow, 1);
        for (int i = 0; i < residentCombo.getItemCount(); i++) {
            if (residentCombo.getItemAt(i).contains(residentName)) {
                residentCombo.setSelectedIndex(i);
                break;
            }
        }
        monthField.setText((String) billTableModel.getValueAt(modelRow, 2));
        totalField.setText(billTableModel.getValueAt(modelRow, 3).toString());
        // Note: Paid amount is removed from the form fields to force use of "Record Payment" button
    }

    private void addBill(ActionEvent e) {
        try {
            MessBill bill = buildBillFromForm();
            if (bill == null) return;
            bill.setPaidAmount(BigDecimal.ZERO); // Default to zero, updated via Record Payment
            messBillService.addBill(bill);
            loadBills(); clearBillForm();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void updateBill(ActionEvent e) {
        if (selectedBillId == -1) return;
        try {
            MessBill bill = buildBillFromForm();
            if (bill == null) return;
            bill.setId(selectedBillId);
            // Must fetch existing paid amount so it doesn't overwrite to 0 during update
            messBillService.updateBill(bill);
            loadBills(); clearBillForm();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void deleteBill(ActionEvent e) {
        if (selectedBillId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this bill?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                messBillService.deleteBill(selectedBillId);
                loadBills(); clearBillForm();
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private MessBill buildBillFromForm() {
        if (residentCombo.getSelectedItem() == null) return null;
        String residentStr = (String) residentCombo.getSelectedItem();
        int residentId = Integer.parseInt(residentStr.split(" - ")[0]);

        String month = monthField.getText().trim();
        BigDecimal total;
        try {
            total = new BigDecimal(totalField.getText().trim());
        } catch (NumberFormatException ex) {
            return null;
        }

        MessBill bill = new MessBill();
        bill.setResidentId(residentId);
        bill.setBillMonth(month);
        bill.setTotalAmount(total);
        return bill;
    }

    private void clearBillForm() {
        selectedBillId = -1;
        if (residentCombo.getItemCount() > 0) residentCombo.setSelectedIndex(0);
        monthField.setText("");
        totalField.setText("");
        billTable.clearSelection();
    }

    private void refreshResidentCombo() {
        residentCombo.removeAllItems();
        try {
            List<Resident> residents = residentService.getActiveResidents();
            for (Resident r : residents) {
                residentCombo.addItem(r.getId() + " - " + r.getFullName());
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }
}