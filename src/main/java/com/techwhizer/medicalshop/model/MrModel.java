package com.techwhizer.medicalshop.model;

public class MrModel {
    private int mr_id;
    private String name;
    private String phone;
    private String company;
    private String email;
    private String address;
    private String createdDate,gender;

    public MrModel(int mr_id, String name, String phone, String company, String email, String address, String createdDate,String gender) {
        this.mr_id = mr_id;
        this.name = name;
        this.phone = phone;
        this.company = company;
        this.email = email;
        this.address = address;
        this.createdDate = createdDate;
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getMr_id() {
        return mr_id;
    }

    public void setMr_id(int mr_id) {
        this.mr_id = mr_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
