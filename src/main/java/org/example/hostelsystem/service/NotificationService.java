package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.NotificationDAO;
import org.example.hostelsystem.model.Notification;

import java.sql.SQLException;
import java.util.List;

public class NotificationService {
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public void sendNotification(Notification note) throws SQLException {
        note.setStatus("UNREAD");
        notificationDAO.addNotification(note);
    }

    public void sendBroadcast(int senderId, String title, String message) throws SQLException {
        Notification note = new Notification();
        note.setSenderId(senderId);
        note.setResidentId(null);
        note.setTitle(title);
        note.setMessage(message);
        note.setType("BROADCAST");
        note.setStatus("UNREAD");
        notificationDAO.addNotification(note);
    }

    public void markAsRead(int id) throws SQLException {
        notificationDAO.markAsRead(id);
    }

    public void deleteNotification(int id) throws SQLException {
        notificationDAO.deleteNotification(id);
    }

    public Notification getNotificationById(int id) throws SQLException {
        return notificationDAO.getNotificationById(id);
    }

    public List<Notification> getNotificationsForResident(int residentId) throws SQLException {
        return notificationDAO.getNotificationsForResident(residentId);
    }

    public List<Notification> getUnreadNotificationsForResident(int residentId) throws SQLException {
        return notificationDAO.getUnreadNotificationsForResident(residentId);
    }

    public List<Notification> getAllNotifications() throws SQLException {
        return notificationDAO.getAllNotifications();
    }
}
