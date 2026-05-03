package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.MessBillDAO;
import org.example.hostelsystem.model.MessBill;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class MessBillService {
    private final MessBillDAO messBillDAO = new MessBillDAO();

    public void addBill(MessBill bill) throws SQLException {
        if (bill.getPaidAmount() == null) {
            bill.setPaidAmount(BigDecimal.ZERO);
        }
        bill.setDueAmount(bill.getTotalAmount().subtract(bill.getPaidAmount()));
        bill.setStatus(calculateStatus(bill.getPaidAmount(), bill.getTotalAmount()));
        messBillDAO.addBill(bill);
    }

    public void updateBill(MessBill bill) throws SQLException {
        if (bill.getPaidAmount() == null) {
            bill.setPaidAmount(BigDecimal.ZERO);
        }
        bill.setDueAmount(bill.getTotalAmount().subtract(bill.getPaidAmount()));
        bill.setStatus(calculateStatus(bill.getPaidAmount(), bill.getTotalAmount()));
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

    private String calculateStatus(BigDecimal paid, BigDecimal total) {
        if (paid.compareTo(total) >= 0) {
            return "PAID";
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            return "PARTIAL";
        } else {
            return "UNPAID";
        }
    }
}
