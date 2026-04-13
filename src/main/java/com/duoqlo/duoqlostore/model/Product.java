package com.duoqlo.duoqlostore.model;

import java.util.List;

public class Product {
    private int productId;
    private String productSku;
    private String productName;
    private String gender;
    private String category;
    private String description;
    private String imagePath;
    private String size;
    private List<ProductSize> sizes;

    // Constructors
    public Product() {}

    public Product(int productId, String productSku, String productName,
                   String gender, String category,
                   String description, String imagePath) {
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.gender = gender;
        this.category = category;
        this.description = description;
        this.imagePath = imagePath;
    }

    public Product(int productId, String productName, String imagePath) {
        this.productId = productId;
        this.productName = productName;
        this.imagePath = imagePath;
    }

    public Product(String productName, String category, String size) {
        this.productName = productName;
        this.category = category;
        this.size = size;
    }

    //productid
    //productsku
    //productname
    //gender
    //category
    //List<Productsize>
    //stock
    //added at
    //status: available OR out of stock




    // Getters and Setters
    public int getProductId() { return productId; }

    public String getProductSku() { return productSku; }

    public String getProductName() { return productName; }

    public String getGender() { return gender; }

    public String getCategory() { return category; }

    public String getDescription() { return description; }

    public String getImagePath() { return imagePath; }

    public String getSize() { return size; }

    public List<ProductSize> getSizes() { return sizes; }

    public void setProductId(int productId) { this.productId = productId; }

    public void setProductSku(String productSku) { this.productSku = productSku; }

    public void setProductName(String productName) { this.productName = productName; }

    public void setGender(String gender) { this.gender = gender; }

    public void setCategory(String category) { this.category = category; }

    public void setDescription(String description) { this.description = description; }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public void setSizes(List<ProductSize> sizes) { this.sizes = sizes; }
}