package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.*;
import com.duoqlo.duoqlostore.view.*;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.util.List;

public class CartController {
    private ProductDAO productDAO = new ProductDAO();
    private CartDAO cartDAO = new CartDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private User user;
    private Cart cart;
    private List<CartItem> cartItemList;

    public CartController(User user) {
        this.user = user;

        getCart(user);
    }

    public User getUser() { return this.user; }

    public void openOrdersPage() {
        OrderController orderController = new OrderController(this.user);
        OrderPage orderPage = new OrderPage(orderController);

        Navigator.goTo(orderPage.initialize());

    }

    public void openProfilePage() {
        ProfileController profileController = new ProfileController(this.user);
        ProfilePage profilePage = new ProfilePage(profileController);

        Navigator.goTo(profilePage.initialize());
    }

    public void getCart(User user) {
        int userId = user.getId();

        if (!cartDAO.userCartExists(userId)) {
            this.cart = cartDAO.createCart(userId);
        } else {
            this.cart = cartDAO.getUserCart(userId);
        }

        cartItemList = cart.getCartItemList();
    }

    public boolean addCartItem(int productSizeId, int quantity, double subTotal) {
        int cartId = cart.getCartId();

        CartItem cartItem = new CartItem(cartId, productSizeId, quantity, subTotal);
        if(cartDAO.insertCartItem(cartItem)) {
            cart.addCartItem(cartItem);

            return true;
        }

        return false;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public int getTotalItems() {
        int totalItem = 0;

        for(CartItem cartItem : cartItemList) {
            totalItem += cartItem.getProductQuantity();
        }

        return totalItem;
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

    public boolean removeFromCart(int productSizeId) {
        if (removeFromList(productSizeId) && removeFromDatabase(productSizeId)) {
            return true;
        } else {
            return false;
        }
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

    public void handleCheckOut() {
        UserDashController dashController = new UserDashController(this.user);

        UserDashboard userDash = new UserDashboard(dashController);
        Navigator.goTo(userDash.initialize());

        PauseTransition delay = new PauseTransition(Duration.millis(100));
        delay.setOnFinished(delayEvent -> {
            AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
            successAlert.show(userDash.getBody(), "Order Confirmed!", Pos.TOP_CENTER);
        });
        delay.play();
    }

    public boolean handleOrder(double totalPrice, int totalItems) {
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalItems(totalItems);
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
        System.out.println(cartItemList.size());

        // Order Item
        for (CartItem cartItem: cartItemList) {
            boolean itemSuccessful = orderDAO.insertOrderItem(orderId, cartItem);
            if (!itemSuccessful) {
                allSuccessful = false;
            }

            boolean deductSuccessful = productDAO.processOrder(cartItem.getProductSizeId(), cartItem.getProductQuantity());
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
}
