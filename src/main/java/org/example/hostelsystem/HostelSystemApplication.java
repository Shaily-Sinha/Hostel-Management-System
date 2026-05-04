package org.example.hostelsystem;

import org.example.hostelsystem.db.DatabaseConnection;
import org.example.hostelsystem.ui.LoginFrame;
import org.example.hostelsystem.ui.util.ModernTheme;

import javax.swing.*;

public class HostelSystemApplication {

    public static void main(String[] args) {
        // ── Apply dark theme BEFORE any Swing component is created ──────────
        ModernTheme.applyGlobalDefaults();

        // Remove the system look-and-feel override (it would override dark theme)
        // If you still want system L&F, comment out the line above and uncomment below:
        // try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        // catch (Exception e) { System.err.println("Failed to set L&F: " + e.getMessage()); }

        SwingUtilities.invokeLater(() -> {
            if (DatabaseConnection.initializeDatabase()) {
                new LoginFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,
                    "Failed to connect to MySQL database." + System.lineSeparator() +
                    "Please ensure:\n" +
                    "1. MySQL Server is running on localhost:3306\n" +
                    "2. Username and password in DatabaseConnection.java are correct\n" +
                    "3. mysql-connector-j JAR is in the classpath",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
