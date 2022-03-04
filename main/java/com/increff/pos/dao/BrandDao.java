package com.increff.pos.dao;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class BrandDao extends AbstractDao {

    private static final String select_id = "select p from BrandPojo p where id=:id";
    private static final String select_all = "select p from BrandPojo p";
    private static final String select_all_id = "select p.id from BrandPojo p";
    private static final String select_all_bid = "select p.id from BrandPojo p where brandName=:brandName";
    private static final String select_all_cid = "select p.id from BrandPojo p where categoryName=:categoryName";
    private static final String search_combo = "select p from BrandPojo p where brandName=:brandName and categoryName=:categoryName";
    private static final String select_all_brandNames = "select distinct p.brandName from BrandPojo p order by brandName";
    private static final String select_all_categoryNames = "select distinct p.categoryName from BrandPojo p where brandName=:brandName order by categoryName";

    @PersistenceContext
    private EntityManager em;

    public void insert(BrandPojo p) {
        em.persist(p);
    }


    public BrandPojo search(BrandPojo p) {
        TypedQuery<BrandPojo> query = getQuery(search_combo, BrandPojo.class);
        query.setParameter("brandName", p.getBrandName());
        query.setParameter("categoryName", p.getCategoryName());
        return getSingle(query);
    }

    public BrandPojo select(Integer id) {
        TypedQuery<BrandPojo> query = getQuery(select_id, BrandPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<BrandPojo> selectAll() {
        TypedQuery<BrandPojo> query = getQuery(select_all, BrandPojo.class);
        return query.getResultList();
    }

    public List<Integer> selectAllId() {
        TypedQuery<Integer> query = getQuery(select_all_id, Integer.class);
        return query.getResultList();
    }

    public void update(BrandPojo p) {
    }

    public String getbrandName(Integer id) {
        return select(id).getBrandName();
    }

    public String getcategoryName(Integer id) {
        return select(id).getCategoryName();
    }

    public BrandPojo getBid(String brandName, String categoryName) throws ApiException {
        TypedQuery<BrandPojo> query = getQuery(search_combo, BrandPojo.class);
        query.setParameter("brandName", brandName);
        query.setParameter("categoryName", categoryName);
        return getSingle(query);
    }

    public List<Integer> getAllcategoryName(String brandName) {
        TypedQuery<Integer> query = getQuery(select_all_bid, Integer.class);
        query.setParameter("brandName", brandName);
        return query.getResultList();
    }

    public List<Integer> getAllbrandName(String categoryName) {
        TypedQuery<Integer> query = getQuery(select_all_cid, Integer.class);
        query.setParameter("categoryName", categoryName);
        return query.getResultList();
    }

    public List<String> getBrandNames() {
        TypedQuery<String> query = getQuery(select_all_brandNames, String.class);
        return query.getResultList();
    }

    public List<String> getCatNames(String brandName) {
        TypedQuery<String> query = getQuery(select_all_categoryNames, String.class);
        query.setParameter("brandName", brandName);
        return query.getResultList();
    }
}
