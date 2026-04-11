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

public class AuthController extends UserController {
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

    public boolean handleLogIn(ActionEvent e, String username, String enteredPassword) {
        try {
            if (userDAO.usernameExists(username)) {
                String storedPassword = userDAO.getPasswordByUsername(username);

                boolean passwordValid = verifyPassword(enteredPassword, storedPassword);

                if(passwordValid) {
                    User loggedInUser = userDAO.getUserByUsername(username);

                    if (loggedInUser != null) {
                        DashboardController dashboardController = new DashboardController();

                        Navigator.setUser(loggedInUser);
                        Navigator.setDashboardController(dashboardController);

                        Navigator.openUserDashboard();
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
        super.updateUsernameFieldStyle(usernameField, errorLabel, userFieldTyped[0], userFieldError[0]);
    }

    public void updatePassFieldStyle(HBox passwordBox, TextField passwordField, Label errorLabel) {
        super.updatePassFieldStyle(passwordBox, passwordField, errorLabel, passFieldTyped[0], passFieldError[0]);
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
