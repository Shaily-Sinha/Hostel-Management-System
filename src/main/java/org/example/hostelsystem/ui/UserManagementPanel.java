package org.example.hostelsystem.ui;

import org.example.hostelsystem.model.User;
import org.example.hostelsystem.service.AuthService;
import org.example.hostelsystem.dao.UserDAO;
import org.example.hostelsystem.ui.util.ModernTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class UserManagementPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField usernameField, fullNameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    public UserManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initTable();
        initForm();
        loadUsers();
    }

    private void initTable() {
        String[] columns = {"ID", "Username", "Role", "Full Name", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(tableModel);
        ModernTheme.styleTable(userTable);

        JScrollPane scrollPane = ModernTheme.scrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        add(scrollPane, BorderLayout.NORTH);
    }

    private void initForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New User"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField(12);
        passwordField = new JPasswordField(12);
        fullNameField = new JTextField(12);
        emailField = new JTextField(12);
        roleCombo = new JComboBox<>(new String[]{"WARDEN", "STUDENT"});

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 3;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addBtn = new JButton("Create User");
        addBtn.setBackground(new Color(60, 179, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setOpaque(true);
        addBtn.setBorderPainted(false);
        addBtn.addActionListener(this::createUser);
        buttonPanel.add(addBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        try {
            for (User user : userDAO.getAllUsers()) {
                tableModel.addRow(new Object[]{user.getId(), user.getUsername(), user.getRole(), user.getFullName(), user.getEmail()});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void createUser(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, password, and full name are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User existing = userDAO.getUserByUsername(username);
            if (existing != null) {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            user.setFullName(fullName);
            user.setEmail(email);
            userDAO.addUser(user);

            JOptionPane.showMessageDialog(this, role + " user created successfully!");
            clearForm();
            loadUsers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        fullNameField.setText("");
        emailField.setText("");
        roleCombo.setSelectedIndex(0);
    }
}
