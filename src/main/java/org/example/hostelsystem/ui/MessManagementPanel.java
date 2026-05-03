package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.FoodMenu;
import org.example.hostelsystem.model.MessBill;
import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.service.FoodMenuService;
import org.example.hostelsystem.service.MessBillService;
import org.example.hostelsystem.service.ResidentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
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
    private JComboBox<String> residentCombo;
    private JTextField monthField;
    private JTextField totalField;
    private JTextField paidField;
    private int selectedBillId = -1;

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

    // ==================== FOOD MENU ====================

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Menu Table
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

        // Menu Form
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

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(menuDateSpinner, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Meal:"), gbc);
        gbc.gridx = 3;
        formPanel.add(mealTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Items:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(new JScrollPane(itemsArea), gbc);

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

        btnPanel.add(addMenuBtn);
        btnPanel.add(updateMenuBtn);
        btnPanel.add(deleteMenuBtn);
        btnPanel.add(clearMenuBtn);
        btnPanel.add(refreshMenuBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

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
            if (menu.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Items cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            foodMenuService.addMenu(menu);
            loadMenus();
            clearMenuForm();
            JOptionPane.showMessageDialog(this, "Menu added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMenu(ActionEvent e) {
        if (selectedMenuId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a menu to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            FoodMenu menu = new FoodMenu();
            menu.setId(selectedMenuId);
            menu.setMenuDate(new Date(((java.util.Date) menuDateSpinner.getValue()).getTime()));
            menu.setMealType((String) mealTypeCombo.getSelectedItem());
            menu.setItems(itemsArea.getText().trim());
            if (menu.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Items cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            foodMenuService.updateMenu(menu);
            loadMenus();
            clearMenuForm();
            JOptionPane.showMessageDialog(this, "Menu updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMenu(ActionEvent e) {
        if (selectedMenuId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a menu to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this menu?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                foodMenuService.deleteMenu(selectedMenuId);
                loadMenus();
                clearMenuForm();
                JOptionPane.showMessageDialog(this, "Menu deleted successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearMenuForm() {
        selectedMenuId = -1;
        menuDateSpinner.setValue(new java.util.Date());
        mealTypeCombo.setSelectedIndex(0);
        itemsArea.setText("");
        menuTable.clearSelection();
    }

    // ==================== MESS BILLS ====================

    private JPanel createBillPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Bill Table
        String[] billCols = {"ID", "Resident", "Month", "Total", "Paid", "Due", "Status"};
        billTableModel = new DefaultTableModel(billCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        billTable = new JTable(billTableModel);
        billTable.setRowHeight(25);
        billTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && billTable.getSelectedRow() != -1) {
                populateBillForm();
            }
        });
        panel.add(new JScrollPane(billTable), BorderLayout.CENTER);

        // Bill Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Mess Bills"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        residentCombo = new JComboBox<>();
        refreshResidentCombo();
        monthField = new JTextField(10);
        totalField = new JTextField(10);
        paidField = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Resident:"), gbc);
        gbc.gridx = 1;
        formPanel.add(residentCombo, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Month (YYYY-MM):"), gbc);
        gbc.gridx = 3;
        formPanel.add(monthField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        formPanel.add(totalField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Paid Amount:"), gbc);
        gbc.gridx = 3;
        formPanel.add(paidField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addBillBtn = new JButton("Add Bill");
        JButton updateBillBtn = new JButton("Update");
        JButton deleteBillBtn = new JButton("Delete");
        JButton clearBillBtn = new JButton("Clear");
        JButton refreshBillBtn = new JButton("Refresh");

        styleButton(addBillBtn, new Color(60, 179, 113));
        styleButton(updateBillBtn, new Color(70, 130, 180));
        styleButton(deleteBillBtn, new Color(220, 20, 60));

        addBillBtn.addActionListener(this::addBill);
        updateBillBtn.addActionListener(this::updateBill);
        deleteBillBtn.addActionListener(this::deleteBill);
        clearBillBtn.addActionListener(e -> clearBillForm());
        refreshBillBtn.addActionListener(e -> loadBills());

        btnPanel.add(addBillBtn);
        btnPanel.add(updateBillBtn);
        btnPanel.add(deleteBillBtn);
        btnPanel.add(clearBillBtn);
        btnPanel.add(refreshBillBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        panel.add(formPanel, BorderLayout.SOUTH);
        loadBills();
        return panel;
    }

    private void loadBills() {
        billTableModel.setRowCount(0);
        try {
            for (MessBill b : messBillService.getAllBills()) {
                billTableModel.addRow(new Object[]{
                    b.getId(), b.getResidentName(), b.getBillMonth(),
                    b.getTotalAmount(), b.getPaidAmount(), b.getDueAmount(), b.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading bills: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateBillForm() {
        int row = billTable.getSelectedRow();
        selectedBillId = (int) billTableModel.getValueAt(row, 0);
        String residentName = (String) billTableModel.getValueAt(row, 1);
        for (int i = 0; i < residentCombo.getItemCount(); i++) {
            if (residentCombo.getItemAt(i).contains(residentName)) {
                residentCombo.setSelectedIndex(i);
                break;
            }
        }
        monthField.setText((String) billTableModel.getValueAt(row, 2));
        totalField.setText(billTableModel.getValueAt(row, 3).toString());
        paidField.setText(billTableModel.getValueAt(row, 4).toString());
    }

    private void addBill(ActionEvent e) {
        try {
            MessBill bill = buildBillFromForm();
            if (bill == null) return;
            messBillService.addBill(bill);
            loadBills();
            clearBillForm();
            JOptionPane.showMessageDialog(this, "Bill added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBill(ActionEvent e) {
        if (selectedBillId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            MessBill bill = buildBillFromForm();
            if (bill == null) return;
            bill.setId(selectedBillId);
            messBillService.updateBill(bill);
            loadBills();
            clearBillForm();
            JOptionPane.showMessageDialog(this, "Bill updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBill(ActionEvent e) {
        if (selectedBillId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this bill?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                messBillService.deleteBill(selectedBillId);
                loadBills();
                clearBillForm();
                JOptionPane.showMessageDialog(this, "Bill deleted successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private MessBill buildBillFromForm() {
        if (residentCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a resident.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String residentStr = (String) residentCombo.getSelectedItem();
        int residentId = Integer.parseInt(residentStr.split(" - ")[0]);

        String month = monthField.getText().trim();
        if (month.isEmpty() || !month.matches("\\d{4}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Month must be in YYYY-MM format.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        BigDecimal total, paid;
        try {
            total = new BigDecimal(totalField.getText().trim());
            paid = new BigDecimal(paidField.getText().trim().isEmpty() ? "0" : paidField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        MessBill bill = new MessBill();
        bill.setResidentId(residentId);
        bill.setBillMonth(month);
        bill.setTotalAmount(total);
        bill.setPaidAmount(paid);
        return bill;
    }

    private void clearBillForm() {
        selectedBillId = -1;
        if (residentCombo.getItemCount() > 0) residentCombo.setSelectedIndex(0);
        monthField.setText("");
        totalField.setText("");
        paidField.setText("");
        billTable.clearSelection();
    }

    private void refreshResidentCombo() {
        residentCombo.removeAllItems();
        try {
            List<Resident> residents = residentService.getActiveResidents();
            for (Resident r : residents) {
                residentCombo.addItem(r.getId() + " - " + r.getFullName());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }
}
