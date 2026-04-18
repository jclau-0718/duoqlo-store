package com.duoqlo.duoqlostore.model;

import com.duoqlo.duoqlostore.AppConfig;

import java.time.LocalDateTime;
import java.util.List;

public class Product {
    private int productId;
    private String productSku;
    private String productName;
    private String gender;
    private String category;
    private String description;
    private String imagePath;
    private LocalDateTime addedDateTime;
    private String addedDateStr;
    private String status;
    private String size;
    private List<ProductSize> sizes;
    private boolean hasStock;

    // Constructors
    public Product() {
    }

    public Product(int productId, String productSku, String productName,
                   String gender, String category, String description,
                   String imagePath, List<ProductSize> sizes, boolean hasStock) {
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.gender = gender;
        this.category = category;
        this.description = description;
        this.imagePath = imagePath;
        this.sizes = sizes;
        this.hasStock = hasStock;
    }

    public Product(int productId, String productName, String category, String size) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.size = size;
    }

    public Product(int productId, String productSku, String productName,
                   String gender, String category, List<ProductSize> sizes, String imagePath,
                   String description, LocalDateTime addedDateTime, String status) {
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.gender = gender;
        this.category = category;
        this.sizes = sizes;
        this.imagePath = imagePath;
        this.description = description;
        this.addedDateTime = addedDateTime;
        this.addedDateStr = addedDateTime.toLocalDate().toString();
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return productId;
    }

    public String getSku() {
        return productSku;
    }

    public String getName() {
        return productName;
    }

    public String getGender() {
        return gender;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getAddedDateStr() {
        return addedDateTime.format(AppConfig.DATE_FORMATTER);
    }

    public String getStatus() {
        return status;
    }

    public String getSize() {
        return size;
    }

    public List<ProductSize> getSizes() {
        return sizes;
    }

    public String getSizeRange() {
        if (!sizes.isEmpty()) {
            String first = sizes.get(0).getSize();
            String last = sizes.get(sizes.size() - 1).getSize();

            String sizeRange = first + " - " + last;

            return sizeRange;
        } else {
            System.out.println("sizes is null");
            return "Hello";
        }

    }

    public String getPriceRange() {
        String first = String.format("%.2f", sizes.get(0).getPrice());
        String last = String.format("%.2f", sizes.get(sizes.size() - 1).getPrice());

        String priceRange = first + " - " + last;

        return priceRange;
    }

    public int getStock() {
        int totalStock = 0;

        for (ProductSize prodSize : sizes) {
            int stock = prodSize.getStockQuantity();
            totalStock += stock;
        }

        return totalStock;
    }

    public boolean hasStock() { return this.hasStock; }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}