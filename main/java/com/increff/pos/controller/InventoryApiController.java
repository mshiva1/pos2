package com.increff.pos.controller;

import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryData1;
import com.increff.pos.model.InventoryData2;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.util.Convert1;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class InventoryApiController {

    @Autowired
    private InventoryService service;
    @Autowired
    private Convert1 convert1;

    @ApiOperation(value = "Adds or Increments Inventory")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.POST)
    public void addInventory(@RequestBody InventoryData1 form) throws ApiException {
        InventoryPojo p = convert(form);
        service.add(p);
    }

    @ApiOperation(value = "Deletes Inventory")
    @RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.DELETE)
    public void deleteInventory(@PathVariable int id) {
        service.delete(id);
    }

    @ApiOperation(value = "Gets a Inventory by ID")
    @RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.GET)
    public InventoryData2 getInventory(@PathVariable int id) throws ApiException {
        return service.get(id);
    }

    @ApiOperation(value = "Gets list of all Inventory")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.GET)
    public List<InventoryData2> getAll() {
        return service.getAll();
    }

    @ApiOperation(value = "Change Quantity by PId")
    @RequestMapping(path = "/api/inventory", method = RequestMethod.PUT)
    public void updateInventory(@RequestBody InventoryData f) throws ApiException {
        InventoryPojo p = convert1.convert(f);
        service.update(p);
    }

    @ApiOperation(value = "Gets list of barcodes")
    @RequestMapping(path = "/api/inventory/barcodes", method = RequestMethod.GET)
    public List<String> getBarcodes() throws ApiException {
        return service.getBarcodes();
    }


    private InventoryPojo convert(InventoryData1 f) throws ApiException {
        InventoryPojo p = new InventoryPojo();
        p.setQuantity(f.getQuantity());
        Integer pid = service.getProductIdBarcode(f.getBarcode());
        p.setProductId(pid);
        return p;
    }
}
