package com.increff.pos.controller;

import com.increff.pos.model.OrderItemData1;
import com.increff.pos.model.OrderItemForm1;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class OrderItemController {

    @Autowired
    private OrderItemService service;

    @ApiOperation(value = "Get Details of Specific Item")
    @RequestMapping(path = "/api/item/", method = RequestMethod.GET)
    public OrderItemData1 getItem(String barcode, String quantity, String sellingPrice) throws ApiException {
        return service.get(barcode, quantity, sellingPrice, true);
    }

    @ApiOperation(value = "Gets list of all Items in Order")
    @RequestMapping(path = "/api/item/order/{id}", method = RequestMethod.GET)
    public List<OrderItemData1> getAll(@PathVariable Integer id) {
        return service.getAll(id);
    }

    @ApiOperation(value = "Creates order and push all items into it")
    @RequestMapping(path = "/api/item", method = RequestMethod.POST)
    public void updateItem(@RequestBody List<OrderItemForm1> forms) throws ApiException {
        service.createOrder(forms);
    }
}
