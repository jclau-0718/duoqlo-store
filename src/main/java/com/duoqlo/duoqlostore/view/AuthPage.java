package com.duoqlo.duoqlostore.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Objects;

public abstract class AuthPage {
    int windowWidth = 1000;
    int windowHeight = 750;

    public abstract Scene initialize();

    public TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setId("text-field");
        textField.setPadding(new Insets(6,8,8,8));

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal) {
                textField.setId("text-field");
                textField.setStyle("-fx-prompt-text-fill: gray");
            } else {
                textField.setId("text-field-focus");
                textField.setStyle("-fx-prompt-text-fill: transparent");
            }
        });

        return textField;
    }

    public PasswordField createPasswordField(String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setId("pass-textfield");

        return passwordField;
    }

    public HBox createPasswordBox(PasswordField passwordField) {

        FontIcon eyeIcon = new FontIcon("far-eye");
        eyeIcon.setIconColor(Color.GREY);
        eyeIcon.setIconSize(16);

        Button showPassButton = new Button("",eyeIcon);
        showPassButton.setId("showpass-button");
        showPassButton.setFocusTraversable(false); // Prevent stealing focus
        showPassButton.setVisible(false);
        showPassButton.setMaxHeight(Double.MAX_VALUE);

        HBox passwordBox = new HBox();

        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { //If text field is focused
                passwordBox.setId("pass-textfield-box-focus");
            } else {
                passwordBox.setId("pass-textfield-box");
            }
            showPassButton.setVisible(newVal);
        });

        passwordBox.getChildren().addAll(passwordField, showPassButton);
        passwordBox.setId("pass-textfield-box");
        passwordBox.setFillHeight(true); //Allow node to expand height until HBox height
        passwordBox.setPadding(new Insets(6,8,8,8));
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        HBox.setHgrow(showPassButton, Priority.ALWAYS);

        return passwordBox;
    }


    public Button primaryButton(String text) {
        Button button = new Button(text);
        button.setId("primary-button");
        button.setMaxWidth(Double.MAX_VALUE);

        return button;
    }

    public Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setId("secondary-button");
        button.setMaxWidth(Double.MAX_VALUE);

        return button;
    }

    public Button secondaryButton(String text, String iconCode){
        FontIcon icon = new FontIcon(iconCode);

        Button button = new Button(text, icon);
        button.setId("secondary-button");
        button.setContentDisplay(ContentDisplay.LEFT); // icon on the left
        button.setMaxWidth(Double.MAX_VALUE);

        icon.iconColorProperty().bind(button.textFillProperty());

        return button;
    }
}
