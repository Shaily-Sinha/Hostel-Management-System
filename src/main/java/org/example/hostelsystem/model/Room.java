package org.example.hostelsystem.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Room {
    private int id;
    private String roomNumber;
    private String roomType;
    private int capacity;
    private BigDecimal pricePerMonth;
    private String status;
    private int floor;
    private String description;
    private Timestamp createdAt;

    public Room() {}

    public Room(int id, String roomNumber, String roomType, int capacity, BigDecimal pricePerMonth,
                String status, int floor, String description, Timestamp createdAt) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = capacity;
        this.pricePerMonth = pricePerMonth;
        this.status = status;
        this.floor = floor;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public BigDecimal getPricePerMonth() { return pricePerMonth; }
    public void setPricePerMonth(BigDecimal pricePerMonth) { this.pricePerMonth = pricePerMonth; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return roomNumber + " (" + roomType + ")";
    }
}
