package com.increff.pos.controller;

import com.increff.pos.model.IntegerData;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.SaleReport;
import com.increff.pos.pojo.OrderPojo;
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
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
public class OrderApiController {

    @Autowired
    private OrderService service;
    @Autowired
    private Convert1 convert1;

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
        List<OrderPojo> list = service.getAll();
        List<OrderData> list2 = new ArrayList<OrderData>();
        for (OrderPojo p : list) {
            list2.add(convert1.convert(p));
        }
        return list2;
    }

    @ApiOperation(value = "created to confirmed")
    @RequestMapping(path = "/api/order/confirm-{id}", method = RequestMethod.POST)
    public void confirmOrder(@PathVariable int id) throws ApiException {
        service.confirm(id);
    }

    @ApiOperation(value = "created to fulfilled")
    @RequestMapping(path = "/api/order/fulfil-{id}", method = RequestMethod.POST)
    public void fulfilOrder(@PathVariable int id) throws ApiException {
        service.fulfil(id);
    }

    @ApiOperation(value = "confirmed to created")
    @RequestMapping(path = "/api/order/sendback-{id}", method = RequestMethod.POST)
    public void sendBack(@PathVariable int id) throws ApiException {
        service.sendBack(id);
    }

    @ApiOperation(value = "Gets Sale Record")
    @RequestMapping(path = "/api/order/sale/", method = RequestMethod.GET)
    public List<SaleReport> getReport(String start, String end, String bname, String cname) throws ApiException {
        return service.report(start, end, bname, cname);
    }
}
