package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.view.UserDashboard;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

public class Navigator {
    private static Stage stage;
    private static User loggedInUser;
    private static DashboardController dashboardController;
    private static UserDashboard userDash;

    private static Stack<Scene> backStack = new Stack<>();
    private static Stack<Scene> forwardStack = new Stack<>();

    public static void setStage(Stage primaryStage){
        stage = primaryStage;
    }

    public static void goTo(Scene newScene){
        if(stage != null){
            Scene currentScene = stage.getScene();

            if(currentScene != null){
                backStack.push(stage.getScene());
            }
            boolean wasMaximized = stage.isMaximized();

            System.out.println("Stage maximized: "+stage.isMaximized());

            forwardStack.clear();
            stage.setScene(newScene);

            if (wasMaximized) {
                stage.setMaximized(true);
            }
        } else {
            System.out.println("Stage not set!");
        }
    }

    public static void setUser(User user) {
        loggedInUser = user;
    }

    public static void setDashboardController(DashboardController dashController) {
        dashboardController = dashController;
        if (loggedInUser != null) {
            dashboardController.setUser(loggedInUser);
        } else {
            System.err.println("User is null (Source: Navigator)");
        }
    }

    public static void openUserDashboard() {
        if (dashboardController != null) {
            userDash = new UserDashboard(dashboardController);
            goTo(userDash.initialize());
        } else {
            System.err.println("dashController is null (Source: Navigator)");
        }
    }

    public static boolean backIsEmpty(){
        if(backStack.isEmpty()){
            return true;
        }

        return false;
    }

    public static boolean forwardIsEmpty(){
        if(forwardStack.isEmpty()){
            return true;
        }

        return false;
    }
}
