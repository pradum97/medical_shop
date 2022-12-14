package com.techwhizer.medicalshop.model;

public class DealerModel {

    private int dealerId;
    private String dealerName , dealerPhone , dealerEmail , dealerGstNum ,dealerAddress  , dealerState , addedSate,dealerDl;

    public DealerModel(int dealerId, String dealerName, String dealerPhone, String dealerEmail, String dealerGstNum,
                       String dealerAddress, String dealerState, String addedSate,String dealerDl) {
        this.dealerId = dealerId;
        this.dealerName = dealerName;
        this.dealerPhone = dealerPhone;
        this.dealerEmail = dealerEmail;
        this.dealerGstNum = dealerGstNum;
        this.dealerAddress = dealerAddress;
        this.dealerState = dealerState;
        this.addedSate = addedSate;
        this.dealerDl = dealerDl;
    }

    public String getDealerDl() {
        return dealerDl;
    }

    public void setDealerDl(String dealerDl) {
        this.dealerDl = dealerDl;
    }

    @Override
    public String toString() {
        return this.getDealerName();
    }

    public int getDealerId() {
        return dealerId;
    }

    public void setDealerId(int dealerId) {
        this.dealerId = dealerId;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerPhone() {
        return dealerPhone;
    }

    public void setDealerPhone(String dealerPhone) {
        this.dealerPhone = dealerPhone;
    }

    public String getDealerEmail() {
        return dealerEmail;
    }

    public void setDealerEmail(String dealerEmail) {
        this.dealerEmail = dealerEmail;
    }

    public String getDealerGstNum() {
        return dealerGstNum;
    }

    public void setDealerGstNum(String dealerGstNum) {
        this.dealerGstNum = dealerGstNum;
    }

    public String getDealerAddress() {
        return dealerAddress;
    }

    public void setDealerAddress(String dealerAddress) {
        this.dealerAddress = dealerAddress;
    }

    public String getDealerState() {
        return dealerState;
    }

    public void setDealerState(String dealerState) {
        this.dealerState = dealerState;
    }

    public String getAddedSate() {
        return addedSate;
    }

    public void setAddedSate(String addedSate) {
        this.addedSate = addedSate;
    }
}
