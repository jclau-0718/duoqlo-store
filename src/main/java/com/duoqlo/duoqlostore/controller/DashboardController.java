package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.view.AdminDashboard;
import com.duoqlo.duoqlostore.view.CartPage;
import com.duoqlo.duoqlostore.view.UserDashboard;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.Objects;

public class DashboardController {
    private User user;
    private String role;

    public void setUser(User user){
        this.user = user;
    }

    public void openDashboard(Stage stage){
        UserDashboard userDash = new UserDashboard();

        role = user.getRole(user.getID());

        if(role != null) {
            System.out.println("Role: "+role);
            if (role.equals("CUSTOMER")) {
                stage.setScene(userDash.initializeUserDash());
            } else if (role.equals("ADMIN")) {
                stage.setScene(userDash.initializeAdminDash());
            } else {
                System.out.println("User role invalid");
            }
        } else {
            System.out.println("Role: "+role);
            System.out.println("Error! User not found");
        }
    }

    public void openCartPage(ActionEvent e){
        CartController cartController = new CartController();

        cartController.setUser(this.user);
        cartController.initCartPage(e);

    }
}
