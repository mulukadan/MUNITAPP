package com.munit.m_unitapp.MODELS;

import java.text.DecimalFormat;

public class DailyMeterReading {
    String name;
    double start=0;
    double end=0;
    double units;
    double unitCost;
    int cost =0;
    String description;

    public DailyMeterReading() {
    }

    public DailyMeterReading(String name, double unitCost) {
        this.name = name;
        this.unitCost = unitCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
        if(end !=0){
            this.units = this.end - this.start;
            if(this.units <0){
                this.units = this.units * -1;
            }
            this.cost = (int) (this.unitCost * this.units);
        }
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
