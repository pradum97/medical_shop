package com.techwhizer.medicalshop.model;

public class UpiModel {
   private int id;
   private String Upi_id;
   private String payeeName;

    public UpiModel(int id, String upi_id, String payeeName) {
        this.id = id;
        Upi_id = upi_id;
        this.payeeName = payeeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpi_id() {
        return Upi_id;
    }

    public void setUpi_id(String upi_id) {
        Upi_id = upi_id;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }
}
