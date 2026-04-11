package com.duoqlo.duoqlostore.controller;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public abstract class UserController {
    public void updateUsernameFieldStyle(TextField usernameField, Label errorLabel,
                                         boolean userFieldTyped, boolean userFieldError) {
        boolean isEmpty = usernameField.getText().isEmpty();

        usernameField.getStyleClass().removeAll("error","valid");

        if(!userFieldTyped) {
            return;
        } else {
            if (isEmpty) {
                usernameField.getStyleClass().remove("error");
                errorLabel.setText("Please enter a username.");
                errorLabel.setVisible(true);
            }
        }

        if(userFieldError) {
            usernameField.getStyleClass().add("error");
            errorLabel.setText("Please enter a valid username.");
            errorLabel.setVisible(true);
        } else if (isEmpty) {
            usernameField.getStyleClass().add("error");
            errorLabel.setText("Please enter a username.");
            errorLabel.setVisible(true);
        } else {
            usernameField.getStyleClass().remove("error");
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }

    public void updatePassFieldStyle(HBox passwordBox, TextField passwordField, Label errorLabel,
                                     boolean passFieldTyped, boolean passFieldError){

        boolean focused = passwordField.isFocused();
        boolean isEmpty = passwordField.getText().isEmpty();

        passwordBox.getStyleClass().removeAll("error","valid");

        if (!passFieldTyped) {
            return;
        } else {
            if (isEmpty) {
                passwordBox.getStyleClass().add("error");
                errorLabel.setText("Please enter a password.");
                errorLabel.setVisible(true);
            }
        }

        if (focused) {
            if (!passwordBox.getStyleClass().contains("focused")) {
                passwordBox.getStyleClass().add("focused");
            }
        } else {
            passwordBox.getStyleClass().remove("focused");
        }

        if(passFieldError) {
            passwordBox.getStyleClass().add("error");
            errorLabel.setText("Please enter a valid password.");
            errorLabel.setVisible(true);
        } else if (isEmpty) {
            passwordBox.getStyleClass().add("error");
            errorLabel.setText("Please enter a password.");
            errorLabel.setVisible(true);
        } else {
            passwordBox.getStyleClass().remove("error");
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }

    public void setupUsernameValidation (TextField usernameField, Label usernameErrorLabel,
                                         final boolean[] userFieldTyped, final boolean[] userFieldError){

        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            userFieldTyped[0] = true;
            userFieldError[0] = false;
            updateUsernameFieldStyle(usernameField, usernameErrorLabel, userFieldTyped[0], userFieldError[0]);
        });

        //Focus Listener (update style only)
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            updateUsernameFieldStyle(usernameField, usernameErrorLabel, userFieldTyped[0], userFieldError[0]);
        });
    }

    public void setupPasswordValidation (HBox passwordBox, PasswordField passwordField, Label passErrorLabel,
                                         final boolean[] passFieldTyped, final boolean[] passFieldError) {

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            passFieldTyped[0] = true;
            passFieldError[0] = false;
            updatePassFieldStyle(passwordBox, passwordField, passErrorLabel, passFieldTyped[0], passFieldError[0]);
        });

        //Focus Listener (update style only)
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            updatePassFieldStyle(passwordBox, passwordField, passErrorLabel, passFieldTyped[0], passFieldError[0]);
        });
    }
}
