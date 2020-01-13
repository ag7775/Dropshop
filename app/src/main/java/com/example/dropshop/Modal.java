package com.example.dropshop;

import com.google.firebase.Timestamp;

public class Modal implements Comparable<Modal> {
    public Modal(String productId, String customerId, String brandCode, String brandName, int productCode, String productDesc, int mrp, int expiry) {
        this.productId = productId;
        this.customerId = customerId;
        this.brandCode = brandCode;
        this.brandName = brandName;
        this.productCode = productCode;
        this.productDesc = productDesc;
        this.mrp = mrp;
        this.expiry = expiry;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public int getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }

    public Modal() {
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(int expiry) {
        this.expiry = expiry;
    }

    String productId;
    String customerId;
    String brandCode;
    String brandName;
    int productCode;
    String productDesc;
    int mrp;
    int expiry;  //firebase timestamp


    //sort asc
    @Override
    public int compareTo(Modal o) {
        int compare = o.getExpiry();
        return (this.getExpiry() - compare);
    }
}
