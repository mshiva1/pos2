package com.increff.pos.controller;

import com.increff.pos.model.CopyForm;
import com.increff.pos.model.IntegerData;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.SaleReport;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Api
@RestController
public class OrderApiController {

    @Autowired
    private OrderService service;

    @ApiOperation(value = "Adds Order")
    @RequestMapping(path = "/api/order/add", method = RequestMethod.GET)
    public IntegerData addOrder() throws ApiException {
        return service.add();
    }

    @ApiOperation(value = "Deletes Order")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.DELETE)
    public void deleteOrder(@PathVariable int id) throws ApiException {
        service.delete(id);
    }

    @ApiOperation(value = "Gets Invoice of Order by ID")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
    public String getOrder(@PathVariable int id) throws ApiException, SQLException {
        return service.getInvoice(id);
    }

    @ApiOperation(value = "Gets list of all Orders")
    @RequestMapping(path = "/api/order", method = RequestMethod.GET)
    public List<OrderData> getAll() {
        return service.getAll();
    }

    @ApiOperation(value = "created to confirmed")
    @RequestMapping(path = "/api/order/confirm-{id}", method = RequestMethod.POST)
    public void confirmOrder(@PathVariable int id) throws ApiException {
        service.confirm(id);
    }

    @ApiOperation(value = "confirmed to fulfilled")
    @RequestMapping(path = "/api/order/fulfil-{id}", method = RequestMethod.POST)
    public void fulfilOrder(@PathVariable int id) throws ApiException {
        service.complete(id);
    }

    @ApiOperation(value = "copying orders ")
    @RequestMapping(path = "/api/order/copy", method = RequestMethod.PUT)
    public void copy(@RequestBody CopyForm cf) throws ApiException {
        service.copy(cf);
    }

    @ApiOperation(value = "Gets Sale Record")
    @RequestMapping(path = "/api/order/sale/", method = RequestMethod.GET)
    public List<SaleReport> getReport(String start, String end, String bname, String cname) throws ApiException {
        return service.saleReport(start, end, bname, cname);
    }

    @ApiOperation(value = "Gets list of barcodes that are in inventory")
    @RequestMapping(path = "/api/order/barcodes", method = RequestMethod.GET)
    public List<String> getBarcodes() throws ApiException {
        return service.getBarcodes();
    }
}
