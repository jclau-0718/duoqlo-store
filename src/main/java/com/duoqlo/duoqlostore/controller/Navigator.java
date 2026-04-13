package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.User;
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
            Scene currentScene = stage.getScene();

            boolean wasMaximized = stage.isMaximized();

            stage.setScene(newScene);

            if (wasMaximized) {
                stage.setMaximized(true);
            }
        } else {
            System.out.println("Stage not set!");
        }
    }

    public static void openDashboard(User user) {
        if (user.getRole().equals("CUSTOMER")) {
            UserDashController controller = new UserDashController(user);
            UserDashboard userDash = new UserDashboard(controller);

            goTo(userDash.initialize());
        } else {
            System.out.println("Open admin dashboard");
        }
    }
}
