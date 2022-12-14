package com.techwhizer.medicalshop.model;

public class ManufacturerModal {
   private int manufacturer_id;
   private String manufacturer_name,createdDate;

    public ManufacturerModal(int manufacturer_id, String manufacturer_name, String createdDate) {
        this.manufacturer_id = manufacturer_id;
        this.manufacturer_name = manufacturer_name;
        this.createdDate = createdDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getManufacturer_id() {
        return manufacturer_id;
    }

    public void setManufacturer_id(int manufacturer_id) {
        this.manufacturer_id = manufacturer_id;
    }

    public String getManufacturer_name() {
        return manufacturer_name;
    }

    public void setManufacturer_name(String manufacturer_name) {
        this.manufacturer_name = manufacturer_name;
    }
}
