package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.view.CartPage;
import javafx.event.ActionEvent;

public class CartController {
    private User user;
    private CartPage cartPage;

    public void setCartPage(CartPage cartPage) {
        this.cartPage = cartPage;
    }

    public void setUser(User user){
        this.user = user;
    }

//    public void showCartPage(){
//        Navigator.goTo(cartPage.initialize());
//    }
}
