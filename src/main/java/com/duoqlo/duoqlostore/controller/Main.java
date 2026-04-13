package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.TableCreator;
import com.duoqlo.duoqlostore.view.*;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Navigator.setStage(stage);

        //LogIn Page
        LogInPage logInPage = new LogInPage();
        UserDashboard userDash = new UserDashboard();
        SignUpPage signUpPage = new SignUpPage();
//        CartPage cartPage = new CartPage();
        AdminDashboard adminDash = new AdminDashboard();

        //Create database
        TableCreator.createTable();

        stage.setTitle("DUOQLO");
        Navigator.goTo(adminDash.initialize());
        stage.show();
        stage.centerOnScreen();
        stage.setMaximized(true);
    }

    public static void main(String[] args){
        launch(args);
    }
}
