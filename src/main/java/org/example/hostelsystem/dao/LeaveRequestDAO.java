package org.example.hostelsystem.dao;

import org.example.hostelsystem.db.DatabaseConnection;
import org.example.hostelsystem.model.LeaveRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {

    public void addLeaveRequest(LeaveRequest request) throws SQLException {
        String sql = "INSERT INTO leave_requests (resident_id, start_date, end_date, reason, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, request.getResidentId());
            stmt.setDate(2, request.getStartDate());
            stmt.setDate(3, request.getEndDate());
            stmt.setString(4, request.getReason());
            stmt.setString(5, request.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) request.setId(rs.getInt(1));
            }
        }
    }

    public void updateLeaveRequest(LeaveRequest request) throws SQLException {
        String sql = "UPDATE leave_requests SET resident_id=?, start_date=?, end_date=?, reason=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, request.getResidentId());
            stmt.setDate(2, request.getStartDate());
            stmt.setDate(3, request.getEndDate());
            stmt.setString(4, request.getReason());
            stmt.setString(5, request.getStatus());
            stmt.setInt(6, request.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteLeaveRequest(int id) throws SQLException {
        String sql = "DELETE FROM leave_requests WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public LeaveRequest getLeaveRequestById(int id) throws SQLException {
        String sql = "SELECT lr.*, r.full_name as resident_name FROM leave_requests lr JOIN residents r ON lr.resident_id = r.id WHERE lr.id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapLeaveRequest(rs);
            }
        }
        return null;
    }

    public List<LeaveRequest> getLeaveRequestsByResident(int residentId) throws SQLException {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = "SELECT lr.*, r.full_name as resident_name FROM leave_requests lr JOIN residents r ON lr.resident_id = r.id WHERE lr.resident_id=? ORDER BY lr.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) requests.add(mapLeaveRequest(rs));
            }
        }
        return requests;
    }

    public List<LeaveRequest> getAllLeaveRequests() throws SQLException {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = "SELECT lr.*, r.full_name as resident_name FROM leave_requests lr JOIN residents r ON lr.resident_id = r.id ORDER BY lr.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) requests.add(mapLeaveRequest(rs));
        }
        return requests;
    }

    public List<LeaveRequest> getPendingLeaveRequests() throws SQLException {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = "SELECT lr.*, r.full_name as resident_name FROM leave_requests lr JOIN residents r ON lr.resident_id = r.id WHERE lr.status='PENDING' ORDER BY lr.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) requests.add(mapLeaveRequest(rs));
        }
        return requests;
    }

    private LeaveRequest mapLeaveRequest(ResultSet rs) throws SQLException {
        LeaveRequest request = new LeaveRequest(
            rs.getInt("id"),
            rs.getInt("resident_id"),
            rs.getDate("start_date"),
            rs.getDate("end_date"),
            rs.getString("reason"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );
        request.setResidentName(rs.getString("resident_name"));
        return request;
    }
}
