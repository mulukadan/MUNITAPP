package com.munit.m_unitapp.MODELS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Employee {
    String id;
    String name = "";
    String status;
    String gender;
    String imgUrl="";
    String phoneNo = "";
    Boolean active;
    String employmentDate = "";
    String dob = "";
    String terminationDate;
    String department = "";
    int salary= 0;
    int advance= 0;
    String idFrontImage ="";
    String idBackImage ="";
    String JobDescription = "";
    List<EmployeePayment> payments = new ArrayList<>();

    public Employee() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(String employmentDate) {
        this.employmentDate = employmentDate;
    }

    public String getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(String terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getIdFrontImage() {
        return idFrontImage;
    }

    public void setIdFrontImage(String idFrontImage) {
        this.idFrontImage = idFrontImage;
    }

    public String getIdBackImage() {
        return idBackImage;
    }

    public void setIdBackImage(String idBackImage) {
        this.idBackImage = idBackImage;
    }

    public String getJobDescription() {
        return JobDescription;
    }

    public void setJobDescription(String jobDescription) {
        JobDescription = jobDescription;
    }

    public List<EmployeePayment> getPayments() {
        return payments;
    }
    public List<EmployeePayment> getPaymentsReversed() {
        List<EmployeePayment> reversrdPayments = new ArrayList<>();
        reversrdPayments.addAll(payments);

        Collections.reverse(reversrdPayments);

        return reversrdPayments;
    }

    public void setPayments(List<EmployeePayment> payments) {
        this.payments = payments;
    }

    public int getAdvance() {
        return advance;
    }

    public void setAdvance(int advance) {
        this.advance = advance;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getIdex(List<Employee> employees , String eID){
        for (int i = 0; i<employees.size(); i++){
            if(employees.get(i).getId().equals(eID)){
                return i;
            }
        }
        return -1;
    }
}
