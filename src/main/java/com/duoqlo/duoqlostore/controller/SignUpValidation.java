package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.UserDAO;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.Map;

public class SignUpValidation {
    private AuthController authController;
    private UserDAO userDAO;

    private final boolean[] hasError = {false};
    private final boolean[] passError = {false};
    private final boolean[] confirmPassError = {false};
    private String textfieldErrorString;
    private String passfieldErrorString;
    private String confirmPassErrorString;
    private String password = "";
    private String confirmPassword = "";

    public SignUpValidation(AuthController authController, UserDAO userDAO){
        this.authController = authController;
        this.userDAO = userDAO;
    }

    private String capsFirst(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private boolean hasAddr(String postcode){
        if(authController.postcodeService.lookup(postcode) != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isUsernameTaken(String username) {
        return userDAO.usernameExists(username);
    }

    private boolean isEmailTaken(String email) {
        return userDAO.emailExists(email);
    }

    private boolean isDuplicateUser(String username, String firstname, String lastname, String email) {
        return userDAO.duplicateExists(username, firstname, lastname, email);
    }

    private String setTextFieldErrorLabel(TextField textField, String value) {

        String fieldName = textField.getPromptText().toLowerCase();

        if(value == null){
            value = "";
        } else {
            value = value.trim();
        }

        if (value.isEmpty() && !fieldName.contains("optional")) {
            return "Input is required.";
        }

        if (value.isEmpty() && fieldName.contains("optional")) {
            return null;
        }

        switch(fieldName){
            case "first name":
            case "last name":
                if (!value.matches("^[A-Za-z\\\\s'-]+$")) {
                    return "Only letters are accepted.";
                }

                if (value.length() == 1) {
                    return "Name is too short.";
                }

                if (value.length() > 30) {
                    return "Name is too long.";
                }

                break;

            case "email (hello@example.com)":
                if (isEmailTaken(value)) {
                    return "Email already registered.";
                }

                if(value.matches(".*[A-Z].*")) {
                    return "Only lower-case letters are allowed.";
                }

                if (value.length() > 30) {
                    return "Email is too long. Maximum 30 characters";
                }

                if (!value.matches("^[^@]*@[^@]*$")) {
                    return "Email must contain '@'.";
                }


                if (!value.endsWith(".com")) {
                    return "Email must end with '.com'.";
                }

                if (value.contains(" ")) {
                    return "Email cannot contain spaces.";
                }

                if(!value.matches("^[a-z0-9.]+@(gmail|yahoo|outlook|hotmail)\\.com$")) {
                    return "Use Gmail, Yahoo, Outlook, or Hotmail only.";
                }

                break;

            case "address line 1":
            case "address line 2 (optional)":
                if (value.length() < 5) {
                    return "Address is too short. Minimum 5 characters.";
                }

                if (value.matches("[@#$%^&*]+")) {
                    return "Address contains invalid characters.";
                }

                if (!value.matches(".*[A-Za-z].*") || !value.matches(".*\\d.*")) {
                    return "Address must contain letters and numbers.";
                }

                break;

            case "city":
            case "state":
                if (!value.matches("^[A-Za-z]+$")) {
                    return "Only letters are allowed.";
                }

                if (value.length() < 2) {
                    return capsFirst(fieldName)+" is too short.";
                }

                if (value.length() > 20) {
                    return capsFirst(fieldName)+" is too long.";
                }

                break;

            case "postcode":
                if (!value.matches("\\d+")) { // \d means a digit, + means one or more
                    return "Only numbers are accepted.";
                }

                if(value.isEmpty()) {
                    return "Please enter a postcode.";
                }

                if(value.length() != 5) {
                    return "Only 5 numbers are accepted.";
                }

                if(value.length() == 5 && !hasAddr(value)) {
                    return "Postcode not found.";
                }

                if(value.length() < 5) {
                    return null;
                }

                break;

            case "username":

                // Username Constraints
                // 1. Must contain >=5 characters (done)
                // 2. Acceptable: Alphabets, numbers, _ and - (done)
                // 3. Restriction: spaces and special symbols (done)
                // 4. Cannot start with _ or - (done)
                // 5. Prevent consecutive special characters

                if (isUsernameTaken(value)) {
                    return "Username already taken.";
                }

                if(value.startsWith("-") || value.startsWith("_")){
                    return "Cannot start with '-' or '_'";
                }

                if(value.length() < 5){
                    return "Must be at least 5 characters long";
                }

                if(value.contains(" ") || value.matches(".*[^\\w-].*")){
                    return "Cannot contain spaces or other special symbols";
                }

                if(!value.matches("[a-zA-Z0-9_-]+")){
                    return "Only letters, digits, _, and - are allowed";
                }

                if(value.contains("__") || value.contains("--")){
                    return "Consecutive special characters are not allowed";
                }

        }

        return null;
    }

    private String setPassFieldErrorLabel(String value, String username){

        if (value == null) {
            value = "";
        } else {
            value = value.trim();
        }

        if (value.isEmpty()) {
            return "Password is required.";
        }

        /*
        Password Constraints:
        1. Minimum 8 characters
        2. Maximum 20 characters
        3. Contains lowercase and uppercase letter
        4. Contains numbers
        5. Contains special characters
        4. No spaces
        5. Does not contain username value
         */

        if (value.contains(" ")) {
            return "Spaces are not allowed.";
        }

        if (value.length() < 8) {
            return "Password is too short. Minimum 8 characters.";
        }

        if (value.length() > 20) {
            return "Password is too long.";
        }

        if (!value.matches(".*[a-z].*") || !value.matches(".*[A-Z].*")) {
            return "Must include a lowercase and uppercase letter";
        }

        if (!value.matches(".*\\d.*")) {
            return "Must include a number.";
        }

        if (!value.matches(".*[^a-zA-Z0-9].*")) {
            return "Must include a special character.";
        }

        if (value.contains(username)) {
            return "Password must not match your username.";
        }

        return null;
    }

    private void updateFieldStyle(TextField textField, Label errorLabel) {
        boolean focused = textField.isFocused();

        textField.getStyleClass().removeAll("error", "valid");

        if (hasError[0]) {
            errorLabel.setText(textfieldErrorString);
            errorLabel.setVisible(true);
            textField.getStyleClass().add("error");
        } else {
            errorLabel.setText("");
            errorLabel.setVisible(false);
            textField.getStyleClass().add("valid");

            if (!focused && textField.getText().isEmpty()) {
                textField.getStyleClass().remove("valid");
            }
        }
    }

    private void updatePassStyle(PasswordField passwordField, Label errorLabel, HBox passwordHBox) {
        boolean focused = passwordField.isFocused();

        passwordHBox.getStyleClass().removeAll("error", "valid");

        if (passError[0]) {
            errorLabel.setText(passfieldErrorString);
            errorLabel.setVisible(true);
            passwordHBox.getStyleClass().add("error");
            if(focused) {
                passwordHBox.getStyleClass().add("focused");
            } else {
                passwordHBox.getStyleClass().remove("focused");
            }
        } else {
            errorLabel.setText("");
            errorLabel.setVisible(false);
            passwordHBox.getStyleClass().add("valid");

            if(focused) {
                passwordHBox.getStyleClass().add("focused");
            } else {
                passwordHBox.getStyleClass().remove("focused");
            }

            if (!focused && passwordField.getText().isEmpty()) {
                passwordHBox.getStyleClass().remove("valid");
            }

            System.out.println("Password styling: "+passwordHBox.getStyleClass());
        }
    }

    private void updateConfirmPassStyle(PasswordField passwordField, Label errorLabel, HBox passwordHBox) {
        boolean focused = passwordField.isFocused();

        passwordHBox.getStyleClass().removeAll("error", "valid");

        if (confirmPassError[0]) {
            errorLabel.setText(confirmPassErrorString);
            errorLabel.setVisible(true);
            passwordHBox.getStyleClass().add("error");
            if (focused) {
                passwordHBox.getStyleClass().add("focused");
            } else {
                passwordHBox.getStyleClass().remove("focused");
            }
        } else {
            errorLabel.setText("");
            errorLabel.setVisible(false);
            passwordHBox.getStyleClass().add("valid");

            if (focused) {
                passwordHBox.getStyleClass().add("focused");
            } else {
                passwordHBox.getStyleClass().remove("focused");
            }

            if (!focused && passwordField.getText().isEmpty()) {
                passwordHBox.getStyleClass().remove("valid");
            }
        }
    }

    public void setupTextFieldValidation(TextField textField, Label errorLabel) {
        //Text Listener (main validation)
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) newVal = "";
            newVal = newVal.trim();

            textfieldErrorString = setTextFieldErrorLabel(textField,newVal);
            hasError[0] = textfieldErrorString != null;

            updateFieldStyle(textField, errorLabel);
        });

        // Focus Listener (updates style only)
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            updateFieldStyle(textField, errorLabel);
        });
    }

    public void setupPasswordsValidation(HBox passwordHBox, PasswordField passwordField, Label passErrorLabel,
                                         HBox confirmPassHBox, PasswordField confirmPassField, Label confirmPassErrorLabel,
                                         TextField usernameField) {

        addPassListeners(passwordHBox, confirmPassHBox, passwordField, confirmPassField, passErrorLabel, confirmPassErrorLabel, usernameField);
        addConfirmPassListeners(confirmPassHBox, confirmPassField, confirmPassErrorLabel);
    }

    public void addPassListeners(HBox passwordHBox, HBox confirmPassHBox, PasswordField passwordField, PasswordField confirmPassField, Label passErrorLabel, Label confirmPassErrorLabel, TextField usernameField) {
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal == null) newVal = "";
            newVal = newVal.trim();

            password = newVal;
            System.out.println("Password: "+password);
            System.out.println("Confirm Pass: "+confirmPassword);

            if (!password.equals(confirmPassword)) {
                confirmPassError[0] = true;
                confirmPassErrorString = "Passwords do not match.";
                if (confirmPassField.getText().isEmpty()) {
                    confirmPassError[0] = false;
                }
            } else {
                confirmPassError[0] = false;
            }

            System.out.println(usernameField.getText());
            passfieldErrorString = setPassFieldErrorLabel(newVal, usernameField.getText());
            passError[0] = passfieldErrorString != null;

            updatePassStyle(passwordField, passErrorLabel, passwordHBox);
            updateConfirmPassStyle(confirmPassField, confirmPassErrorLabel, confirmPassHBox);
        });

        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            updatePassStyle(passwordField, passErrorLabel, passwordHBox);
            updateConfirmPassStyle(confirmPassField, confirmPassErrorLabel, confirmPassHBox);
        });
    }

    public void addConfirmPassListeners(HBox confirmPassHBox, PasswordField confirmPassField, Label errorLabel) {
        confirmPassField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) newVal = "";
            newVal = newVal.trim();

            confirmPassword = newVal;
            System.out.println("Password: "+password);
            System.out.println("Confirm Pass: "+confirmPassword);

            if(!confirmPassword.equals(password)) {
                confirmPassError[0] = true;
            } else {
                confirmPassError[0] = false;
            }

            if (confirmPassError[0]) {
                confirmPassErrorString = "Passwords do not match.";
            } else {
                confirmPassErrorString = "";
            }

            updateConfirmPassStyle(confirmPassField, errorLabel, confirmPassHBox);
        });

        confirmPassField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            updateConfirmPassStyle(confirmPassField, errorLabel, confirmPassHBox);
        });
    }

    public boolean personalInfoInvalid(Map<TextField, Label> fieldErrorMap) {
        TextField firstInvalidField = null;
        String firstInvalidError = null;

        for (Map.Entry<TextField, Label> entry : fieldErrorMap.entrySet()) {
            TextField field = entry.getKey();
            System.out.println("field: " + field.getPromptText());
            Label errorLabel = entry.getValue();

            String error = setTextFieldErrorLabel(field, field.getText());

            if (error != null) {
                if (firstInvalidField == null) {
                    firstInvalidField = field;
                    firstInvalidError = error;
                }

                errorLabel.setText(error);
                errorLabel.setVisible(true);

                // Force update the field style
                field.getStyleClass().removeAll("error", "valid");
                field.getStyleClass().add("error");

            } else {
                errorLabel.setText("");
                errorLabel.setVisible(false);
                field.getStyleClass().removeAll("error", "valid");
                if (!field.getText().isEmpty()) {
                    field.getStyleClass().add("valid");
                }
            }
        }

        if (firstInvalidField != null) {
            // Set the shared error state to the first invalid field's error
            textfieldErrorString = firstInvalidError;
            hasError[0] = true;

            // Request focus
            firstInvalidField.requestFocus();
            firstInvalidField.positionCaret(firstInvalidField.getText().length());

            Label firstErrorLabel = fieldErrorMap.get(firstInvalidField);
            if (firstErrorLabel != null) {
                updateFieldStyle(firstInvalidField, firstErrorLabel);
            }

            return true;
        }

        return false;
    }

    public void revalidateField(TextField textField, Label errorLabel) {
        String error = setTextFieldErrorLabel(textField, textField.getText());
        hasError[0] = error != null;
        textfieldErrorString = error;
        updateFieldStyle(textField, errorLabel);
    }

}
