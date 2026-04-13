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
    private UserDAO userDAO = new UserDAO();
    private PostcodeService postcodeService = new PostcodeService();

    private boolean registered = false;
    private final boolean[] userFieldError = {false};
    private final boolean[] userFieldTyped = {false};
    private final boolean[] passFieldError = {false};
    private final boolean[] passFieldTyped = {false};

    public boolean getRegistered() {
        return registered;
    }

    public void setUserFieldError(boolean hasError) {
        this.userFieldError[0] = hasError;
    }

    public void setPassFieldError(boolean hasError) {
        this.passFieldError[0] = hasError;
    }

    public boolean isUserActive(String username) {
        return userDAO.isActive(username);
    }

    public boolean handleLogIn(String username, String enteredPassword) {
        try {
            if (userDAO.usernameExists(username)) {

                String storedPassword = userDAO.getPasswordByUsername(username);

                boolean passwordValid = verifyPassword(enteredPassword, storedPassword);

                if(passwordValid) {
                    User loggedInUser = userDAO.getUserByUsername(username);

                    if (loggedInUser != null) {
                        Navigator.openDashboard(loggedInUser);
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

    public void updateUsernameFieldStyle(TextField usernameField, Label errorLabel) {
        boolean isEmpty = usernameField.getText().isEmpty();

        usernameField.getStyleClass().removeAll("error","valid");

        if(!userFieldTyped[0]) {
            return;
        }

        if(userFieldError[0]) {
            usernameField.getStyleClass().add("error");
            errorLabel.setText("Please enter a valid username.");
            errorLabel.setVisible(true);
        } else if (isEmpty) {
            usernameField.getStyleClass().add("error");
            errorLabel.setText("Please enter a username.");
            errorLabel.setVisible(true);
        } else {
            usernameField.getStyleClass().remove("error");
            usernameField.getStyleClass().add("valid");
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }

    public void updatePassFieldStyle(HBox passwordBox, TextField passwordField, Label errorLabel){
        boolean focused = passwordField.isFocused();
        boolean isEmpty = passwordField.getText().isEmpty();

        passwordBox.getStyleClass().removeAll("error","valid", "focused");

        if (!passFieldTyped[0]) { // Remove initial focus on the password field
            return;
        }

        if (focused) {
            passwordBox.getStyleClass().add("focused");
        } else {
            passwordBox.getStyleClass().remove("focused");
        }

        if(passFieldError[0]) {
            passwordBox.getStyleClass().add("error");
            errorLabel.setText("Please enter a valid password.");
            errorLabel.setVisible(true);
        } else if (isEmpty) {
            passwordBox.getStyleClass().add("error");
            errorLabel.setText("Please enter a password.");
            errorLabel.setVisible(true);
        } else {
            passwordBox.getStyleClass().remove("error");
            passwordBox.getStyleClass().add("valid");
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }

    public void setupUsernameValidation (TextField usernameField, Label usernameErrorLabel){

        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            userFieldError[0] = false;
            userFieldTyped[0] = true;
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
            passFieldTyped[0] = true;
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
