package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.model.UserDAO;
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

public abstract class AuthPage {

    int windowWidth = 1000;
    int windowHeight = 750;

    public abstract Scene initialize();

    public TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setMaxWidth(Double.MAX_VALUE);
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

    public HBox createPasswordHBox(PasswordField passwordField) {
        FontIcon eyeIcon = new FontIcon("far-eye");
        eyeIcon.setIconColor(Color.GREY);
        eyeIcon.setIconSize(16);

        Button showPassButton = new Button("",eyeIcon);
        showPassButton.setId("showpass-button");
        showPassButton.setFocusTraversable(false); // Prevent stealing focus
        showPassButton.setVisible(false);

        HBox passwordHBox = new HBox();
        passwordHBox.getChildren().addAll(passwordField, showPassButton);
        passwordHBox.setFillHeight(true); //Allow node to expand height until HBox height
        passwordHBox.getStyleClass().add("password-box");
        passwordHBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        HBox.setHgrow(showPassButton, Priority.ALWAYS);

        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { //If text field is focused
                passwordField.setStyle("-fx-prompt-text-fill: transparent");
                passwordHBox.getStyleClass().add("focused");
            } else {
                passwordField.setStyle("-fx-prompt-text-fill: gray");
                passwordHBox.getStyleClass().remove("focused");
            }
            showPassButton.setVisible(newVal);
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
