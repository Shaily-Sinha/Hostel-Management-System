package org.example.hostelsystem.model;

import java.sql.Timestamp;

public class Notification {
    private int id;
    private int senderId;
    private Integer residentId;
    private String title;
    private String message;
    private String type;
    private String status;
    private Timestamp createdAt;

    private String senderName;
    private String residentName;

    public Notification() {}

    public Notification(int id, int senderId, Integer residentId, String title,
                        String message, String type, String status, Timestamp createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.residentId = residentId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public Integer getResidentId() { return residentId; }
    public void setResidentId(Integer residentId) { this.residentId = residentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
}
