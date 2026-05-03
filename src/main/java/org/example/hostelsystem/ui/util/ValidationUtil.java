package org.example.hostelsystem.ui.util;

import javax.swing.*;

public class ValidationUtil {

    public static boolean requireNonEmpty(JComponent parent, String fieldName, String value) {
        if (value == null || value.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, fieldName + " is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public static boolean requirePositiveInt(JComponent parent, String fieldName, String value) {
        try {
            int num = Integer.parseInt(value.trim());
            if (num <= 0) {
                JOptionPane.showMessageDialog(parent, fieldName + " must be a positive number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, fieldName + " must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    public static boolean requirePositiveDecimal(JComponent parent, String fieldName, String value) {
        try {
            double num = Double.parseDouble(value.trim());
            if (num < 0) {
                JOptionPane.showMessageDialog(parent, fieldName + " cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, fieldName + " must be a valid amount.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    public static boolean requireComboSelection(JComponent parent, String fieldName, JComboBox<?> combo) {
        if (combo.getSelectedItem() == null || combo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Please select a " + fieldName + ".", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public static boolean validateEmail(JComponent parent, String email) {
        if (email == null || email.trim().isEmpty()) return true;
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(regex)) {
            JOptionPane.showMessageDialog(parent, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public static boolean validateDateRange(JComponent parent, java.sql.Date startDate, java.sql.Date endDate) {
        if (endDate != null && endDate.before(startDate)) {
            JOptionPane.showMessageDialog(parent, "End date must be after start date.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
