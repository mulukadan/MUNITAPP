package com.munit.m_unitapp.MODELS;

import java.util.Date;
import java.util.List;

public class CarWashDailySummary {
    String title;
    String date;
    List<CarwashExpenseModel> attentantsTotals;
    List<CarwashExpenseModel> expenseTotals;
    int labourTotal;
    int expense;
    int overallTotal;
    int balTotal;
    int motorbikes;
    int cars;
    int trucks;
    int others;
    int profit;
    String year_week;
    String year_month;
    String year;
    int sortValue = 0;
    int count = 1; //used for summary recs count

    private DailyMeterReading waterReading = new DailyMeterReading("Water", 250);
    private DailyMeterReading DailyTokenReading = new DailyMeterReading("Token", 30);


    public CarWashDailySummary() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        //Set sortValue
        try {
            this.sortValue = (int) (new Date(date).getTime() / 1000);
//            this.sortValue = Integer.parseInt(date.substring(0,date.indexOf("/"))) + Integer.parseInt(date.substring(date.indexOf("/")+1, date.lastIndexOf("/"))) + Integer.parseInt(date.substring(date.lastIndexOf("/")+1));
        } catch (Exception e) {

        }
    }

    public List<CarwashExpenseModel> getAttentantsTotals() {
        return attentantsTotals;
    }

    public void setAttentantsTotals(List<CarwashExpenseModel> attentantsTotals) {
        this.attentantsTotals = attentantsTotals;
    }

    public int getLabourTotal() {
        return labourTotal;
    }

    public void setLabourTotal(int labourTotal) {
        this.labourTotal = labourTotal;
    }

    public int getExpense() {
        return expense;
    }

    public void setExpense(int expense) {
        this.expense = expense;
    }

    public int getOverallTotal() {
        return overallTotal;
    }

    public void setOverallTotal(int overallTotal) {
        this.overallTotal = overallTotal;
    }

    public int getBalTotal() {
        return balTotal;
    }

    public void setBalTotal(int balTotal) {
        this.balTotal = balTotal;
    }

    public String getYear_week() {
        return year_week;
    }

    public void setYear_week(String year_week) {
        this.year_week = year_week;
    }

    public String getYear_month() {
        return year_month;
    }

    public void setYear_month(String year_month) {
        this.year_month = year_month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getSortValue() {
        return sortValue;
    }

    public void setSortValue(int sortValue) {
        this.sortValue = sortValue;
    }

    public int getMotorbikes() {
        return motorbikes;
    }

    public void setMotorbikes(int motorbikes) {
        this.motorbikes = motorbikes;
    }

    public int getCars() {
        return cars;
    }

    public void setCars(int cars) {
        this.cars = cars;
    }

    public int getTrucks() {
        return trucks;
    }

    public void setTrucks(int trucks) {
        this.trucks = trucks;
    }

    public int getOthers() {
        return others;
    }

    public void setOthers(int others) {
        this.others = others;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public DailyMeterReading getWaterReading() {
        return waterReading;
    }

    public void setWaterReading(DailyMeterReading waterReading) {
        this.waterReading = waterReading;
    }

    public DailyMeterReading getDailyTokenReading() {
        return DailyTokenReading;
    }

    public void setDailyTokenReading(DailyMeterReading dailyTokenReading) {
        DailyTokenReading = dailyTokenReading;
    }

    public List<CarwashExpenseModel> getExpenseTotals() {
        return expenseTotals;
    }

    public void setExpenseTotals(List<CarwashExpenseModel> expenseTotals) {
        this.expenseTotals = expenseTotals;
    }
}
