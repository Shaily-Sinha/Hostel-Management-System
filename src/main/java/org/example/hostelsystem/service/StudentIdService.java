package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.StudentIdDAO;
import org.example.hostelsystem.model.StudentId;

import java.sql.SQLException;
import java.util.List;

public class StudentIdService {
    private final StudentIdDAO studentIdDAO = new StudentIdDAO();

    public void addStudentId(StudentId sid) throws SQLException {
        studentIdDAO.addStudentId(sid);
    }

    public void updateStudentId(StudentId sid) throws SQLException {
        studentIdDAO.updateStudentId(sid);
    }

    public void deleteStudentId(int id) throws SQLException {
        studentIdDAO.deleteStudentId(id);
    }

    public StudentId getStudentIdById(int id) throws SQLException {
        return studentIdDAO.getStudentIdById(id);
    }

    public StudentId getStudentIdByResident(int residentId) throws SQLException {
        return studentIdDAO.getStudentIdByResident(residentId);
    }

    public List<StudentId> getAllStudentIds() throws SQLException {
        return studentIdDAO.getAllStudentIds();
    }
}
