package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.UserDAO;
import org.example.hostelsystem.model.User;

import java.sql.SQLException;

public class AuthService {
    private static User currentUser;
    private final UserDAO userDAO = new UserDAO();

    public boolean login(String username, String password) {
        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                currentUser = user;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public static boolean isWarden() {
        return currentUser != null && "WARDEN".equals(currentUser.getRole());
    }

    public static boolean isStudent() {
        return currentUser != null && "STUDENT".equals(currentUser.getRole());
    }
}
