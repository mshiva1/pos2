package com.increff.pos.service;

import com.increff.pos.dao.*;
import com.increff.pos.model.OrderItemData1;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.OrderItemForm1;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.Convert1;
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
    public void add(OrderItemForm p, Boolean validations) throws ApiException {
        ProductPojo pp = daoP.selectBarcode(p.getBarcode());
        if (pp == null)
            throw new ApiException("This barcode does not Exist");
        Integer product_id = pp.getId();
        ProductPojo p1 = daoP.selectId(product_id);
        List<OrderItemPojo> oip = dao.select(product_id, p.getOrderId()); // currently present item
        InventoryPojo ip = daoI.select(product_id);

        if (p.getSellingPrice() == 0)
            p.setSellingPrice(p1.getMrp());
        OrderItemPojo p2 = convert.convert(p, product_id);

        if (validations) {

            p=validateAdd(p,p1,oip,ip);
            for (OrderItemPojo oip1 : oip) {
                if (oip1.getSellingPrice() == p.getSellingPrice()) {
                    //if match with existing item just add its quantity
                    oip1.setQuantity(oip1.getQuantity() + p.getQuantity());
                    return;
                }
            }
        }
        dao.insert(p2);
    }

    private OrderItemForm validateAdd(OrderItemForm p, ProductPojo p1, List<OrderItemPojo> oip, InventoryPojo ip) throws ApiException {

        Integer added = 0;
        for (OrderItemPojo oip1 : oip) {
            added += oip1.getQuantity();
        }
        Integer available;
        if (ip == null)
            throw new ApiException("Product not in Inventory");
        else if (!oip.isEmpty())
            available = ip.getQuantity() - added;     //since quantity that can be added
        else
            available = ip.getQuantity();

        p.setSellingPrice((float) ((double) Math.round(p.getSellingPrice() * 100)) / 100);
        if (p.getQuantity() > available)
            throw new ApiException("Quantity required is not Available (Available :" + available + ")");
        if (p.getSellingPrice() < 0)
            throw new ApiException("Selling Price should be positive");
        if (p.getSellingPrice() > p1.getMrp())
            throw new ApiException("Selling Price Error (Mrp:" + p1.getMrp() + ")");
        if (p.getQuantity() <= 0)
            throw new ApiException("Quantity should be Positive");
        return p;
    }

    @Transactional
    public void delete(int id) throws ApiException {
        OrderItemPojo ex = dao.select(id);
        String status = daoO.select(ex.getOrder_id()).getStatus();
        InventoryPojo e = daoI.select(ex.getProduct_id());
        if (status.equals("confirmed")) {       //only add inventory if order is confirmed
            if (e == null) {
                InventoryPojo p = new InventoryPojo();
                p.setProductId(ex.getProduct_id());
                p.setQuantity(ex.getQuantity());
                daoI.insert(p);
            } else e.setQuantity(e.getQuantity() + ex.getQuantity());
        }
        dao.delete(id);
    }

    @Transactional
    public List<OrderItemData1> getAll(Integer orderId) {
        List<OrderItemData1> retval = new ArrayList<>();
        List<OrderItemPojo> list = dao.selectByOrderId(orderId);

        for (OrderItemPojo p : list) {
            ProductPojo p1 = daoP.selectId(p.getProduct_id());
            BrandPojo bp = daoB.select(p1.getCategoryId());
            OrderItemData1 t = convert.convert(p,p1,bp);
            retval.add(t);
        }

        return retval;
    }

    public OrderItemData1 get(Integer id) throws ApiException {
        OrderItemPojo p = dao.select(id);
        ProductPojo p1 = daoP.selectId(p.getProduct_id());
        BrandPojo bp = daoB.select(p1.getCategoryId());
        return convert.convert(p,p1,bp);
    }


    @Transactional(rollbackOn = ApiException.class)
    public void update(OrderItemForm1 form, Integer id) throws ApiException {

        OrderItemPojo p = dao.select(id);
        ProductPojo p1 = daoP.selectId(form.getProductId());
        InventoryPojo ip = daoI.select(form.getProductId());
        Integer available = 0;
        if (ip != null)
            available = ip.getQuantity();

        form=validateUpdate(form,p1);

        List <OrderItemPojo> existing=dao.select(form.getProductId(), form.getOrderId());
        Integer added = 0;
        for (OrderItemPojo oip1 : existing) {
            added += oip1.getQuantity();
        }

        if (form.getOrderId() == 0)    //          confirmed order editing
        {
            if (form.getQuantity() > available+p.getQuantity())
                throw new ApiException("Quantity required is not Available (Available :" + available + ")");
        } else {                       //          not confirmed order editing
            if (form.getQuantity()+added > available + p.getQuantity())
                throw new ApiException("Quantity required is not Available (Available :" + (available + p.getQuantity()) + ")");
        }
        p.setSellingPrice(form.getSellingPrice());
        p.setQuantity(form.getQuantity());
    }

    private OrderItemForm1 validateUpdate(OrderItemForm1 form, ProductPojo p1) throws ApiException {
        if (form.getSellingPrice() == 0)
            form.setSellingPrice(p1.getMrp());
        form.setSellingPrice((float) ((double) Math.round(form.getSellingPrice() * 100)) / 100);
        if (p1.getMrp() < form.getSellingPrice())
            throw new ApiException("Selling Price cannot be more than MRP");
        if (form.getSellingPrice() <= 0)
            throw new ApiException("Selling Price should be Positive");
        if (form.getQuantity() <= 0)
            throw new ApiException("Quantity should be positive");

        return form;
    }
}
