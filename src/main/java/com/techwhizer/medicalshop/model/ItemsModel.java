package com.techwhizer.medicalshop.model;

public class ItemsModel {
    private int itemId;
    private String productName;
    private String unit;
    private String packing;
    private int company_id;
    private String companyName;
    private int mfr_id;
    private int discount_id;
    private Double discount;
    private int mr_id;
    private String expiryDate;
    private int gstId;
    private int cGst;
    private int sGst;
    private int iGst;
    private double purchaseMrp;
    private double mrp;
    private double saleRate;
    private String batchNumber;
    private String lotNumber;
    private String type;
    private String narcotic;
    private String itemType;
    private int status;
    private String createdDate;
    private long tabPerStrip;

    public ItemsModel(int itemId, String productName, String unit, String packing, int company_id, String companyName, int mfr_id, int discount_id,
                      Double discount, int mr_id, String expiryDate, int gstId, int cGst, int sGst, int iGst, double purchaseMrp, double mrp,
                      double saleRate, String batchNumber, String lotNumber, String type, String narcotic, String itemType, int status, String createdDate) {
        this.itemId = itemId;
        this.productName = productName;
        this.unit = unit;
        this.packing = packing;
        this.company_id = company_id;
        this.companyName = companyName;
        this.mfr_id = mfr_id;
        this.discount_id = discount_id;
        this.discount = discount;
        this.mr_id = mr_id;
        this.expiryDate = expiryDate;
        this.gstId = gstId;
        this.cGst = cGst;
        this.sGst = sGst;
        this.iGst = iGst;
        this.purchaseMrp = purchaseMrp;
        this.mrp = mrp;
        this.saleRate = saleRate;
        this.batchNumber = batchNumber;
        this.lotNumber = lotNumber;
        this.type = type;
        this.narcotic = narcotic;
        this.itemType = itemType;
        this.status = status;
        this.createdDate = createdDate;
    }

    // for parameter in add product

    public ItemsModel(String productName, String unit, String packing, int discount_id,
                      int gstId, double purchaseMrp, double mrp,
                      double saleRate, String type, String narcotic, String itemType, int status,long tabPerStrip) {
        this.productName = productName;
        this.unit = unit;
        this.packing = packing;
        this.company_id = company_id;
        this.mfr_id = mfr_id;
        this.discount_id = discount_id;
        this.mr_id = mr_id;
        this.gstId = gstId;
        this.purchaseMrp = purchaseMrp;
        this.mrp = mrp;
        this.saleRate = saleRate;
        this.type = type;
        this.narcotic = narcotic;
        this.itemType = itemType;
        this.status = status;
        this.tabPerStrip = tabPerStrip;
    }

    public long getTabPerStrip() {
        return tabPerStrip;
    }

    public void setTabPerStrip(int tabPerStrip) {
        this.tabPerStrip = tabPerStrip;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
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


    public int getDiscount_id() {
        return discount_id;
    }

    public void setDiscount_id(int discount_id) {
        this.discount_id = discount_id;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }


    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getGstId() {
        return gstId;
    }

    public void setGstId(int gstId) {
        this.gstId = gstId;
    }

    public int getcGst() {
        return cGst;
    }

    public void setcGst(int cGst) {
        this.cGst = cGst;
    }

    public int getsGst() {
        return sGst;
    }

    public void setsGst(int sGst) {
        this.sGst = sGst;
    }

    public int getiGst() {
        return iGst;
    }

    public void setiGst(int iGst) {
        this.iGst = iGst;
    }

    public double getPurchaseMrp() {
        return purchaseMrp;
    }

    public void setPurchaseMrp(double purchaseMrp) {
        this.purchaseMrp = purchaseMrp;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public double getSaleRate() {
        return saleRate;
    }

    public void setSaleRate(double saleRate) {
        this.saleRate = saleRate;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNarcotic() {
        return narcotic;
    }

    public void setNarcotic(String narcotic) {
        this.narcotic = narcotic;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
