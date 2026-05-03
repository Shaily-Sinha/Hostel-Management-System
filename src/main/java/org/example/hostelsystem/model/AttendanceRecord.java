package org.example.hostelsystem.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class AttendanceRecord {
    private int id;
    private int residentId;
    private Date attendanceDate;
    private Timestamp checkInTime;
    private BigDecimal locationLat;
    private BigDecimal locationLng;
    private boolean biometricVerified;
    private boolean locationVerified;
    private String status;

    private String residentName;

    public AttendanceRecord() {}

    public AttendanceRecord(int id, int residentId, Date attendanceDate, Timestamp checkInTime,
                            BigDecimal locationLat, BigDecimal locationLng, boolean biometricVerified,
                            boolean locationVerified, String status) {
        this.id = id;
        this.residentId = residentId;
        this.attendanceDate = attendanceDate;
        this.checkInTime = checkInTime;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.biometricVerified = biometricVerified;
        this.locationVerified = locationVerified;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getResidentId() { return residentId; }
    public void setResidentId(int residentId) { this.residentId = residentId; }

    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }

    public Timestamp getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Timestamp checkInTime) { this.checkInTime = checkInTime; }

    public BigDecimal getLocationLat() { return locationLat; }
    public void setLocationLat(BigDecimal locationLat) { this.locationLat = locationLat; }

    public BigDecimal getLocationLng() { return locationLng; }
    public void setLocationLng(BigDecimal locationLng) { this.locationLng = locationLng; }

    public boolean isBiometricVerified() { return biometricVerified; }
    public void setBiometricVerified(boolean biometricVerified) { this.biometricVerified = biometricVerified; }

    public boolean isLocationVerified() { return locationVerified; }
    public void setLocationVerified(boolean locationVerified) { this.locationVerified = locationVerified; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
}
