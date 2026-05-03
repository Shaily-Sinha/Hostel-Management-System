package org.example.hostelsystem.model;

import java.sql.Date;
import java.sql.Timestamp;

public class LateArrivalIntimation {
    private int id;
    private int residentId;
    private Date arrivalDate;
    private String expectedTime;
    private String reason;
    private String status;
    private Timestamp createdAt;

    private String residentName;

    public LateArrivalIntimation() {}

    public LateArrivalIntimation(int id, int residentId, Date arrivalDate, String expectedTime,
                                 String reason, String status, Timestamp createdAt) {
        this.id = id;
        this.residentId = residentId;
        this.arrivalDate = arrivalDate;
        this.expectedTime = expectedTime;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getResidentId() { return residentId; }
    public void setResidentId(int residentId) { this.residentId = residentId; }

    public Date getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(Date arrivalDate) { this.arrivalDate = arrivalDate; }

    public String getExpectedTime() { return expectedTime; }
    public void setExpectedTime(String expectedTime) { this.expectedTime = expectedTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
}
