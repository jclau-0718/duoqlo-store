package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.*;
import com.duoqlo.duoqlostore.view.*;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class CartController {
    private ProductDAO productDAO = new ProductDAO();
    private CartDAO cartDAO = new CartDAO();
    private OrderDAO orderDAO = new OrderDAO();

    private User user;
    private Cart cart;
    private List<CartItem> cartItemList;

    private CartItem tempCartItem;

    public CartController(User user) {
        this.user = user;

        getCart(user);
    }

    public void refreshCart() {
        getCart(this.user);
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

        if(this.cart == null) {
            throw new RuntimeException("Failed to fetch cart");
        }

        cartItemList = cart.getCartItemList();

    }

    public boolean addCartItem(int productSizeId, int quantity, double subTotal) {
        int cartId = cart.getCartId();

        if(cartDAO.prodSizeExist(productSizeId)) {
            if (cartDAO.updateCartItem(productSizeId, quantity, subTotal)) {
                cartDAO.updateCartLastUpdated(cartId);

                return true;
            }
        } else {
            CartItem cartItem = new CartItem(cartId, productSizeId, quantity, subTotal);
            if (cartDAO.insertCartItem(cartItem)) {
                cart.addCartItem(cartItem);

                cartDAO.updateCartLastUpdated(cartId);

                return true;
            }
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

    public String getLastUpdatedDate() {
        return this.cart.getLastUpdatedDate(); }

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

    public void setTempCartItem(int productSizeId, int quantity, double subTotal) {
        tempCartItem = new CartItem(productSizeId, quantity, subTotal);
    }

    public CartItem getTempCartItem() { return this.tempCartItem; }

    public boolean handleOrder(double totalPrice, int totalItems, Payment paymentMethod) {
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalItems(totalItems);
        order.setTotalPrice(totalPrice);
        order.setShippingAddress(user.getFullAddress());
        order.setPaymentMethod(paymentMethod);

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

            boolean deductSuccessful = productDAO.processOrder(cartItem.getProductSizeId(), cartItem.getProductQuantity());
            if (!deductSuccessful) {
                allSuccessful = false;
            }
        }

        clearCart();

        return allSuccessful;
    }

    public void handleCheckOut() {
        UserDashController dashController = new UserDashController(this.user);

        UserDashboard userDash = new UserDashboard(dashController);
        Navigator.goTo(userDash.initialize());

        PauseTransition delay = new PauseTransition(Duration.millis(100));
        delay.setOnFinished(delayEvent -> {
            AlertMsg successAlert = new AlertMsg(AlertType.SUCCESS);
            successAlert.show(userDash.getBody(), "Order Confirmed!", Pos.TOP_CENTER);
        });
        delay.play();
    }

    public void clearCart() {
        cartDAO.clearCart(cart.getCartId());
        cartItemList.clear();
    }

    public void cleanup() {
        cartItemList.clear();
        cart = null;
        user = null;
    }
}
