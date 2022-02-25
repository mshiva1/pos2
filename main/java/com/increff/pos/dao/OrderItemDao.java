package com.increff.pos.dao;

import com.increff.pos.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao {

    private static final String delete_id = "delete from OrderItemPojo p where id=:id";
    private static final String select_id = "select p from OrderItemPojo p where id=:id";
    private static final String select_order = "select p from OrderItemPojo p where order_id=:order_id";
    private static final String select_pid = "select p.product_id from OrderItemPojo p";
    private static final String delete_items = "delete from OrderItemPojo where order_id=:id";
    private static final String select_with = "select p from OrderItemPojo p where product_id=:pid AND order_id=:oid";

    @PersistenceContext
    private EntityManager em;

    public void insert(OrderItemPojo p) {
        em.persist(p);
    }

    public int delete(int id) {
        Query query = em.createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }
    public OrderItemPojo select(int id) {
        TypedQuery<OrderItemPojo> query = getQuery(select_id, OrderItemPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<OrderItemPojo> selectByOrderId(Integer id) {
        TypedQuery<OrderItemPojo> query = getQuery(select_order, OrderItemPojo.class);
        query.setParameter("order_id", id);
        return query.getResultList();
    }

    public void update(OrderItemPojo p) {
    }

    public void deleteItem(int id) {
        Query query1 = em.createQuery(delete_items);
        query1.setParameter("id", id);
        query1.executeUpdate();
    }

    public List<Integer> getAllPId() {
        TypedQuery<Integer> query = getQuery(select_pid, Integer.class);
        return query.getResultList();
    }

    public List<OrderItemPojo> select(Integer pid, Integer oid) {
        TypedQuery<OrderItemPojo> query = getQuery(select_with, OrderItemPojo.class);
        query.setParameter("pid", pid);
        query.setParameter("oid", oid);
        return query.getResultList();
    }
}
