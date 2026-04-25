package com.duoqlo.duoqlostore.model;

public class OrderItem {
    private ProductDAO productDAO = new ProductDAO();

    private Product product;
    private int orderItemId;
    private int orderId;
    private int productSizeId;
    private int quantity;
    private double subTotal;

    public int getOrderItemId() {
        return orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductSizeId() {
        return productSizeId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public double getSubTotal() {
        return this.subTotal;
    }

    public Product getProduct() { return this.product; }

    public int getProductId() { return this.product.getId(); }

    public String getProductName() { return this.product.getName(); }

    public String getProductSize() { return this.product.getSize(); }

    public String getGender() { return productDAO.getGender(this.productSizeId); }

    public String getCategory() {
        return this.product.getCategory();
    }

    public String getSize() {
        return this.product.getSize();
    }

    public String getImagePath(int productSizeId) {
        return this.productDAO.getImagePath(productSizeId);
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProductSizeId(int productSizeId) {
        this.productSizeId = productSizeId;

        this.product = productDAO.getProduct(this.productSizeId);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }
}