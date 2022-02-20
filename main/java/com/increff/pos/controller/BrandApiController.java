package com.increff.pos.controller;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api
@RestController
public class BrandApiController {

    @Autowired
    private BrandService service;
    @Autowired
    private Convert1 convert1;

    @ApiOperation(value = "Adds Brand")
    @RequestMapping(path = "/api/brand", method = RequestMethod.POST)
    public void addBrand(@RequestBody BrandForm form) throws ApiException {
        BrandPojo p = convert1.convert(form);
        service.add(p);
    }

    @ApiOperation(value = "Deletes Brand")
    @RequestMapping(path = "/api/brand/{id}", method = RequestMethod.DELETE)
    public void deleteBrand(@PathVariable int id) throws ApiException {
        service.delete(id);
    }

    @ApiOperation(value = "Gets a Brand by ID")
    @RequestMapping(path = "/api/brand/{id}", method = RequestMethod.GET)
    public BrandData getBrand(@PathVariable int id) throws ApiException {
        BrandPojo p = service.get(id);
        return convert1.convert(p);
    }

    @ApiOperation(value = "Gets list of all Brands")
    @RequestMapping(path = "/api/brand", method = RequestMethod.GET)
    public List<BrandData> getAll() {
        List<BrandPojo> list = service.getAll();
        List<BrandData> list2 = new ArrayList<BrandData>();
        for (BrandPojo p : list) {
            list2.add(convert1.convert(p));
        }
        return list2;
    }

    @ApiOperation(value = "Updates an Brand")
    @RequestMapping(path = "/api/brand/{id}", method = RequestMethod.PUT)
    public void updateBrand(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
        BrandPojo p = convert1.convert(f);
        service.update(id, p);
    }

}
