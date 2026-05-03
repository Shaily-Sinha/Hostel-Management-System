package org.example.hostelsystem.dao;

import org.example.hostelsystem.db.DatabaseConnection;
import org.example.hostelsystem.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public void addNotification(Notification note) throws SQLException {
        String sql = "INSERT INTO notifications (sender_id, resident_id, title, message, type, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, note.getSenderId());
            if (note.getResidentId() != null) {
                stmt.setInt(2, note.getResidentId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, note.getTitle());
            stmt.setString(4, note.getMessage());
            stmt.setString(5, note.getType());
            stmt.setString(6, note.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) note.setId(rs.getInt(1));
            }
        }
    }

    public void updateNotification(Notification note) throws SQLException {
        String sql = "UPDATE notifications SET sender_id=?, resident_id=?, title=?, message=?, type=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, note.getSenderId());
            if (note.getResidentId() != null) {
                stmt.setInt(2, note.getResidentId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, note.getTitle());
            stmt.setString(4, note.getMessage());
            stmt.setString(5, note.getType());
            stmt.setString(6, note.getStatus());
            stmt.setInt(7, note.getId());
            stmt.executeUpdate();
        }
    }

    public void markAsRead(int id) throws SQLException {
        String sql = "UPDATE notifications SET status='READ' WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void deleteNotification(int id) throws SQLException {
        String sql = "DELETE FROM notifications WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Notification getNotificationById(int id) throws SQLException {
        String sql = "SELECT n.*, u.full_name as sender_name, r.full_name as resident_name " +
                     "FROM notifications n JOIN users u ON n.sender_id = u.id LEFT JOIN residents r ON n.resident_id = r.id WHERE n.id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapNotification(rs);
            }
        }
        return null;
    }

    public List<Notification> getNotificationsForResident(int residentId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.*, u.full_name as sender_name, r.full_name as resident_name " +
                     "FROM notifications n JOIN users u ON n.sender_id = u.id LEFT JOIN residents r ON n.resident_id = r.id " +
                     "WHERE n.resident_id=? OR n.type='BROADCAST' ORDER BY n.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapNotification(rs));
            }
        }
        return list;
    }

    public List<Notification> getUnreadNotificationsForResident(int residentId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.*, u.full_name as sender_name, r.full_name as resident_name " +
                     "FROM notifications n JOIN users u ON n.sender_id = u.id LEFT JOIN residents r ON n.resident_id = r.id " +
                     "WHERE (n.resident_id=? OR n.type='BROADCAST') AND n.status='UNREAD' ORDER BY n.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapNotification(rs));
            }
        }
        return list;
    }

    public List<Notification> getAllNotifications() throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.*, u.full_name as sender_name, r.full_name as resident_name " +
                     "FROM notifications n JOIN users u ON n.sender_id = u.id LEFT JOIN residents r ON n.resident_id = r.id ORDER BY n.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapNotification(rs));
        }
        return list;
    }

    private Notification mapNotification(ResultSet rs) throws SQLException {
        Notification note = new Notification(
            rs.getInt("id"),
            rs.getInt("sender_id"),
            rs.getObject("resident_id") != null ? rs.getInt("resident_id") : null,
            rs.getString("title"),
            rs.getString("message"),
            rs.getString("type"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );
        note.setSenderName(rs.getString("sender_name"));
        note.setResidentName(rs.getString("resident_name"));
        return note;
    }
}
