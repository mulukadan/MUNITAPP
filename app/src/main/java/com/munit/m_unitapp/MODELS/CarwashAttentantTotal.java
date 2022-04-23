package com.munit.m_unitapp.MODELS;

public class CarwashAttentantTotal {
    String name;
    int total;

    public CarwashAttentantTotal() {
    }

    public CarwashAttentantTotal(String name, int total) {
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
}
