package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.LogInController;
import com.duoqlo.duoqlostore.controller.SceneManager;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.*;
import java.util.Objects;
import org.kordamp.ikonli.javafx.FontIcon;

public class LogInPage {
    private LogInController controller = new LogInController();

    public ImageView createLogo(){
        int logoHeight = 80;

        Image logo = new Image(Objects.requireNonNull(UserDashboard.class.getResource("/logo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(logoHeight);
        logoView.setPreserveRatio(true);

        return logoView;
    }

    public TextField createUsernameField(){
        TextField usernameField = new TextField();
        usernameField.setId("input-field");
        usernameField.setPromptText("Username");

        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // TextField lost focus
                String username = usernameField.getText().trim();
                if (!controller.checkUsername(username)) {
                    // Set border to red
                    usernameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                } else {
                    usernameField.setStyle("");
                }
            }
        });

        return usernameField;
    }

    public PasswordField createPasswordField() {

        PasswordField passwordField = new PasswordField();
        passwordField.setId("input-field");
        passwordField.setPromptText("Password");

        return passwordField;
    }

    public StackPane createPasswordBox(PasswordField passwordField) {

        FontIcon eyeIcon = new FontIcon("far-eye");
        eyeIcon.setIconColor(Color.GREY);
        eyeIcon.setIconSize(16);

        Button showPassButton = new Button("",eyeIcon);
        showPassButton.setId("showpass-button");
        showPassButton.setFocusTraversable(false); // so it doesn't steal focus
        showPassButton.setVisible(false);

        // Show eye icon only when focused
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            showPassButton.setVisible(newVal);
        });

        StackPane passwordPane = new StackPane();
        passwordPane.getChildren().addAll(passwordField,showPassButton);
        passwordPane.setAlignment(showPassButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(showPassButton, new Insets(0, 5, 7, 0));

        return passwordPane;
    }

    public VBox createLogInForm(){
        int height = 300;
        int width = 300;

        //Logo
        HBox logoBox = new HBox(createLogo());
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(15,0,15,0));

        //Log In Button
        Button logInButton = new Button("Log In");
        logInButton.setId("log-in-button");

        //Sign Up Button
        Button signUpButton = new Button("Sign Up?");
        signUpButton.setId(("sign-up-button"));

        //TextFields
        TextField usernameField = createUsernameField();
        PasswordField passwordField = createPasswordField();

        logInButton.setOnAction(e-> SceneManager.switchScene(e,
                                                controller.openDashboard(usernameField.getText(),passwordField.getText())));

        VBox buttonBox = new VBox(5, logInButton,signUpButton);

        HBox buttonRow = new HBox(buttonBox);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setPadding(new Insets(60,0,0,0));

        VBox logInBox = new VBox();
        logInBox.getChildren().addAll(
                logoBox,
                usernameField,
                createPasswordBox(passwordField),
                buttonRow
        );
        logInBox.setMaxHeight(height);
        logInBox.setMaxWidth(width);

        return logInBox;
    }
}
