package com.increff.pos.service;

import com.increff.pos.model.BrandForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BrandServiceTest extends AbstractUnitTest {
    @Autowired
    private BrandService bs;

    @Test(expected = ApiException.class)
    public void testAdd() throws ApiException {
        BrandForm p = new BrandForm();
        p.setBname("Brand");
        p.setCname("");
        bs.add(p);
    }

    @Test(expected = ApiException.class)
    public void testDuplicateAdd() throws ApiException {
        BrandForm p = new BrandForm();
        p.setBname("Brand");
        p.setCname("Category");
        bs.add(p);
        bs.add(p);
    }

    @Test(expected = ApiException.class)
    public void testNormalise() throws ApiException {
        BrandForm p = new BrandForm();
        p.setBname("Brand");
        p.setCname("Category");
        bs.add(p);
        p.setCname(" Category ");
        bs.add(p);
    }
}
