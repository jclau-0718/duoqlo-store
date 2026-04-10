package com.duoqlo.duoqlostore.view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public abstract class AuthPage extends ApplicationPage {

    public abstract Scene initialize();

    public TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setMaxWidth(Double.MAX_VALUE);
//        textField.setId("text-field");
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

    public Label createErrorLabel() {
        Label label = new Label();
        label.setVisible(false);
        label.setManaged(true);
        label.setId("error-msg");

        return label;
    }

    public VBox createTextFieldBox(TextField textField, Label errorLabel) {
        VBox box = new VBox(textField, errorLabel);
        box.setFillWidth(true);
        box.setId("text-field-box");

        VBox.setMargin(errorLabel, new Insets(3, 0, 0, 0));

        return box;
    }

    public PasswordField createPasswordField(String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);

        return passwordField;
    }

    public HBox createPasswordHBox(PasswordField passwordField, TextField visiblePassField) {
        FontIcon eyeSlashIcon = new FontIcon("far-eye-slash");
        eyeSlashIcon.setIconColor(Color.LIGHTGRAY);
        eyeSlashIcon.setIconSize(16);

        FontIcon eyeIcon = new FontIcon("far-eye");
        eyeIcon.setIconColor(Color.LIGHTGRAY);
        eyeIcon.setIconSize(16);

        visiblePassField.getStyleClass().add("visible-pass");
        visiblePassField.setVisible(false);
        visiblePassField.setManaged(false);

        Button toggleButton = new Button("",eyeSlashIcon);
        toggleButton.setId("showpass-button");
        toggleButton.setFocusTraversable(false); // Prevent stealing focus
        toggleButton.setVisible(false);

        final boolean[] passwordVisible = {false};

        toggleButton.setOnAction(e -> {
            if (!passwordVisible[0]) {
                //Make password visible
                //Update toggleButton icon
                toggleButton.setGraphic(eyeIcon);

                visiblePassField.setText(passwordField.getText());
                visiblePassField.setVisible(true);
                visiblePassField.setManaged(true);
                visiblePassField.requestFocus();
                visiblePassField.positionCaret(visiblePassField.getText().length());

                passwordField.setVisible(false);
                passwordField.setManaged(false);

                passwordVisible[0] = true;
            } else {
                //Hide password
                //Update toggleButton icon
                toggleButton.setGraphic(eyeSlashIcon);

                passwordField.setText(visiblePassField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                passwordField.requestFocus();
                passwordField.positionCaret(passwordField.getText().length());

                visiblePassField.setVisible(false);
                visiblePassField.setManaged(false);

                passwordVisible[0] = false;
            }
        });

        HBox passwordHBox = new HBox();
        passwordHBox.getChildren().addAll(passwordField, visiblePassField, toggleButton);
        passwordHBox.setFillHeight(true); //Allow node to expand height until HBox height
        passwordHBox.getStyleClass().add("password-box");
        passwordHBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        HBox.setHgrow(visiblePassField, Priority.ALWAYS);
        HBox.setHgrow(toggleButton, Priority.ALWAYS);

        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { //If password field is focused
                passwordField.setStyle("-fx-prompt-text-fill: transparent");
                passwordHBox.getStyleClass().add("focused");
                toggleButton.setVisible(true);
            } else {
                if (!passwordField.getText().isEmpty()) {
                    passwordField.setStyle("-fx-prompt-text-fill: transparent");
                    passwordHBox.getStyleClass().add("focused");
                    toggleButton.setVisible(true);
                } else {
                    passwordField.setStyle("-fx-prompt-text-fill: gray");
                    passwordHBox.getStyleClass().remove("focused");
                    toggleButton.setVisible(false);
                }
            }
        });

        visiblePassField.textProperty().addListener((obs, oldVal, newVal) -> {
            passwordField.setText(visiblePassField.getText());
        });

        visiblePassField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { //If text field is focused
                visiblePassField.setStyle("-fx-prompt-text-fill: transparent");
                passwordHBox.getStyleClass().add("focused");
                toggleButton.setVisible(true);
            } else {
                if (!visiblePassField.getText().isEmpty()) {
                    visiblePassField.setStyle("-fx-prompt-text-fill: transparent");
                    passwordHBox.getStyleClass().add("focused");
                    toggleButton.setVisible(true);
                } else {
                    visiblePassField.setStyle("-fx-prompt-text-fill: gray");
                    passwordHBox.getStyleClass().remove("focused");
                    toggleButton.setVisible(false);
                }
            }
        });

        return passwordHBox;
    }

    public VBox createPasswordVBox(HBox passwordHBox, Label errorLabel) {
        VBox passwordVBox = new VBox(passwordHBox, errorLabel);
        passwordVBox.setFillWidth(true);

        VBox.setMargin(errorLabel, new Insets(3, 0, 0, 0));

        return passwordVBox;
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

    public HBox popUpBox(FontIcon icon, Label label) {
        HBox hbox = new HBox(icon, label);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(20, 10, 20 , 20));
        hbox.setMaxHeight(50);
        hbox.setMaxWidth(250);
        HBox.setMargin(icon, new Insets(0, 7, 0, 0));

        return hbox;
    }

    public void playPopUpAnimation(Node node) {
        node.setOpacity(0);     //Start invisible
        node.setTranslateY(-5); //5px above

        //Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        //Move down
        TranslateTransition moveDown = new TranslateTransition(Duration.seconds(0.5), node);
        moveDown.setFromY(-50);
        moveDown.setToY(0);

        ParallelTransition enterAnimation = new ParallelTransition(fadeIn, moveDown);

        //Wait for 5 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(5));

        // Fade out animation
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), node);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        //Remove node after fade out
        fadeOut.setOnFinished(event -> {
            if (node.getParent() != null) {
                ((javafx.scene.layout.Pane) node.getParent()).getChildren().remove(node);
            }
        });

        // Chain all animations
        SequentialTransition fullAnimation = new SequentialTransition(
                enterAnimation,
                pause,
                fadeOut
        );

        fullAnimation.play();
    }
}
