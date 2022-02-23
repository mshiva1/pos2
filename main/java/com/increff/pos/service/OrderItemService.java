package com.increff.pos.service;

import com.increff.pos.controller.Convert1;
import com.increff.pos.dao.*;
import com.increff.pos.model.OrderItemData1;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.OrderItemForm1;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemDao dao;
    @Autowired
    private OrderDao daoO;
    @Autowired
    private ProductDao daoP;
    @Autowired
    private InventoryDao daoI;
    @Autowired
    private Convert1 convert;
    @Autowired
    private BrandDao daoB;

    @Transactional(rollbackOn = ApiException.class)
    public void add(OrderItemForm p) throws ApiException {
        Integer product_id = daoP.getProductIdBarcode(p.getBarcode());
        ProductPojo p1 = daoP.selectId(product_id);
        InventoryPojo ip = daoI.select(product_id);


        p.setSellingPrice((float)((double) Math.round(p.getSellingPrice() * 100) )/ 100);
        Integer available;
        if (ip == null)
            throw new ApiException("Product not in Inventory");
        else
            available = ip.getQuantity();

        if (!(daoO.getAllOrderId()).contains(p.getOrderId()))
            throw new ApiException("This Order doesnt exist");

        if (p.getQuantity() > available)
            throw new ApiException("Quantity required is not Available (Available :" + available + ")");

        if (p.getSellingPrice() < 0)
            throw new ApiException("Selling Price should be positive");

        if (p.getSellingPrice() > p1.getMrp())
            throw new ApiException("Selling Price Error (Mrp:"+p1.getMrp()+")");

        if (p.getSellingPrice() == 0)
            p.setSellingPrice(p1.getMrp());

        if (p.getQuantity() <= 0)
            throw new ApiException("Quantity should be Positive");

        if (available == p.getQuantity())
            daoI.delete(product_id);
        else
            ip.setQuantity(ip.getQuantity() - p.getQuantity());

        OrderItemPojo p2 = convert.convert(p, product_id);
        OrderItemPojo ex = dao.search(p2);

        if (ex != null) {
            ex.setSellingPrice(p.getSellingPrice());
            ex.setQuantity(ex.getQuantity() + p.getQuantity());
        } else
            dao.insert(p2);
    }

    @Transactional
    public void delete(int id) throws ApiException {
        OrderItemPojo ex = dao.select(id);
        InventoryPojo e=daoI.select(ex.getProduct_id());

        if(e==null){
            InventoryPojo p = new InventoryPojo();
            p.setProductId(ex.getProduct_id());
            p.setQuantity(ex.getQuantity());
            daoI.insert(p);
        }
        else e.setQuantity(e.getQuantity()+ex.getQuantity());

        dao.delete(id);
    }

    @Transactional
    public List<OrderItemData1> getAll(Integer orderId) {
        List<OrderItemData1> retval = new ArrayList<>();
        List<OrderItemPojo> list = dao.selectByOrderId(orderId);

        for (OrderItemPojo p : list) {
            OrderItemData1 t = convert.convert(p);
            ProductPojo p1 = daoP.selectId(p.getProduct_id());
            BrandPojo bp = daoB.select(p1.getCategoryId());
            t.setBarcode(p1.getBarcode());
            t.setMrp(p1.getMrp());
            t.setName(p1.getName());
            t.setBname(bp.getBname());
            t.setCname(bp.getCname());
            retval.add(t);
        }

        return retval;
    }

    public OrderItemData1 get(Integer id) throws ApiException {
        OrderItemPojo p = dao.select(id);
        OrderItemData1 t = convert.convert(p);
        ProductPojo p1 = daoP.selectId(p.getProduct_id());
        BrandPojo bp = daoB.select(p1.getCategoryId());
        t.setBarcode(p1.getBarcode());
        t.setMrp(p1.getMrp());
        t.setName(p1.getName());
        t.setBname(bp.getBname());
        t.setCname(bp.getCname());
        return t;
    }

    @Transactional
    public void update(OrderItemForm1 form, Integer id) throws ApiException {
        ProductPojo p1 = daoP.selectId(form.getProductId());
        InventoryPojo ip = daoI.select(form.getProductId());
        Integer available = 0;
        if (ip != null)
            available = ip.getQuantity();

        if (form.getSellingPrice() == 0)
            form.setSellingPrice(p1.getMrp());


        form.setSellingPrice((float)((double) Math.round(form.getSellingPrice() * 100) )/ 100);
        if (p1.getMrp() < form.getSellingPrice())
            throw new ApiException("Selling Price cant be more than MRP");

        if (form.getQuantity() <= 0)
            throw new ApiException("Quantity should be positive");

        OrderItemPojo p = dao.select(id);

        if (form.getQuantity() > available + p.getQuantity())
            throw new ApiException("Quantity required is not Available (Available :" + available + ")");

        p.setSellingPrice(form.getSellingPrice());
        ip.setQuantity(ip.getQuantity() + p.getQuantity() - form.getQuantity());
        p.setQuantity(form.getQuantity());
    }
}
