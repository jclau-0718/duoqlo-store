package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.Navigator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Objects;

public class BasePage {
    protected int windowWidth = 1000;
    protected int windowHeight = 750;

    protected HBox searchBar;
    protected Button backButton;
    protected Button forwardButton;

    public BasePage(){
        this.searchBar = createSearchBar();
        this.backButton = createBackButton();
        this.forwardButton = createForwardButton();
    }

    public HBox createHeaderBox(HBox middleHBox, HBox rightBox) {
        int logoHeight = 35;
        Image logo = new Image(Objects.requireNonNull(UserDashboard.class.getResource("/logo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(logoHeight);
        logoView.setPreserveRatio(true);

        Button logoButton = new Button();
        logoButton.setGraphic(logoView);
        logoButton.setId("logo-button");

        //Spacer
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox header = new HBox(5); //Button-to-Button space
        header.setId("header-menu");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setPrefHeight(10);
        header.setPadding(new Insets(20)); //Space between all button and HBox edge
        header.setAlignment(Pos.CENTER); // vertically centers all children

        header.getChildren().addAll(logoButton,spacer1,middleHBox,spacer2,rightBox);

        return header;
    }

    public HBox createSearchBar(){
        TextField inputField = new TextField();
        inputField.setStyle("""
                -fx-border-color: transparent;
                -fx-background-color: transparent;
                """);
        inputField.setPromptText("Type to search");

        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.setIconColor(Color.web("EE5702"));
        Button searchButton = new Button("", searchIcon);

        HBox searchBar = new HBox(5, inputField, searchButton);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(0,0,0,0));
        searchBar.setStyle("""
                -fx-background-color: #E0E0E0;
                -fx-background-radius: 20;
                -fx-border-color: #E0E0E0;
                -fx-border-radius: 20;
                -fx-border-width: 1;
                """);
        searchBar.setMaxHeight(10);

        return searchBar;
    }

    public Button createBackButton(){
        FontIcon backIcon = new FontIcon("fas-arrow-left");
        backIcon.setIconColor(Color.web("EE5702")); //Orange color

        Button backButton = new Button("", backIcon);
        backButton.setStyle("""
                -fx-background-color: transparent;
                -fx-border-color: transparent;
                """);
        if(Navigator.backIsEmpty()){
            backButton.setDisable(true);
        } else {
            backButton.setDisable(false);
        }

        return backButton;
    }

    public Button createForwardButton(){
        FontIcon forwardIcon = new FontIcon("fas-arrow-right");
        forwardIcon.setIconColor(Color.web("EE5702")); //Orange color

        Button forwardButton = new Button("", forwardIcon);
        forwardButton.setStyle("""
                -fx-background-color: transparent;
                -fx-border-color: transparent;
                """);
        if(Navigator.forwardIsEmpty()){
            forwardButton.setDisable(true);
        } else {
            forwardButton.setDisable(false);
        }

        return forwardButton;
    }

    public BorderPane createNavigationBar(){
        BorderPane navBar = new BorderPane();
        navBar.setLeft(backButton);
        navBar.setRight(forwardButton);

        return navBar;
    }

    public void setButtonStyle(Button button){
        button.setStyle("""
                -fx-background-color: #EE5702;
                -fx-border-color: #EE5702;
                -fx-border-radius: 5;
                -fx-border-width: 2;
                -fx-text-fill: white;
                -fx-cursor: hand;
                -fx-font-family: Arial;
                -fx-font-size: 18;
                """);
    }
}
