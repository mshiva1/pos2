package com.increff.pos.model;

import java.sql.Blob;
import java.sql.Timestamp;
public class OrderData {

    private int id;
    private String status;
    private Timestamp invoice_time;
    private Timestamp order_time;
    private Blob invoice;

    public Blob getInvoice() {
        return invoice;
    }

    public void setInvoice(Blob invoice) {
        this.invoice = invoice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getInvoice_time() {
        return invoice_time;
    }

    public void setInvoice_time(Timestamp invoice_time) {
        this.invoice_time = invoice_time;
    }

    public Timestamp getOrder_time() {
        return order_time;
    }

    public void setOrder_time(Timestamp order_time) {
        this.order_time = order_time;
    }
}
