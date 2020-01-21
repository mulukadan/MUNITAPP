package com.munit.m_unitapp.MODELS;

public class HSMSObject {
    String initials;
    String schoolName;
    int cost;
    String installDate;
    String lastActivationDate;
    String expiryDate;
    String ActiveKey;

    public HSMSObject() {
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }

    public String getLastActivationDate() {
        return lastActivationDate;
    }

    public void setLastActivationDate(String lastActivationDate) {
        this.lastActivationDate = lastActivationDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getActiveKey() {
        return ActiveKey;
    }

    public void setActiveKey(String activeKey) {
        ActiveKey = activeKey;
    }
}
