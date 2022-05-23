package com.munit.m_unitapp.MODELS;

public class CarwashExpenseModel {
    int count = 1;
    String name;
    int total;

    public CarwashExpenseModel() {
    }

    public CarwashExpenseModel(String name, int total) {
        this.name = name;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    public void addToTotal(int amt) {
        this.total = this.total + amt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
