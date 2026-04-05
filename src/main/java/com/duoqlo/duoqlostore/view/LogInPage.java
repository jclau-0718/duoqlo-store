package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.AuthController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.geometry.*;

import java.util.Objects;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

public class LogInPage extends AuthPage {
    private AuthController controller;

    private final int usernameTopPad = 50;
    private final int passwordTopPad = 35;
    private final int buttonTopPad = 60;

    private HBox errorBox;
    private HBox successBox;

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

    public void showErrorBox(StackPane root) {
        FontIcon errorIcon = new FontIcon("far-times-circle");
        errorIcon.setIconColor(Color.RED);

        Label errorLabel = new Label("Invalid username or password.");
        errorLabel.setId("popup-error");

        errorBox = popUpBox(errorIcon, errorLabel);
        errorBox.setId("popup-box");

        root.getChildren().add(errorBox);
        StackPane.setAlignment(errorBox, Pos.TOP_CENTER);
        StackPane.setMargin(errorBox, new Insets(20, 0, 0, 0));

        playPopUpAnimation(errorBox);
    }

    public void showSuccessBox(StackPane root) {
        FontIcon checkIcon = new FontIcon("far-check-circle");
        checkIcon.setIconColor(Color.LIMEGREEN);

        Label successLabel = new Label("Account created!");
        successLabel.setId("popup-success");

        successBox = popUpBox(checkIcon, successLabel);
        successBox.setId("popup-box");

        root.getChildren().add(successBox);
        StackPane.setAlignment(successBox, Pos.TOP_CENTER);
        StackPane.setMargin(successBox, new Insets(20, 0, 0, 0));

        playPopUpAnimation(successBox);
    }

    public VBox createLogInForm(){
        int height = 100;
        int width = 300;

        //Logo
        HBox logoBox = new HBox(createLogo());
        logoBox.setAlignment(Pos.CENTER);

        //Log In Button
        Button logInButton = primaryButton("LOG IN");

        //Sign Up Button
        Button signUpButton = secondaryButton("SIGN UP");

        //Username Section
        TextField usernameField = createTextField("Username");
        Label usernameErrorLabel = createErrorLabel();
        VBox usernameBox = createTextFieldBox(usernameField, usernameErrorLabel);
        usernameBox.setMinWidth(360);

        controller.setupUsernameValidation(usernameField, usernameErrorLabel);

        //Password Section
        PasswordField passwordField = createPasswordField("Password");
        HBox passwordHBox = createPasswordHBox(passwordField);
        Label passwordErrorLabel = createErrorLabel();
        VBox passwordBox = createPasswordVBox(passwordHBox, passwordErrorLabel);

        controller.setupPasswordValidation(passwordHBox, passwordField, passwordErrorLabel);

        logInButton.setOnAction(e -> {
            if (controller.handleLogIn(e, usernameField.getText(), passwordField.getText())) {
                return;
            } else {
                controller.setTextFieldError(true);
                controller.setPassFieldError(true);
                controller.updateUsernameFieldStyle(usernameField, usernameErrorLabel);
                controller.updatePassFieldStyle(passwordHBox, passwordField, passwordErrorLabel);
                StackPane root = (StackPane) ((Node) e.getSource()).getScene().getRoot();
                root.getChildren().remove(errorBox);
                showErrorBox(root);

                System.out.println("Styling(in condition): "+usernameField.getStyleClass());
            }
        });

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
        logInBox.setId("login-box");

        VBox.setMargin(usernameBox, new Insets(usernameTopPad,0,0,0));
        VBox.setMargin(passwordBox, new Insets(passwordTopPad,0,0,0));
        VBox.setMargin(logInButton, new Insets(buttonTopPad,0,20,0));
        VBox.setMargin(signUpButton, new Insets(0,0,0,0));

        return logInBox;
    }

    public Scene initialize(){
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(createLogInForm());

        StackPane root = new StackPane(borderPane);

        if(controller.getRegistered()) {
            root.getChildren().remove(successBox);
            showSuccessBox(root);
        }

        Platform.runLater(() -> {root.requestFocus();}); //Remove initial focus on Username TextField
        root.setOnMouseClicked(e -> root.requestFocus()); //Allow unfocus on TextField

        Scene logInScene = new Scene(root, windowWidth, windowHeight);
        logInScene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/css/login-page.css")
                ).toExternalForm()
        );

        return logInScene;
    }
}
