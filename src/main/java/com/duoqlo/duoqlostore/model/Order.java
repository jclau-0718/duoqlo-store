package com.duoqlo.duoqlostore.model;

public class Order {
    private int orderId;
    private int userId;
    private String orderDate;
    private double totalPrice;
    private String status;
    private String shippingAddress;

    public int getOrderId() { return this.orderId; }

    public int getUserId() { return this.userId; }

    public String getOrderDate() { return this.orderDate; }

    public double getTotalPrice() { return this.totalPrice; }

    public String getStatus() { return this.status; }

    public String getShippingAddress() { return this.shippingAddress; }

    public void setOrderId(int orderId) { this.orderId = orderId; }

    public void setUserId(int userId) { this.userId = userId; }

    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public void setStatus(String status) { this.status = status; }

    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
}
