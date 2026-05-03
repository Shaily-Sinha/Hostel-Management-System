package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.ResidentDAO;
import org.example.hostelsystem.model.Resident;

import java.sql.SQLException;
import java.util.List;

public class ResidentService {
    private final ResidentDAO residentDAO = new ResidentDAO();

    public void addResident(Resident resident) throws SQLException {
        residentDAO.addResident(resident);
    }

    public void updateResident(Resident resident) throws SQLException {
        residentDAO.updateResident(resident);
    }

    public void deleteResident(int id) throws SQLException {
        residentDAO.deleteResident(id);
    }

    public Resident getResidentById(int id) throws SQLException {
        return residentDAO.getResidentById(id);
    }

    public List<Resident> getAllResidents() throws SQLException {
        return residentDAO.getAllResidents();
    }

    public List<Resident> getActiveResidents() throws SQLException {
        return residentDAO.getActiveResidents();
    }
}
