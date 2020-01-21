package com.munit.m_unitapp.MODELS;

public class PoolTableRecord {
    String date;
    double tableOneTotal = 0;
    double tableTwoTotal = 0;
    double total = 0;
    String user;
    String employee;

    public PoolTableRecord() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTableOneTotal() {
        return tableOneTotal;
    }

    public void setTableOneTotal(double tableOneTotal) {
        this.tableOneTotal = tableOneTotal;
    }

    public double getTableTwoTotal() {
        return tableTwoTotal;
    }

    public void setTableTwoTotal(double tableTwoTotal) {
        this.tableTwoTotal = tableTwoTotal;
    }

    public double getTotal() {

        return tableOneTotal + tableTwoTotal;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }
}
