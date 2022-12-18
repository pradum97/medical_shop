package com.techwhizer.medicalshop.model;

public class RegularInvoiceModel {

    private String productName , mfr,pack, batch,expiry;
    double mrp,discountAmount,amount;
    private int strip,pcs;

    public RegularInvoiceModel(String productName, String mfr, String pack, String batch, String expiry, double mrp,
                               double discountAmount, double amount, int strip, int pcs) {
        this.productName = productName;
        this.mfr = mfr;
        this.pack = pack;
        this.batch = batch;
        this.expiry = expiry;
        this.mrp = mrp;
        this.discountAmount = discountAmount;
        this.amount = amount;
        this.strip = strip;
        this.pcs = pcs;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getStrip() {
        return strip;
    }

    public void setStrip(int strip) {
        this.strip = strip;
    }

    public int getPcs() {
        return pcs;
    }

    public void setPcs(int pcs) {
        this.pcs = pcs;
    }
}
