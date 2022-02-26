package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.InventoryData2;
import com.increff.pos.pojo.InventoryPojo;
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

    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryPojo p) throws ApiException {
        List<Integer> cur = getAllPId();
        if (p.getQuantity() <= 0)
            throw new ApiException("Quantity should be positive (Given: " + p.getQuantity() + ")");
        InventoryPojo e = dao.select(p.getProductId());
        if (e != null) {
            e.setQuantity(e.getQuantity() + p.getQuantity());
        } else if (cur.contains(p.getProductId()))
            dao.insert(p);
        else
            throw new ApiException("No (Brand-Category) present with id:" + p.getProductId());

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
        return convert(p);
    }

    private InventoryData2 convert(InventoryPojo p) {
        InventoryData2 i = new InventoryData2();
        i.setProductId(p.getProductId());
        i.setQuantity(p.getQuantity());
        i.setBarcode(daoP.selectId(p.getProductId()).getBarcode());
        return i;
    }

    @Transactional
    public List<InventoryData2> getAll() {
        List<InventoryPojo> ret = dao.selectAll();
        List<InventoryData2> retval = new ArrayList<>();

        for (InventoryPojo p : ret) {
            retval.add(convert(p));
        }
        Collections.reverse(retval);
        return retval;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(InventoryPojo p) throws ApiException {
        InventoryPojo ex = dao.select(p.getProductId());
        if (p.getQuantity() <= 0)
            throw new ApiException("Quantity should be positive");
        ex.setQuantity(p.getQuantity());
        dao.update(ex);
    }

    @Transactional
    public Integer getProductIdBarcode(String barcode) throws ApiException {
        try {
            return daoP.getProductIdBarcode(barcode);
        } catch (Exception e) {
            throw new ApiException("Barcode do not exist");
        }
    }

    public List<String> getBarcodes() {
        return daoP.getBarcodes();
    }

}
