package com.techwhizer.medicalshop.model;

public class GstInvoiceModel {

    private String productName, mfr, pack, batch, expiry;
    double mrp, discountAmount;
    private int quantity;
    private String saleDate;
    private long hsn;
    private double sgst;
    private double cgst;
    private double igst;

    public GstInvoiceModel(String productName, String mfr, String pack, String batch,
                           String expiry, double mrp, double discountAmount, int quantity, String saleDate, long hsn,
                           double sgst, double cgst, double igst) {
        this.productName = productName;
        this.mfr = mfr;
        this.pack = pack;
        this.batch = batch;
        this.expiry = expiry;
        this.mrp = mrp;
        this.discountAmount = discountAmount;
        this.quantity = quantity;
        this.saleDate = saleDate;
        this.hsn = hsn;
        this.sgst = sgst;
        this.cgst = cgst;
        this.igst = igst;
    }

    public long getHsn() {
        return hsn;
    }

    public void setHsn(long hsn) {
        this.hsn = hsn;
    }

    public double getSgst() {
        return sgst;
    }

    public void setSgst(double sgst) {
        this.sgst = sgst;
    }

    public double getCgst() {
        return cgst;
    }

    public void setCgst(double cgst) {
        this.cgst = cgst;
    }

    public double getIgst() {
        return igst;
    }

    public void setIgst(double igst) {
        this.igst = igst;
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
