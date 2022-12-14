package com.techwhizer.medicalshop.model;

public class ShopModel {

 private  String shopName , shopPhone_1 , shopPhone_2 , shopEmail , shopAddress , gstNumber , foodLicence,drugLicence;

    public ShopModel(String shopName, String shopPhone_1, String shopPhone_2, String shopEmail, String shopAddress,
                     String gstNumber, String foodLicence, String drugLicence) {
        this.shopName = shopName;
        this.shopPhone_1 = shopPhone_1;
        this.shopPhone_2 = shopPhone_2;
        this.shopEmail = shopEmail;
        this.shopAddress = shopAddress;
        this.gstNumber = gstNumber;
        this.foodLicence = foodLicence;
        this.drugLicence = drugLicence;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopPhone_1() {
        return shopPhone_1;
    }

    public void setShopPhone_1(String shopPhone_1) {
        this.shopPhone_1 = shopPhone_1;
    }

    public String getShopPhone_2() {
        return shopPhone_2;
    }

    public void setShopPhone_2(String shopPhone_2) {
        this.shopPhone_2 = shopPhone_2;
    }

    public String getShopEmail() {
        return shopEmail;
    }

    public void setShopEmail(String shopEmail) {
        this.shopEmail = shopEmail;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getFoodLicence() {
        return foodLicence;
    }

    public void setFoodLicence(String foodLicence) {
        this.foodLicence = foodLicence;
    }

    public String getDrugLicence() {
        return drugLicence;
    }

    public void setDrugLicence(String drugLicence) {
        this.drugLicence = drugLicence;
    }
}
