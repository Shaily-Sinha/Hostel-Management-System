 # Hostel Management System

A desktop-based Hostel Management System built with Java Swing and MySQL. It provides role-based access control for Administrators, Wardens, and Students to manage rooms, residents, bookings, attendance, mess services, leave requests, notifications, and more.

---

## Tech Stack

- **Language:** Java 25
- **UI Framework:** Java Swing (with system look-and-feel)
- **Database:** MySQL 8.0+
- **Build Tool:** Maven
- **Dependencies:**
  - `mysql-connector-j` 9.2.0 (MySQL JDBC driver)
  - `json` 20250107 (JSON handling for attendance server)
- **Biometric Verification:** WebAuthn Platform Authenticator (browser-based fingerprint scan)
- **Location Verification:** Browser Geolocation API with server-side Haversine distance validation

---

## Features

### Role-Based Access Control
- **Admin:** Full access to all modules and user management
- **Warden:** Access to management panels for rooms, residents, bookings, attendance, mess, leave, and notifications
- **Student:** Access to personal profile, attendance marking, leave requests, late arrival notifications, mess menu view, student ID view, and personal notifications

### Core Modules

| Module | Description |
|--------|-------------|
| **Room Management** | Add, update, and manage hostel rooms with capacity, type, price, floor, and availability status |
| **Resident Management** | Manage resident profiles, assign rooms, track check-in/check-out dates, and monitor active status |
| **Booking Management** | Handle room bookings with check-in/check-out dates, status tracking, and total amount calculation |
| **Attendance System** | Browser-based attendance marking with GPS location verification and biometric authentication (WebAuthn). Automatic ABSENT marking on failure. Custom date selection supported. |
| **Mess & Food Management** | Manage daily food menus (Breakfast, Lunch, Dinner) and generate monthly mess bills for residents |
| **Leave Requests** | Residents can apply for leave; admins/wardens can approve or reject requests |
| **Late Arrival Intimations** | Residents can notify about late arrivals with expected time and reason |
| **Student ID Management** | Issue and track student ID cards linked to residents |
| **Notifications** | Send broadcast or individual notifications from admin to residents |
| **User Management** | Create and manage system users with role assignments (admin only) |

---

## Architecture

The project follows a layered architecture pattern:

```
org.example.hostelsystem
|
|-- attendance/        # HTTP server for browser-based attendance verification
|-- dao/               # Data Access Objects (database CRUD operations)
|-- db/                # Database connection and initialization
|-- model/             # Entity/POJO classes
|-- service/           # Business logic layer
|-- ui/                # Swing UI panels and frames
|-- ui/util/           # UI utilities (e.g., validation)
```

### Layers

1. **Model Layer** (`model/`)
   - Plain Java objects representing database entities: `User`, `Resident`, `Room`, `Booking`, `AttendanceRecord`, `FoodMenu`, `MessBill`, `LeaveRequest`, `LateArrivalIntimation`, `StudentId`, `Notification`

2. **DAO Layer** (`dao/`)
   - Direct database interaction using JDBC
   - One DAO per entity: `UserDAO`, `ResidentDAO`, `RoomDAO`, `BookingDAO`, `AttendanceDAO`, `FoodMenuDAO`, `MessBillDAO`, `LeaveRequestDAO`, `LateArrivalIntimationDAO`, `StudentIdDAO`, `NotificationDAO`

3. **Service Layer** (`service/`)
   - Business logic and transaction coordination
   - `AuthService` handles login/logout and session management
   - Other services: `ResidentService`, `RoomService`, `BookingService`, `AttendanceService`, `FoodMenuService`, `MessBillService`, `LeaveRequestService`, `LateArrivalIntimationService`, `NotificationService`, `StudentIdService`

4. **UI Layer** (`ui/`)
   - Swing-based desktop interface
   - `LoginFrame`: Authentication screen
   - `MainDashboard`: Role-based tabbed dashboard
   - Management panels for admin/warden
   - Student panels for self-service features

