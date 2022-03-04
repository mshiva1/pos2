package com.increff.pos.model;

public class ProductData1 extends ProductData implements Comparable<ProductData1> {

    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public int compareTo(ProductData1 o) {
        return getName().compareTo(o.getName());
    }
}
