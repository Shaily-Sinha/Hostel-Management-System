package org.example.hostelsystem.attendance;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.hostelsystem.service.AttendanceService;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;

public class AttendanceServer {

    // Hostel location coordinates (example: Delhi, India - change to your actual hostel location)
    private static final double HOSTEL_LAT = 30.413506634623747;
    private static final double HOSTEL_LNG = 77.96812672857398;
    // Maximum allowed distance from hostel in meters
    private static final double MAX_DISTANCE_METERS = 100;

    private final HttpServer server;
    private final AttendanceService attendanceService;
    private volatile int pendingResidentId = -1;
    private volatile Date pendingAttendanceDate;
    private volatile boolean attendanceProcessed = false;
    private volatile String lastAttendanceMessage = "";

    public AttendanceServer(int port, AttendanceService attendanceService) throws IOException {
        this.attendanceService = attendanceService;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/attendance", new AttendancePageHandler());
        server.createContext("/verify", new VerifyHandler());
        server.setExecutor(null);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public void setPendingResidentId(int residentId, Date attendanceDate) {
        this.pendingResidentId = residentId;
        this.pendingAttendanceDate = attendanceDate;
        this.attendanceProcessed = false;
        this.lastAttendanceMessage = "";
    }

    public boolean isAttendanceProcessed() {
        return attendanceProcessed;
    }

    public String getLastAttendanceMessage() {
        return lastAttendanceMessage;
    }

    /**
     * Calculates distance between two coordinates using Haversine formula.
     * @return distance in meters
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int EARTH_RADIUS = 6371000; // meters
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    private boolean isWithinHostelPremises(double lat, double lng) {
        double distance = calculateDistance(HOSTEL_LAT, HOSTEL_LNG, lat, lng);
        return distance <= MAX_DISTANCE_METERS;
    }

    private String loadHtml() {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = getClass().getResourceAsStream("/attendance.html");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            return getFallbackHtml();
        }
        return sb.toString();
    }

    private String getFallbackHtml() {
        return "<!DOCTYPE html><html><body><h1>Error loading attendance page</h1></body></html>";
    }

    private class AttendancePageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = loadHtml();
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private class VerifyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }

            JSONObject json = new JSONObject(body.toString());
            boolean biometricVerified = json.optBoolean("biometricVerified", false);
            double lat = json.optDouble("latitude", 0);
            double lng = json.optDouble("longitude", 0);

            // Server-side location validation - do NOT trust the browser
            boolean locationVerified = false;
            double distance = -1;
            if (lat != 0 && lng != 0) {
                distance = calculateDistance(HOSTEL_LAT, HOSTEL_LNG, lat, lng);
                locationVerified = distance <= MAX_DISTANCE_METERS;
            }

            JSONObject response = new JSONObject();
            boolean success = false;
            String message;

            if (pendingResidentId == -1) {
                message = "No resident selected. Please initiate attendance from the application.";
            } else if (lat == 0 && lng == 0) {
                message = "Location data missing. Please allow location access.";
                try {
                    attendanceService.markAbsent(pendingResidentId, BigDecimal.valueOf(0), BigDecimal.valueOf(0), pendingAttendanceDate);
                } catch (SQLException ignored) {}
            } else if (!locationVerified) {
                message = String.format(
                    "Location verification failed. You are %.0f meters away from the hostel. Maximum allowed: %.0f meters.",
                    distance, MAX_DISTANCE_METERS);
                try {
                    attendanceService.markAbsent(pendingResidentId, BigDecimal.valueOf(lat), BigDecimal.valueOf(lng), pendingAttendanceDate);
                } catch (SQLException ignored) {}
            } else if (!biometricVerified) {
                message = "Biometric verification failed. Please use your device's fingerprint or face scanner.";
                try {
                    attendanceService.markAbsent(pendingResidentId, BigDecimal.valueOf(lat), BigDecimal.valueOf(lng), pendingAttendanceDate);
                } catch (SQLException ignored) {}
            } else {
                try {
                    attendanceService.markAttendance(
                        pendingResidentId,
                        BigDecimal.valueOf(lat),
                        BigDecimal.valueOf(lng),
                        true,
                        true,
                        pendingAttendanceDate
                    );
                    success = true;
                    message = String.format(
                        "Attendance marked successfully! Verified at %.0f meters from hostel.", distance);
                } catch (SQLException e) {
                    message = "Database error: " + e.getMessage();
                } catch (IllegalStateException e) {
                    message = e.getMessage();
                }
            }

            attendanceProcessed = true;
            lastAttendanceMessage = message;
            response.put("success", success);
            response.put("message", message);

            byte[] bytes = response.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
