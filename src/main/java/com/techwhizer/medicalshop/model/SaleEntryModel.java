package com.techwhizer.medicalshop.model;

public class SaleEntryModel {
    private int itemId;
    private int cartId;
    private int stockId;
    private String productName;
    private double saleRate;
    private String pack;
    private int strip;
    private int pcs;
    private String expiryDate;
    private int discountId;
    private double discount;
    private int gstId;
    private double totalGst;
    private double amount;
    private  long hsn;
    private  int iGst;
    private  int cGst;
    private  int sGst;
    private double gstAmount,purchaseRate,mrp;
    private String batch;
    private int mfrId;
    private double amtAsPerMrp;

    public SaleEntryModel(int itemId,int cartId, int stockId,String productName, double saleRate, String pack, int strip, int pcs, String expiryDate, int discountId, double discount, int gstId,
                          double totalGst, double amount, long hsn, int iGst, int cGst, int sGst, double gstAmount,
                          double purchaseRate,double mrp,String batch , int mfrId,double amtAsPerMrp) {
        this.itemId = itemId;
        this.cartId = cartId;
        this.stockId = stockId;
        this.productName = productName;
        this.saleRate = saleRate;
        this.pack = pack;
        this.strip = strip;
        this.pcs = pcs;
        this.expiryDate = expiryDate;
        this.discountId = discountId;
        this.discount = discount;
        this.gstId = gstId;
        this.totalGst = totalGst;
        this.amount = amount;
        this.hsn = hsn;
        this.iGst = iGst;
        this.cGst = cGst;
        this.sGst = sGst;
        this.gstAmount = gstAmount;
        this.purchaseRate = purchaseRate;
        this.mrp = mrp;
        this.batch = batch;
        this.mfrId = mfrId;
        this.amtAsPerMrp = amtAsPerMrp;

    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public double getAmtAsPerMrp() {
        return amtAsPerMrp;
    }

    public void setAmtAsPerMrp(double amtAsPerMrp) {
        this.amtAsPerMrp = amtAsPerMrp;
    }

    public String getBatch() {
        return batch;
    }
    public void setBatch(String batch) {
        this.batch = batch;
    }

    public int getMfrId() {
        return mfrId;
    }

    public void setMfrId(int mfrId) {
        this.mfrId = mfrId;
    }

    public double getPurchaseRate() {
        return purchaseRate;
    }

    public void setPurchaseRate(double purchaseRate) {
        this.purchaseRate = purchaseRate;
    }

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }

    public int getiGst() {
        return iGst;
    }

    public void setiGst(int iGst) {
        this.iGst = iGst;
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

    public double getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(double gstAmount) {
        this.gstAmount = gstAmount;
    }

    public long getHsn() {
        return hsn;
    }

    public void setHsn(long hsn) {
        this.hsn = hsn;
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

    public double getSaleRate() {
        return saleRate;
    }

    public void setSaleRate(double saleRate) {
        this.saleRate = saleRate;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public int getStrip() {
        return strip;
    }

    public void setStrip(int strip) {
        this.strip = strip;
    }

    public int getPcs() {
        return pcs;
    }

    public void setPcs(int pcs) {
        this.pcs = pcs;
    }

    public void setTotalGst(double totalGst) {
        this.totalGst = totalGst;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getGstId() {
        return gstId;
    }

    public void setGstId(int gstId) {
        this.gstId = gstId;
    }

    public double getTotalGst() {
        return totalGst;
    }

    public void setTotalGst(int totalGst) {
        this.totalGst = totalGst;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
