package com.increff.pos.model;

public class BrandData extends BrandForm implements Comparable <BrandData>{

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Override
    public int compareTo(BrandData o) {
        int retval = getBname().compareTo(o.getBname());
        if (retval != 0) return retval;
        return getCname().compareTo(o.getCname());
    }
}
