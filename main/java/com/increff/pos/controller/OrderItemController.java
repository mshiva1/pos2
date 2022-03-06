package com.increff.pos.controller;

import com.increff.pos.model.OrderItemData1;
import com.increff.pos.model.OrderItemForm1;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.OrderItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Api
@RestController
public class OrderItemController {

    @Autowired
    private OrderItemService service;

    @ApiOperation(value = "Get Details of Specific Item")
    @RequestMapping(path = "/api/item/", method = RequestMethod.GET)
    public OrderItemData1 getItem(String barcode, String quantity, String sellingPrice,String added) throws ApiException {
        return service.get(barcode, quantity, sellingPrice,added);
    }

    @ApiOperation(value = "Gets list of all Items in Order")
    @RequestMapping(path = "/api/item/{id}", method = RequestMethod.GET)
    public List<OrderItemData1> getAllByOrderId(@PathVariable Integer id) {
        return service.getAll(id);
    }

    @ApiOperation(value = "Creates order and push all items into it")
    @RequestMapping(path = "/api/item", method = RequestMethod.POST)
    public void newOrder(@RequestBody List<OrderItemForm1> forms) throws ApiException {
        service.createOrder(forms);
    }
    @ApiOperation(value = "Edits order and push all items into it")
    @RequestMapping(path = "/api/item/{id}", method = RequestMethod.PUT)
    public void editOrder(@PathVariable Integer id, @RequestBody List<OrderItemForm1> forms) throws ApiException {
            service.editOrder(forms,id);
    }
}
