package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.AuthController;
import com.duoqlo.duoqlostore.controller.InfoValidation;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class SignUpPage extends AuthPage{
    private AuthController controller = new AuthController();
    private InfoValidation validator = new InfoValidation();

    private TextField firstNameField, lastNameField, emailField, address1Field, address2Field,
            cityField, postcodeField, stateField, usernameField, visiblePassField, visibleConfirmPassField;
    private PasswordField passwordField, confirmPassField;
    private HBox passwordHBox, confirmPassHBox;

    private VBox firstNameBox, lastNameBox, emailBox, address1Box, address2Box,
            cityBox, postcodeBox, stateBox, countryBox, usernameBox, passwordBox, confirmPassBox;

    private Label firstNameError, lastNameError, emailError, address1Error, address2Error,
            cityError, postcodeError, stateError, usernameError, passwordError, confirmPassError;

    private Map<TextField, Label> fieldErrorMap = new LinkedHashMap<>();

    public Region createLine(){
        Region line = new Region();
        line.setStyle("-fx-background-color: #EBEBEB");
        line.setMaxHeight(3);

        return line;
    }

    public HBox createSectionTitle(String title){
        Region leftLine = createLine();
        Region rightLine = createLine();

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-title");

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(leftLine,titleLabel,rightLine);

        HBox.setHgrow(leftLine, Priority.ALWAYS);
        HBox.setHgrow(rightLine, Priority.ALWAYS);
        HBox.setMargin(leftLine, new Insets(0,5,0,0));
        HBox.setMargin(titleLabel, new Insets(0,5,0,5));
        HBox.setMargin(rightLine, new Insets(0,0,0,5));

        return titleBox;
    }

    private void initFieldBoxes() {
        // First Name
        firstNameField = createTextField("First Name");
        firstNameError = createErrorLabel();
        firstNameBox = createTextFieldBox(firstNameField, firstNameError);

        // Last Name
        lastNameField = createTextField("Last Name");
        lastNameError = createErrorLabel();
        lastNameBox = createTextFieldBox(lastNameField, lastNameError);

        // Email
        emailField = createTextField("Email (hello@example.com)");
        emailError = createErrorLabel();
        emailBox = createTextFieldBox(emailField, emailError);

        // Address 1
        address1Field = createTextField("Address Line 1");
        address1Error = createErrorLabel();
        address1Box = createTextFieldBox(address1Field, address1Error);

        // Address 2 (optional)
        address2Field = createTextField("Address Line 2 (Optional)");
        address2Error = createErrorLabel();
        address2Box = createTextFieldBox(address2Field, address2Error);

        // City
        cityField = createTextField("City");
        cityError = createErrorLabel();
        cityBox = createTextFieldBox(cityField, cityError);

        // Postcode
        postcodeField = createTextField("Postcode");
        postcodeError = createErrorLabel();
        postcodeBox = createTextFieldBox(postcodeField, postcodeError);

        // State
        stateField = createTextField("State");
        stateError = createErrorLabel();
        stateBox = createTextFieldBox(stateField, stateError);

        //Country
        TextField countryField = createTextField("Country");
        countryField.setText("Malaysia");
        countryField.setEditable(false);
        countryField.getStyleClass().add("valid");
        Label countryError = new Label("");
        countryBox = createTextFieldBox(countryField, countryError);

        // Username
        usernameField = createTextField("Username");
        usernameError = createErrorLabel();
        usernameBox = createTextFieldBox(usernameField, usernameError);

        // Password
        passwordField = createPasswordField("Password");

        visiblePassField = new TextField();
        visiblePassField.setPromptText(passwordField.getPromptText());

        passwordHBox = createPasswordHBox(passwordField, visiblePassField);
        passwordError = createErrorLabel();
        passwordBox = createPasswordVBox(passwordHBox, passwordError);

        // Confirm Password
        confirmPassField = createPasswordField("Confirm Password");

        visibleConfirmPassField = new TextField();
        visibleConfirmPassField.setPromptText(confirmPassField.getPromptText());

        confirmPassHBox = createPasswordHBox(confirmPassField, visibleConfirmPassField);
        confirmPassError = createErrorLabel();
        confirmPassBox = createPasswordVBox(confirmPassHBox, confirmPassError);

        setupAllFieldValidation();
    }

    private void setupAllFieldValidation() {
        validator.setupTextFieldValidation(firstNameField, firstNameError);
        validator.setupTextFieldValidation(lastNameField, lastNameError);
        validator.setupTextFieldValidation(emailField, emailError);
        validator.setupTextFieldValidation(address1Field, address1Error);
        validator.setupTextFieldValidation(address2Field, address2Error);
        validator.setupTextFieldValidation(cityField, cityError);
        validator.setupTextFieldValidation(postcodeField, postcodeError);
        validator.setupTextFieldValidation(stateField, stateError);
        validator.setupTextFieldValidation(usernameField, usernameError);
        validator.setupPasswordsValidation(passwordBox, confirmPassBox, usernameField);
    }

    private ArrayList<String> getFieldsValue() {
        ArrayList<String> values = new ArrayList<>();

        values.add(usernameField.getText());
        values.add(controller.hashPassword(passwordField.getText()));
        values.add(firstNameField.getText());
        values.add(lastNameField.getText());
        values.add(emailField.getText());
        values.add(address1Field.getText());
        values.add(address2Field.getText());
        values.add(cityField.getText());
        values.add(postcodeField.getText());
        values.add(stateField.getText());

        return values;
    }

    private void packFieldError() {
        fieldErrorMap.put(firstNameField, firstNameError);
        fieldErrorMap.put(lastNameField, lastNameError);
        fieldErrorMap.put(emailField, emailError);
        fieldErrorMap.put(address1Field, address1Error);
        fieldErrorMap.put(address2Field, address2Error);
        fieldErrorMap.put(cityField, cityError);
        fieldErrorMap.put(postcodeField, postcodeError);
        fieldErrorMap.put(stateField, stateError);
    }

    private GridPane personalInfoBox() {
        HBox titleSection = createSectionTitle("Personal Info");

        GridPane grid = new GridPane();
        grid.add(titleSection,0, 0, 2, 1);
        grid.add(firstNameBox, 0, 1, 1, 1);
        grid.add(lastNameBox, 1, 1, 1, 1);
        grid.add(emailBox, 0, 2, 2, 1);
        grid.add(address1Box, 0, 3, 1, 1);
        grid.add(address2Box, 1, 3, 1, 1);
        grid.add(cityBox, 0, 4, 1, 1);
        grid.add(postcodeBox, 1, 4, 1, 1);
        grid.add(stateBox, 0, 5, 1, 1);
        grid.add(countryBox, 1, 5, 1, 1);

        grid.setHgap(10);
        grid.setVgap(15);

        GridPane.setHgrow(firstNameBox, Priority.ALWAYS);
        GridPane.setHgrow(lastNameBox, Priority.ALWAYS);
        GridPane.setHgrow(emailBox, Priority.ALWAYS);
        GridPane.setHgrow(address1Box, Priority.ALWAYS);
        GridPane.setHgrow(address2Box, Priority.ALWAYS);
        GridPane.setHgrow(cityBox, Priority.ALWAYS);
        GridPane.setHgrow(postcodeBox, Priority.ALWAYS);
        GridPane.setHgrow(stateBox, Priority.ALWAYS);
        GridPane.setHgrow(countryBox, Priority.ALWAYS);

        controller.setupAddressTracker(postcodeField, cityField, stateField);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS); // Expand to fill available width

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    public VBox loginDetailsBox() {
        HBox titleSection = createSectionTitle("Login Details");

        Label description = new Label("Use these details to log in to your account.");
        description.getStyleClass().add("description");

        VBox loginDetailsBox = new VBox();
        loginDetailsBox.getChildren().addAll(titleSection, description, usernameBox, passwordBox, confirmPassBox);
        VBox.setMargin(titleSection, new Insets(0,0,5,0));
        VBox.setMargin(description, new Insets(0,0,0,0));
        VBox.setMargin(usernameBox,new Insets(5,0,5,0));
        VBox.setMargin(passwordBox,new Insets(5,0,5,0));
        VBox.setMargin(confirmPassBox,new Insets(5,0,5,0));

        return loginDetailsBox;
    }

    public HBox personalInfoButtonBox() {
        Button backButton = secondaryButton("BACK");
        Button nextButton = primaryButton("NEXT");

        HBox buttonBox = new HBox(backButton, nextButton);
        buttonBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(backButton, Priority.ALWAYS);
        HBox.setHgrow(nextButton, Priority.ALWAYS);
        HBox.setMargin(backButton, new Insets(0, 10, 0, 0));

        backButton.setOnAction(e -> controller.backToLogIn(e));
        nextButton.setOnAction(e -> {
            if (validator.personalInfoInvalid(fieldErrorMap)) {
                return;
            } else {
                BorderPane root = (BorderPane) ((Node) e.getSource()).getScene().getRoot();
                root.setCenter(loginDetailsForm());
            }
        });

        return buttonBox;
    }

    public HBox logInDetailsButtonBox() {
        Button backButton = secondaryButton("BACK");
        Button signUpButton = primaryButton("SIGN UP");

        HBox buttonBox = new HBox(backButton, signUpButton);
        buttonBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(backButton, Priority.ALWAYS);
        HBox.setHgrow(signUpButton, Priority.ALWAYS);
        HBox.setMargin(backButton, new Insets(0, 10, 0, 0));

        backButton.setOnAction(e -> {
            BorderPane root = (BorderPane) ((Node) e.getSource()).getScene().getRoot();
            Platform.runLater(() -> {root.requestFocus();}); //Remove initial focus on Username TextField

            //Revalidate all fields
            for (Map.Entry<TextField, Label> entry : fieldErrorMap.entrySet()) {
                validator.revalidateField(entry.getKey(), entry.getValue());
            }

            root.setCenter(personalInfoForm());

            root.setCenter(personalInfoForm());
        });

        signUpButton.setOnAction(e -> {
            try {
                ArrayList<String> fieldValues = getFieldsValue();
                fieldValues.add("CUSTOMER");

                if (!usernameError.getText().isEmpty()) { //Username has error
                    usernameField.requestFocus();
                    usernameField.positionCaret(usernameField.getText().length());
                    return;
                } else if (!passwordError.getText().isEmpty()) { //Password has error
                    passwordField.requestFocus();
                    passwordField.positionCaret(passwordField.getText().length());
                    return;
                } else if (!confirmPassError.getText().isEmpty()){ //Confirm Password has error
                    confirmPassField.requestFocus();
                    confirmPassField.positionCaret(confirmPassField.getText().length());
                } else {
                    if(controller.handleSignUp(fieldValues)) { //Create account in database
                        LogInPage logInPage = new LogInPage(controller);

                        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                        stage.setScene(logInPage.initialize());
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return buttonBox;
    }

    public VBox createForm(Node bodyBox, Node buttonBox) {
        Label title = new Label("SIGN UP");
        title.getStyleClass().add("signup-title");

        VBox form = new VBox();
        form.getChildren().addAll(title, bodyBox, buttonBox);
        form.getStyleClass().add("signup-form");
        form.setMaxHeight(167);
        form.setMaxWidth(500);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20,20,20,20));

        VBox.setMargin(title, new Insets(0,0,10,0));
        VBox.setMargin(bodyBox, new Insets(10, 0, 10, 0));
        VBox.setMargin(buttonBox, new Insets(35, 0, 0, 0));

        return form;
    }

    public VBox personalInfoForm() {
        GridPane personalInfoBox = personalInfoBox();
        HBox buttonBox = personalInfoButtonBox();

        VBox form = createForm(personalInfoBox, buttonBox);

        return form;
    }

    public VBox loginDetailsForm() {
        VBox loginDetailsBox = loginDetailsBox();
        HBox buttonBox = logInDetailsButtonBox();

        VBox form = createForm(loginDetailsBox, buttonBox);

        return form;
    }

    public Scene initialize(){
        initFieldBoxes();
        packFieldError();

        BorderPane root = new BorderPane();
        root.setCenter(personalInfoForm());

        Scene signUpScene = setScene(root, "signup-page");

        return signUpScene;
    }
}
