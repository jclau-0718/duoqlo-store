package com.duoqlo.duoqlostore.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private CartDAO cartDAO = new CartDAO();

    private int cartId;
    private int userId;
    private String lastUpdatedDate;
    private int productSizeId;
    private int productQuantity;
    private List<CartItem> cartItemList = new ArrayList<>();

    public Cart(int userId) {
        this.userId = userId;
    }

    public Cart (int cartId, int userId, String lastUpdatedDate) {
        this.cartId = cartId;
        this.userId = userId;
        this.lastUpdatedDate = lastUpdatedDate;

        for (CartItem cartItem: cartDAO.getCartItems(cartId)) {
            cartItemList.add(cartItem);
        }

//        System.out.println(cartItemList);
    }

    public int getCartId() { return this.cartId; }

    public void addCartItem(CartItem cartItem) { cartItemList.add(cartItem); }

    public List<CartItem> getCartItemList() { return this.cartItemList; }
}
