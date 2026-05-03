package org.example.hostelsystem.model;

import java.sql.Date;
import java.sql.Timestamp;

public class StudentId {
    private int id;
    private int residentId;
    private String studentIdNumber;
    private Date issueDate;
    private Date expiryDate;
    private String status;
    private Timestamp createdAt;

    private String residentName;

    public StudentId() {}

    public StudentId(int id, int residentId, String studentIdNumber, Date issueDate,
                     Date expiryDate, String status, Timestamp createdAt) {
        this.id = id;
        this.residentId = residentId;
        this.studentIdNumber = studentIdNumber;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getResidentId() { return residentId; }
    public void setResidentId(int residentId) { this.residentId = residentId; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
}
