package com.increff.pos.model;

public class BrandData extends BrandForm implements Comparable<BrandData> {

    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int compareTo(BrandData o) {
        Integer retval = getBrandName().compareTo(o.getBrandName());
        if (retval != 0) return retval;
        return getCategoryName().compareTo(o.getCategoryName());
    }
}
