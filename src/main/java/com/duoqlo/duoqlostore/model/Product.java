package com.duoqlo.duoqlostore.model;

import java.util.List;

public class Product {
    private int productId;
    private String productSku;
    private String productName;
    private String gender;
    private String category;
    private String subCategory;
    private String description;
    private String imagePath;
    private List<ProductSize> sizes;

    // Constructors
    public Product() {}

    public Product(int productId, String productSku, String productName,
                   String gender, String category, String subCategory,
                   String description, String imagePath) {
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.gender = gender;
        this.category = category;
        this.subCategory = subCategory;
        this.description = description;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getProductId() { return productId; }

    public String getProductSku() { return productSku; }

    public String getProductName() { return productName; }

    public String getGender() { return gender; }

    public String getCategory() { return category; }

    public String getSubCategory() { return subCategory; }

    public String getDescription() { return description; }

    public String getImagePath() { return imagePath; }

    public List<ProductSize> getSizes() { return sizes; }

    public void setProductId(int productId) { this.productId = productId; }

    public void setProductSku(String productSku) { this.productSku = productSku; }

    public void setProductName(String productName) { this.productName = productName; }

    public void setGender(String gender) { this.gender = gender; }

    public void setCategory(String category) { this.category = category; }

    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public void setDescription(String description) { this.description = description; }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public void setSizes(List<ProductSize> sizes) { this.sizes = sizes; }
}