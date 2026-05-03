package org.example.hostelsystem.dao;

import org.example.hostelsystem.db.DatabaseConnection;
import org.example.hostelsystem.model.Resident;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResidentDAO {

    public void addResident(Resident resident) throws SQLException {
        String sql = "INSERT INTO residents (full_name, email, phone, address, emergency_contact, date_of_birth, gender, id_proof, room_id, check_in_date, check_out_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, resident.getFullName());
            stmt.setString(2, resident.getEmail());
            stmt.setString(3, resident.getPhone());
            stmt.setString(4, resident.getAddress());
            stmt.setString(5, resident.getEmergencyContact());
            stmt.setDate(6, resident.getDateOfBirth());
            stmt.setString(7, resident.getGender());
            stmt.setString(8, resident.getIdProof());
            stmt.setObject(9, resident.getRoomId());
            stmt.setDate(10, resident.getCheckInDate());
            stmt.setDate(11, resident.getCheckOutDate());
            stmt.setString(12, resident.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) resident.setId(rs.getInt(1));
            }
        }
    }

    public void updateResident(Resident resident) throws SQLException {
        String sql = "UPDATE residents SET full_name=?, email=?, phone=?, address=?, emergency_contact=?, date_of_birth=?, gender=?, id_proof=?, room_id=?, check_in_date=?, check_out_date=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resident.getFullName());
            stmt.setString(2, resident.getEmail());
            stmt.setString(3, resident.getPhone());
            stmt.setString(4, resident.getAddress());
            stmt.setString(5, resident.getEmergencyContact());
            stmt.setDate(6, resident.getDateOfBirth());
            stmt.setString(7, resident.getGender());
            stmt.setString(8, resident.getIdProof());
            stmt.setObject(9, resident.getRoomId());
            stmt.setDate(10, resident.getCheckInDate());
            stmt.setDate(11, resident.getCheckOutDate());
            stmt.setString(12, resident.getStatus());
            stmt.setInt(13, resident.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteResident(int id) throws SQLException {
        String sql = "DELETE FROM residents WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Resident getResidentById(int id) throws SQLException {
        String sql = "SELECT * FROM residents WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResident(rs);
            }
        }
        return null;
    }

    public List<Resident> getAllResidents() throws SQLException {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM residents ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) residents.add(mapResident(rs));
        }
        return residents;
    }

    public List<Resident> getActiveResidents() throws SQLException {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM residents WHERE status='ACTIVE' ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) residents.add(mapResident(rs));
        }
        return residents;
    }

    private Resident mapResident(ResultSet rs) throws SQLException {
        return new Resident(
            rs.getInt("id"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getString("emergency_contact"),
            rs.getDate("date_of_birth"),
            rs.getString("gender"),
            rs.getString("id_proof"),
            (Integer) rs.getObject("room_id"),
            rs.getDate("check_in_date"),
            rs.getDate("check_out_date"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );
    }
}
