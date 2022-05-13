package com.munit.m_unitapp.MODELS;

public class DailyMeterReading {
    String name;
    float start=0;
    float end=0;
    float units;
    float unitCost;
    int cost =0;
    String description;

    public DailyMeterReading() {
    }

    public DailyMeterReading(String name, float unitCost) {
        this.name = name;
        this.unitCost = unitCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
        if(end !=0){
            this.units = this.end - this.start;
            if(this.units <0){
                this.units = this.units * -1;
            }
            this.cost = (int) (this.unitCost * this.units);
        }
    }

    public float getUnits() {
        return units;
    }

    public void setUnits(float units) {
        this.units = units;
    }

    public float getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(float unitCost) {
        this.unitCost = unitCost;
    }

    public float getCost() {
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
