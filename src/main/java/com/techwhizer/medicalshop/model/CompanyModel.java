package com.techwhizer.medicalshop.model;

public class CompanyModel {
    private int company_id;
    private String companyName;
    private String companyAddress;
    private String createdDate;

    public CompanyModel(int company_id, String companyName, String companyAddress, String createdDate) {
        this.company_id = company_id;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return getCompanyName();
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
