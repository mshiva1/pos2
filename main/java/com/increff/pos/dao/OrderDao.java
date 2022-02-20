package com.increff.pos.dao;

import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao {

    private static final String delete_id = "delete from OrderPojo p where id=:id";
    private static final String select_id = "select p from OrderPojo p where id=:id";
    private static final String select_all = "select p from OrderPojo p where status='fulfilled' or status='confirmed'";
    private static final String select_all_order = "select p.id from OrderPojo p";
    private static final String get_order = "select p.id from OrderPojo p where status=:status";
    private static final String get_between = "select p.id from OrderPojo p where (status='fulfilled' or status='confirmed') and invoice_time < :end and invoice_time > :start";

    @PersistenceContext
    private EntityManager em;

    public Integer insert(OrderPojo p) {
        em.persist(p);
        em.flush();
        return p.getId();
    }

    public void delete(int id) {
        Query query = em.createQuery(delete_id);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    public OrderPojo select(int id) {
        TypedQuery<OrderPojo> query = getQuery(select_id, OrderPojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public List<OrderPojo> selectAll() {
        TypedQuery<OrderPojo> query = getQuery(select_all, OrderPojo.class);
        return query.getResultList();
    }

    public List<Integer> getAllOrderId() {
        TypedQuery<Integer> query = getQuery(select_all_order, Integer.class);
        return query.getResultList();
    }

    public List<Integer> getOrder(String status) {
        TypedQuery<Integer> query = getQuery(get_order, Integer.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    public List<Integer> getBetween(Timestamp start, Timestamp end) {
        TypedQuery<Integer> query = getQuery(get_between, Integer.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();

    }

}
