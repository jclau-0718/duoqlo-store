package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminDashController {
    private UserDAO userDAO = new UserDAO();
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<User> admins = FXCollections.observableArrayList();
    private ObservableList<Product> products = FXCollections.observableArrayList();
    private ObservableList<Order> orders = FXCollections.observableArrayList();

    public void retrieveAllData() {
        retrieveUsers();
        retrieveAdmins();;
        retrieveProducts();
        retrieveOrders();
    }

    public void retrieveUsers() {
        this.users.setAll(userDAO.getAllUsersObservable());
    }

    public void retrieveAdmins() {
        this.admins.setAll(userDAO.getAllAdminsObservable());
    }

    public void retrieveProducts() {
        this.products.setAll(productDAO.getAllProductsObservable());
    }

    public void retrieveOrders() {
        this.orders.setAll(orderDAO.getAllOrdersObservable());
    }

    public ObservableList<User> getUsers() {
        return this.users;
    }

    public ObservableList<User> getAdmins() {
        return this.admins;
    }

    public int getTotalUsers() {
        return this.users.size();
    }

    public int getTotalProducts() {
        return this.products.size();
    }

    public int getTotalOrders() {
        return this.orders.size();
    }

    public int getTotalAdmins() {
        return this.admins.size();
    }

    public boolean deactivateUser(int userId) {
        try {
            for (User user : users) {
                if (user.getId() == userId) {
                    if (userDAO.deactivateUser(userId)) {
                        user.setIs_active(0);

                        //Update ObservableList
                        int index = users.indexOf(user);
                        if (index >= 0) {
                            users.set(index, user);
                        }

                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean reactivateUser(int userId) {
        try {
            for (User user : users) {
                if (user.getId() == userId) {
                    if (userDAO.reactivateUser(userId)) {
                        user.setIs_active(1);

                        //Update ObservableList
                        int index = users.indexOf(user);
                        if (index >= 0) {
                            users.set(index, user);
                        }

                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
