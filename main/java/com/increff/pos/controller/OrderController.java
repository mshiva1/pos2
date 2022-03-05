package com.increff.pos.controller;

import com.increff.pos.model.OrderData;
import com.increff.pos.model.SaleReport;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@Api
@RestController
public class OrderController {

    @Autowired
    private OrderService service;

    @ApiOperation(value = "Cancels Order")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.DELETE)
    public void deleteOrder(@PathVariable Integer id) throws ApiException {
        service.delete(id);
    }

    @ApiOperation(value = "Gets Invoice of Order by ID")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
    public String getOrder(@PathVariable Integer id) throws ApiException, SQLException {
        return service.getInvoice(id);
    }

    @ApiOperation(value = "Gets list of all Orders")
    @RequestMapping(path = "/api/order", method = RequestMethod.GET)
    public List<OrderData> getAll() {
        return service.getAll();
    }


    @ApiOperation(value = "confirmed to completed")
    @RequestMapping(path = "/api/order/fulfil-{id}", method = RequestMethod.POST)
    public void fulfilOrder(@PathVariable Integer id) throws ApiException {
        service.complete(id);
    }

    @ApiOperation(value = "Gets Sale Record")
    @RequestMapping(path = "/api/order/sale/", method = RequestMethod.GET)
    public List<SaleReport> getReport(String start, String end, String brandName, String categoryName) {
        return service.saleReport(start, end, brandName, categoryName);
    }
}
