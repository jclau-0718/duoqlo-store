package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.TableCreator;
import com.duoqlo.duoqlostore.view.LogInPage;
import com.duoqlo.duoqlostore.view.SignUpPage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

import com.duoqlo.duoqlostore.view.UserDashboard;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Navigator.setStage(stage);

        //LogIn Page
        LogInPage logInPage = new LogInPage();
        UserDashboard userDash = new UserDashboard();
        SignUpPage signUpPage = new SignUpPage();

        //Create database
        TableCreator.createTable();

        stage.setTitle("DUOQLO");
        Navigator.goTo(signUpPage.initialize());
//        stage.setScene(userDash.initializeUserDash());
//        stage.getIcons().add(new Image(Objects.requireNonNull(UserDashboard.class.getResourceAsStream("/logo.png"))));
        stage.show();
        stage.centerOnScreen();
    }

    public static void main(String[] args){
        launch(args);
    }

}
