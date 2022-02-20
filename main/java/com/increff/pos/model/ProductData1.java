package com.increff.pos.model;

public class ProductData1 extends ProductData {

    private int quantity;
    private int id;
    private String bname;
    private String cname;
    private String barcode;
    private String name;
    private float mrp;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getBname() {
        return bname;
    }

    @Override
    public void setBname(String bname) {
        this.bname = bname;
    }

    @Override
    public String getCname() {
        return cname;
    }

    @Override
    public void setCname(String cname) {
        this.cname = cname;
    }

    @Override
    public String getBarcode() {
        return barcode;
    }

    @Override
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public float getMrp() {
        return mrp;
    }

    @Override
    public void setMrp(float mrp) {
        this.mrp = mrp;
    }
}
