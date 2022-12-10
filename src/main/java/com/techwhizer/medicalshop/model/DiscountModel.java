package com.techwhizer.medicalshop.model;

public class DiscountModel {

    private int discount_id ;
    private String discountName;
    private int discount;
    private String description;

    public DiscountModel(int discount_id, String discountName, int discount, String description) {
        this.discount_id = discount_id;
        this.discountName = discountName;
        this.discount = discount;
        this.description = description;
    }

    public int getDiscount_id() {
        return discount_id;
    }

    public void setDiscount_id(int discount_id) {
        this.discount_id = discount_id;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {

        return this.getDiscount()+" % "+" - "+this.getDiscountName();
    }
}
