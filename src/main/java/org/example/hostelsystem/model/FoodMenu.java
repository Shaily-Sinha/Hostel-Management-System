package org.example.hostelsystem.model;

import java.sql.Date;
import java.sql.Timestamp;

public class FoodMenu {
    private int id;
    private Date menuDate;
    private String mealType;
    private String items;
    private Timestamp createdAt;

    public FoodMenu() {}

    public FoodMenu(int id, Date menuDate, String mealType, String items, Timestamp createdAt) {
        this.id = id;
        this.menuDate = menuDate;
        this.mealType = mealType;
        this.items = items;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getMenuDate() { return menuDate; }
    public void setMenuDate(Date menuDate) { this.menuDate = menuDate; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getItems() { return items; }
    public void setItems(String items) { this.items = items; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