5. **Attendance Server** (`attendance/`)
   - Embedded HTTP server (`AttendanceServer`) that serves the attendance verification HTML page
   - Handles location verification and biometric confirmation via REST endpoint
   - Uses shared in-memory state (`pendingResidentId`) to bind the Swing UI attendance request to the browser verification session

---

## Database Schema

The application auto-initializes the following MySQL database and tables on startup:

**Database:** `hostel_db`

| Table | Description |
|-------|-------------|
| `users` | System users with roles (admin, warden, student) |
| `rooms` | Hostel room inventory with capacity, type, price, and status |
| `residents` | Resident profiles with personal details and room assignment |
| `bookings` | Room booking records |
| `attendance` | Daily attendance with GPS coordinates, biometric/location flags, and status |
| `food_menu` | Daily meal menus categorized by meal type |
| `mess_bills` | Monthly mess billing per resident |
| `leave_requests` | Resident leave applications with approval status |
| `late_arrival_intimations` | Late arrival notifications from residents |
| `student_ids` | Student ID card records |
| `notifications` | System notifications (broadcast or targeted) |

### Database Configuration

Edit `DatabaseConnection.java` before running:

```java
private static final String URL = "jdbc:mysql://localhost:3306/hostel_db";
private static final String USER = "root";
private static final String PASSWORD = ""; // Default XAMPP password is empty. Update if yours is different.
```

---

## How to Run

### Option 1: Using run.bat (Windows)

```batch
run.bat
```

This script will:
1. Download required JAR dependencies (MySQL Connector, JSON library) automatically
2. Compile all Java source files
3. Copy resources to the output directory
4. Launch the application

### Option 2: Using Maven

Compile the project:
```bash
mvn compile
```

Then run using the provided script (recommended):
```bash
run.bat
```

Or manually (ensure dependencies are in `target/dependency` first):
```bash
java -cp "target/classes;target/dependency/*" org.example.hostelsystem.HostelSystemApplication
```

### Prerequisites

- JDK 25 installed and configured in PATH
- MySQL Server running on `localhost:3306`
- MySQL user credentials configured in `DatabaseConnection.java`

---

## First-Time Setup

There are **no default login credentials**. The application does not auto-seed any users.

To create the first admin, run this SQL in your MySQL client (e.g., phpMyAdmin):

```sql
USE hostel_db;
INSERT INTO users (username, password, role, full_name, email)
VALUES ('admin', 'admin123', 'ADMIN', 'System Administrator', 'admin@hostel.com');
```

After the first admin is created, additional users can be added from the **User Management** panel inside the app.

---

## Attendance Verification Flow

The attendance system uses a hybrid desktop-browser architecture:

1. **Initiate:** User clicks "Mark Attendance" in the Swing UI
2. **Session State:** The backend stores the resident ID and attendance date in shared memory (`pendingResidentId`)
3. **Browser Launch:** A browser opens to `http://localhost:8765/attendance`
4. **Location Verification:** The HTML page requests GPS coordinates via the Geolocation API and validates distance from the hostel using the Haversine formula (max 100 meters)
5. **Biometric Verification:** The page uses the WebAuthn API with `authenticatorAttachment: 'platform'` to trigger the device's native fingerprint or face scanner
6. **Server Validation:** Both location and biometric results are sent to the `/verify` endpoint. The server uses the pending resident ID, recalculates distance server-side, and marks attendance as:
   - **PRESENT** if both location and biometric pass
   - **ABSENT** if either check fails (automatic failure recording)

**Note:** Because the server uses a single shared state variable for the pending resident, only one attendance session should be active at a time. If two users initiate attendance simultaneously, the second request will overwrite the first.

---

## Project Structure

