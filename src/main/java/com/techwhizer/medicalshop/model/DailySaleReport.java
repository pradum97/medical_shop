package com.techwhizer.medicalshop.model;

public class DailySaleReport {


    private int itemId;
    private int totalItems;
    private String productName;
    private double totalNetAmount;
    private int totalTab;
    private String qtyUnit , fullQuantity;
    private int stripPerTab;

    public DailySaleReport(int itemId, int totalItems, String productName, double totalNetAmount,
                           int totalTab, String qtyUnit, String fullQuantity, int stripPerTab) {
        this.itemId = itemId;
        this.totalItems = totalItems;
        this.productName = productName;
        this.totalNetAmount = totalNetAmount;
        this.totalTab = totalTab;
        this.qtyUnit = qtyUnit;
        this.fullQuantity = fullQuantity;
        this.stripPerTab = stripPerTab;
    }

    public int getStripPerTab() {
        return stripPerTab;
    }

    public void setStripPerTab(int stripPerTab) {
        this.stripPerTab = stripPerTab;
    }

    public String getFullQuantity() {
        return fullQuantity;
    }

    public void setFullQuantity(String fullQuantity) {
        this.fullQuantity = fullQuantity;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getTotalTab() {
        return totalTab;
    }

    public void setTotalTab(int totalTab) {
        this.totalTab = totalTab;
    }

    public String getQtyUnit() {
        return qtyUnit;
    }

    public void setQtyUnit(String qtyUnit) {
        this.qtyUnit = qtyUnit;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getTotalNetAmount() {
        return totalNetAmount;
    }

    public void setTotalNetAmount(double totalNetAmount) {
        this.totalNetAmount = totalNetAmount;
    }
}
