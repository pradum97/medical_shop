package com.techwhizer.medicalshop.model.chooserModel;

public class BatchChooserModel {

    private int stockId;
    private String itemName;
    private String batch;
    private String expiryDate;
    private int quantity;
    private String quantityUnit;
    private String fullQty;
    private int stripTab;
    private int purchaseItemId;

    public BatchChooserModel(int stockId, String itemName, String batch,
                             String expiryDate, int quantity, String quantityUnit , String fullQty,int stripTab,int purchaseItemId) {
        this.stockId = stockId;
        this.itemName = itemName;
        this.batch = batch;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.fullQty = fullQty;
        this.stripTab = stripTab;
        this.purchaseItemId = purchaseItemId;
    }

    public int getPurchaseItemId() {
        return purchaseItemId;
    }

    public void setPurchaseItemId(int purchaseItemId) {
        this.purchaseItemId = purchaseItemId;
    }

    public int getStripTab() {
        return stripTab;
    }

    public void setStripTab(int stripTab) {
        this.stripTab = stripTab;
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
}
