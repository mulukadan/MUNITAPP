package com.munit.m_unitapp.MODELS;

public class EmployeePayment {
    String id;
    int amount;
    String description;
    int initialAdvTotal = 0;
    int current;
    String type;// Types A = Advance, S = Salary

    public EmployeePayment() {
    }

    public EmployeePayment(String id, int amount, String description, int initialAdvTotal, int current, String type) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.initialAdvTotal = initialAdvTotal;
        this.current = current;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getInitialAdvTotal() {
        return initialAdvTotal;
    }

    public void setInitialAdvTotal(int initialAdvTotal) {
        this.initialAdvTotal = initialAdvTotal;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
