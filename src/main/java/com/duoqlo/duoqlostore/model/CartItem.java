package com.duoqlo.duoqlostore.model;

public class CartItem {
    private  ProductDAO productDAO = new ProductDAO();
    private int cartId;
    private int productSizeId;
    private int productQuantity;
    private double subTotal;

    private String productName;
    private String category;
    private String size;

    public CartItem () {}

    public CartItem(int cartId, int productSizeId, int productQuantity, double subTotal) {
        this.cartId = cartId;
        this.productSizeId = productSizeId;
        this.productQuantity = productQuantity;
        this.subTotal = subTotal;

        setProductName();
        setCategory();
        setSize();
    }

    public CartItem(int productSizeId, int productQuantity, double subTotal) {
        this.productSizeId = productSizeId;
        this.productQuantity = productQuantity;
        this.subTotal = subTotal;

        setProductName();
        setCategory();
        setSize();
    }

    public int getCartId() { return this.cartId; }

    public int getProductSizeId() {
        return this.productSizeId;
    }

    public int getProductQuantity() {
        return this.productQuantity;
    }

    public String getProductName() { return this.productName; }

    public String getCategory() { return this.category; }

    public double getSubTotal() { return this.subTotal; }

    public String getSize() { return this.size; }

    public void setProductSizeId(int productSizeId) { this.productSizeId = productSizeId; }

    public void setProductQuantity(int productQuantity) { this.productQuantity = productQuantity; }

    public void setSubTotal(double subTotal) { this.subTotal = subTotal; }

    public void setProductName() {
        String productName = productDAO.getProductName(productSizeId);

        if (productName != null) {
            this.productName = productName;
        } else {
            this.productName = "NAME";
        }
    }

    public void setCategory() {
        String category = productDAO.getCategory(productSizeId);

        if (category != null) {
            this.category = category;
        } else {
            this.category = "SHIRT";
        }
    }

    public void setSize() {
        String size = productDAO.getSize(productSizeId);

        if (size != null) {
            this.size = size;
        } else {
            this.size = "XXX";
        }
    }
}
