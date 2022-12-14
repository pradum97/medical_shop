package com.techwhizer.medicalshop.model;

public class PatientModel {
    private int patientId;
    private String name;
    private String phone;
    private String address;
    private String idNumber;
    private String discount;
    private String gender;
    private String age;
    private String careOf;
    private String weight;
    private String bp;
    private String pulse;
    private String sugar;
    private String registered_date;


    public PatientModel(int patientId, String name, String phone, String address, String idNumber, String discount, String gender,
                        String age, String careOf, String weight, String bp, String pulse, String sugar, String registered_date) {
        this.patientId = patientId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.idNumber = idNumber;
        this.discount = discount;
        this.gender = gender;
        this.age = age;
        this.careOf = careOf;
        this.weight = weight;
        this.bp = bp;
        this.pulse = pulse;
        this.sugar = sugar;
        this.registered_date = registered_date;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCareOf() {
        return careOf;
    }

    public void setCareOf(String careOf) {
        this.careOf = careOf;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBp() {
        return bp;
    }

    public void setBp(String bp) {
        this.bp = bp;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getSugar() {
        return sugar;
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }

    public String getRegistered_date() {
        return registered_date;
    }

    public void setRegistered_date(String registered_date) {
        this.registered_date = registered_date;
    }
}
