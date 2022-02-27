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
    private static final String select_all_bid = "select p.id from BrandPojo p where bname=:bname";
    private static final String select_all_cid = "select p.id from BrandPojo p where cname=:cname";
    private static final String search_combo = "select p from BrandPojo p where bname=:bname and cname=:cname";
    private static final String select_all_bnames = "select distinct p.bname from BrandPojo p order by bname";
    private static final String select_all_cnames = "select distinct p.cname from BrandPojo p where bname=:bname order by cname";

    @PersistenceContext
    private EntityManager em;

    public void insert(BrandPojo p) {
        em.persist(p);
    }


    public BrandPojo search(BrandPojo p) {
        TypedQuery<BrandPojo> query = getQuery(search_combo, BrandPojo.class);
        query.setParameter("bname", p.getBname());
        query.setParameter("cname", p.getCname());
        return getSingle(query);
    }

    public BrandPojo select(int id) {
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

    public String getBname(Integer id) {
        return select(id).getBname();
    }

    public String getCname(Integer id) {
        return select(id).getCname();
    }

    public BrandPojo getBid(String bname, String cname) throws ApiException {
        TypedQuery<BrandPojo> query = getQuery(search_combo, BrandPojo.class);
        query.setParameter("bname", bname);
        query.setParameter("cname", cname);
        return getSingle(query);
    }

    public List<Integer> getAllCname(String bname) {
        TypedQuery<Integer> query = getQuery(select_all_bid, Integer.class);
        query.setParameter("bname", bname);
        return query.getResultList();
    }

    public List<Integer> getAllBname(String cname) {
        TypedQuery<Integer> query = getQuery(select_all_cid, Integer.class);
        query.setParameter("cname", cname);
        return query.getResultList();
    }

    public List<String> getBrandNames() {
        TypedQuery<String> query = getQuery(select_all_bnames, String.class);
        return query.getResultList();
    }

    public List<String> getCatNames(String bname) {
        TypedQuery<String> query = getQuery(select_all_cnames, String.class);
        query.setParameter("bname", bname);
        return query.getResultList();
    }
}
