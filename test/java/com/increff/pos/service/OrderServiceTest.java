package com.increff.pos.service;

import com.increff.pos.model.OrderData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OrderServiceTest extends AbstractUnitTest {
    @Autowired
    OrderService os;

    @Test
    public void testAdd() throws ApiException {
        os.add();
    }

    @Test(expected = ApiException.class)
    public void testEmptyOrderConfirming() throws ApiException {
        Integer orderId = os.add().getId();
        os.confirm(orderId);
    }

    @Test(expected = ApiException.class)
    public void testCompletedOrderCancelling() throws ApiException {
        List<OrderData> all_orders = os.getAll();
        for (OrderData od : all_orders)
            if (od.getStatus().equals("completed"))
                os.delete(od.getId());
    }


}
