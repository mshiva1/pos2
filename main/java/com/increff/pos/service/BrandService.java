package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandDao dao;
    @Autowired
    private ProductDao daoP;

    protected static void normalize(BrandPojo p) {
        p.setBname(StringUtil.toLowerCase(p.getBname()));
        p.setCname(StringUtil.toLowerCase(p.getCname()));
    }

    @Transactional(rollbackOn = ApiException.class)
    public void add(BrandPojo p) throws ApiException {
        normalize(p);
        if (StringUtil.isEmpty(p.getBname())) {
            throw new ApiException("Brand Name is required");
        }
        if (StringUtil.isEmpty(p.getCname())) {


            throw new ApiException("Category Name is required");
        }
        if (dao.search(p) != null) {
            throw new ApiException("This Brand-Category Pair Exists");
        }
        //checks for existing combination of brand and category
        dao.insert(p);
    }

    @Transactional
    public void delete(int id) throws ApiException {
        if (daoP.selectAllCId().contains(id))
            throw new ApiException("Product related to this combo is present. First remove It");
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional
    public List<BrandPojo> getAll() {
        List<BrandPojo> retval = dao.selectAll();
        Collections.reverse(retval);
        return retval;
    }

    public List<Integer> getAllId() {
        return dao.selectAllId();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(int id, BrandPojo p) throws ApiException {
        // normalize(p);
        BrandPojo ex = getCheck(id);
        if (dao.search(p) != null) {
            throw new ApiException("This Brand-Category Pair Exists");
        }
        if (!p.getBname().isEmpty()) ex.setBname(p.getBname());

        if (!p.getCname().isEmpty()) ex.setCname(p.getCname());
        dao.update(ex);
    }

    @Transactional
    public BrandPojo getCheck(int id) throws ApiException {
        BrandPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("given ID does not exist, id: " + id);
        }
        return p;
    }
}
