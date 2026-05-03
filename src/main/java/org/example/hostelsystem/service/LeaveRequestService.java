package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.LeaveRequestDAO;
import org.example.hostelsystem.model.LeaveRequest;

import java.sql.SQLException;
import java.util.List;

public class LeaveRequestService {
    private final LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();

    public void applyForLeave(LeaveRequest request) throws SQLException {
        request.setStatus("PENDING");
        leaveRequestDAO.addLeaveRequest(request);
    }

    public void updateLeaveRequest(LeaveRequest request) throws SQLException {
        leaveRequestDAO.updateLeaveRequest(request);
    }

    public void approveLeave(int id) throws SQLException {
        LeaveRequest request = leaveRequestDAO.getLeaveRequestById(id);
        if (request != null) {
            request.setStatus("APPROVED");
            leaveRequestDAO.updateLeaveRequest(request);
        }
    }

    public void rejectLeave(int id) throws SQLException {
        LeaveRequest request = leaveRequestDAO.getLeaveRequestById(id);
        if (request != null) {
            request.setStatus("REJECTED");
            leaveRequestDAO.updateLeaveRequest(request);
        }
    }

    public void deleteLeaveRequest(int id) throws SQLException {
        leaveRequestDAO.deleteLeaveRequest(id);
    }

    public LeaveRequest getLeaveRequestById(int id) throws SQLException {
        return leaveRequestDAO.getLeaveRequestById(id);
    }

    public List<LeaveRequest> getLeaveRequestsByResident(int residentId) throws SQLException {
        return leaveRequestDAO.getLeaveRequestsByResident(residentId);
    }

    public List<LeaveRequest> getAllLeaveRequests() throws SQLException {
        return leaveRequestDAO.getAllLeaveRequests();
    }

    public List<LeaveRequest> getPendingLeaveRequests() throws SQLException {
        return leaveRequestDAO.getPendingLeaveRequests();
    }
}
