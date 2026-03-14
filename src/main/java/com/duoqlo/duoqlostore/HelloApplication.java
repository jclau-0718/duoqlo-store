package com.duoqlo.duoqlostore;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        int windowWidth = 1200;
        int windowHeight = 750;

        int iconSize = 24;

//        FontIcon cartIcon = new FontIcon("fas-shopping-cart");
//        cartIcon.setIconSize(24);
//        cartIcon.setIconColor(Color.rgb(248,141,5));
//
//        Button cartButton = new Button("",cartIcon);
        //Header Menu
        HBox header = new HBox(5);
        header.setId("header-menu");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setPrefHeight(45);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER); // vertically centers all children

        //Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMaxWidth(900);

        //Add Logo
        Image logo = new Image(Objects.requireNonNull(getClass().getResource("icons/logo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(55);
        logoView.setPreserveRatio(true);

        //Search Button
        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.setIconSize(iconSize);
        searchIcon.setIconColor(Color.WHITE);
        Button searchButton = new Button("",searchIcon);

        //Cart Button
        FontIcon cartIcon = new FontIcon("fas-shopping-cart");
        cartIcon.setIconSize(iconSize);
        cartIcon.setIconColor(Color.WHITE);
        Button cartButton = new Button("",cartIcon);

        //Profile Button
        FontIcon profileIcon = new FontIcon("far-user");
        profileIcon.setIconSize(iconSize);
        profileIcon.setIconColor(Color.WHITE);
        Button profileButton = new Button("",profileIcon);

        header.getChildren().addAll(logoView,spacer,searchButton,cartButton,profileButton);

        BorderPane root = new BorderPane();
        root.setTop(header);

        Scene scene = new Scene(root, windowWidth, windowHeight);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        stage.setTitle("DUOQLO");
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/logo.png"))));
        stage.show();
        stage.centerOnScreen();
    }

    public static void main(String[] args){
        launch(args);
    }
}