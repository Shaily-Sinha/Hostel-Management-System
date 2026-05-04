package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.FoodMenu;
import org.example.hostelsystem.model.MessBill;
import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.service.FoodMenuService;
import org.example.hostelsystem.service.MessBillService;
import org.example.hostelsystem.service.ResidentService;
import org.example.hostelsystem.ui.util.ModernTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class MessStudentPanel extends JPanel {

    private final FoodMenuService foodMenuService = new FoodMenuService();
    private final MessBillService messBillService = new MessBillService();
    private final ResidentService residentService = new ResidentService();

    private JTable menuTable;
    private DefaultTableModel menuTableModel;
    private JTable billTable;
    private DefaultTableModel billTableModel;
    private JSpinner dateSpinner;

    public MessStudentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initTabs();
    }

    private void initTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 13));
        tabbedPane.addTab("Today's Menu", createMenuPanel());
        tabbedPane.addTab("My Mess Bills", createBillPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        JButton viewBtn = new JButton("View Menu");
        viewBtn.addActionListener(e -> loadMenuForDate());
        topPanel.add(new JLabel("Select Date:"));
        topPanel.add(dateSpinner);
        topPanel.add(viewBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Meal Type", "Items"};
        menuTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        menuTable = new JTable(menuTableModel);
        ModernTheme.styleTable(menuTable);
        panel.add(ModernTheme.scrollPane(menuTable), BorderLayout.CENTER);

        loadMenuForDate();
        return panel;
    }

    private void loadMenuForDate() {
        menuTableModel.setRowCount(0);
        Date selectedDate = new Date(((java.util.Date) dateSpinner.getValue()).getTime());
        try {
            List<FoodMenu> menus = foodMenuService.getMenuByDate(selectedDate);
            if (menus.isEmpty()) {
                menuTableModel.addRow(new Object[]{"—", "No menu available for this date."});
            } else {
                for (FoodMenu m : menus) {
                    menuTableModel.addRow(new Object[]{m.getMealType(), m.getItems()});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading menu: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createBillPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Month", "Total Amount", "Paid Amount", "Due Amount", "Status"};
        billTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        billTable = new JTable(billTableModel);
        ModernTheme.styleTable(billTable);
        panel.add(ModernTheme.scrollPane(billTable), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh Bills");
        refreshBtn.addActionListener(e -> loadStudentBills());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadStudentBills();
        return panel;
    }

    private void loadStudentBills() {
        billTableModel.setRowCount(0);
        Integer residentId = getStudentResidentId();
        if (residentId == null) {
            billTableModel.addRow(new Object[]{"—", "—", "—", "—", "Profile not linked"});
            return;
        }
        try {
            List<MessBill> bills = messBillService.getBillsByResident(residentId);
            if (bills.isEmpty()) {
                billTableModel.addRow(new Object[]{"—", "—", "—", "—", "No bills found"});
            } else {
                for (MessBill b : bills) {
                    billTableModel.addRow(new Object[]{
                        b.getBillMonth(), b.getTotalAmount(), b.getPaidAmount(),
                        b.getDueAmount(), b.getStatus()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading bills: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Integer getStudentResidentId() {
        String email = AuthService.getCurrentUser().getEmail();
        try {
            List<Resident> residents = residentService.getAllResidents();
            for (Resident r : residents) {
                if (r.getEmail() != null && r.getEmail().equalsIgnoreCase(email)) {
                    return r.getId();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
