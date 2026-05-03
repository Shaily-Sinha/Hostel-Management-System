package org.example.hostelsystem.dao;

import org.example.hostelsystem.db.DatabaseConnection;
import org.example.hostelsystem.model.StudentId;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentIdDAO {

    public void addStudentId(StudentId sid) throws SQLException {
        String sql = "INSERT INTO student_ids (resident_id, student_id_number, issue_date, expiry_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, sid.getResidentId());
            stmt.setString(2, sid.getStudentIdNumber());
            stmt.setDate(3, sid.getIssueDate());
            stmt.setDate(4, sid.getExpiryDate());
            stmt.setString(5, sid.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) sid.setId(rs.getInt(1));
            }
        }
    }

    public void updateStudentId(StudentId sid) throws SQLException {
        String sql = "UPDATE student_ids SET resident_id=?, student_id_number=?, issue_date=?, expiry_date=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sid.getResidentId());
            stmt.setString(2, sid.getStudentIdNumber());
            stmt.setDate(3, sid.getIssueDate());
            stmt.setDate(4, sid.getExpiryDate());
            stmt.setString(5, sid.getStatus());
            stmt.setInt(6, sid.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteStudentId(int id) throws SQLException {
        String sql = "DELETE FROM student_ids WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public StudentId getStudentIdById(int id) throws SQLException {
        String sql = "SELECT si.*, r.full_name as resident_name FROM student_ids si JOIN residents r ON si.resident_id = r.id WHERE si.id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapStudentId(rs);
            }
        }
        return null;
    }

    public StudentId getStudentIdByResident(int residentId) throws SQLException {
        String sql = "SELECT si.*, r.full_name as resident_name FROM student_ids si JOIN residents r ON si.resident_id = r.id WHERE si.resident_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapStudentId(rs);
            }
        }
        return null;
    }

    public List<StudentId> getAllStudentIds() throws SQLException {
        List<StudentId> list = new ArrayList<>();
        String sql = "SELECT si.*, r.full_name as resident_name FROM student_ids si JOIN residents r ON si.resident_id = r.id ORDER BY si.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapStudentId(rs));
        }
        return list;
    }

    private StudentId mapStudentId(ResultSet rs) throws SQLException {
        StudentId sid = new StudentId(
            rs.getInt("id"),
            rs.getInt("resident_id"),
            rs.getString("student_id_number"),
            rs.getDate("issue_date"),
            rs.getDate("expiry_date"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );
        sid.setResidentName(rs.getString("resident_name"));
        return sid;
    }
}
