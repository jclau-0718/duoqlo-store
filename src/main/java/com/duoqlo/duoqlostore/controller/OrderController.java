package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.Order;
import com.duoqlo.duoqlostore.model.OrderDAO;
import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.view.CartPage;
import com.duoqlo.duoqlostore.view.OrderPage;
import com.duoqlo.duoqlostore.view.ProfilePage;

import java.util.List;

public class OrderController {
    private  User user;
    private OrderDAO orderDAO = new OrderDAO();

    public OrderController(User user) {
        this.user = user;
    }

    public void openCartPage() {
        CartController cartController = new CartController(this.user);
        CartPage cartPage = new CartPage(cartController);

        Navigator.goTo(cartPage.initialize());
    }

    public void openProfilePage() {
        ProfileController profileController = new ProfileController(this.user);
        ProfilePage profilePage = new ProfilePage(profileController);

        Navigator.goTo(profilePage.initialize());
    }

    public List<Order> getOrders() {
        return orderDAO.getOrders(user.getId());
    }
}
