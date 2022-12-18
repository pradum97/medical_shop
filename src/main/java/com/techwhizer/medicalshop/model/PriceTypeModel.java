package com.techwhizer.medicalshop.model;

public class PriceTypeModel {
    private double purchaseRate = 0.0;
    private double mrp = 0.0;
    private double saleRate = 0.0;

    public PriceTypeModel(double purchaseRate, double mrp, double saleRate) {
        this.purchaseRate = purchaseRate;
        this.mrp = mrp;
        this.saleRate = saleRate;
    }

    public double getPurchaseRate() {
        return purchaseRate;
    }

    public void setPurchaseRate(double purchaseRate) {
        this.purchaseRate = purchaseRate;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public double getSaleRate() {
        return saleRate;
    }

    public void setSaleRate(double saleRate) {
        this.saleRate = saleRate;
    }
}
