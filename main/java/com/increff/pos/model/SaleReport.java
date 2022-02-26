package com.increff.pos.model;

public class SaleReport implements Comparable<SaleReport>{
    Integer categoryId;
    String bname;
    String cname;
    Integer quantity;
    Float revenue;



    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
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
        int retval=getBname().compareTo(o.getBname());
        if(retval!=0) return retval;
        return getCname().compareTo(o.getCname());
    }
}
