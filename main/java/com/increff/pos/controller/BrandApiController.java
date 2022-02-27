package com.increff.pos.controller;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class BrandApiController {

    @Autowired
    private BrandService service;

    @ApiOperation(value = "Adds Brand")
    @RequestMapping(path = "/api/brand", method = RequestMethod.POST)
    public void addBrand(@RequestBody BrandForm form) throws ApiException {
        service.add(form);
    }

    @ApiOperation(value = "Gets a Brand by ID")
    @RequestMapping(path = "/api/brand/{id}", method = RequestMethod.GET)
    public BrandData getBrand(@PathVariable int id) throws ApiException {
        return service.get(id);
    }

    @ApiOperation(value = "Gets list of all Brands")
    @RequestMapping(path = "/api/brand", method = RequestMethod.GET)
    public List<BrandData> getAll() {
        return service.getAll();
    }

    @ApiOperation(value = "Updates an Brand")
    @RequestMapping(path = "/api/brand/{id}", method = RequestMethod.PUT)
    public void updateBrand(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
        service.update(id, f);
    }

}
