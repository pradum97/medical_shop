package com.techwhizer.medicalshop.model;

public class TaxDetails {

    private double  sgst , cgst , igst ;
    private double taxableAmount ;
    private long hsn ;

    public TaxDetails(double sgst, double cgst, double igst, double taxableAmount, long hsn) {
        this.sgst = sgst;
        this.cgst = cgst;
        this.igst = igst;
        this.taxableAmount = taxableAmount;
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

    public double getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(double taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public long getHsn() {
        return hsn;
    }

    public void setHsn(long hsn) {
        this.hsn = hsn;
    }
}
