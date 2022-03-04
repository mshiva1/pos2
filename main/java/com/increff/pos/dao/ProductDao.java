package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao {

    private static final String select_id = "select p from ProductPojo p where id=:id";
    private static final String select_barcode = "select p from ProductPojo p where barcode=:barcode";
    private static final String select_all = "select p from ProductPojo p";
    private static final String select_all_pid = "select p.id from ProductPojo p";
    private static final String select_all_cid = "select p.categoryId from ProductPojo p";
    private static final String select_all_by_cid = "select p.id from ProductPojo p where categoryId=:bId";
    private static final String select_all_barcode = "select p.barcode from ProductPojo p order by barcode";

    @PersistenceContext
    private EntityManager em;

    public Integer insert(ProductPojo p) {
        //create a new product and return its ID
        em.persist(p);
        em.flush();
        return p.getId();
    }

    public ProductPojo selectId(Integer id) {
        TypedQuery<ProductPojo> query = getQuery(select_id, ProductPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public ProductPojo selectBarcode(String barcode) {
        TypedQuery<ProductPojo> query = getQuery(select_barcode, ProductPojo.class);
        query.setParameter("barcode", barcode);
        return getSingle(query);
    }

    public List<ProductPojo> selectAll() {
        TypedQuery<ProductPojo> query = getQuery(select_all, ProductPojo.class);
        return query.getResultList();
    }

    public List<Integer> selectAllPId() {
        TypedQuery<Integer> query = getQuery(select_all_pid, Integer.class);
        return query.getResultList();
    }

    public List<Integer> selectAllCId() {
        TypedQuery<Integer> query = getQuery(select_all_cid, Integer.class);
        return query.getResultList();
    }

    public List<Integer> selectFromCatId(Integer bId) {
        TypedQuery<Integer> query = getQuery(select_all_by_cid, Integer.class);
        query.setParameter("bId", bId);
        return query.getResultList();
    }

    public void update(ProductPojo p) {
    }

    public List<String> getBarcodes() {
        TypedQuery<String> query = getQuery(select_all_barcode, String.class);
        return query.getResultList();
    }
}
