package org.example.hostelsystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/hostel_db";
    private static final String USER = "root";
    // IMPORTANT: Change this to your MySQL password before running
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "MySQL JDBC Driver not found. Please ensure mysql-connector-j is in the classpath.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean initializeDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASSWORD);
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS hostel_db");
        } catch (SQLException e) {
            System.err.println("Failed to create database: " + e.getMessage());
            return false;
        }

        try (Connection dbConn = getConnection();
                Statement dbStmt = dbConn.createStatement()) {

            dbStmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "username VARCHAR(50) NOT NULL UNIQUE," +
                            "password VARCHAR(255) NOT NULL," +
                            "role VARCHAR(20) NOT NULL," +
                            "full_name VARCHAR(100)," +
                            "email VARCHAR(100)," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            dbStmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS rooms (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "room_number VARCHAR(20) NOT NULL UNIQUE," +
                            "room_type VARCHAR(50) NOT NULL," +
                            "capacity INT NOT NULL," +
                            "price_per_month DECIMAL(10,2) NOT NULL," +
                            "status VARCHAR(20) DEFAULT 'AVAILABLE'," +
                            "floor INT," +
                            "description TEXT," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            dbStmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS residents (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "full_name VARCHAR(100) NOT NULL," +
                            "email VARCHAR(100)," +
                            "phone VARCHAR(20)," +
                            "address TEXT," +
                            "emergency_contact VARCHAR(100)," +
                            "date_of_birth DATE," +
                            "gender VARCHAR(10)," +
                            "id_proof VARCHAR(100)," +
                            "room_id INT," +
                            "check_in_date DATE," +
                            "check_out_date DATE," +
                            "status VARCHAR(20) DEFAULT 'ACTIVE'," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (room_id) REFERENCES rooms(id))");

            try {
                dbStmt.executeUpdate("ALTER TABLE residents ADD COLUMN webauthn_id VARCHAR(255) UNIQUE");
            } catch (SQLException ignored) {
                // Column might already exist
            }

            dbStmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS bookings (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "resident_id INT NOT NULL," +
                            "room_id INT NOT NULL," +
                            "booking_date DATE NOT NULL," +
                            "check_in_date DATE NOT NULL," +
                            "check_out_date DATE," +
                            "status VARCHAR(20) DEFAULT 'PENDING'," +
                            "total_amount DECIMAL(10,2)," +
                            "notes TEXT," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (resident_id) REFERENCES residents(id)," +
                            "FOREIGN KEY (room_id) REFERENCES rooms(id))");

            dbStmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS attendance (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "resident_id INT NOT NULL," +
                            "attendance_date DATE NOT NULL," +
                            "check_in_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "location_lat DECIMAL(10,8)," +
                            "location_lng DECIMAL(11,8)," +
                            "biometric_verified BOOLEAN DEFAULT FALSE," +
                            "location_verified BOOLEAN DEFAULT FALSE," +
                            "status VARCHAR(20) DEFAULT 'PRESENT'," +
                            "FOREIGN KEY (resident_id) REFERENCES residents(id)," +
                            "UNIQUE KEY unique_attendance (resident_id, attendance_date))");

            dbStmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS food_menu (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "menu_date DATE NOT NULL," +
                            "meal_type VARCHAR(20) NOT NULL," +
                            "items TEXT NOT NULL," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "UNIQUE KEY unique_menu (menu_date, meal_type))");

            dbStmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS mess_bills (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "resident_id INT NOT NULL," +
                            "bill_month VARCHAR(7) NOT NULL," +
                            "total_amount DECIMAL(10,2) NOT NULL," +
                            "paid_amount DECIMAL(10,2) DEFAULT 0.00," +
                            "due_amount DECIMAL(10,2) NOT NULL," +
                            "status VARCHAR(20) DEFAULT 'UNPAID'," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (resident_id) REFERENCES residents(id)," +
                            "UNIQUE KEY unique_bill (resident_id, bill_month))");

            dbStmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS leave_requests (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "resident_id INT NOT NULL," +
                            "start_date DATE NOT NULL," +
                            "end_date DATE NOT NULL," +
                            "reason TEXT NOT NULL," +
                            "status VARCHAR(20) DEFAULT 'PENDING'," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (resident_id) REFERENCES residents(id))");

            dbStmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS late_arrival_intimations (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "resident_id INT NOT NULL," +
                            "arrival_date DATE NOT NULL," +
                            "expected_time VARCHAR(10) NOT NULL," +
                            "reason TEXT NOT NULL," +
                            "status VARCHAR(20) DEFAULT 'NOTIFIED'," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (resident_id) REFERENCES residents(id))");

            dbStmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS student_ids (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "resident_id INT NOT NULL," +
                            "student_id_number VARCHAR(50) NOT NULL UNIQUE," +
                            "issue_date DATE NOT NULL," +
                            "expiry_date DATE," +
                            "status VARCHAR(20) DEFAULT 'ISSUED'," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (resident_id) REFERENCES residents(id))");

            dbStmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS notifications (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "sender_id INT NOT NULL," +
                            "resident_id INT," +
                            "title VARCHAR(200) NOT NULL," +
                            "message TEXT NOT NULL," +
                            "type VARCHAR(20) DEFAULT 'BROADCAST'," +
                            "status VARCHAR(20) DEFAULT 'UNREAD'," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (sender_id) REFERENCES users(id)," +
                            "FOREIGN KEY (resident_id) REFERENCES residents(id))");

            return true;
        } catch (SQLException e) {
            System.err.println("Failed to initialize tables: " + e.getMessage());
            return false;
        }
    }
}
