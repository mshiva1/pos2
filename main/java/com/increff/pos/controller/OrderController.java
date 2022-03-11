package com.increff.pos.controller;

import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderIdNumberData;
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
public class OrderController {

    @Autowired
    private OrderService service;

    @ApiOperation(value = "Cancels Order")
    @RequestMapping(path = "/api/order/delete", method = RequestMethod.PATCH)
    public void deleteOrder(@RequestBody OrderIdNumberData oin) throws ApiException  {
        service.delete(oin);
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

    @ApiOperation(value = "created to completed")
    @RequestMapping(path = "/api/order/complete", method = RequestMethod.PATCH)
    public void completeOrder(@RequestBody OrderIdNumberData oin) throws ApiException {
        service.complete(oin);
    }

    @ApiOperation(value = "Gets Sale Record")
    @RequestMapping(path = "/api/order/sale/", method = RequestMethod.GET)
    public List<SaleReport> getReport(String start, String end, String brandName, String categoryName) {
        return service.saleReport(start, end, brandName, categoryName);
    }
}
