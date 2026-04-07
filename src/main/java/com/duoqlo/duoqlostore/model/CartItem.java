package com.duoqlo.duoqlostore.model;

public class CartItem {
    private  ProductDAO productDAO = new ProductDAO();
    private int cartId;
    private int productSizeId;
    private int productQuantity;
    private double subTotal;
    private String addedDate;

    // Usage: CartRow
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


    public void setProductName() {
        String pn = productDAO.getProductName(productSizeId);

        if (pn != null) {
            this.productName = pn;
        } else {
            this.productName = "NAME";
        }
    }

    public void setCategory() {
        String c = productDAO.getCategory(productSizeId);

        if (c != null) {
            this.category = c;
        } else {
            this.category = "SHIRT";
        }
    }

    public void setSize() {
        String s = productDAO.getSize(productSizeId);

        if (s != null) {
            this.size = s;
        } else {
            this.size = "XXX";
        }
    }
}
