package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryData1;
import com.increff.pos.model.InventoryData2;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.util.Convert1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao dao;
    @Autowired
    private ProductDao daoP;
    @Autowired
    private Convert1 convert1;

    @Transactional(rollbackOn = ApiException.class)
    public void addFromData(InventoryData1 f) throws ApiException {
        Integer pid = this.getProductIdBarcode(f.getBarcode());
        InventoryPojo p = convert1.convert(f, pid);
        this.add(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryPojo p) throws ApiException {
        List<Integer> cur = getAllPId();    //list of all product id(s)
        if (p.getQuantity() <= 0)
            throw new ApiException("Quantity should be positive (Given: " + p.getQuantity() + ")");
        InventoryPojo e = dao.select(p.getProductId());
        if (e != null) {
            //if already present increment
            e.setQuantity(e.getQuantity() + p.getQuantity());
        } else if (cur.contains(p.getProductId())) {
            //else add new row
            dao.insert(p);
        }
    }

    @Transactional
    public List<Integer> getAllPId() {
        return daoP.selectAllPId();
    }

    @Transactional
    public void delete(int id) {
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryData2 get(int id) throws ApiException {
        InventoryPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Inventory Out of Stock for, Product Id: " + id);
        }
        return convert1.convert(p, daoP.selectId(p.getProductId()).getBarcode());
    }


    @Transactional
    public List<InventoryData2> getAll() {
        List<InventoryPojo> ret = dao.selectAll();
        List<InventoryData2> retval = new ArrayList<>();
        for (InventoryPojo p : ret) {
            retval.add(convert1.convert(p, daoP.selectId(p.getProductId()).getBarcode()));
        }
        Collections.reverse(retval);
        return retval;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(InventoryData f) throws ApiException {
        InventoryPojo p = convert1.convert(f);
        InventoryPojo ex = dao.select(p.getProductId());
        if (p.getQuantity() <= 0)
            throw new ApiException("Quantity should be positive");
        ex.setQuantity(p.getQuantity());
        dao.update(ex);
    }

    @Transactional
    public Integer getProductIdBarcode(String barcode) throws ApiException {
        try {
            return daoP.selectBarcode(barcode).getId();
        } catch (Exception e) {
            throw new ApiException("Barcode do not exist");
        }
    }
    public List<String> getBarcodes() {
        return daoP.getBarcodes();
    }

}
