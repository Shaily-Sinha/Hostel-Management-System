package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.BookingDAO;
import org.example.hostelsystem.model.Booking;

import java.sql.SQLException;
import java.util.List;

public class BookingService {
    private final BookingDAO bookingDAO = new BookingDAO();

    public void addBooking(Booking booking) throws SQLException {
        bookingDAO.addBooking(booking);
    }

    public void updateBooking(Booking booking) throws SQLException {
        bookingDAO.updateBooking(booking);
    }

    public void deleteBooking(int id) throws SQLException {
        bookingDAO.deleteBooking(id);
    }

    public Booking getBookingById(int id) throws SQLException {
        return bookingDAO.getBookingById(id);
    }

    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.getAllBookings();
    }

    public List<Booking> getBookingsByStatus(String status) throws SQLException {
        return bookingDAO.getBookingsByStatus(status);
    }

    public void confirmBooking(int id) throws SQLException {
        Booking booking = bookingDAO.getBookingById(id);
        if (booking != null) {
            booking.setStatus("CONFIRMED");
            bookingDAO.updateBooking(booking);
        }
    }

    public void cancelBooking(int id) throws SQLException {
        Booking booking = bookingDAO.getBookingById(id);
        if (booking != null) {
            booking.setStatus("CANCELLED");
            bookingDAO.updateBooking(booking);
        }
    }
}
