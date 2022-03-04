package com.increff.pos.controller;

import com.increff.pos.model.OrderItemData1;
import com.increff.pos.model.OrderItemForm;
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

    @ApiOperation(value = "Adds or update items")
    @RequestMapping(path = "/api/item/", method = RequestMethod.POST)
    public void addItem(@RequestBody OrderItemForm form) throws ApiException {
        service.add(form, true);
    }

    @ApiOperation(value = "Deletes Items")
    @RequestMapping(path = "/api/item/{id}", method = RequestMethod.DELETE)
    public void deleteItem(@PathVariable Integer id) throws ApiException {
        service.delete(id);
    }

    @ApiOperation(value = "Gets list of all Items in Order")
    @RequestMapping(path = "/api/item/order/{id}", method = RequestMethod.GET)
    public List<OrderItemData1> getAll(@PathVariable Integer id) {
        return service.getAll(id);
    }

    @ApiOperation(value = "Gets specific Item")
    @RequestMapping(path = "/api/item/{id}", method = RequestMethod.GET)
    public OrderItemData1 get(@PathVariable Integer id) throws ApiException {
        return service.get(id);
    }

    @ApiOperation(value = "Update item")
    @RequestMapping(path = "/api/item/{id}", method = RequestMethod.POST)
    public void updateItem(@RequestBody OrderItemForm1 form, @PathVariable Integer id) throws ApiException {
        service.update(form, id);
    }
}
