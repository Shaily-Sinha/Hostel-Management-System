package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.LateArrivalIntimationDAO;
import org.example.hostelsystem.model.LateArrivalIntimation;

import java.sql.SQLException;
import java.util.List;

public class LateArrivalIntimationService {
    private final LateArrivalIntimationDAO intimationDAO = new LateArrivalIntimationDAO();

    public void submitIntimation(LateArrivalIntimation intimation) throws SQLException {
        intimation.setStatus("NOTIFIED");
        intimationDAO.addIntimation(intimation);
    }

    public void updateIntimation(LateArrivalIntimation intimation) throws SQLException {
        intimationDAO.updateIntimation(intimation);
    }

    public void acknowledgeIntimation(int id) throws SQLException {
        LateArrivalIntimation item = intimationDAO.getIntimationById(id);
        if (item != null) {
            item.setStatus("ACKNOWLEDGED");
            intimationDAO.updateIntimation(item);
        }
    }

    public void excuseIntimation(int id) throws SQLException {
        LateArrivalIntimation item = intimationDAO.getIntimationById(id);
        if (item != null) {
            item.setStatus("EXCUSED");
            intimationDAO.updateIntimation(item);
        }
    }

    public void deleteIntimation(int id) throws SQLException {
        intimationDAO.deleteIntimation(id);
    }

    public LateArrivalIntimation getIntimationById(int id) throws SQLException {
        return intimationDAO.getIntimationById(id);
    }

    public List<LateArrivalIntimation> getIntimationsByResident(int residentId) throws SQLException {
        return intimationDAO.getIntimationsByResident(residentId);
    }

    public List<LateArrivalIntimation> getAllIntimations() throws SQLException {
        return intimationDAO.getAllIntimations();
    }

    public List<LateArrivalIntimation> getNotifiedIntimations() throws SQLException {
        return intimationDAO.getNotifiedIntimations();
    }
}
