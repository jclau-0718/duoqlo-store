package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.AuthController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.*;

import java.util.Objects;

import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

public class LogInPage extends AuthPage {
    private final int usernameTopPad = 50;
    private final int passwordTopPad = usernameTopPad;
    private final int buttonTopPad = 60;

    private AuthController controller = new AuthController();

    public ImageView createLogo(){
        int logoHeight = 60;

        Image logo = new Image(Objects.requireNonNull(UserDashboard.class.getResource("/logo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(logoHeight);
        logoView.setPreserveRatio(true);

        return logoView;
    }

    public VBox createUsernameSection(TextField usernameField){
//        HBox usernameHBox = new HBox(usernameField);
//
//        usernameHBox.setId("text-field-box");
//        HBox.setHgrow(usernameField, Priority.ALWAYS);
//        usernameHBox.setPadding(new Insets(6,8,8,8));

        Label usernameErrorLabel = new Label("");
        usernameErrorLabel.setVisible(false);
        usernameErrorLabel.setManaged(false);

        controller.setupUsernameValidation(usernameField, usernameErrorLabel);

        VBox usernameBox = new VBox(usernameField, usernameErrorLabel);
        VBox.setMargin(usernameErrorLabel, new Insets(7,0,7,0));

        return usernameBox;
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
        VBox usernameBox = createUsernameSection(usernameField);
        usernameBox.setMinWidth(360);

        //Password Section
        PasswordField passwordField = createPasswordField("Password");
        HBox passwordBox = createPasswordBox(passwordField);

        logInButton.setOnAction(e ->
                controller.handleLogIn(e, usernameField.getText(), passwordField.getText()));
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
        BorderPane root = new BorderPane();
        root.setCenter(createLogInForm());

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
