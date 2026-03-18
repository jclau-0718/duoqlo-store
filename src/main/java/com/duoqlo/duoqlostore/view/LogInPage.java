package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.LogInPageHandler;
import com.duoqlo.duoqlostore.model.UserDAO;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.*;
import java.util.Objects;
import org.kordamp.ikonli.javafx.FontIcon;

public class LogInPage {
    public static ImageView createLogo(){
        int logoHeight = 80;

        Image logo = new Image(Objects.requireNonNull(HomePage.class.getResource("/logo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(logoHeight);
        logoView.setPreserveRatio(true);

        return logoView;
    }

    public static TextField createUsernameField(){
        TextField usernameField = new TextField();
        usernameField.setId("input-field");
        usernameField.setPromptText("Username");

        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // TextField lost focus
                String username = usernameField.getText().trim();
                if (!UserDAO.usernameExists(username)) {
                    // Set border to red
                    usernameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                }
            }
        });
        return usernameField;
    }

    public static StackPane createPasswordField(){
        PasswordField passwordInputField = new PasswordField();
        passwordInputField.setId("input-field");
        passwordInputField.setPromptText("Password");

        FontIcon eyeIcon = new FontIcon("far-eye");
        eyeIcon.setIconColor(Color.GREY);
        eyeIcon.setIconSize(16);

        Button showPassButton = new Button("",eyeIcon);
        showPassButton.setId("showpass-button");
        showPassButton.setFocusTraversable(false); // so it doesn't steal focus
        showPassButton.setVisible(false);

        // Show eye icon only when focused
        passwordInputField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            showPassButton.setVisible(newVal);
        });

        StackPane passwordField = new StackPane();
        passwordField.getChildren().addAll(passwordInputField,showPassButton);
        passwordField.setAlignment(showPassButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(showPassButton, new Insets(0, 5, 7, 0));

        return passwordField;
    }

    public static VBox createInputField(){
        VBox inputField = new VBox();
        inputField.getChildren().addAll(createUsernameField(),createPasswordField());

        return inputField;
    }

    public static VBox createButtons(){
        Button logInButton = new Button("Log In");
        logInButton.setId("log-in-button");

        Button signUpButton = new Button("Sign Up?");
        signUpButton.setId(("sign-up-button"));

        //Instance of event handler
        LogInPageHandler handler = new LogInPageHandler();

        logInButton.setOnAction(handler);
        signUpButton.setOnAction(handler);

        VBox box = new VBox();
        box.getChildren().addAll(logInButton,signUpButton);

        return box;
    }

    public static VBox createLogInForm(){
        int height = 300;
        int width = 300;
        HBox logoBox = new HBox(createLogo());
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(15,0,15,0));

        HBox buttonBox = new HBox(createButtons());
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(60,0,0,0));

        VBox logInBox = new VBox();
        logInBox.getChildren().addAll(
                logoBox,
                createUsernameField(),
                createPasswordField(),
                buttonBox
        );
        logInBox.setMaxHeight(height);
        logInBox.setMaxWidth(width);

        return logInBox;
    }
}
