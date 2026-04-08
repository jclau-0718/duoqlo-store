package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.*;
import com.duoqlo.duoqlostore.view.AlertMsg;
import com.duoqlo.duoqlostore.view.OrderPage;
import com.duoqlo.duoqlostore.view.UserDashboard;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class CartController {
    private ProductDAO productDAO = new ProductDAO();
    private CartDAO cartDAO = new CartDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private User user;
    private Cart cart;
    private List<CartItem> cartItemList;

    public void setUser(User user){
        this.user = user;
    }

    public User getUser() { return this.user; }

    public void setCart(Cart cart) {
        this.cart = cart;

        cartItemList = cart.getCartItemList();
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

    private boolean removeFromList(int productSizeId) {
        // Remove from local cartItemList
        for(CartItem cartItem: cartItemList) {
            if(cartItem.getProductSizeId() == productSizeId) {
                cartItemList.remove(cartItem);
                return true;
            }
        }

        return false;
    }

    private boolean removeFromDatabase(int productSizeId) {
        return cartDAO.removeCartItem(productSizeId);
    }

    public boolean removeFromCart(int productSizeId) {
        if (removeFromList(productSizeId) && removeFromDatabase(productSizeId)) {
            return true;
        } else {
            return false;
        }
    }

    public void handleCheckOut() {
        DashboardController dashController = new DashboardController();
        dashController.setUser(this.user);

        UserDashboard userDash = new UserDashboard(dashController);
        Navigator.goTo(userDash.initialize());

        PauseTransition delay = new PauseTransition(Duration.millis(100));
        delay.setOnFinished(delayEvent -> {
            AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
            successAlert.show(userDash.getBody(), "Order Confirmed!", Pos.TOP_CENTER);
        });
        delay.play();
    }

    public boolean handleOrder(double totalPrice) {
        // Order
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalPrice(totalPrice);
        order.setShippingAddress(user.getFullAddress());

        Order insertedOrder = orderDAO.insertOrder(order);
        if (insertedOrder != null) {
            order = insertedOrder;
        } else {
            System.out.println("Error! Order failed (Source: CartController)");
            return false;
        }

        int orderId = order.getOrderId();
        boolean allSuccessful = true;

        // Order Item
        for (CartItem cartItem: cartItemList) {
            boolean itemSuccessful = orderDAO.insertOrderItem(orderId, cartItem);
            if (!itemSuccessful) {
                allSuccessful = false;
            }

            boolean deductSuccessful = productDAO.deductStock(cartItem.getProductSizeId(), cartItem.getProductQuantity());
            if (!deductSuccessful) {
                allSuccessful = false;
            }
        }

        cleanup();

        return allSuccessful;
    }

    private void cleanup() {
        cartDAO.clearCart(cart.getCartId());
        cartItemList.clear();
    }

    public void openOrderPage() {
        DashboardController dashController = new DashboardController();
        dashController.setUser(this.user);

        OrderPage orderPage = new OrderPage();
        orderPage.setController(dashController);

        Navigator.goTo(orderPage.initialize());
    }
}
