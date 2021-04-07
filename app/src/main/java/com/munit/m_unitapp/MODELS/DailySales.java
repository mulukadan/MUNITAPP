package com.munit.m_unitapp.MODELS;

public class DailySales {
    String userId;
    String userName;
    String date;
    int computer_service = 0;
    int computer_sales = 0;
    int movies = 0;
    int games = 0;
    int mpesaTill =0;
    int cashPayment =0;
    int total = 0;

    public DailySales() {
    }

    public DailySales(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getComputer_service() {
        return computer_service;
    }

    public void setComputer_service(int computer_service) {
        this.computer_service = computer_service;
    }

    public int getComputer_sales() {
        return computer_sales;
    }

    public void setComputer_sales(int computer_sales) {
        this.computer_sales = computer_sales;
    }

    public int getMovies() {
        return movies;
    }

    public void setMovies(int movies) {
        this.movies = movies;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) {  this.userId = userId;   }

    public int getMpesaTill() {
        return mpesaTill;
    }

    public void setMpesaTill(int mpesaTill) {
        this.mpesaTill = mpesaTill;
    }

    public int getCashPayment() {
        cashPayment = getTotal()-getMpesaTill();
        return cashPayment;
    }

    public void setCashPayment(int cashPayment) {
        this.cashPayment = cashPayment;
    }

    public int getTotal() {
        total = computer_service+computer_sales+movies+games;
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
