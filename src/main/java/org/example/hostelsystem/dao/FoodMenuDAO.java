package org.example.hostelsystem.dao;

import org.example.hostelsystem.db.DatabaseConnection;
import org.example.hostelsystem.model.FoodMenu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodMenuDAO {

    public void addMenu(FoodMenu menu) throws SQLException {
        String sql = "INSERT INTO food_menu (menu_date, meal_type, items) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, menu.getMenuDate());
            stmt.setString(2, menu.getMealType());
            stmt.setString(3, menu.getItems());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) menu.setId(rs.getInt(1));
            }
        }
    }

    public void updateMenu(FoodMenu menu) throws SQLException {
        String sql = "UPDATE food_menu SET menu_date=?, meal_type=?, items=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, menu.getMenuDate());
            stmt.setString(2, menu.getMealType());
            stmt.setString(3, menu.getItems());
            stmt.setInt(4, menu.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteMenu(int id) throws SQLException {
        String sql = "DELETE FROM food_menu WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public FoodMenu getMenuById(int id) throws SQLException {
        String sql = "SELECT * FROM food_menu WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapMenu(rs);
            }
        }
        return null;
    }

    public List<FoodMenu> getMenuByDate(Date date) throws SQLException {
        List<FoodMenu> menus = new ArrayList<>();
        String sql = "SELECT * FROM food_menu WHERE menu_date=? ORDER BY FIELD(meal_type, 'BREAKFAST', 'LUNCH', 'DINNER')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) menus.add(mapMenu(rs));
            }
        }
        return menus;
    }

    public List<FoodMenu> getAllMenus() throws SQLException {
        List<FoodMenu> menus = new ArrayList<>();
        String sql = "SELECT * FROM food_menu ORDER BY menu_date DESC, FIELD(meal_type, 'BREAKFAST', 'LUNCH', 'DINNER')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) menus.add(mapMenu(rs));
        }
        return menus;
    }

    private FoodMenu mapMenu(ResultSet rs) throws SQLException {
        return new FoodMenu(
            rs.getInt("id"),
            rs.getDate("menu_date"),
            rs.getString("meal_type"),
            rs.getString("items"),
            rs.getTimestamp("created_at")
        );
    }
}
