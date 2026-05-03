package org.example.hostelsystem.dao;

import org.example.hostelsystem.db.DatabaseConnection;
import org.example.hostelsystem.model.MessBill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessBillDAO {

    public void addBill(MessBill bill) throws SQLException {
        String sql = "INSERT INTO mess_bills (resident_id, bill_month, total_amount, paid_amount, due_amount, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, bill.getResidentId());
            stmt.setString(2, bill.getBillMonth());
            stmt.setBigDecimal(3, bill.getTotalAmount());
            stmt.setBigDecimal(4, bill.getPaidAmount());
            stmt.setBigDecimal(5, bill.getDueAmount());
            stmt.setString(6, bill.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) bill.setId(rs.getInt(1));
            }
        }
    }

    public void updateBill(MessBill bill) throws SQLException {
        String sql = "UPDATE mess_bills SET resident_id=?, bill_month=?, total_amount=?, paid_amount=?, due_amount=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bill.getResidentId());
            stmt.setString(2, bill.getBillMonth());
            stmt.setBigDecimal(3, bill.getTotalAmount());
            stmt.setBigDecimal(4, bill.getPaidAmount());
            stmt.setBigDecimal(5, bill.getDueAmount());
            stmt.setString(6, bill.getStatus());
            stmt.setInt(7, bill.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteBill(int id) throws SQLException {
        String sql = "DELETE FROM mess_bills WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public MessBill getBillById(int id) throws SQLException {
        String sql = "SELECT b.*, r.full_name as resident_name FROM mess_bills b JOIN residents r ON b.resident_id = r.id WHERE b.id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapBill(rs);
            }
        }
        return null;
    }

    public List<MessBill> getBillsByResident(int residentId) throws SQLException {
        List<MessBill> bills = new ArrayList<>();
        String sql = "SELECT b.*, r.full_name as resident_name FROM mess_bills b JOIN residents r ON b.resident_id = r.id WHERE b.resident_id=? ORDER BY b.bill_month DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) bills.add(mapBill(rs));
            }
        }
        return bills;
    }

    public List<MessBill> getAllBills() throws SQLException {
        List<MessBill> bills = new ArrayList<>();
        String sql = "SELECT b.*, r.full_name as resident_name FROM mess_bills b JOIN residents r ON b.resident_id = r.id ORDER BY b.bill_month DESC, r.full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) bills.add(mapBill(rs));
        }
        return bills;
    }

    private MessBill mapBill(ResultSet rs) throws SQLException {
        MessBill bill = new MessBill(
            rs.getInt("id"),
            rs.getInt("resident_id"),
            rs.getString("bill_month"),
            rs.getBigDecimal("total_amount"),
            rs.getBigDecimal("paid_amount"),
            rs.getBigDecimal("due_amount"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );
        bill.setResidentName(rs.getString("resident_name"));
        return bill;
    }
}
