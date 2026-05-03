package org.example.hostelsystem.service;

import org.example.hostelsystem.dao.FoodMenuDAO;
import org.example.hostelsystem.model.FoodMenu;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class FoodMenuService {
    private final FoodMenuDAO foodMenuDAO = new FoodMenuDAO();

    public void addMenu(FoodMenu menu) throws SQLException {
        foodMenuDAO.addMenu(menu);
    }

    public void updateMenu(FoodMenu menu) throws SQLException {
        foodMenuDAO.updateMenu(menu);
    }

    public void deleteMenu(int id) throws SQLException {
        foodMenuDAO.deleteMenu(id);
    }

    public FoodMenu getMenuById(int id) throws SQLException {
        return foodMenuDAO.getMenuById(id);
    }

    public List<FoodMenu> getMenuByDate(Date date) throws SQLException {
        return foodMenuDAO.getMenuByDate(date);
    }

    public List<FoodMenu> getAllMenus() throws SQLException {
        return foodMenuDAO.getAllMenus();
    }
}
