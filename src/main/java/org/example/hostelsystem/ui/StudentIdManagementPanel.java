package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.Resident;
import org.example.hostelsystem.model.StudentId;
import org.example.hostelsystem.service.ResidentService;
import org.example.hostelsystem.service.StudentIdService;
import org.example.hostelsystem.ui.util.ModernTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class StudentIdManagementPanel extends JPanel {

    private final StudentIdService studentIdService = new StudentIdService();
    private final ResidentService residentService = new ResidentService();

    private JTable idTable;
    private DefaultTableModel tableModel;
    private JComboBox<ResidentComboItem> residentCombo;
    private JTextField idNumberField;
    private JSpinner issueDateSpinner;
    private JSpinner expiryDateSpinner;
    private JComboBox<String> statusCombo;

    private int selectedId = -1;

    public StudentIdManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadStudentIds();
    }

    private void initComponents() {
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Student ID Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        residentCombo = new JComboBox<>();
        loadResidents();
        idNumberField = new JTextField(15);
        issueDateSpinner = new JSpinner(new SpinnerDateModel());
        issueDateSpinner.setEditor(new JSpinner.DateEditor(issueDateSpinner, "yyyy-MM-dd"));
        expiryDateSpinner = new JSpinner(new SpinnerDateModel());
        expiryDateSpinner.setEditor(new JSpinner.DateEditor(expiryDateSpinner, "yyyy-MM-dd"));
        statusCombo = new JComboBox<>(new String[]{"ISSUED", "LOST", "RENEWED", "EXPIRED"});

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Resident:"), gbc);
        gbc.gridx = 1;
        formPanel.add(residentCombo, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("ID Number:"), gbc);
        gbc.gridx = 3;
        formPanel.add(idNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Issue Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(issueDateSpinner, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Expiry Date:"), gbc);
        gbc.gridx = 3;
        formPanel.add(expiryDateSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");

        styleButton(addBtn, new Color(60, 179, 113));
        styleButton(updateBtn, new Color(70, 130, 180));
        styleButton(deleteBtn, new Color(220, 20, 60));
        styleButton(clearBtn, new Color(108, 117, 125));

        addBtn.addActionListener(this::addStudentId);
        updateBtn.addActionListener(this::updateStudentId);
        deleteBtn.addActionListener(this::deleteStudentId);
        clearBtn.addActionListener(e -> clearForm());

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Resident", "Student ID Number", "Issue Date", "Expiry Date", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        idTable = new JTable(tableModel);
        idTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ModernTheme.styleTable(idTable);
        idTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateForm();
        });
        add(ModernTheme.scrollPane(idTable), BorderLayout.CENTER);
    }

    private void loadResidents() {
        residentCombo.removeAllItems();
        try {
            List<Resident> residents = residentService.getAllResidents();
            for (Resident r : residents) {
                residentCombo.addItem(new ResidentComboItem(r.getId(), r.getFullName()));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading residents: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentIds() {
        tableModel.setRowCount(0);
        try {
            List<StudentId> list = studentIdService.getAllStudentIds();
            for (StudentId sid : list) {
                tableModel.addRow(new Object[]{
                    sid.getId(), sid.getResidentName(), sid.getStudentIdNumber(),
                    sid.getIssueDate(), sid.getExpiryDate(), sid.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading student IDs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateForm() {
        int row = idTable.getSelectedRow();
        if (row == -1) return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        try {
            StudentId sid = studentIdService.getStudentIdById(selectedId);
            if (sid == null) return;
            for (int i = 0; i < residentCombo.getItemCount(); i++) {
                if (residentCombo.getItemAt(i).id == sid.getResidentId()) {
                    residentCombo.setSelectedIndex(i);
                    break;
                }
            }
            idNumberField.setText(sid.getStudentIdNumber());
            issueDateSpinner.setValue(sid.getIssueDate());
            if (sid.getExpiryDate() != null) {
                expiryDateSpinner.setValue(sid.getExpiryDate());
            }
            statusCombo.setSelectedItem(sid.getStatus());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        selectedId = -1;
        idNumberField.setText("");
        statusCombo.setSelectedIndex(0);
        idTable.clearSelection();
    }

    private void addStudentId(ActionEvent e) {
        try {
            StudentId sid = new StudentId();
            sid.setResidentId(((ResidentComboItem) residentCombo.getSelectedItem()).id);
            sid.setStudentIdNumber(idNumberField.getText().trim());
            sid.setIssueDate(new Date(((java.util.Date) issueDateSpinner.getValue()).getTime()));
            Date expiryDate = new Date(((java.util.Date) expiryDateSpinner.getValue()).getTime());
            sid.setExpiryDate(expiryDate);
            sid.setStatus((String) statusCombo.getSelectedItem());
            studentIdService.addStudentId(sid);
            loadStudentIds();
            clearForm();
            JOptionPane.showMessageDialog(this, "Student ID added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudentId(ActionEvent e) {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student ID to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            StudentId sid = new StudentId();
            sid.setId(selectedId);
            sid.setResidentId(((ResidentComboItem) residentCombo.getSelectedItem()).id);
            sid.setStudentIdNumber(idNumberField.getText().trim());
            sid.setIssueDate(new Date(((java.util.Date) issueDateSpinner.getValue()).getTime()));
            Date expiryDate = new Date(((java.util.Date) expiryDateSpinner.getValue()).getTime());
            sid.setExpiryDate(expiryDate);
            sid.setStatus((String) statusCombo.getSelectedItem());
            studentIdService.updateStudentId(sid);
            loadStudentIds();
            clearForm();
            JOptionPane.showMessageDialog(this, "Student ID updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudentId(ActionEvent e) {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student ID to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student ID?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                studentIdService.deleteStudentId(selectedId);
                loadStudentIds();
                clearForm();
                JOptionPane.showMessageDialog(this, "Student ID deleted!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }

    private static class ResidentComboItem {
        int id;
        String name;
        ResidentComboItem(int id, String name) { this.id = id; this.name = name; }
        @Override
        public String toString() { return name; }
    }
}
