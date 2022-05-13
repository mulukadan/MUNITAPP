package com.munit.m_unitapp.MODELS;

public class CarwashRec {
    String recordedBy;
    String date;
    String recordedTimeNDate;
    String vehicleType;
    String regNo;
    String attendant;
    int amount;
    String desc;
    boolean paid;

    public CarwashRec() {
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRecordedTimeNDate() {
        return recordedTimeNDate;
    }

    public void setRecordedTimeNDate(String recordedTimeNDate) {
        this.recordedTimeNDate = recordedTimeNDate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getAttendant() {
        return attendant;
    }

    public void setAttendant(String attendant) {
        this.attendant = attendant;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

}
