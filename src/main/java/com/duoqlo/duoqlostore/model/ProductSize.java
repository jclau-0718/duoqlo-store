package com.duoqlo.duoqlostore.model;

public class ProductSize {
    private int sizeId;
    private String sizeSKU;
    private int productId;
    private String size;
    private int stockQuantity;
    private double price;

    // Constructors
    public ProductSize() {}

    public ProductSize(int sizeId, String sizeSKU, int productId,
                       String size, int stockQuantity, double price) {
        this.sizeId = sizeId;
        this.sizeSKU = sizeSKU;
        this.productId = productId;
        this.size = size;
        this.stockQuantity = stockQuantity;
        this.price = price;
    }

    // Getters and Setters
    public int getSizeId() { return sizeId; }
    public void setSizeId(int sizeId) { this.sizeId = sizeId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getSizeSKU() { return sizeSKU; }
    public void setSizeSKU(String sizeSKU) { this.sizeSKU = sizeSKU; }
}