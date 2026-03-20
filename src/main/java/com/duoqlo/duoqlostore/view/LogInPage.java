package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.LogInController;
import com.duoqlo.duoqlostore.controller.SceneManager;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.*;

import java.util.Locale;
import java.util.Objects;
import org.kordamp.ikonli.javafx.FontIcon;

public class LogInPage {
    private final int usernameTopPad = 50;
    private final int passwordTopPad = usernameTopPad;
    private final int buttonTopPad = 60;

    private LogInController controller = new LogInController();

    public ImageView createLogo(){
        int logoHeight = 80;

        Image logo = new Image(Objects.requireNonNull(UserDashboard.class.getResource("/logo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(logoHeight);
        logoView.setPreserveRatio(true);

        return logoView;
    }

    public TextField createUsernameField() {
        TextField usernameField = new TextField();
        usernameField.setId("text-field");
        usernameField.setPromptText("Username");
        usernameField.setAlignment(Pos.CENTER_LEFT);

        return usernameField;
    }

    public VBox createUsernameBox(TextField usernameField){
        HBox usernameHBox = new HBox(usernameField);

        usernameHBox.setId("text-field-box");
        HBox.setHgrow(usernameField, Priority.ALWAYS);
        usernameHBox.setPadding(new Insets(6,8,8,8));

        Label usernameErrorLabel = new Label("");
        HBox usernameErrorBox = new HBox(usernameErrorLabel);
        usernameErrorBox.setAlignment(Pos.CENTER_LEFT);
        usernameErrorBox.setPadding(new Insets(0,0,0,10));
        usernameErrorBox.setVisible(false);
        usernameErrorBox.setManaged(false);

        controller.setupUsernameValidation(usernameHBox, usernameField, usernameErrorBox, usernameErrorLabel);

        VBox usernameBox = new VBox(usernameHBox, usernameErrorBox);
        VBox.setMargin(usernameErrorBox, new Insets(7,0,7,0));

        return usernameBox;
    }

    public PasswordField createPasswordField() {

        PasswordField passwordField = new PasswordField();
        passwordField.setId("text-field");
        passwordField.setPromptText("Password");

        return passwordField;
    }

    public HBox createPasswordHBox(PasswordField passwordField){
        HBox passwordHBox = new HBox();

        FontIcon eyeIcon = new FontIcon("far-eye");
        eyeIcon.setIconColor(Color.GREY);
        eyeIcon.setIconSize(16);

        Button showPassButton = new Button("",eyeIcon);
        showPassButton.setId("showpass-button");
        showPassButton.setFocusTraversable(false); // Prevent stealing focus
        showPassButton.setVisible(false);
        showPassButton.setMaxHeight(Double.MAX_VALUE);

        // Show eye icon only when focused
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { //If text field is focused
                passwordHBox.setId("text-field-box-focus");
            } else {
                passwordHBox.setId("text-field-box");
            }
            showPassButton.setVisible(newVal);
        });

        passwordHBox.getChildren().addAll(passwordField, showPassButton);
        passwordHBox.setId("text-field-box");
        passwordHBox.setFillHeight(true); //Allow node to expand height until HBox height
        passwordHBox.setPadding(new Insets(6,8,8,8));
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        HBox.setHgrow(showPassButton, Priority.ALWAYS);

        return passwordHBox;
    }

    public VBox createLogInForm(){
        int height = 100;
        int width = 300;

        //Logo
        HBox logoBox = new HBox(createLogo());
        logoBox.setAlignment(Pos.CENTER);

        //Log In Button
        Button logInButton = new Button("Log In");
        logInButton.setId("log-in-button");
        logInButton.setMaxWidth(Double.MAX_VALUE);

        //Sign Up Button
        Button signUpButton = new Button("Sign Up?");
        signUpButton.setId(("sign-up-button"));
        signUpButton.setMaxWidth(Double.MAX_VALUE);

        //Username Section
        TextField usernameField = createUsernameField();
        VBox usernameBox = createUsernameBox(usernameField);

        //Password Section
        PasswordField passwordField = createPasswordField();
        HBox passwordHBox = createPasswordHBox(passwordField);

        logInButton.setOnAction(e ->
                controller.handleLogIn(e, usernameField.getText(), passwordField.getText()));

        VBox buttonBox = new VBox(5, logInButton,signUpButton);
        buttonBox.setAlignment(Pos.CENTER);

        HBox buttonHBox = new HBox(buttonBox);
        buttonHBox.setAlignment(Pos.BOTTOM_RIGHT);

        VBox logInBox = new VBox();
        logInBox.getChildren().addAll(
                logoBox,
                usernameBox,
                passwordHBox,
                logInButton,
                signUpButton
        );
        logInBox.setMaxHeight(height);
        logInBox.setMaxWidth(width);
        logInBox.setAlignment(Pos.CENTER);
        logInBox.setPadding(new Insets(20,20,20,20));
        logInBox.setId("login-box");

        VBox.setMargin(usernameBox, new Insets(usernameTopPad,0,0,0));
        VBox.setMargin(passwordHBox, new Insets(passwordTopPad,0,0,0));
        VBox.setMargin(logInButton, new Insets(buttonTopPad,0,20,0));
        VBox.setMargin(signUpButton, new Insets(0,0,20,0));

        return logInBox;
    }
}
