package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.util.Convert1;
import com.increff.pos.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class BrandService {

    @Autowired
    private BrandDao dao;
    @Autowired
    private Convert1 convert1;

    private static void normalize(BrandPojo p) {
        p.setBrandName(StringUtil.toLowerCase(p.getBrandName()));
        p.setCategoryName(StringUtil.toLowerCase(p.getCategoryName()));
    }

    private void validateAdd(BrandPojo p) throws ApiException {
        if (StringUtil.isEmpty(p.getBrandName())) {
            throw new ApiException("Brand Name is required");
        }
        if (StringUtil.isEmpty(p.getCategoryName())) {
            throw new ApiException("Category Name is required");
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public void add(BrandForm form) throws ApiException {

        BrandPojo p = convert1.convert(form);
        normalize(p);
        validateAdd(p);
        if (dao.search(p) != null) {
            throw new ApiException("This Brand-Category Pair Exists");
        }
        //checks for existing combination of brand and category
        dao.insert(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandData get(Integer id) throws ApiException {
        return convert1.convert(getCheck(id));
    }

    @Transactional
    public List<BrandData> getAll() {
        List<BrandPojo> list = dao.selectAll();
        Collections.reverse(list);
        List<BrandData> list2 = new ArrayList<>();
        for (BrandPojo p : list) {
            list2.add(convert1.convert(p));
        }
        Collections.sort(list2);
        return list2;
    }


    @Transactional(rollbackOn = ApiException.class)
    public void update(Integer id, BrandForm f) throws ApiException {
        BrandPojo p = convert1.convert(f);
        normalize(p);
        BrandPojo ex = getCheck(id);
        BrandPojo exist = dao.search(p);
        if (exist != null && !Objects.equals(exist.getId(), id)) {
            throw new ApiException("This Brand-Category Pair Exists");
        }
        if (!p.getBrandName().isEmpty()) ex.setBrandName(p.getBrandName());
        if (!p.getCategoryName().isEmpty()) ex.setCategoryName(p.getCategoryName());
        dao.update(ex);
    }

    @Transactional
    public BrandPojo getCheck(Integer id) throws ApiException {
        BrandPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("given ID does not exist, id: " + id);
        }
        return p;
    }
}
