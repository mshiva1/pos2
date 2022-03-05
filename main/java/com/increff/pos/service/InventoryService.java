package com.increff.pos.service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.model.InventoryData;
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
    public void add(InventoryPojo p) throws ApiException {
        List<Integer> cur = getAllPId();    //list of all product id(s)
        if (p.getQuantity() < 0)
            throw new ApiException("Quantity should be positive (Given: " + p.getQuantity() + ")");
        InventoryPojo e = dao.select(p.getProductId());
        if (e != null) {
            //if already present update
            e.setQuantity(p.getQuantity());
        } else if (!cur.contains(p.getProductId())) {
            //else throw Exception
            throw new ApiException("Barcode do not exist");
        } else {
            dao.insert(p);
        }
    }

    @Transactional
    public List<Integer> getAllPId() {
        return daoP.selectAllPId();
    }

    @Transactional
    public void delete(Integer id) {
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryData2 get(Integer id) throws ApiException {
        InventoryPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Inventory Out of Stock for, Product Id: " + id);
        }
        return convert1.convert(p, daoP.selectId(p.getProductId()));
    }


    @Transactional
    public List<InventoryData2> getAll() {
        List<InventoryPojo> ret = dao.selectAll();
        List<InventoryData2> retval = new ArrayList<>();
        for (InventoryPojo p : ret) {
            retval.add(convert1.convert(p, daoP.selectId(p.getProductId())));
        }
        Collections.reverse(retval);
        return retval;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(InventoryData f) throws ApiException {
        InventoryPojo p = convert1.convert(f);
        InventoryPojo ex = dao.select(p.getProductId());
        if (p.getQuantity() < 0)
            throw new ApiException("Quantity should be non-negative");
        ex.setQuantity(p.getQuantity());
        dao.update(ex);
    }


}
