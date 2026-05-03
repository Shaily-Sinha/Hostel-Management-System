package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.AttendanceDAO;
import org.example.hostelsystem.model.AttendanceRecord;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class AttendanceService {
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    public void markAttendance(int residentId, BigDecimal lat, BigDecimal lng, boolean biometricVerified, boolean locationVerified, Date attendanceDate) throws SQLException {
        Date date = attendanceDate != null ? attendanceDate : new Date(System.currentTimeMillis());
        AttendanceRecord existing = attendanceDAO.getAttendanceByResidentAndDate(residentId, date);
        if (existing != null) {
            throw new IllegalStateException("Attendance already marked for this date");
        }
        AttendanceRecord record = new AttendanceRecord();
        record.setResidentId(residentId);
        record.setAttendanceDate(date);
        record.setLocationLat(lat);
        record.setLocationLng(lng);
        record.setBiometricVerified(biometricVerified);
        record.setLocationVerified(locationVerified);
        record.setStatus("PRESENT");
        attendanceDAO.addAttendance(record);
    }

    public boolean isAttendanceMarked(int residentId, Date date) throws SQLException {
        return attendanceDAO.getAttendanceByResidentAndDate(residentId, date) != null;
    }

    public List<AttendanceRecord> getAttendanceByDate(Date date) throws SQLException {
        return attendanceDAO.getAttendanceByDate(date);
    }

    public List<AttendanceRecord> getAttendanceByResident(int residentId) throws SQLException {
        return attendanceDAO.getAttendanceByResident(residentId);
    }

    public List<AttendanceRecord> getAllAttendance() throws SQLException {
        return attendanceDAO.getAllAttendance();
    }

    public void markAbsent(int residentId, BigDecimal lat, BigDecimal lng, Date attendanceDate) throws SQLException {
        Date date = attendanceDate != null ? attendanceDate : new Date(System.currentTimeMillis());
        if (attendanceDAO.getAttendanceByResidentAndDate(residentId, date) != null) {
            return;
        }
        AttendanceRecord record = new AttendanceRecord();
        record.setResidentId(residentId);
        record.setAttendanceDate(date);
        record.setLocationLat(lat);
        record.setLocationLng(lng);
        record.setBiometricVerified(false);
        record.setLocationVerified(false);
        record.setStatus("ABSENT");
        attendanceDAO.addAttendance(record);
    }
}
