package org.example.hostelsystem.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class MessBill {
    private int id;
    private int residentId;
    private String billMonth;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal fineAmount; // New field for late payment fines
    private BigDecimal dueAmount;
    private String status;
    private Timestamp createdAt;

    private String residentName;

    public MessBill() {
        // Good practice to initialize BigDecimals to avoid NullPointerExceptions
        this.totalAmount = BigDecimal.ZERO;
        this.paidAmount = BigDecimal.ZERO;
        this.fineAmount = BigDecimal.ZERO;
        this.dueAmount = BigDecimal.ZERO;
    }

    public MessBill(int id, int residentId, String billMonth, BigDecimal totalAmount,
                    BigDecimal paidAmount, BigDecimal fineAmount, BigDecimal dueAmount,
                    String status, Timestamp createdAt) {
        this.id = id;
        this.residentId = residentId;
        this.billMonth = billMonth;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.fineAmount = fineAmount; // Included in constructor
        this.dueAmount = dueAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getResidentId() { return residentId; }
    public void setResidentId(int residentId) { this.residentId = residentId; }

    public String getBillMonth() { return billMonth; }
    public void setBillMonth(String billMonth) { this.billMonth = billMonth; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    // --- New Getter and Setter for Fine Amount ---
    public BigDecimal getFineAmount() { return fineAmount; }
    public void setFineAmount(BigDecimal fineAmount) { this.fineAmount = fineAmount; }

    public BigDecimal getDueAmount() { return dueAmount; }
    public void setDueAmount(BigDecimal dueAmount) { this.dueAmount = dueAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
}