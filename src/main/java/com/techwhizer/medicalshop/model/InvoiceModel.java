package com.techwhizer.medicalshop.model;

public class InvoiceModel {

    private int sale_main_id , totalItems;
    private String patientName , patientPhone  ,billType , invoiceDate , invoiceNumber ;

    public InvoiceModel(int sale_main_id, int totalItems, String patientName, String patientPhone,
                        String billType, String invoiceDate, String invoiceNumber) {
        this.sale_main_id = sale_main_id;
        this.totalItems = totalItems;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.billType = billType;
        this.invoiceDate = invoiceDate;
        this.invoiceNumber = invoiceNumber;
    }

    public int getSale_main_id() {
        return sale_main_id;
    }

    public void setSale_main_id(int sale_main_id) {
        this.sale_main_id = sale_main_id;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public String getCustomerName() {
        return patientName;
    }

    public void setCustomerName(String patientName) {
        this.patientName = patientName;
    }

    public String getCustomerPhone() {
        return patientPhone;
    }

    public void setCustomerPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
