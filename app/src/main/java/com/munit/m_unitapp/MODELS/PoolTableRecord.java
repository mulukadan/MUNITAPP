package com.munit.m_unitapp.MODELS;

public class PoolTableRecord {
    String date;
    double tableOneTotal = 0;
    double tableTwoTotal = 0;
    double tableThreeTotal = 0;
    double biz1Total = 0;
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

    public double getTableThreeTotal() {
        return tableThreeTotal;
    }

    public void setTableThreeTotal(double tableThreeTotal) {
        this.tableThreeTotal = tableThreeTotal;
    }

    public double getBiz1Total() {
        return tableOneTotal + tableTwoTotal;
    }

    public void setBiz1Total(double biz1Total) {
        this.biz1Total = biz1Total;
    }

    public double getTotal() {
        return tableOneTotal + tableTwoTotal+ tableThreeTotal;
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
