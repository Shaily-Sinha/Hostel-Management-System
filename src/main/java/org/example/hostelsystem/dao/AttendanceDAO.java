package org.example.hostelsystem.dao;

import org.example.hostelsystem.db.DatabaseConnection;
import org.example.hostelsystem.model.AttendanceRecord;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public void addAttendance(AttendanceRecord record) throws SQLException {
        String sql = "INSERT INTO attendance (resident_id, attendance_date, location_lat, location_lng, biometric_verified, location_verified, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, record.getResidentId());
            stmt.setDate(2, record.getAttendanceDate());
            stmt.setBigDecimal(3, record.getLocationLat());
            stmt.setBigDecimal(4, record.getLocationLng());
            stmt.setBoolean(5, record.isBiometricVerified());
            stmt.setBoolean(6, record.isLocationVerified());
            stmt.setString(7, record.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) record.setId(rs.getInt(1));
            }
        }
    }

    public AttendanceRecord getAttendanceByResidentAndDate(int residentId, Date date) throws SQLException {
        String sql = "SELECT a.*, r.full_name as resident_name FROM attendance a JOIN residents r ON a.resident_id = r.id WHERE a.resident_id=? AND a.attendance_date=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            stmt.setDate(2, date);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapAttendance(rs);
            }
        }
        return null;
    }

    public List<AttendanceRecord> getAttendanceByDate(Date date) throws SQLException {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = "SELECT a.*, r.full_name as resident_name FROM attendance a JOIN residents r ON a.resident_id = r.id WHERE a.attendance_date=? ORDER BY r.full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) records.add(mapAttendance(rs));
            }
        }
        return records;
    }

    public List<AttendanceRecord> getAttendanceByResident(int residentId) throws SQLException {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = "SELECT a.*, r.full_name as resident_name FROM attendance a JOIN residents r ON a.resident_id = r.id WHERE a.resident_id=? ORDER BY a.attendance_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) records.add(mapAttendance(rs));
            }
        }
        return records;
    }

    public List<AttendanceRecord> getAllAttendance() throws SQLException {
        List<AttendanceRecord> records = new ArrayList<>();
        String sql = "SELECT a.*, r.full_name as resident_name FROM attendance a JOIN residents r ON a.resident_id = r.id ORDER BY a.attendance_date DESC, r.full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) records.add(mapAttendance(rs));
        }
        return records;
    }

    private AttendanceRecord mapAttendance(ResultSet rs) throws SQLException {
        AttendanceRecord record = new AttendanceRecord(
            rs.getInt("id"),
            rs.getInt("resident_id"),
            rs.getDate("attendance_date"),
            rs.getTimestamp("check_in_time"),
            rs.getBigDecimal("location_lat"),
            rs.getBigDecimal("location_lng"),
            rs.getBoolean("biometric_verified"),
            rs.getBoolean("location_verified"),
            rs.getString("status")
        );
        record.setResidentName(rs.getString("resident_name"));
        return record;
    }
}
