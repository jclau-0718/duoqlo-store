package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.model.UserDAO;
import com.duoqlo.duoqlostore.view.LogInPage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;

public class AuthController {
    public UserDAO userDAO = new UserDAO();
    private AuthService auth = new AuthService();
    public PostcodeService postcodeService = new PostcodeService();

    private boolean registered = false;
    private final boolean[] textFieldError = {false};
    private final boolean[] passFieldError = {false};

    public boolean getRegistered() {
        return registered;
    }

    public void setTextFieldError(boolean hasError) {
        this.textFieldError[0] = hasError;
    }

    public void setPassFieldError(boolean hasError) {
        this.passFieldError[0] = hasError;
    }

    public boolean checkCredentials(String username, String password){
        return userDAO.checkCredentials(username, password);
    }

    public boolean handleLogIn(ActionEvent e, String username, String enteredPassword) {
        try {
            if (userDAO.usernameExists(username)) {
                String storedPassword = userDAO.getPasswordByUsername(username);

                boolean passwordValid = verifyPassword(enteredPassword, storedPassword);

                if(passwordValid) {
                    User loggedInUser = userDAO.getUserByUsername(username);

                    if (loggedInUser != null) {
                        DashboardController dashboardController = new DashboardController();

                        dashboardController.setUser(loggedInUser);

                        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

                        dashboardController.openDashboard(stage);
                        return true;
                    }
                }
            }

            return false;

        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    private boolean verifyPassword(String enteredPassword, String storedPassword) {
        return BCrypt.checkpw(enteredPassword, storedPassword);
    }

    public void updateUsernameFieldStyle(TextField textField, Label errorLabel) {
        boolean focused = textField.isFocused();

        textField.getStyleClass().removeAll("error","valid");

        if(textFieldError[0]) {
            textField.getStyleClass().add("error");
            errorLabel.setText("Please enter a valid username.");
            errorLabel.setVisible(true);
        } else if (textField.getText().isEmpty()) {
            textField.getStyleClass().add("error");
            errorLabel.setText("Please enter a username.");
            errorLabel.setVisible(true);
        } else {
            textField.getStyleClass().add("valid");
            errorLabel.setText("");
            errorLabel.setVisible(false);

            if (!focused && textField.getText().isEmpty()) {
                textField.getStyleClass().remove("valid");
            }
        }

        System.out.println("Styling(after): "+textField.getStyleClass());
    }
    public void updatePassFieldStyle(HBox passwordBox, TextField passwordField, Label errorLabel){
        boolean focused = passwordField.isFocused();

        passwordBox.getStyleClass().removeAll("error","valid");

        if (focused) {
            if (!passwordBox.getStyleClass().contains("focused")) {
                passwordBox.getStyleClass().add("focused");
            }
        } else {
            passwordBox.getStyleClass().remove("focused");
        }

        if(passFieldError[0]) {
            passwordBox.getStyleClass().add("error");
            errorLabel.setText("Please enter a valid password.");
            errorLabel.setVisible(true);
        } else if (passwordField.getText().isEmpty()) {
            passwordBox.getStyleClass().add("error");
            errorLabel.setText("Please enter a password.");
            errorLabel.setVisible(true);
        } else {
            passwordBox.getStyleClass().add("valid");
            errorLabel.setText("");
            errorLabel.setVisible(false);

            if (!focused && passwordField.getText().isEmpty()) {
                passwordBox.getStyleClass().remove("valid");
            }
        }
    }

    public void setupUsernameValidation (TextField usernameField, Label usernameErrorLabel){

        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            textFieldError[0] = false;

            updateUsernameFieldStyle(usernameField, usernameErrorLabel);
        });

        //Focus Listener (update style only)
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            updateUsernameFieldStyle(usernameField, usernameErrorLabel);
        });
    }

    public void setupPasswordValidation (HBox passwordBox, PasswordField passwordField, Label passErrorLabel) {
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            passFieldError[0] = false;

            updatePassFieldStyle(passwordBox, passwordField, passErrorLabel);
        });

        //Focus Listener (update style only)
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            updatePassFieldStyle(passwordBox, passwordField, passErrorLabel);
        });
    }

    public void backToLogIn(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        LogInPage logInPage = new LogInPage(this); // Pass current controller
        stage.setScene(logInPage.initialize());
    }


    public void setupAddressTracker(TextField postcodeField, TextField cityField, TextField stateField) {
        final boolean[] hasAddr = {false};

        postcodeField.textProperty().addListener((obs, oldVal, newVal) -> {
            Address addr = postcodeService.lookup(newVal);

            if (addr != null) { //Address found
                if (newVal.length() == 5) {
                    cityField.setText(addr.getCity());
                    cityField.setEditable(false);

                    stateField.setText(addr.getState());
                    stateField.setEditable(false);

                    hasAddr[0] = true;
                }
            } else {
                cityField.setText("");
                cityField.setEditable(true);

                stateField.setText("");
                stateField.setEditable(true);

                hasAddr[0] = false;
            }
        });
    }

    public boolean handleSignUp(ArrayList<String> fieldValues){
        try {
            User signedUpUser = new User();
            signedUpUser.setInfo(fieldValues);

            userDAO.insert(signedUpUser);
            registered = true;
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            registered = false;
            return false;
        }
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
