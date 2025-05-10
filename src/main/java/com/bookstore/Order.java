package com.bookstore;

import java.util.List;

public class Order {
    private int orderId;
    private int customerId;
    private List<CartItem> items;
    private double total;

    public Order() {}

    public Order(int orderId, int customerId, List<CartItem> items, double total) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.total = total;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}