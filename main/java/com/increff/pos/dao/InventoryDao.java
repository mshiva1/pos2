package com.increff.pos.dao;

import com.increff.pos.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class InventoryDao extends AbstractDao {

    private static final String delete_id = "delete from InventoryPojo p where productId=:id";
    private static final String select_id = "select p from InventoryPojo p where productId=:id";
    private static final String select_all = "select p from InventoryPojo p";
    private static final String select_all_products="select p.productId from InventoryPojo p";


    @PersistenceContext
    private EntityManager em;

    public void insert(InventoryPojo p) {
        em.persist(p);
    }

    public int delete(int id) {
        Query query = em.createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public InventoryPojo select(int id) {
        TypedQuery<InventoryPojo> query = getQuery(select_id, InventoryPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<InventoryPojo> selectAll() {
        TypedQuery<InventoryPojo> query = getQuery(select_all, InventoryPojo.class);
        return query.getResultList();
    }
    public List<Integer> getProducts(){
        TypedQuery<Integer> query = getQuery(select_all_products, Integer.class);
        return query.getResultList();
    }
    public void update(InventoryPojo p) {
    }

}
