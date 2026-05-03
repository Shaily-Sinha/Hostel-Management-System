package org.example.hostelsystem.dao;

import org.example.hostelsystem.db.DatabaseConnection;
import org.example.hostelsystem.model.LateArrivalIntimation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LateArrivalIntimationDAO {

    public void addIntimation(LateArrivalIntimation intimation) throws SQLException {
        String sql = "INSERT INTO late_arrival_intimations (resident_id, arrival_date, expected_time, reason, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, intimation.getResidentId());
            stmt.setDate(2, intimation.getArrivalDate());
            stmt.setString(3, intimation.getExpectedTime());
            stmt.setString(4, intimation.getReason());
            stmt.setString(5, intimation.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) intimation.setId(rs.getInt(1));
            }
        }
    }

    public void updateIntimation(LateArrivalIntimation intimation) throws SQLException {
        String sql = "UPDATE late_arrival_intimations SET resident_id=?, arrival_date=?, expected_time=?, reason=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, intimation.getResidentId());
            stmt.setDate(2, intimation.getArrivalDate());
            stmt.setString(3, intimation.getExpectedTime());
            stmt.setString(4, intimation.getReason());
            stmt.setString(5, intimation.getStatus());
            stmt.setInt(6, intimation.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteIntimation(int id) throws SQLException {
        String sql = "DELETE FROM late_arrival_intimations WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public LateArrivalIntimation getIntimationById(int id) throws SQLException {
        String sql = "SELECT li.*, r.full_name as resident_name FROM late_arrival_intimations li JOIN residents r ON li.resident_id = r.id WHERE li.id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapIntimation(rs);
            }
        }
        return null;
    }

    public List<LateArrivalIntimation> getIntimationsByResident(int residentId) throws SQLException {
        List<LateArrivalIntimation> list = new ArrayList<>();
        String sql = "SELECT li.*, r.full_name as resident_name FROM late_arrival_intimations li JOIN residents r ON li.resident_id = r.id WHERE li.resident_id=? ORDER BY li.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapIntimation(rs));
            }
        }
        return list;
    }

    public List<LateArrivalIntimation> getAllIntimations() throws SQLException {
        List<LateArrivalIntimation> list = new ArrayList<>();
        String sql = "SELECT li.*, r.full_name as resident_name FROM late_arrival_intimations li JOIN residents r ON li.resident_id = r.id ORDER BY li.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapIntimation(rs));
        }
        return list;
    }

    public List<LateArrivalIntimation> getNotifiedIntimations() throws SQLException {
        List<LateArrivalIntimation> list = new ArrayList<>();
        String sql = "SELECT li.*, r.full_name as resident_name FROM late_arrival_intimations li JOIN residents r ON li.resident_id = r.id WHERE li.status='NOTIFIED' ORDER BY li.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapIntimation(rs));
        }
        return list;
    }

    private LateArrivalIntimation mapIntimation(ResultSet rs) throws SQLException {
        LateArrivalIntimation item = new LateArrivalIntimation(
            rs.getInt("id"),
            rs.getInt("resident_id"),
            rs.getDate("arrival_date"),
            rs.getString("expected_time"),
            rs.getString("reason"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );
        item.setResidentName(rs.getString("resident_name"));
        return item;
    }
}
