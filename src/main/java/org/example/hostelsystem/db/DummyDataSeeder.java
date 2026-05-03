package org.example.hostelsystem.db;

import java.sql.*;

public class DummyDataSeeder {

    public static void seedIfEmpty() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Seed rooms if empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rooms");
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Seeding rooms data...");
                stmt.executeUpdate(
                    "INSERT INTO rooms (room_number, room_type, capacity, price_per_month, status, floor, description) VALUES " +
                    "('101', 'Single', 1, 9000.00, 'AVAILABLE', 1, 'AC, WiFi, Personal Washroom, Study Table, Wardrobe')," +
                    "('102', 'Single', 1, 9000.00, 'AVAILABLE', 1, 'AC, WiFi, Personal Washroom, Study Table, Wardrobe')," +
                    "('103', 'Single', 1, 7000.00, 'AVAILABLE', 1, 'Non-AC, WiFi, Shared Washroom, Study Table, Wardrobe')," +
                    "('104', 'Double', 2, 8000.00, 'AVAILABLE', 1, 'AC, WiFi, Personal Washroom, 2 Study Tables, 2 Wardrobes')," +
                    "('105', 'Double', 2, 6500.00, 'AVAILABLE', 1, 'Non-AC, WiFi, Shared Washroom, 2 Study Tables, 2 Wardrobes')," +
                    "('106', 'Triple', 3, 5500.00, 'AVAILABLE', 1, 'Non-AC, WiFi, Shared Washroom, 3 Study Tables, 3 Wardrobes')," +
                    "('107', 'Triple', 3, 7000.00, 'AVAILABLE', 1, 'AC, WiFi, Shared Washroom, 3 Study Tables, 3 Wardrobes')," +
                    "('108', 'Single', 1, 9500.00, 'AVAILABLE', 1, 'Premium AC, WiFi, Personal Washroom, TV, Mini Fridge')," +
                    "('201', 'Single', 1, 9000.00, 'AVAILABLE', 2, 'AC, WiFi, Personal Washroom, Study Table, Wardrobe')," +
                    "('202', 'Single', 1, 7000.00, 'AVAILABLE', 2, 'Non-AC, WiFi, Shared Washroom, Study Table, Wardrobe')," +
                    "('203', 'Double', 2, 8000.00, 'AVAILABLE', 2, 'AC, WiFi, Personal Washroom, 2 Study Tables, 2 Wardrobes')," +
                    "('204', 'Double', 2, 6500.00, 'AVAILABLE', 2, 'Non-AC, WiFi, Shared Washroom, 2 Study Tables, 2 Wardrobes')," +
                    "('205', 'Double', 2, 8500.00, 'AVAILABLE', 2, 'AC, WiFi, Personal Washroom, Balcony, 2 Study Tables')," +
                    "('206', 'Triple', 3, 7000.00, 'AVAILABLE', 2, 'AC, WiFi, Shared Washroom, 3 Study Tables, 3 Wardrobes')," +
                    "('207', 'Triple', 3, 5500.00, 'AVAILABLE', 2, 'Non-AC, WiFi, Shared Washroom, 3 Study Tables, 3 Wardrobes')," +
                    "('301', 'Single', 1, 10000.00, 'AVAILABLE', 3, 'Premium AC, WiFi, Personal Washroom, TV, Sofa')," +
                    "('302', 'Double', 2, 8500.00, 'AVAILABLE', 3, 'AC, WiFi, Personal Washroom, Balcony, 2 Study Tables')," +
                    "('303', 'Double', 2, 6500.00, 'AVAILABLE', 3, 'Non-AC, WiFi, Shared Washroom, 2 Study Tables, 2 Wardrobes')," +
                    "('304', 'Triple', 3, 7000.00, 'AVAILABLE', 3, 'AC, WiFi, Shared Washroom, 3 Study Tables, 3 Wardrobes')," +
                    "('305', 'Dormitory', 4, 4000.00, 'AVAILABLE', 3, 'Non-AC, WiFi, Shared Washroom, Bunk Beds, Lockers')"
                );
                System.out.println("20 rooms seeded successfully.");
            }

            // Seed residents if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM residents");
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Seeding residents data...");
                stmt.executeUpdate(
                    "INSERT INTO residents (full_name, email, phone, address, emergency_contact, date_of_birth, gender, id_proof, room_id, check_in_date, status) VALUES " +
                    "('Aarav Sharma', 'aarav@email.com', '9876543210', 'Delhi', 'Rajesh Sharma', '2002-03-15', 'Male', 'Aadhaar-1234', 1, '2026-04-01', 'ACTIVE')," +
                    "('Vihaan Patel', 'vihaan@email.com', '9876543211', 'Mumbai', 'Anita Patel', '2001-07-22', 'Male', 'Aadhaar-1235', 2, '2026-04-01', 'ACTIVE')," +
                    "('Ishaan Gupta', 'ishaan@email.com', '9876543212', 'Bangalore', 'Suresh Gupta', '2003-01-10', 'Male', 'Aadhaar-1236', 3, '2026-04-02', 'ACTIVE')," +
                    "('Aditya Singh', 'aditya@email.com', '9876543213', 'Jaipur', 'Meera Singh', '2002-11-05', 'Male', 'Aadhaar-1237', 4, '2026-04-02', 'ACTIVE')," +
                    "('Krishna Rao', 'krishna@email.com', '9876543214', 'Hyderabad', 'Lakshmi Rao', '2001-09-18', 'Male', 'Aadhaar-1238', 4, '2026-04-03', 'ACTIVE')," +
                    "('Rohan Mehta', 'rohan@email.com', '9876543215', 'Pune', 'Priya Mehta', '2002-05-30', 'Male', 'Aadhaar-1239', 5, '2026-04-03', 'ACTIVE')," +
                    "('Arjun Nair', 'arjun@email.com', '9876543216', 'Chennai', 'Vijay Nair', '2003-02-14', 'Male', 'Aadhaar-1240', 5, '2026-04-04', 'ACTIVE')," +
                    "('Dhruv Kumar', 'dhruv@email.com', '9876543217', 'Kolkata', 'Sunita Kumar', '2002-08-08', 'Male', 'Aadhaar-1241', 6, '2026-04-04', 'ACTIVE')," +
                    "('Vivaan Reddy', 'vivaan@email.com', '9876543218', 'Visakhapatnam', 'Ravi Reddy', '2001-12-25', 'Male', 'Aadhaar-1242', 7, '2026-04-05', 'ACTIVE')," +
                    "('Ayaan Khan', 'ayaan@email.com', '9876543219', 'Lucknow', 'Fatima Khan', '2003-04-02', 'Male', 'Aadhaar-1243', 8, '2026-04-05', 'ACTIVE')"
                );
                System.out.println("10 residents seeded successfully.");
            }

            // Seed bookings if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM bookings");
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Seeding bookings data...");
                stmt.executeUpdate(
                    "INSERT INTO bookings (resident_id, room_id, booking_date, check_in_date, check_out_date, status, total_amount, notes) VALUES " +
                    "(1, 1, '2026-03-20', '2026-04-01', '2026-06-30', 'CONFIRMED', 27000.00, 'Full semester booking')," +
                    "(2, 2, '2026-03-22', '2026-04-01', '2026-06-30', 'CONFIRMED', 27000.00, 'Full semester booking')," +
                    "(3, 3, '2026-03-25', '2026-04-02', '2026-06-30', 'CONFIRMED', 21000.00, 'Full semester booking')," +
                    "(4, 4, '2026-03-28', '2026-04-02', '2026-06-30', 'CONFIRMED', 24000.00, 'Shared room with Krishna')," +
                    "(5, 4, '2026-03-28', '2026-04-03', '2026-06-30', 'CONFIRMED', 24000.00, 'Shared room with Aditya')," +
                    "(6, 5, '2026-03-30', '2026-04-03', '2026-06-30', 'CONFIRMED', 19500.00, 'Shared room with Arjun')," +
                    "(7, 5, '2026-03-30', '2026-04-04', '2026-06-30', 'CONFIRMED', 19500.00, 'Shared room with Rohan')," +
                    "(8, 6, '2026-04-01', '2026-04-04', '2026-06-30', 'PENDING', 16500.00, 'Awaiting confirmation')," +
                    "(9, 7, '2026-04-02', '2026-04-05', '2026-06-30', 'PENDING', 21000.00, 'Awaiting confirmation')," +
                    "(10, 8, '2026-04-03', '2026-04-05', '2026-06-30', 'PENDING', 28500.00, 'Premium single room')"
                );
                System.out.println("10 bookings seeded successfully.");
            }

            // Seed food menu if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM food_menu");
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Seeding food menu data...");
                stmt.executeUpdate(
                    "INSERT INTO food_menu (menu_date, meal_type, items) VALUES " +
                    "('2026-04-25', 'Breakfast', 'Poha, Jalebi, Milk, Banana')," +
                    "('2026-04-25', 'Lunch', 'Dal Tadka, Rice, Chapati, Mix Veg, Raita, Gulab Jamun')," +
                    "('2026-04-25', 'Dinner', 'Paneer Butter Masala, Naan, Jeera Rice, Salad, Ice Cream')," +
                    "('2026-04-26', 'Breakfast', 'Aloo Paratha, Curd, Pickle, Tea')," +
                    "('2026-04-26', 'Lunch', 'Rajma Chawal, Chapati, Aloo Gobi, Papad, Lassi')," +
                    "('2026-04-26', 'Dinner', 'Chicken Curry, Rice, Chapati, Cucumber Raita, Fruit Custard')," +
                    "('2026-04-27', 'Breakfast', 'Idli, Sambhar, Coconut Chutney, Filter Coffee')," +
                    "('2026-04-27', 'Lunch', 'Sambar Rice, Avial, Rasam, Papad, Payasam')," +
                    "('2026-04-27', 'Dinner', 'Biryani, Raita, Mirchi Ka Salan, Double Ka Meetha')," +
                    "('2026-04-28', 'Breakfast', 'Upma, Coconut Chutney, Boiled Eggs, Tea')," +
                    "('2026-04-28', 'Lunch', 'Chole Bhature, Pulao, Raita, Salad, Kesari Halwa')," +
                    "('2026-04-28', 'Dinner', 'Matar Paneer, Tandoori Roti, Veg Pulao, Dal Makhani, Gajar Ka Halwa')"
                );
                System.out.println("Food menu seeded successfully.");
            }

            // Seed mess bills if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM mess_bills");
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Seeding mess bills data...");
                stmt.executeUpdate(
                    "INSERT INTO mess_bills (resident_id, bill_month, total_amount, paid_amount, due_amount, status) VALUES " +
                    "(1, '2026-04', 4500.00, 4500.00, 0.00, 'PAID')," +
                    "(2, '2026-04', 4500.00, 2000.00, 2500.00, 'PARTIAL')," +
                    "(3, '2026-04', 4500.00, 0.00, 4500.00, 'UNPAID')," +
                    "(4, '2026-04', 4500.00, 4500.00, 0.00, 'PAID')," +
                    "(5, '2026-04', 4500.00, 1500.00, 3000.00, 'PARTIAL')," +
                    "(6, '2026-04', 4500.00, 0.00, 4500.00, 'UNPAID')," +
                    "(7, '2026-04', 4500.00, 4500.00, 0.00, 'PAID')," +
                    "(8, '2026-04', 4500.00, 3000.00, 1500.00, 'PARTIAL')," +
                    "(9, '2026-04', 4500.00, 0.00, 4500.00, 'UNPAID')," +
                    "(10, '2026-04', 4500.00, 4500.00, 0.00, 'PAID')"
                );
                System.out.println("Mess bills seeded successfully.");
            }

            // Seed leave requests if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM leave_requests");
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Seeding leave requests data...");
                stmt.executeUpdate(
                    "INSERT INTO leave_requests (resident_id, start_date, end_date, reason, status) VALUES " +
                    "(1, '2026-04-20', '2026-04-22', 'Family function at home', 'APPROVED')," +
                    "(2, '2026-04-21', '2026-04-23', 'Medical appointment', 'PENDING')," +
                    "(3, '2026-04-24', '2026-04-26', 'Sister wedding', 'APPROVED')," +
                    "(4, '2026-04-18', '2026-04-19', 'Personal work', 'REJECTED')," +
                    "(5, '2026-04-27', '2026-04-30', 'Exam preparation at home', 'PENDING')," +
                    "(6, '2026-04-22', '2026-04-24', 'Festival celebration', 'APPROVED')," +
                    "(7, '2026-04-25', '2026-04-25', 'One day outing', 'PENDING')," +
                    "(8, '2026-04-19', '2026-04-20', 'Relative visit', 'REJECTED')," +
                    "(9, '2026-04-28', '2026-05-02', 'Summer vacation early leave', 'PENDING')," +
                    "(10, '2026-04-23', '2026-04-24', 'College fest participation', 'APPROVED')"
                );
                System.out.println("Leave requests seeded successfully.");
            }

            // Seed late arrival intimations if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM late_arrival_intimations");
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Seeding late arrival intimations data...");
                stmt.executeUpdate(
                    "INSERT INTO late_arrival_intimations (resident_id, arrival_date, expected_time, reason, status) VALUES " +
                    "(1, '2026-04-25', '22:30', 'Late library study session', 'ACKNOWLEDGED')," +
                    "(2, '2026-04-24', '23:00', 'Movie night with friends', 'NOTIFIED')," +
                    "(3, '2026-04-23', '21:45', 'College project group meeting', 'EXCUSED')," +
                    "(4, '2026-04-22', '22:00', 'Traffic jam due to rain', 'ACKNOWLEDGED')," +
                    "(5, '2026-04-25', '23:30', 'Part-time job shift extended', 'NOTIFIED')," +
                    "(6, '2026-04-21', '20:15', 'Sports practice ran late', 'EXCUSED')," +
                    "(7, '2026-04-24', '22:45', 'Family dinner outside', 'ACKNOWLEDGED')," +
                    "(8, '2026-04-23', '21:00', 'Missed college bus', 'NOTIFIED')," +
                    "(9, '2026-04-22', '23:15', 'Hackathon participation', 'EXCUSED')," +
                    "(10, '2026-04-25', '22:30', 'Shopping for hostel supplies', 'ACKNOWLEDGED')"
                );
                System.out.println("Late arrival intimations seeded successfully.");
            }

            // Seed student IDs if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM student_ids");
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Seeding student IDs data...");
                stmt.executeUpdate(
                    "INSERT INTO student_ids (resident_id, student_id_number, issue_date, expiry_date, status) VALUES " +
                    "(1, 'HS-2026-001', '2026-04-01', '2027-03-31', 'ISSUED')," +
                    "(2, 'HS-2026-002', '2026-04-01', '2027-03-31', 'ISSUED')," +
                    "(3, 'HS-2026-003', '2026-04-02', '2027-03-31', 'LOST')," +
                    "(4, 'HS-2026-004', '2026-04-02', '2027-03-31', 'RENEWED')," +
                    "(5, 'HS-2026-005', '2026-04-03', '2027-03-31', 'ISSUED')," +
                    "(6, 'HS-2026-006', '2026-04-03', '2025-03-31', 'EXPIRED')," +
                    "(7, 'HS-2026-007', '2026-04-04', '2027-03-31', 'ISSUED')," +
                    "(8, 'HS-2026-008', '2026-04-04', '2027-03-31', 'LOST')," +
                    "(9, 'HS-2026-009', '2026-04-05', '2027-03-31', 'RENEWED')," +
                    "(10, 'HS-2026-010', '2026-04-05', '2025-03-31', 'EXPIRED')"
                );
                System.out.println("Student IDs seeded successfully.");
            }

            // Seed notifications if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM notifications");
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Seeding notifications data...");
                stmt.executeUpdate(
                    "INSERT INTO notifications (sender_id, resident_id, title, message, type, status) VALUES " +
                    "(1, NULL, 'Welcome to Hostel', 'Welcome to the new semester! Please collect your ID cards from the office.', 'BROADCAST', 'UNREAD')," +
                    "(1, NULL, 'Mess Menu Update', 'New South Indian breakfast items have been added to the mess menu starting Monday.', 'BROADCAST', 'UNREAD')," +
                    "(1, NULL, 'Maintenance Notice', 'Water supply will be interrupted on Sunday from 10 AM to 2 PM for tank cleaning.', 'BROADCAST', 'READ')," +
                    "(1, 1, 'Leave Request Approved', 'Your leave request from 20-Apr to 22-Apr has been approved. Have a safe journey!', 'PERSONAL', 'UNREAD')," +
                    "(1, 2, 'Mess Bill Pending', 'Your mess bill for April 2026 is partially paid. Please clear the remaining 2500.', 'PERSONAL', 'UNREAD')," +
                    "(1, 3, 'Late Arrival Excused', 'Your late arrival intimation for 23-Apr has been excused by the warden.', 'PERSONAL', 'READ')," +
                    "(1, 4, 'Room Allocation Notice', 'You have been allocated to Room 104 (Double Sharing). Welcome!', 'PERSONAL', 'READ')," +
                    "(1, 5, 'Leave Request Received', 'Your leave request is under review. You will be notified once it is processed.', 'PERSONAL', 'UNREAD')," +
                    "(1, 6, 'Student ID Expired', 'Your hostel ID card has expired on 31-Mar-2025. Please apply for renewal immediately.', 'PERSONAL', 'UNREAD')," +
                    "(1, 8, 'Late Arrival Rejected', 'Your late arrival request for 19-Apr has been rejected. Please adhere to hostel timings.', 'PERSONAL', 'READ')"
                );
                System.out.println("Notifications seeded successfully.");
            }

            // Seed student users if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'STUDENT'");
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Seeding student users data...");
                stmt.executeUpdate(
                    "INSERT INTO users (username, password, role, full_name, email) VALUES " +
                    "('aarav', 'student123', 'STUDENT', 'Aarav Sharma', 'aarav@email.com')," +
                    "('vihaan', 'student123', 'STUDENT', 'Vihaan Patel', 'vihaan@email.com')," +
                    "('ishaan', 'student123', 'STUDENT', 'Ishaan Gupta', 'ishaan@email.com')," +
                    "('aditya', 'student123', 'STUDENT', 'Aditya Singh', 'aditya@email.com')," +
                    "('krishna', 'student123', 'STUDENT', 'Krishna Rao', 'krishna@email.com')," +
                    "('rohan', 'student123', 'STUDENT', 'Rohan Mehta', 'rohan@email.com')," +
                    "('arjun', 'student123', 'STUDENT', 'Arjun Nair', 'arjun@email.com')," +
                    "('dhruv', 'student123', 'STUDENT', 'Dhruv Kumar', 'dhruv@email.com')," +
                    "('vivaan', 'student123', 'STUDENT', 'Vivaan Reddy', 'vivaan@email.com')," +
                    "('ayaan', 'student123', 'STUDENT', 'Ayaan Khan', 'ayaan@email.com')"
                );
                System.out.println("10 student users seeded successfully.");
            }

            // Seed warden if empty
            rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'WARDEN'");
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Seeding warden user data...");
                stmt.executeUpdate(
                    "INSERT INTO users (username, password, role, full_name, email) VALUES " +
                    "('warden', 'warden123', 'WARDEN', 'Chief Warden', 'warden@hostel.com')"
                );
                System.out.println("Warden account created: warden / warden123");
            }

        } catch (SQLException e) {
            System.err.println("Failed to seed dummy data: " + e.getMessage());
        }
    }

    public static void ensureDefaultAdmin() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.executeUpdate(
                    "INSERT INTO users (username, password, role, full_name, email) VALUES " +
                    "('admin', 'admin123', 'ADMIN', 'System Administrator', 'admin@hostel.com')"
                );
                System.out.println("Default admin account created: admin / admin123");
            }
        } catch (SQLException e) {
            System.err.println("Failed to create default admin: " + e.getMessage());
        }
    }

    public static void printLoginCredentials() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT username, password, role, full_name FROM users ORDER BY FIELD(role, 'ADMIN', 'WARDEN', 'STUDENT'), id"
             )) {
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                    HOSTEL SYSTEM LOGIN                       ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  Role      │  Username      │  Password    │  Full Name     ║");
            System.out.println("╠════════════╪════════════════╪══════════════╪════════════════╣");
            while (rs.next()) {
                String role = String.format("%-10s", rs.getString("role"));
                String user = String.format("%-14s", rs.getString("username"));
                String pass = String.format("%-12s", rs.getString("password"));
                String name = String.format("%-14s", rs.getString("full_name"));
                System.out.println("║  " + role + "│  " + user + "│  " + pass + "│  " + name + "║");
            }
            System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        } catch (SQLException e) {
            System.err.println("Failed to print login credentials: " + e.getMessage());
        }
    }
}
