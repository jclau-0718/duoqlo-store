package com.duoqlo.duoqlostore.model;

public class SalesRecord {
    private String labelValue;
    private double revenue;
    private int totalItems;
    private int orders;

    public SalesRecord(String labelValue, double revenue,
                       int totalItems, int orders) {
        this.labelValue = labelValue;
        this.revenue = revenue;
        this.totalItems = totalItems;
        this.orders = orders;
    }

    public String getLabelValue() { return labelValue; }

    public double getRevenue() { return revenue; }

    public int getTotalItems() { return totalItems; }

    public int getOrders() { return orders; }

    public boolean isTotal() {
        return this.labelValue.toLowerCase().equals("total");
    }
}
