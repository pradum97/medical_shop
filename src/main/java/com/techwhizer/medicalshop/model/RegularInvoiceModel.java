package com.techwhizer.medicalshop.model;

public class RegularInvoiceModel {

    private String productName , mfr,pack, batch,expiry;
    double mrp,discountAmount;
    private int quantity;
    private String saleDate;

    public RegularInvoiceModel(String productName, String mfr, String pack, String batch,
                               String expiry, double mrp, double discountAmount, int quantity,String saleDate) {
        this.productName = productName;
        this.mfr = mfr;
        this.pack = pack;
        this.batch = batch;
        this.expiry = expiry;
        this.mrp = mrp;
        this.discountAmount = discountAmount;
        this.quantity = quantity;
        this.saleDate = saleDate;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMfr() {
        return mfr;
    }

    public void setMfr(String mfr) {
        this.mfr = mfr;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
