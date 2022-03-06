package com.increff.pos.service;

import com.increff.pos.dao.*;
import com.increff.pos.model.OrderItemData1;
import com.increff.pos.model.OrderItemForm;
import com.increff.pos.model.OrderItemForm1;
import com.increff.pos.pojo.*;
import com.increff.pos.util.Convert1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
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
    private BrandDao daoB;
    @Autowired
    private Convert1 convert;
    @Autowired
    private OrderService serO;

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemData1 get(String barcode, String quantity, String sellingPrice,String added) throws ApiException {
        OrderItemForm p = new OrderItemForm();
        p.setBarcode(barcode);
        if (sellingPrice.isEmpty())
            p.setSellingPrice(0);
        else
            p.setSellingPrice(Float.parseFloat(sellingPrice));
        p.setQuantity(Integer.parseInt(quantity));

        ProductPojo pp = daoP.selectBarcode(p.getBarcode());
        if (pp == null)
            throw new ApiException("This barcode does not Exist");
        BrandPojo bp = daoB.select(pp.getCategoryId());
        InventoryPojo ip = daoI.select(pp.getId());

        if (p.getSellingPrice() == 0)
            p.setSellingPrice(pp.getMrp());

        p = validateAdd(p, pp, ip,Integer.parseInt(added));

        OrderItemData1 oid = new OrderItemData1();
        oid.setBarcode(p.getBarcode());
        oid.setQuantity(p.getQuantity());
        oid.setSellingPrice(p.getSellingPrice());
        oid.setProductId(pp.getId());
        oid.setName(pp.getName());
        oid.setMrp(pp.getMrp());
        oid.setBrandName(bp.getBrandName());
        oid.setCategoryName(bp.getCategoryName());
        return oid;
    }

    private OrderItemForm validateAdd(OrderItemForm p, ProductPojo p1, InventoryPojo ip,Integer added) throws ApiException {

        Integer available;
        if (ip == null)
            throw new ApiException("Product not in Inventory");
        else
            available = ip.getQuantity()-added;

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
    public void delete(Integer id) throws ApiException {
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
            OrderItemData1 t = convert.convert(p, p1, bp);
            retval.add(t);
        }

        return retval;
    }

    public OrderItemData1 get(Integer id) throws ApiException {
        OrderItemPojo p = dao.select(id);
        ProductPojo p1 = daoP.selectId(p.getProduct_id());
        BrandPojo bp = daoB.select(p1.getCategoryId());
        return convert.convert(p, p1, bp);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void createOrder(List<OrderItemForm1> forms) throws ApiException {
        //create new order
        OrderPojo op = new OrderPojo();
        op.setStatus("created");
        op.setOrder_time(Timestamp.from(Instant.now()));
        Integer orderId = daoO.insert(op);

        //add items to OrderItemPojo after decrementing inventory
        for (OrderItemForm1 oif : forms) {
            OrderItemPojo oip = new OrderItemPojo();
            InventoryPojo ip = daoI.select(oif.getProductId());
            if (ip.getQuantity() < oif.getQuantity())
                throw new ApiException("Inventory Not available for" + daoP.selectId(oip.getProduct_id()).getName() + "(" + daoP.selectId(oip.getProduct_id()).getBarcode() + ")Try removing it and adding it again");
            else
                ip.setQuantity(ip.getQuantity() - oif.getQuantity());
            oip.setOrder_id(orderId);
            oip.setProduct_id(oif.getProductId());
            oip.setSellingPrice(oif.getSellingPrice());
            oip.setQuantity(oif.getQuantity());
            dao.insert(oip);
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public void editOrder(List<OrderItemForm1> forms,Integer orderId) throws ApiException {
        try{
            serO.delete(orderId);
        }
        catch (ApiException e){
            throw new ApiException("Completed orders cant be Edited");
        }
        //remove all items regarding orderId
        OrderPojo op= daoO.select(orderId);
        op.setStatus("created");
        op.setOrder_time(Timestamp.from(Instant.now()));
        //add all items to OrderItemPojo
        for (OrderItemForm1 oif : forms) {
            OrderItemPojo oip = new OrderItemPojo();
            InventoryPojo ip = daoI.select(oif.getProductId());
            if (ip.getQuantity() < oif.getQuantity())
                throw new ApiException("Inventory Not available for" + daoP.selectId(oip.getProduct_id()).getName() + "(" + daoP.selectId(oip.getProduct_id()).getBarcode() + ")Try removing it and adding it again");
            else
                ip.setQuantity(ip.getQuantity() - oif.getQuantity());
            oip.setOrder_id(orderId);
            oip.setProduct_id(oif.getProductId());
            oip.setSellingPrice(oif.getSellingPrice());
            oip.setQuantity(oif.getQuantity());
            dao.insert(oip);
        }
    }
}
