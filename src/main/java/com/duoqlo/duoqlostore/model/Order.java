package com.duoqlo.duoqlostore.model;

import com.duoqlo.duoqlostore.AppConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private User user;
    private int userId;
    private LocalDateTime orderDateTime;
    private String orderDateStr;
    private int totalItems;
    private double totalPrice;
    private String status;
    private String shippingAddress;
    private List<OrderItem> orderItemList = new ArrayList<>();

    public Order(){}

    public Order(int orderId, int userId, LocalDateTime orderDateTime,
                 double totalPrice, String status, String shippingAddress,
                 List<OrderItem> orderItemList) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDateTime = orderDateTime;
        this.orderDateStr = orderDateTime.toLocalDate().toString();
        this.totalPrice = totalPrice;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.orderItemList = orderItemList;

    }

    public Order(int orderId, User user, LocalDateTime orderDateTime, int totalItems,
                 double totalPrice, String status, String shippingAddress) {
        this.orderId = orderId;
        this.user = user;
        this.userId = user.getId();
        this.orderDateTime = orderDateTime;
        this.orderDateStr = orderDateTime.toLocalDate().toString();
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
        this.status = status;
        this.shippingAddress = shippingAddress;
    }

    public int getOrderId() { return this.orderId; }

    public int getUserId() { return this.userId; }

    public String getUsername() { return this.user.getUsername(); }

    public String getFullName() { return user.getLastName() + ", " + user.getFirstName(); }

    public LocalDateTime getOrderDateTime() { return this.orderDateTime; }

    public String getOrderDateString() { return this.orderDateTime.format(AppConfig.DATE_FORMATTER); }

    public int getTotalItems() { return this.totalItems; }

    public double getTotalPrice() { return this.totalPrice; }

    public String getStatus() { return this.status; }

    public String getShippingAddress() { return this.shippingAddress; }

    public List<OrderItem> getOrderItemList() { return this.orderItemList; }

    public void setOrderId(int orderId) { this.orderId = orderId; }

    public void setUserId(int userId) { this.userId = userId; }

    public void setOrderDate(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;

        this.orderDateStr = orderDateTime.format(AppConfig.DATE_FORMATTER);
    }

    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public void setStatus(String status) { this.status = status; }

    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public void setOrderItemList(List<OrderItem> orderItemList) { this.orderItemList = orderItemList; }
}
