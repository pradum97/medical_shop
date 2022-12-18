package com.techwhizer.medicalshop.model;

public class StockModel {
    private int stockId;
   private String itemName;
   private String packing;
   private int quantity;
    private String quantityUnit;
    private String batch;
    private String expiry;
    private double purchaseRate;
    private double mrp;
    private double saleRate;
    private String fullQty;

    public StockModel(int stockId, String itemName, String packing, int quantity,
                      String quantityUnit, String batch, String expiry, double purchaseRate, double mrp, double saleRate,String fullQty) {
        this.stockId = stockId;
        this.itemName = itemName;
        this.packing = packing;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.batch = batch;
        this.expiry = expiry;
        this.purchaseRate = purchaseRate;
        this.mrp = mrp;
        this.saleRate = saleRate;
        this.fullQty = fullQty;
    }

    public String getFullQty() {
        return fullQty;
    }

    public void setFullQty(String fullQty) {
        this.fullQty = fullQty;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
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
