package com.increff.pos.util;

import com.increff.pos.model.*;
import com.increff.pos.pojo.*;
import org.springframework.stereotype.Service;

@Service

public class Convert1 {

    public BrandData convert(BrandPojo p) {
        BrandData d = new BrandData();
        d.setBrandName(p.getBrandName());
        d.setCategoryName(p.getCategoryName());
        d.setId(p.getId());
        return d;
    }

    public BrandPojo convert(BrandForm f) {
        BrandPojo p = new BrandPojo();
        p.setBrandName(f.getBrandName());
        p.setCategoryName(f.getCategoryName());
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

    public OrderItemData1 convert(OrderItemPojo f, ProductPojo pp, BrandPojo bp) {
        OrderItemData1 p = new OrderItemData1();
        p.setBrandName(bp.getBrandName());
        p.setCategoryName(bp.getCategoryName());
        p.setBarcode(pp.getBarcode());
        p.setMrp(pp.getMrp());
        p.setName(pp.getName());
        p.setQuantity(f.getQuantity());
        p.setProductId(f.getProduct_id());
        p.setSellingPrice(f.getSellingPrice());
        p.setId(f.getId());
        return p;
    }

    public ProductData convert(ProductPojo p, String brandName, String categoryName) {
        ProductData d = new ProductData();
        d.setBarcode(p.getBarcode());
        d.setBrandName(brandName);
        d.setCategoryName(categoryName);
        d.setMrp(p.getMrp());
        d.setName(p.getName());
        d.setId(p.getId());
        return d;
    }

    public ProductData1 convert(ProductPojo p, String brandName, String categoryName, Integer quantity) {
        ProductData1 d = new ProductData1();
        d.setQuantity(quantity);
        d.setBarcode(p.getBarcode());
        d.setBrandName(brandName);
        d.setCategoryName(categoryName);
        d.setMrp(p.getMrp());
        d.setName(p.getName());
        d.setId(p.getId());
        return d;
    }

    public ProductPojo convert(ProductForm p, Integer bid) {
        ProductPojo d = new ProductPojo();
        d.setBarcode(p.getBarcode());
        d.setCategoryId(bid);
        d.setMrp(p.getMrp());
        d.setName(p.getName());
        return d;
    }


    public InventoryData2 convert(InventoryPojo p, ProductPojo pp) {
        InventoryData2 i = new InventoryData2();
        i.setProductId(p.getProductId());
        i.setQuantity(p.getQuantity());
        i.setBarcode(pp.getBarcode());
        i.setName(pp.getName());
        return i;
    }
}