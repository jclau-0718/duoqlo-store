package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.view.CartPage;
import com.duoqlo.duoqlostore.view.UserDashboard;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class DashboardController {
    private User user;
    private String role;

    public void setUser(User user){
        this.user = user;
    }

    public void openDashboard(Stage stage){
        UserDashboard userDash = new UserDashboard();

        role = user.getRole(user.getId());

        if(role != null) {
            System.out.println("Role: "+role);
            if (role.equals("CUSTOMER")) {
                stage.setScene(userDash.initialize());
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

    public void openCartPage(){
        CartController cartController = new CartController();
        CartPage cartPage = new CartPage();

        cartController.setUser(this.user);
        Navigator.goTo(cartPage.initialize());
    }
}
