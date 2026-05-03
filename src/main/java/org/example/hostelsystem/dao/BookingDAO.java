package org.example.hostelsystem.dao;

import org.example.hostelsystem.db.DatabaseConnection;
import org.example.hostelsystem.model.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public void addBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (resident_id, room_id, booking_date, check_in_date, check_out_date, status, total_amount, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getResidentId());
            stmt.setInt(2, booking.getRoomId());
            stmt.setDate(3, booking.getBookingDate());
            stmt.setDate(4, booking.getCheckInDate());
            stmt.setDate(5, booking.getCheckOutDate());
            stmt.setString(6, booking.getStatus());
            stmt.setBigDecimal(7, booking.getTotalAmount());
            stmt.setString(8, booking.getNotes());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) booking.setId(rs.getInt(1));
            }
        }
    }

    public void updateBooking(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET resident_id=?, room_id=?, booking_date=?, check_in_date=?, check_out_date=?, status=?, total_amount=?, notes=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, booking.getResidentId());
            stmt.setInt(2, booking.getRoomId());
            stmt.setDate(3, booking.getBookingDate());
            stmt.setDate(4, booking.getCheckInDate());
            stmt.setDate(5, booking.getCheckOutDate());
            stmt.setString(6, booking.getStatus());
            stmt.setBigDecimal(7, booking.getTotalAmount());
            stmt.setString(8, booking.getNotes());
            stmt.setInt(9, booking.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteBooking(int id) throws SQLException {
        String sql = "DELETE FROM bookings WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Booking getBookingById(int id) throws SQLException {
        String sql = "SELECT b.*, r.full_name as resident_name, rm.room_number FROM bookings b JOIN residents r ON b.resident_id = r.id JOIN rooms rm ON b.room_id = rm.id WHERE b.id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapBooking(rs);
            }
        }
        return null;
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, r.full_name as resident_name, rm.room_number FROM bookings b JOIN residents r ON b.resident_id = r.id JOIN rooms rm ON b.room_id = rm.id ORDER BY b.booking_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) bookings.add(mapBooking(rs));
        }
        return bookings;
    }

    public List<Booking> getBookingsByStatus(String status) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, r.full_name as resident_name, rm.room_number FROM bookings b JOIN residents r ON b.resident_id = r.id JOIN rooms rm ON b.room_id = rm.id WHERE b.status=? ORDER BY b.booking_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) bookings.add(mapBooking(rs));
            }
        }
        return bookings;
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking(
            rs.getInt("id"),
            rs.getInt("resident_id"),
            rs.getInt("room_id"),
            rs.getDate("booking_date"),
            rs.getDate("check_in_date"),
            rs.getDate("check_out_date"),
            rs.getString("status"),
            rs.getBigDecimal("total_amount"),
            rs.getString("notes"),
            rs.getTimestamp("created_at")
        );
        b.setResidentName(rs.getString("resident_name"));
        b.setRoomNumber(rs.getString("room_number"));
        return b;
    }
}
