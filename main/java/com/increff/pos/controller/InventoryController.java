package com.increff.pos.controller;

import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryData2;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class InventoryController {

    @Autowired
    private InventoryService service;

    @ApiOperation(value = "Gets a Inventory by ID")
    @RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.GET)
    public InventoryData2 getInventory(@PathVariable Integer id) throws ApiException {
        return service.get(id);
    }

    @ApiOperation(value = "Gets list of all Inventory")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.GET)
    public List<InventoryData2> getAllInventory() {
        return service.getAll();
    }

    @ApiOperation(value = "Change Quantity by PId")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.PUT)
    public void updateInventory(@RequestBody InventoryData f) throws ApiException {
        service.update(f);
    }
}
