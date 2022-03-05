package com.increff.pos.service;

import com.increff.pos.dao.*;
import com.increff.pos.model.OrderData;
import com.increff.pos.model.OrderItemData1;
import com.increff.pos.model.SaleReport;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.util.Convert1;
import com.increff.pos.util.PdfHelper;
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
import java.util.Collections;
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
    @Autowired
    private Convert1 convert1;

    @Transactional
    public void delete(Integer id) throws ApiException {
        if (get(id).getStatus().equals("completed"))
            throw new ApiException("Completed Orders cant be Cancelled");
        List<OrderItemPojo> items = daoOI.selectByOrderId(id);
        String status = dao.select(id).getStatus();
        for (OrderItemPojo oip : items) {
            if (status.equals("created")) {
                //only add to inventory if confirmed order is cancelled
                InventoryPojo ip = new InventoryPojo();
                ip.setProductId(oip.getProduct_id());
                ip.setQuantity(oip.getQuantity());
                {
                    InventoryPojo e = daoI.select(ip.getProductId());
                    if (e == null)
                        daoI.insert(ip);
                    else
                        e.setQuantity(e.getQuantity() + ip.getQuantity());

                }
                dao.select(id).setStatus("cancelled");
            }
        }
        daoOI.deleteItem(id);
        //this deletes all its related items in OrderItemPojo
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo get(Integer id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("given ID does not exist, id: " + id);
        }
        return p;
    }

    @Transactional(rollbackOn = ApiException.class)
    public String getInvoice(Integer id) throws ApiException, SQLException {
        OrderPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("given ID does not exist, id: " + id);
        }
        Blob blb = p.getInvoice();
        byte[] bytes = blb.getBytes(1, (int) blb.length());
        return Base64.getEncoder().encodeToString(bytes);
        //encoded to base64 to pass through api
    }

    @Transactional
    public List<OrderData> getAll() {
        List<OrderPojo> list = dao.selectAll();
        List<OrderData> list2 = new ArrayList<>();
        for (OrderPojo p : list) {
            list2.add(convert1.convert(p));
        }
        Collections.reverse(list2);
        return list2;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void complete(Integer id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null)
            throw new ApiException("Order Not Found");
        p.setStatus("completed");
        p.setInvoice_time(Timestamp.from(Instant.now()));
        p.setInvoice(generateInvoice(p.getId(), p.getOrder_time(), p.getInvoice_time()));
        System.out.println("invoice saved");
    }

    public List<Integer> selectOrdersBetween(String start, String end) {
        Timestamp timeStart, timeEnd;

        //if start is empty means start time is -inf
        if (start.isEmpty())
            timeStart = new Timestamp(0);
        else
            timeStart = Timestamp.valueOf(start + " 00:00:01");

        //if end is empty means end is today
        if (end.isEmpty())
            timeEnd = Timestamp.from(Instant.now());
        else
            timeEnd = Timestamp.valueOf(end + " 23:59:59");

        return dao.getBetween(timeStart, timeEnd);
    }

    public List<Integer> selectBrandCategory(String brandName, String categoryName) {
        List<Integer> brandsList = new ArrayList<>();

        if (brandName.isEmpty() && categoryName.isEmpty()) {
            //all brandNames and categoryNames
            List<BrandPojo> brandList = daoB.selectAll();
            for (BrandPojo bp : brandList) {
                brandsList.add(bp.getId());
            }
        } else if (brandName.isEmpty()) {
            //all brandNames
            brandsList = daoB.getAllbrandName(categoryName);
        } else if (categoryName.isEmpty()) {
            //all categoryNames
            brandsList = daoB.getAllcategoryName(brandName);
        } else {
            try {
                Integer id = daoB.getBid(brandName, categoryName).getId();
                brandsList.add(id);
            }
            //in case brand category not found
            catch (ApiException e) {
                return null;
            }
        }
        return brandsList;
    }

    @Transactional
    public List<SaleReport> saleReport(String start, String end, String brandName, String categoryName) {

        List<SaleReport> retval = new ArrayList<>();
        List<Integer> brandsList = selectBrandCategory(brandName, categoryName);
        List<Integer> ordersList = selectOrdersBetween(start, end);
        for (Integer i : brandsList) {
            SaleReport r = new SaleReport();
            r.setCategoryId(i);
            r.setBrandName(daoB.getbrandName(i));
            r.setCategoryName(daoB.getcategoryName(i));

            Integer quantity = 0;
            Float revenue = (float) 0;

            List<Integer> productsList = daoP.selectFromCatId(i);
            for (Integer pid : productsList) {
                for (Integer oid : ordersList) {
                    List<OrderItemPojo> p = daoOI.select(pid, oid);
                    for (OrderItemPojo oip : p) {
                        quantity += oip.getQuantity();
                        revenue += oip.getQuantity() * oip.getSellingPrice();
                    }
                }
            }
            r.setQuantity(quantity);
            r.setRevenue(revenue);
            retval.add(r);
        }
        Collections.sort(retval);
        return retval;
    }

    @Transactional
    public Blob generateInvoice(Integer id, Timestamp order, Timestamp invoice) {
        List<OrderItemData1> items;
        Float total = (float) 0;
        items = serOI.getAll(id);
        for (OrderItemData1 i : items) {
            total += i.getSellingPrice() * i.getQuantity();
        }
        PdfHelper pdf = new PdfHelper();
        String xmlstr = pdf.getxmlStream(order, invoice, id, items, total);
        try {
            pdf.convertToPDF(xmlstr, id);
            String name = "src//main//resources//output//invoice.pdf";
            File fp = new File(name);
            byte[] bytes = FileUtils.readFileToByteArray(fp);
            return new SerialBlob(bytes);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
