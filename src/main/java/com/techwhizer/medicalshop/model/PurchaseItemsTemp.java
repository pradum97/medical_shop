package com.techwhizer.medicalshop.model;

public class PurchaseItemsTemp {
    private int itemId;
    private String itemsName;
    private String batch;
    private String expiryDate;
    private String unit;
    private int tabPerStrip;
    private String packing;
    private String lotNum;
    private int quantity;
    private String quantityUnit;
    private double purchasePrice , mrp,salePrice;

    public PurchaseItemsTemp(int itemId, String itemsName, String batch, String expiryDate, String unit, int tabPerStrip, String packing,
                             String lotNum, int quantity, String quantityUnit, double purchasePrice, double mrp, double salePrice) {
        this.itemId = itemId;
        this.itemsName = itemsName;
        this.batch = batch;
        this.expiryDate = expiryDate;
        this.unit = unit;
        this.tabPerStrip = tabPerStrip;
        this.packing = packing;
        this.lotNum = lotNum;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.purchasePrice = purchasePrice;
        this.mrp = mrp;
        this.salePrice = salePrice;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemsName() {
        return itemsName;
    }

    public void setItemsName(String itemsName) {
        this.itemsName = itemsName;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getTabPerStrip() {
        return tabPerStrip;
    }

    public void setTabPerStrip(int tabPerStrip) {
        this.tabPerStrip = tabPerStrip;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public String getLotNum() {
        return lotNum;
    }

    public void setLotNum(String lotNum) {
        this.lotNum = lotNum;
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

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }
}
