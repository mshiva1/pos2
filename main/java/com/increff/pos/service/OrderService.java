package com.increff.pos.service;

import com.increff.pos.dao.*;
import com.increff.pos.model.IntegerData;
import com.increff.pos.model.OrderItemData1;
import com.increff.pos.model.SaleReport;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.util.PdfGeneration;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.io.File;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao dao;
    @Autowired
    private OrderItemDao daoOI;
    @Autowired
    private BrandDao daoB;
    @Autowired
    private ProductDao daoP;
    @Autowired
    private InventoryDao daoI;
    @Autowired
    private OrderItemService serOI;

    @Transactional(rollbackOn = ApiException.class)
    public IntegerData add() throws ApiException {
        IntegerData id = new IntegerData();
        //if partial order present
        List<Integer> p1 = dao.getOrder("created");
        if (!p1.isEmpty()) {
            id.setId(p1.get(0));
        }
        //if no partial order present
        else {
            OrderPojo p = new OrderPojo();
            p.setStatus("created");
            p.setOrder_time(Timestamp.from(Instant.now()));
            p.setInvoice(null);
            id.setId(dao.insert(p));
        }
        return id;
    }

    @Transactional
    public void delete(int id) throws ApiException {
        if (get(id).getStatus().equals("fulfilled"))
            throw new ApiException("Fulfilled Orders cant be deleted");
        List <OrderItemPojo> items=daoOI.selectByOrderId(id);
        for(OrderItemPojo oip : items) {
            InventoryPojo ip= new InventoryPojo();
            ip.setProductId(oip.getProduct_id());
            ip.setQuantity(oip.getQuantity());
            {
                InventoryPojo e = daoI.select(ip.getProductId());
                if (e == null)
                    daoI.insert(ip);
                else
                    e.setQuantity(e.getQuantity() + ip.getQuantity());

            }
        }
        daoOI.deleteItem(id);
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo get(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("given ID does not exist, id: " + id);
        }
        return p;
    }
    @Transactional(rollbackOn = ApiException.class)
    public String getInvoice(int id) throws ApiException, SQLException {
        OrderPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("given ID does not exist, id: " + id);
        }
        Blob blb=p.getInvoice();
        byte[] bytes=blb.getBytes(1,(int)blb.length());
        return Base64.getEncoder().encodeToString(bytes);

    }

    @Transactional
    public List<OrderPojo> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void confirm(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null)
            throw new ApiException("Order Not Found");
        if (p.getStatus().equals("created"))
            p.setStatus("confirmed");
        p.setOrder_time(Timestamp.from(Instant.now()));
    }

    @Transactional(rollbackOn = ApiException.class)
    public void fulfil(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null)
            throw new ApiException("Order Not Found");
        p.setStatus("fulfilled");
        p.setInvoice_time(Timestamp.from(Instant.now()));
        p.setInvoice(generateInvoice(p.getId(),p.getOrder_time(),p.getInvoice_time()));
        System.out.println("invoice saved");
    }

    @Transactional(rollbackOn = ApiException.class)
    public void sendBack(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null)
            throw new ApiException("Order Not Found");
        p.setStatus("created");
        p.setOrder_time(Timestamp.from(Instant.now()));
    }


    @Transactional
    public List<SaleReport> report(String start, String end, String bname, String cname) throws ApiException {

        List<SaleReport> retval = new ArrayList<>();
        List<Integer> ordersList = new ArrayList<>();
        List<Integer> brandsList = new ArrayList<>();
        if (start.isEmpty() || end.isEmpty()) {
            //sets all orders
            List<OrderPojo> orderList = dao.selectAll();
            for (OrderPojo op : orderList) {
                ordersList.add(op.getId());
            }
        } else {
            //orders between start and end
            Timestamp timeStart = Timestamp.valueOf(start + " 00:00:01");
            Timestamp timeEnd = Timestamp.valueOf(end + " 23:59:59");
            if(timeStart.after(timeEnd))
                throw new ApiException("Startdate cant be after Enddate");
            ordersList = dao.getBetween(timeStart, timeEnd);
        }
        if (bname.isEmpty() && cname.isEmpty()) {
            //all bnames and cnames
            List<BrandPojo> brandList = daoB.selectAll();
            for (BrandPojo bp : brandList) {
                brandsList.add(bp.getId());
            }
        } else if (bname.isEmpty()) {
            //all bnames
            brandsList = daoB.getAllBname(cname);
        } else if (cname.isEmpty()) {
            //all cnames
            brandsList = daoB.getAllCname(bname);
        } else {
            brandsList = new ArrayList<>();
            try {
                Integer id = daoB.getBid(bname, cname).getId();
                brandsList.add(id);
            }
            //incase brand category not found
            catch (ApiException e) {
                return null;
            }
        }
        for (Integer i : brandsList) {
            SaleReport r = new SaleReport();
            r.setCategoryId(i);
            r.setBname(daoB.getBname(i));
            r.setCname(daoB.getCname(i));
            List<Integer> productsList = daoP.selectFromCatId(i);
            Integer quantity = 0;
            Float revenue = (float) 0;
            for (Integer pid : productsList)
                for (Integer oid : ordersList) {
                    OrderItemPojo p = daoOI.select(pid, oid);
                    if (p != null) {
                        quantity += p.getQuantity();
                        revenue += p.getQuantity() * p.getSellingPrice();
                    }
                }
            r.setQuantity(quantity);
            r.setRevenue(revenue);
            retval.add(r);
        }
        return retval;
    }

    @Transactional
    public Blob generateInvoice(Integer id,Timestamp order,Timestamp invoice) {
        List<OrderItemData1> items;
        Integer quantity=0;
        Float total=(float) 0;
        items=serOI.getAll(id);
        for( OrderItemData1 i : items){
            total+=i.getSellingPrice()*i.getQuantity();
            quantity+=i.getQuantity();
        }
        PdfGeneration pdf = new PdfGeneration();
        String xmlstr=getxmlStream(order,invoice,id,items,quantity,total);
        try{
            pdf.convertToPDF(xmlstr,id);
            String name = "src//main//resources//output//invoice.pdf";
            File fp = new File(name);
            byte[] bytes = FileUtils.readFileToByteArray(fp);
            return new SerialBlob(bytes );
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return null;
    }
    String getxmlStream(Timestamp order,Timestamp invoice,Integer id,List<OrderItemData1> items,Integer quantity,Float total){

        StringBuilder ret= new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <?xml-stylesheet type=\"application/xml\"?> <users-data> <header-section> <data-type >Invoice</data-type> <odate>");
        ret.append(order);
        ret.append("</odate><idate>");
        ret.append(invoice);
        ret.append("</idate><id>");
        ret.append(id);
        ret.append("</id><total>");
        ret.append(total);
        ret.append("</total><quantity>");
        ret.append(quantity);
        ret.append("</quantity></header-section>");
        Integer i=0;
        for(OrderItemData1 oip : items){
            ret.append("<table-data><sno>");
            ret.append(i++);
            ret.append("</sno><bc>");
            ret.append(oip.getBname()).append('-').append(oip.getCname());
            ret.append("</bc><name>");
            ret.append(oip.getName());
            ret.append("</name><barcode>");
            ret.append(oip.getBarcode());
            ret.append("</barcode><quantity>");
            ret.append(oip.getQuantity());
            ret.append("</quantity><price>");
            ret.append(oip.getSellingPrice());
            ret.append("</price><total>");
            ret.append(oip.getQuantity() * oip.getSellingPrice());
            ret.append("</total></table-data>");
        }
        ret.append("</users-data>");
        return ret.toString();
    }
}
