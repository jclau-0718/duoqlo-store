package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.view.AdminDashboard;
import com.duoqlo.duoqlostore.view.UserDashboard;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigator {
    private static Stage stage;

    public static void setStage(Stage primaryStage){
        stage = primaryStage;
    }

    public static void goTo(Scene newScene){
        if(stage != null){
            boolean wasMaximized = stage.isMaximized();

            stage.setScene(newScene);

            if (wasMaximized) {
                stage.setMaximized(true);
            }
        } else {
            System.err.println("Stage not set!");
        }
    }

    public static void openDashboard(User user) {
        if (user.getRole().equals("CUSTOMER")) {
            UserDashController controller = new UserDashController(user);
            UserDashboard userDash = new UserDashboard(controller);

            goTo(userDash.initialize());
        } else {
            AdminDashController controller = new AdminDashController(user);
            AdminDashboard adminDash = new AdminDashboard(controller);

            goTo(adminDash.initialize());
        }
    }

    public static void openUserDashboard(UserDashController controller) {
        UserDashboard userDash = new UserDashboard(controller);

        goTo(userDash.initialize());
    }
}
