package com.increff.pos.model;

public class SaleReport implements Comparable<SaleReport> {
    Integer categoryId;
    String brandName;
    String categoryName;
    Integer quantity;
    Float revenue;


    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getRevenue() {
        return revenue;
    }

    public void setRevenue(Float revenue) {
        this.revenue = revenue;
    }

    @Override
    public int compareTo(SaleReport o) {
        Integer retval = getBrandName().compareTo(o.getBrandName());
        if (retval != 0) return retval;
        return getCategoryName().compareTo(o.getCategoryName());
    }
}
