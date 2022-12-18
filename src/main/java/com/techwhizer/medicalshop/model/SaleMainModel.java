package com.techwhizer.medicalshop.model;

public class SaleMainModel {
    private int sale_main_id, patientId, sellerId;
    private String patientName, patientPhone, patientAddress;
    private double additionalDisc, totalTaxAmount, netAmount;
    private String paymentMode, billType, invoiceNumber;
    private String sellerName, sellingDate;

    public SaleMainModel(int sale_main_id, int patientId, int sellerId, String patientName, String patientPhone, String patientAddress, double additionalDisc,
                         double totalTaxAmount, double netAmount, String paymentMode, String billType, String invoiceNumber, String sellerName, String sellingDate) {
        this.sale_main_id = sale_main_id;
        this.patientId = patientId;
        this.sellerId = sellerId;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.patientAddress = patientAddress;
        this.additionalDisc = additionalDisc;
        this.totalTaxAmount = totalTaxAmount;
        this.netAmount = netAmount;
        this.paymentMode = paymentMode;
        this.billType = billType;
        this.invoiceNumber = invoiceNumber;
        this.sellerName = sellerName;
        this.sellingDate = sellingDate;
    }

    public int getSale_main_id() {
        return sale_main_id;
    }

    public void setSale_main_id(int sale_main_id) {
        this.sale_main_id = sale_main_id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public double getAdditionalDisc() {
        return additionalDisc;
    }

    public void setAdditionalDisc(double additionalDisc) {
        this.additionalDisc = additionalDisc;
    }

    public double getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(double totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellingDate() {
        return sellingDate;
    }

    public void setSellingDate(String sellingDate) {
        this.sellingDate = sellingDate;
    }
}

