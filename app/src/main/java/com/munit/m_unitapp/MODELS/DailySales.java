package com.munit.m_unitapp.MODELS;

public class DailySales {
    String user;
    String date;
    int computer_service = 0;
    int computer_sales = 0;
    int photos = 0;
    int video = 0;
    int movies = 0;
    int games = 0;
    int total = 0;

    public DailySales() {
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

    public int getPhotos() {
        return photos;
    }

    public void setPhotos(int photos) {
        this.photos = photos;
    }

    public int getVideo() {
        return video;
    }

    public void setVideo(int video) {
        this.video = video;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getTotal() {
        total = computer_service+computer_sales+photos+video+movies+games;
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
