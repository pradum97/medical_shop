package com.techwhizer.medicalshop.model;

public class SaleItemsModel {

    private int sale_item_Id, productId;
    private String productName;
    private double mrp ,taxAmount, netAmount;
    private String quantity;
    private int hsn, tax ,  igst , cgst , sgst;
    private String sellingDate ;

    public SaleItemsModel(int sale_item_Id, int productId, String productName, double mrp,
                          double taxAmount, double netAmount, String quantity, int hsn, int tax, int igst, int cgst, int sgst, String sellingDate) {
        this.sale_item_Id = sale_item_Id;
        this.productId = productId;
        this.productName = productName;
        this.mrp = mrp;
        this.taxAmount = taxAmount;
        this.netAmount = netAmount;
        this.quantity = quantity;
        this.hsn = hsn;
        this.tax = tax;
        this.igst = igst;
        this.cgst = cgst;
        this.sgst = sgst;
        this.sellingDate = sellingDate;
    }

    public int getSale_item_Id() {
        return sale_item_Id;
    }

    public void setSale_item_Id(int sale_item_Id) {
        this.sale_item_Id = sale_item_Id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getHsn() {
        return hsn;
    }

    public void setHsn(int hsn) {
        this.hsn = hsn;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public int getIgst() {
        return igst;
    }

    public void setIgst(int igst) {
        this.igst = igst;
    }

    public int getCgst() {
        return cgst;
    }

    public void setCgst(int cgst) {
        this.cgst = cgst;
    }

    public int getSgst() {
        return sgst;
    }

    public void setSgst(int sgst) {
        this.sgst = sgst;
    }

    public String getSellingDate() {
        return sellingDate;
    }

    public void setSellingDate(String sellingDate) {
        this.sellingDate = sellingDate;
    }
}
