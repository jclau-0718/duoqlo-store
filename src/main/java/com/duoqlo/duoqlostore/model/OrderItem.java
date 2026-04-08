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
        return quantity;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public String getProductName() {
        return product.getProductName();
    }

    public String getCategory() {
        return product.getCategory();
    }

    public String getSize() {
        return product.getSize();
    }

    public String getImagePath(int productSizeId) {
        return productDAO.getImagePath(productSizeId);
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