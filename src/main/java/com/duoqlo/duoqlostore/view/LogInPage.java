package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.AuthController;
import com.duoqlo.duoqlostore.controller.Navigator;
import com.duoqlo.duoqlostore.model.User;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.geometry.*;

import java.util.Objects;

import javafx.stage.Stage;

public class LogInPage extends AuthPage {
    private AuthController controller;

    private AlertMsg errorAlert = new AlertMsg(AlertType.ERROR);

    private StackPane root;

    private InputField usernameField;

    private final int usernameTopPad = 50;
    private final int passwordTopPad = 35;
    private final int buttonTopPad = 60;

    public LogInPage() {
        this.controller = new AuthController();
    }

    public LogInPage(AuthController controller) {
        this.controller = controller;
    }

    public ImageView createLogo(){
        int logoHeight = 60;

        Image logo = new Image(Objects.requireNonNull(UserDashboard.class.getResource("/logo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(logoHeight);
        logoView.setPreserveRatio(true);

        return logoView;
    }

    public VBox createLogInForm(){
        int height = 100;
        int width = 300;

        //Logo
        HBox logoBox = new HBox(createLogo());
        logoBox.setAlignment(Pos.CENTER);

        //Username Section
        usernameField = createInputField("Username");
        usernameField.getStyleClass().add("username-field");
        Label usernameErrorLabel = createErrorLabel();
        VBox usernameBox = createTextFieldBox(usernameField, usernameErrorLabel);
        usernameBox.setMinWidth(360);

        controller.setupUsernameValidation(usernameField, usernameErrorLabel);

        //Password Section
        PasswordField passwordField = createPasswordField("Password");

        TextField visiblePassField = new TextField();
        visiblePassField.setPromptText(passwordField.getPromptText());

        HBox passwordHBox = createPasswordHBox(passwordField, visiblePassField);
        passwordHBox.getStyleClass().add("password-box");
        Label passwordErrorLabel = createErrorLabel();
        VBox passwordBox = createPasswordVBox(passwordHBox, passwordErrorLabel);

        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        //Log In Button
        PrimaryButton logInButton = new PrimaryButton("Log In");
        logInButton.setFontSize(18);
        logInButton.setMaxWidth(Double.MAX_VALUE);
        logInButton.setDefaultButton(true);

        logInButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if(!controller.handleLogIn(username, password)) {

                errorAlert.show(root, "Invalid username or password.", Pos.TOP_CENTER);

                controller.setUserFieldError(true);
                controller.setPassFieldError(true);
                controller.updateUsernameFieldStyle(usernameField, usernameErrorLabel);
                controller.updatePassFieldStyle(passwordHBox, passwordField, passwordErrorLabel);
            } else if (!controller.isUserActive(username)) {
                errorAlert.show(root, "Account has been deactivated.", Pos.TOP_CENTER);
            } else {
                User loggedInUser = controller.getUser(username);

                if (loggedInUser != null) {
                    Navigator.openDashboard(loggedInUser);
                } else {
                    errorAlert.show(root, "User not found. Please try again.", Pos.TOP_CENTER);
                }
            }

        });


        controller.setupPasswordValidation(passwordHBox, passwordField, passwordErrorLabel);

        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                logInButton.fire();;
            }
        });

        //Sign Up Button
        SecondaryButton signUpButton = new SecondaryButton("Sign Up");
        signUpButton.setFontSize(18);
        signUpButton.setMaxWidth(Double.MAX_VALUE);

        signUpButton.setOnAction(e -> {
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new SignUpPage().initialize());
        });

        VBox buttonBox = new VBox(5, logInButton,signUpButton);
        buttonBox.setAlignment(Pos.CENTER);

        HBox buttonHBox = new HBox(buttonBox);
        buttonHBox.setAlignment(Pos.BOTTOM_RIGHT);

        VBox logInBox = new VBox();
        logInBox.getChildren().addAll(
                logoBox,
                usernameBox,
                passwordBox,
                logInButton,
                signUpButton
        );
        logInBox.setMaxHeight(height);
        logInBox.setMaxWidth(width);
        logInBox.setAlignment(Pos.CENTER);
        logInBox.setPadding(new Insets(20,20,20,20));
        logInBox.getStyleClass().add("login-box");

        VBox.setMargin(usernameBox, new Insets(usernameTopPad,0,0,0));
        VBox.setMargin(passwordBox, new Insets(passwordTopPad,0,0,0));
        VBox.setMargin(logInButton, new Insets(buttonTopPad,0,20,0));
        VBox.setMargin(signUpButton, new Insets(0,0,0,0));

        return logInBox;
    }

    public Scene initialize(){
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(createLogInForm());

        root = new StackPane(borderPane);

        if(controller.getRegistered()) {
            AlertMsg successAlert = new AlertMsg(AlertType.SUCCESS);
            successAlert.show(root, "Account created!", Pos.TOP_CENTER);
        }

        Scene logInScene = setScene(root, "login-page");

        usernameField.positionCaret(0);

        return logInScene;
    }
}