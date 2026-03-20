package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.TableCreator;
import com.duoqlo.duoqlostore.view.LogInPage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

import com.duoqlo.duoqlostore.view.UserDashboard;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        int windowWidth = 1000;
        int windowHeight = 750;

        //LogIn Page
//        LogInPage logInPage = new LogInPage();

//        BorderPane root = new BorderPane();
//        root.setCenter(logInPage.createLogInForm());
//        Platform.runLater(() -> {root.requestFocus();}); //Remove initial focus on Username TextField
//        root.setOnMouseClicked(e -> root.requestFocus()); //Allow unfocus on TextField

        UserDashboard userDash = new UserDashboard();

        //Create database
        TableCreator.createTable();

//        Scene scene = new Scene(root, windowWidth, windowHeight);
//        scene.getStylesheets().add(
//                Objects.requireNonNull(
//                        getClass().getResource("/css/login-page.css")
//                ).toExternalForm()
//        );

        stage.setTitle("DUOQLO");
        stage.setScene(userDash.initializeUserDash());
//        stage.getIcons().add(new Image(Objects.requireNonNull(UserDashboard.class.getResourceAsStream("/logo.png"))));
        stage.show();
        stage.centerOnScreen();
    }

    public static void main(String[] args){
        launch(args);
    }

}
