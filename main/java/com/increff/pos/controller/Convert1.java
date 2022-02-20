package com.increff.pos.controller;

import com.increff.pos.model.*;
import com.increff.pos.pojo.*;
import com.increff.pos.service.ApiException;
import org.springframework.stereotype.Service;

@Service

public class Convert1 {

    public BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setBname(p.getBname());
        d.setCname(p.getCname());
        d.setId(p.getId());
        return d;
    }

    public BrandPojo convert(BrandForm f) {
        BrandPojo p = new BrandPojo();
        p.setBname(f.getBname());
        p.setCname(f.getCname());
        return p;
    }


    public InventoryData convert(InventoryPojo f) {
        InventoryData p = new InventoryData();
        p.setQuantity(f.getQuantity());
        p.setProductId(f.getProductId());
        return p;
    }

    public InventoryPojo convert(InventoryData f) {
        InventoryPojo p = new InventoryPojo();
        p.setQuantity(f.getQuantity());
        p.setProductId(f.getProductId());
        return p;
    }

    public OrderData convert(OrderPojo f) {
        OrderData p = new OrderData();
        p.setId(f.getId());
        p.setStatus(f.getStatus());
        p.setInvoice_time(f.getInvoice_time());
        p.setOrder_time(f.getOrder_time());
        p.setInvoice(null);
        return p;
    }

    public OrderItemData1 convert(OrderItemPojo f) {
        OrderItemData1 p = new OrderItemData1();
        p.setQuantity(f.getQuantity());
        p.setProductId(f.getProduct_id());
        p.setSellingPrice(f.getSellingPrice());
        p.setId(f.getId());
        return p;
    }

    public OrderItemPojo convert(OrderItemForm f, Integer product_id) {
        OrderItemPojo p = new OrderItemPojo();
        p.setQuantity(f.getQuantity());
        p.setOrder_id(f.getOrderId());
        p.setProduct_id(product_id);
        p.setSellingPrice(f.getSellingPrice());
        return p;
    }

    public ProductData convert(ProductPojo p, String bname, String cname) throws ApiException {
        ProductData d = new ProductData();
        d.setBarcode(p.getBarcode());
        d.setBname(bname);
        d.setCname(cname);
        d.setMrp(p.getMrp());
        d.setName(p.getName());
        d.setId(p.getId());
        return d;
    }

    public ProductPojo convert(ProductForm p, Integer bid) throws ApiException {
        ProductPojo d = new ProductPojo();
        d.setBarcode(p.getBarcode());
        d.setCategoryId(bid);
        d.setMrp(p.getMrp());
        d.setName(p.getName());
        return d;
    }


}