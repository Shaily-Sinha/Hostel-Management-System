package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.MessBillDAO;
import org.example.hostelsystem.model.MessBill;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class MessBillService {
    private final MessBillDAO messBillDAO = new MessBillDAO();

    public void addBill(MessBill bill) throws SQLException {
        initializeDefaults(bill);
        recalculateBill(bill);
        messBillDAO.addBill(bill);
    }

    public void updateBill(MessBill bill) throws SQLException {
        initializeDefaults(bill);
        recalculateBill(bill);
        messBillDAO.updateBill(bill);
    }

    public void deleteBill(int id) throws SQLException {
        messBillDAO.deleteBill(id);
    }

    public MessBill getBillById(int id) throws SQLException {
        return messBillDAO.getBillById(id);
    }

    public List<MessBill> getBillsByResident(int residentId) throws SQLException {
        return messBillDAO.getBillsByResident(residentId);
    }

    public List<MessBill> getAllBills() throws SQLException {
        return messBillDAO.getAllBills();
    }

    // ==========================================
    // NEW FEATURES: Payments & Fines
    // ==========================================

    /**
     * Handles partial payments from the UI, updates the bill, and logs the history.
     */
    public void recordPayment(int billId, BigDecimal paymentAmount, String method) throws SQLException {
        // 1. Fetch the current bill
        MessBill bill = messBillDAO.getBillById(billId);
        if (bill == null) {
            throw new SQLException("Bill not found with ID: " + billId);
        }

        // 2. Add the new payment to the existing paid amount
        BigDecimal newPaidAmount = bill.getPaidAmount().add(paymentAmount);
        bill.setPaidAmount(newPaidAmount);

        // 3. Recalculate Due and Status
        recalculateBill(bill);

        // 4. Update the bill in the database
        messBillDAO.updateBill(bill);

        // 5. Save the payment history to the 'mess_payments' table
        messBillDAO.insertPaymentHistory(billId, paymentAmount, method);
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    // Prevents NullPointerExceptions if a value is missing
    private void initializeDefaults(MessBill bill) {
        if (bill.getTotalAmount() == null) bill.setTotalAmount(BigDecimal.ZERO);
        if (bill.getPaidAmount() == null) bill.setPaidAmount(BigDecimal.ZERO);
        if (bill.getFineAmount() == null) bill.setFineAmount(BigDecimal.ZERO);
    }

    // Keeps Due Amount and Status accurate based on Fines and Payments
    private void recalculateBill(MessBill bill) {
        // Total Owed = Total Amount + Fine Amount
        BigDecimal totalOwed = bill.getTotalAmount().add(bill.getFineAmount());

        // Due = Total Owed - Paid
        BigDecimal due = totalOwed.subtract(bill.getPaidAmount());

        // Prevent negative due amounts (if they overpay)
        if (due.compareTo(BigDecimal.ZERO) < 0) {
            due = BigDecimal.ZERO;
        }

        bill.setDueAmount(due);
        bill.setStatus(calculateStatus(bill.getPaidAmount(), totalOwed));
    }

    private String calculateStatus(BigDecimal paid, BigDecimal totalOwed) {
        if (paid.compareTo(totalOwed) >= 0) {
            return "PAID";
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            return "PARTIAL";
        } else {
            return "UNPAID";
        }
    }
}