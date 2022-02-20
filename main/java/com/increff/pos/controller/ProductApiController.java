package com.increff.pos.controller;

import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductData1;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class ProductApiController {

    @Autowired
    private ProductService service;


    @ApiOperation(value = "Adds Product")
    @RequestMapping(path = "/api/product", method = RequestMethod.POST)
    public void addProduct(@RequestBody ProductForm form) throws ApiException {
        service.add(form);
    }

    @ApiOperation(value = "Deletes Product")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.DELETE)
    public void deleteproduct(@PathVariable int id) throws ApiException {
        service.delete(id);
    }

    @ApiOperation(value = "Gets a Product by ID")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.GET)
    public ProductData getProduct(@PathVariable int id) throws ApiException {
        return service.get(id);
    }

    @ApiOperation(value = "Gets list of all Products")
    @RequestMapping(path = "/api/product", method = RequestMethod.GET)
    public List<ProductData1> getAll() throws ApiException {
        return service.getAll();
    }

    @ApiOperation(value = "Updates an Product")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.PUT)
    public void updateProduct(@PathVariable int id, @RequestBody ProductPojo f) throws ApiException {
        service.update(id, f);
    }

    @ApiOperation(value = "Gets list of all Brandnames")
    @RequestMapping(path = "/api/product/brands", method = RequestMethod.GET)
    public List<String> getBrandNames() throws ApiException {
        return service.getBrandNames();
    }

    @ApiOperation(value = "Gets list of Categories related to bname")
    @RequestMapping(path = "/api/product/brands/{bname}", method = RequestMethod.GET)
    public List<String> getBrandNames(@PathVariable String bname) throws ApiException {
        return service.getCatNames(bname);
    }

}
