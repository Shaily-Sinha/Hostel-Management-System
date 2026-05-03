package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.RoomDAO;
import org.example.hostelsystem.db.DatabaseConnection;
import org.example.hostelsystem.model.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RoomService {
    private final RoomDAO roomDAO = new RoomDAO();

    public void addRoom(Room room) throws SQLException {
        roomDAO.addRoom(room);
    }

    public void updateRoom(Room room) throws SQLException {
        roomDAO.updateRoom(room);
    }

    public void deleteRoom(int id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            // Delete bookings for this room
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM bookings WHERE room_id=?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            // Unassign residents from this room
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE residents SET room_id=NULL WHERE room_id=?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            // Delete the room
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM rooms WHERE id=?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public Room getRoomById(int id) throws SQLException {
        return roomDAO.getRoomById(id);
    }

    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.getAllRooms();
    }

    public List<Room> getAvailableRooms() throws SQLException {
        return roomDAO.getAvailableRooms();
    }

    public int getCurrentOccupancy(int roomId) throws SQLException {
        return roomDAO.getCurrentOccupancy(roomId);
    }
}