```
HostelSystem/
├── src/
│   ├── main/
│   │   ├── java/org/example/hostelsystem/
│   │   │   ├── HostelSystemApplication.java
│   │   │   ├── attendance/
│   │   │   │   └── AttendanceServer.java
│   │   │   ├── dao/
│   │   │   │   ├── AttendanceDAO.java
│   │   │   │   ├── BookingDAO.java
│   │   │   │   ├── FoodMenuDAO.java
│   │   │   │   ├── LateArrivalIntimationDAO.java
│   │   │   │   ├── LeaveRequestDAO.java
│   │   │   │   ├── MessBillDAO.java
│   │   │   │   ├── NotificationDAO.java
│   │   │   │   ├── ResidentDAO.java
│   │   │   │   ├── RoomDAO.java
│   │   │   │   ├── StudentIdDAO.java
│   │   │   │   └── UserDAO.java
│   │   │   ├── db/
│   │   │   │   └── DatabaseConnection.java
│   │   │   ├── model/
│   │   │   │   ├── AttendanceRecord.java
│   │   │   │   ├── Booking.java
│   │   │   │   ├── FoodMenu.java
│   │   │   │   ├── LateArrivalIntimation.java
│   │   │   │   ├── LeaveRequest.java
│   │   │   │   ├── MessBill.java
│   │   │   │   ├── Notification.java
│   │   │   │   ├── Resident.java
│   │   │   │   ├── Room.java
│   │   │   │   ├── StudentId.java
│   │   │   │   └── User.java
│   │   │   ├── service/
│   │   │   │   ├── AttendanceService.java
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── BookingService.java
│   │   │   │   ├── FoodMenuService.java
│   │   │   │   ├── LateArrivalIntimationService.java
│   │   │   │   ├── LeaveRequestService.java
│   │   │   │   ├── MessBillService.java
│   │   │   │   ├── NotificationService.java
│   │   │   │   ├── ResidentService.java
│   │   │   │   ├── RoomService.java
│   │   │   │   └── StudentIdService.java
│   │   │   └── ui/
│   │   │       ├── util/
│   │   │       │   └── ValidationUtil.java
│   │   │       ├── AttendancePanel.java
│   │   │       ├── BookingManagementPanel.java
│   │   │       ├── LateArrivalManagementPanel.java
│   │   │       ├── LateArrivalStudentPanel.java
│   │   │       ├── LeaveManagementPanel.java
│   │   │       ├── LeaveStudentPanel.java
│   │   │       ├── LoginFrame.java
│   │   │       ├── MainDashboard.java
│   │   │       ├── MessManagementPanel.java
│   │   │       ├── MessStudentPanel.java
│   │   │       ├── NotificationManagementPanel.java
│   │   │       ├── NotificationStudentPanel.java
│   │   │       ├── ResidentManagementPanel.java
│   │   │       ├── RoomManagementPanel.java
│   │   │       ├── StudentIdManagementPanel.java
│   │   │       ├── StudentIdStudentPanel.java
│   │   │       ├── StudentProfilePanel.java
│   │   │       └── UserManagementPanel.java
│   │   └── resources/
│   │       └── attendance.html
│   └── test/
├── pom.xml
├── run.bat
└── README.md
```

---

## Important Notes

- **MySQL Password:** Update the `PASSWORD` field in `DatabaseConnection.java` before running the application for the first time.
- **Dummy Data:** The system automatically seeds sample data (rooms, residents, users, notifications, student IDs) on first launch if the database is empty.
- **Attendance Location:** The default hostel coordinates are hardcoded in `AttendanceServer.java` and `attendance.html`. Update `HOSTEL_LAT` and `HOSTEL_LNG` to match your actual hostel location.
- **Browser Support:** The biometric verification requires a modern browser that supports the WebAuthn API (Chrome, Edge, Safari, Firefox).
- **Platform Authenticator:** Biometric verification uses the device's built-in sensor (Windows Hello, Touch ID, Face ID, Android fingerprint). It does not support external USB fingerprint scanners.
