package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.CartItem;
import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.view.CartPage;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CartController {
    private User user;
    private List<CartItem> cartItemList = new ArrayList<>();

    public void setUser(User user){
        this.user = user;
    }

    public void addCartItem(CartItem cartItem) {
        cartItemList.add(cartItem);
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public boolean listIsEmpty() {
        return cartItemList.isEmpty();
    }

    public double getSubTotal() {
        double subtotal = 0;
        for (CartItem cartItem: cartItemList) {
            subtotal += cartItem.getSubTotal();
        }

        return subtotal;
    }
}
