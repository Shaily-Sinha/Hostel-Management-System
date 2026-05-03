package org.example.hostelsystem.ui;

import org.example.hostelsystem.db.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResidentHistoryDialog extends JDialog {

    private final int residentId;
    private final String residentName;

    public ResidentHistoryDialog(Frame parent, int residentId, String residentName) {
        super(parent, "History Details - " + residentName, true);
        this.residentId = residentId;
        this.residentName = residentName;

        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Attendance", createTablePanel(getAttendanceData(), new String[]{"Date", "Check-in Time", "Location Verified", "Biometric", "Status"}));
        tabbedPane.addTab("Mess Bills", createTablePanel(getMessBillData(), new String[]{"Month", "Total", "Paid", "Due", "Status"}));
        tabbedPane.addTab("Leave Requests", createTablePanel(getLeaveData(), new String[]{"Start Date", "End Date", "Reason", "Status"}));
        tabbedPane.addTab("Bookings", createTablePanel(getBookingData(), new String[]{"Room", "Booking Date", "Check-In", "Check-Out", "Amount", "Status"}));

        add(tabbedPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTablePanel(Object[][] data, String[] columns) {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private Object[][] getAttendanceData() {
        return fetchData("SELECT attendance_date, check_in_time, location_verified, biometric_verified, status FROM attendance WHERE resident_id=? ORDER BY attendance_date DESC", 5);
    }

    private Object[][] getMessBillData() {
        return fetchData("SELECT bill_month, total_amount, paid_amount, due_amount, status FROM mess_bills WHERE resident_id=? ORDER BY bill_month DESC", 5);
    }

    private Object[][] getLeaveData() {
        return fetchData("SELECT start_date, end_date, reason, status FROM leave_requests WHERE resident_id=? ORDER BY start_date DESC", 4);
    }

    private Object[][] getBookingData() {
        return fetchData("SELECT room_id, booking_date, check_in_date, check_out_date, total_amount, status FROM bookings WHERE resident_id=? ORDER BY booking_date DESC", 6);
    }

    private Object[][] fetchData(String sql, int columnCount) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            try (ResultSet rs = stmt.executeQuery()) {
                java.util.List<Object[]> rowList = new java.util.ArrayList<>();
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int j = 0; j < columnCount; j++) {
                        row[j] = rs.getObject(j + 1);
                    }
                    rowList.add(row);
                }
                return rowList.toArray(new Object[0][0]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Object[0][columnCount];
        }
    }
}
