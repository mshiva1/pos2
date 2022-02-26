package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductData1;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.util.Convert1;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;
    @Autowired
    private BrandDao daoB;
    @Autowired
    private InventoryDao daoI;
    @Autowired
    private OrderItemDao daoOI;
    @Autowired
    private Convert1 convert;

    protected static void normalize(ProductPojo p) throws ApiException {
        p.setMrp((float) ((double) Math.round(p.getMrp() * 100)) / 100);
        p.setName(StringUtil.toLowerCase(p.getName()));
        if (p.getMrp() <= 0)
            throw new ApiException("MRP should be Positive");
    }

    @Transactional(rollbackOn = ApiException.class)
    public void add(ProductForm form) throws ApiException {
        ProductPojo p = convert.convert(form, getBid(form.getBname(), form.getCname()));
        normalize(p);
        if (dao.selectBarcode(p.getBarcode()) != null) {
            throw new ApiException("This Barcode Exists");
        }
        List<Integer> all_category = getAllCId();
        if (all_category.contains(p.getCategoryId()))
            dao.insert(p);
        else
            throw new ApiException("Brand-Category pair doesn't exist");
    }

    @Transactional
    public List<Integer> getAllCId() {
        return daoB.selectAllId();
    }

    @Transactional
    public void delete(int id) throws ApiException {
        if (daoOI.getAllPId().contains(id))
            throw new ApiException("There are items in orders with this ProductId");
        if (daoI.select(id) != null)
            throw new ApiException("There are items in Inventory with this ProductId");
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductData get(int id) throws ApiException {
        ProductPojo pp = getCheck(id);
        return convert.convert(pp, daoB.getBname(pp.getCategoryId()), daoB.getCname(pp.getCategoryId()));
    }

    @Transactional
    public List<ProductData1> getAll() throws ApiException {
        List<ProductPojo> list = dao.selectAll();
        List<ProductData1> list2 = new ArrayList<ProductData1>();
        Integer quantity;
        for (ProductPojo p : list) {
            InventoryPojo ip = daoI.select(p.getId());
            if (ip == null) quantity = 0;
            else quantity = ip.getQuantity();
            list2.add(convert.convert(p, daoB.getBname(p.getCategoryId()), daoB.getCname(p.getCategoryId()), quantity));
        }
        Collections.reverse(list2);
        return list2;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(int id, ProductPojo p) throws ApiException {
        normalize(p);
        ProductPojo ex = getCheck(id);
        if (dao.selectBarcode(p.getBarcode()) != null && !ex.getBarcode().equals(p.getBarcode())) {
            throw new ApiException("This Barcode Exists");
        }
        if (p.getBarcode() != null) ex.setBarcode(p.getBarcode());
        if (p.getName() != null) ex.setName(p.getName());
        if (p.getMrp() <= 0)
            throw new ApiException("MRP should be Positive");
        ex.setMrp(p.getMrp());
        dao.update(ex);
    }

    @Transactional
    public ProductPojo getCheck(int id) throws ApiException {
        ProductPojo p = dao.selectId(id);
        if (p == null) {
            throw new ApiException("given ID does not exist, id: " + id);
        }
        return p;
    }

    public Integer getBid(String bname, String cname) throws ApiException {
        if (bname.isEmpty())
            throw new ApiException("Brand Name is Required");
        if (cname.isEmpty())
            throw new ApiException("Category Name is Required");
        try {
            return (daoB.getBid(bname, cname)).getId();
        } catch (Exception e) {
            throw new ApiException("Brand Category combo not found");
        }
    }

    @Transactional
    public List<String> getBrandNames() {
        return daoB.getBrandNames();
    }

    @Transactional
    public List<String> getCatNames(String bname) {
        return daoB.getCatNames(bname);
    }

}
