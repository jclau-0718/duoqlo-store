package com.duoqlo.duoqlostore.model;

import java.util.List;

public class Cart {
    private int cartId;
    private int userId;
    private String lastUpdatedDate;
    private int productSizeId;
    private int productQuantity;
    private List<CartItem> cartItemList;

    public Cart (int cartId, int userId, String lastUpdatedDate) {
        this.cartId = cartId;
        this.userId = userId;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Cart(int userId, int productSizeId, int productQuantity) {
        this.userId = userId;
        this.productSizeId = productSizeId;
        this.productQuantity = productQuantity;
    }

    public int getCartId() { return this.cartId; }

    public void addCartItem(CartItem cartItem) { cartItemList.add(cartItem); }
}
