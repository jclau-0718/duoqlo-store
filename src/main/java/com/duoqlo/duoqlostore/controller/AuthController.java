package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.model.UserDAO;
import com.duoqlo.duoqlostore.view.LogInPage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class AuthController {
    private UserDAO userDAO = new UserDAO();
    private AuthService auth = new AuthService();
    private PostcodeService postcodeService = new PostcodeService();

    public boolean checkCredentials(String username, String password){
        return userDAO.checkCredentials(username, password);
    }

    public void handleLogIn(ActionEvent e, String username, String password) {
        try {
            User loggedInUser = auth.login(username, password);

            if (loggedInUser != null) {
                DashboardController dashboardController = new DashboardController();

                dashboardController.setUser(loggedInUser);

                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

                dashboardController.openDashboard(stage);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Parent openDashboard(String username, String password){
        System.out.println("Trying login: " + username + ", " + password);

        //Authentication valid
        if(checkCredentials(username, password)){
            int userID = userDAO.getIDByUsername(username);
            String role;

            role = userDAO.getRole(userID);
            if(role != null && role.equals("CUSTOMER")){
                return SceneManager.createUserDash();
            } else if(role != null && role.equals("ADMIN")){
                return SceneManager.createAdminDash();
            } else {
                System.out.println("Role not recognized for userID: " + userID);
            }
        }
        else {
            System.out.println("Invalid username or password!");
        }

        return null;
    }

    public void updateTextFieldStyle(TextField textField, boolean isFocused, boolean hasError) {
        String id;

        if (!isFocused && !hasError) {            //Not focused and no error
            id = "text-field";
        } else if (!isFocused && hasError) {      //Not focused and has error
            id = "text-field-error";
        } else if (isFocused && !hasError) {      //Focused and no error
            id = "text-field-focus";
        } else {                                  //Focused and has error
            id = "text-field-focus-error";
        }

        textField.setId(id);
    }

    // Username Constraints
    // 1. Must contain >=5 characters (done)
    // 2. Acceptable: Alphabets, numbers, _ and - (done)
    // 3. Restriction: spaces and special symbols (done)
    // 4. Cannot start with _ or - (done)
    // 5. Prevent consecutive special characters

    public Label createUsernameError(String username){

        if(username.equals("")){
            return new Label("Invalid username");
        }

        if(username.startsWith("-") || username.startsWith("_")){
            return new Label("Cannot start with '-' or '_'");
        }

        if(username.length() < 5){
            return new Label("Must be at least 5 characters long");
        }

        if(username.contains(" ") || username.matches(".*[^\\w-].*")){
            return new Label("Cannot contain spaces or other special symbols");
        }

        if(!username.matches("[a-zA-Z0-9_-]+")){
            return new Label("Only letters, digits, _, and - are allowed");
        }

        if(username.contains("__") || username.contains("--")){
            return new Label("Consecutive special characters are not allowed");
        }

        return new Label(""); //No error
    }

    public void setupUsernameValidation (TextField usernameField, Label usernameErrorLabel){
        // Track focus state
        final boolean[] isFocused = {false};

        // TEXT LISTENER
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            String username = newVal.trim();

            Label errorMsg = createUsernameError(username);
            boolean hasError = !errorMsg.getText().isEmpty();

            // Show / hide error
            if (hasError) {
                usernameErrorLabel.setText(errorMsg.getText());
                usernameErrorLabel.setId("error-msg");
                usernameErrorLabel.setVisible(true);
                usernameErrorLabel.setManaged(true);
                usernameErrorLabel.setVisible(true);
                usernameErrorLabel.setManaged(true);
            } else {
                usernameErrorLabel.setText("");
                usernameErrorLabel.setVisible(false);
                usernameErrorLabel.setManaged(false);
                usernameErrorLabel.setVisible(false);
                usernameErrorLabel.setManaged(false);
            }

            // Update style
            updateTextFieldStyle(usernameField, isFocused[0], hasError);
        });

        // FOCUS LISTENER
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            isFocused[0] = newVal;

            String username = usernameField.getText().trim();
            Label errorMsg = createUsernameError(username);
            boolean hasError = !errorMsg.getText().isEmpty();

            if (hasError) {
                usernameErrorLabel.setText(errorMsg.getText());
                usernameErrorLabel.setId("error-msg");
                usernameErrorLabel.setVisible(true);
                usernameErrorLabel.setManaged(true);
                usernameErrorLabel.setVisible(true);
                usernameErrorLabel.setManaged(true);

            } else {
                usernameErrorLabel.setText("");
                usernameErrorLabel.setVisible(false);
                usernameErrorLabel.setManaged(false);
                usernameErrorLabel.setVisible(false);
                usernameErrorLabel.setManaged(false);
            }

            updateTextFieldStyle(usernameField, newVal, hasError);
        });
    }

    public void backToLogIn(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new LogInPage().initialize());
    }

    public boolean setupAddressTracker(TextField postcodeField, TextField cityField, TextField stateField) {
        final boolean[] hasAddr = {false};

        postcodeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal.length() == 5){
                Address addr = postcodeService.lookup(newVal);

                if (addr != null) {
                    cityField.setText(addr.getCity());
                    cityField.setEditable(false);

                    stateField.setText(addr.getState());
                    stateField.setEditable(false);
                    hasAddr[0] = true;
                } else {
                    hasAddr[0] = false;
                }
            } else {
                cityField.setText(null);
                stateField.setText(null);
                hasAddr[0] = false;
            }

            cityField.setEditable(true);
            stateField.setEditable(true);
        });

        return hasAddr[0];
    }

    public void setupPostcodeValidation(TextField postcodeField) {
        String postcode = postcodeField.getText();

        if(!postcode.matches("[0-9]*")){ //Does not contain any numbers

        }
    }

    public void handleSignUp(ArrayList<String> fieldValues){
        User signedUpUser = new User();
        signedUpUser.setInfo(fieldValues);

        userDAO.insert(signedUpUser);
    }
}
