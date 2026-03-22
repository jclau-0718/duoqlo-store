package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.AuthController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Objects;

public class SignUpPage extends AuthPage {
    private AuthController controller = new AuthController();

    private TextField firstNameField, lastNameField, emailField, address1Field, address2Field,
            cityField, postcodeField, stateField, usernameField;
    private PasswordField passwordField, confirmPassField;

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
        titleLabel.setId("section-title");

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);

        HBox.setHgrow(leftLine, Priority.ALWAYS);
        HBox.setHgrow(rightLine, Priority.ALWAYS);
        HBox.setMargin(leftLine, new Insets(0,5,0,0));
        HBox.setMargin(titleLabel, new Insets(0,5,0,5));
        HBox.setMargin(rightLine, new Insets(0,0,0,5));

        titleBox.getChildren().addAll(leftLine,titleLabel,rightLine);

        return titleBox;
    }

    public void initFields() {
        firstNameField = createTextField("First Name");
        lastNameField = createTextField("Last Name");
        emailField = createTextField("Email (hello@example.com)");
        address1Field = createTextField("Address Line 1");
        address2Field = createTextField("Address Line 2 (Optional)");
        cityField = createTextField("City");
        postcodeField = createTextField("Postal Code");
        stateField = createTextField("State");

        usernameField = createTextField("Username");
        passwordField = createPasswordField("Password");
        confirmPassField = createPasswordField("Confirm Password");
    }

    public ArrayList<String> getFieldsValue() {
        ArrayList<String> values = new ArrayList<>();

        values.add(usernameField.getText());
        values.add(passwordField.getText());
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

    public GridPane personalInfoBox() {
        HBox titleSection = createSectionTitle("Personal Info");

        TextField countryField = createTextField("Country");
        countryField.setText("Malaysia");
        countryField.setEditable(false);

        GridPane grid = new GridPane();
        grid.add(titleSection,0, 0, 2, 1);
        grid.add(firstNameField,0, 1, 1, 1);
        grid.add(lastNameField, 1, 1, 1, 1);
        grid.add(emailField, 0, 2, 2, 1);
        grid.add(address1Field, 0, 3, 1, 1);
        grid.add(address2Field, 1, 3, 1, 1);
        grid.add(cityField, 0, 4, 1, 1);
        grid.add(postcodeField, 1, 4, 1, 1);
        grid.add(stateField, 0, 5, 1, 1);
        grid.add(countryField, 1, 5, 1, 1);

        grid.setHgap(10);
        grid.setVgap(20);

        GridPane.setHgrow(firstNameField, Priority.ALWAYS);
        GridPane.setHgrow(lastNameField, Priority.ALWAYS);
        GridPane.setHgrow(emailField, Priority.ALWAYS);
        GridPane.setHgrow(address1Field, Priority.ALWAYS);
        GridPane.setHgrow(address2Field, Priority.ALWAYS);
        GridPane.setHgrow(cityField, Priority.ALWAYS);
        GridPane.setHgrow(postcodeField, Priority.ALWAYS);
        GridPane.setHgrow(stateField, Priority.ALWAYS);
        GridPane.setHgrow(countryField, Priority.ALWAYS);

        controller.setupAddressTracker(postcodeField, cityField, stateField);

        return grid;
    }

    public VBox loginDetailsBox() {
        HBox titleSection = createSectionTitle("Login Details");

        Label description = new Label("Use these details to log in to your account.");
        description.setId("description");

        HBox passwordBox = createPasswordBox(passwordField);
        HBox confirmPassBox = createPasswordBox(confirmPassField);

        VBox loginDetailsBox = new VBox();
        loginDetailsBox.getChildren().addAll(titleSection, description, usernameField, passwordBox, confirmPassBox);
        VBox.setMargin(titleSection, new Insets(0,0,5,0));
        VBox.setMargin(description, new Insets(0,0,0,0));
        VBox.setMargin(usernameField,new Insets(5,0,5,0));
        VBox.setMargin(passwordField,new Insets(5,0,5,0));
        VBox.setMargin(confirmPassField,new Insets(5,0,5,0));

        return loginDetailsBox;
    }

    public HBox firstPageButtonBox() {
        Button backButton = secondaryButton("BACK");
        Button nextButton = primaryButton("NEXT");

        HBox buttonBox = new HBox(backButton, nextButton);
        buttonBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(backButton, Priority.ALWAYS);
        HBox.setHgrow(nextButton, Priority.ALWAYS);
        HBox.setMargin(backButton, new Insets(0, 10, 0, 0));

        backButton.setOnAction(e -> controller.backToLogIn(e));
        nextButton.setOnAction(e -> {
            BorderPane root = (BorderPane) ((Node) e.getSource()).getScene().getRoot();
            root.setCenter(loginDetailsForm());
        });

        return buttonBox;
    }

    public HBox finalPageButtonBox() {
        Button backButton = secondaryButton("BACK");
        Button signUpButton = primaryButton("SIGN UP");

        HBox buttonBox = new HBox(backButton, signUpButton);
        buttonBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(backButton, Priority.ALWAYS);
        HBox.setHgrow(signUpButton, Priority.ALWAYS);
        HBox.setMargin(backButton, new Insets(0, 10, 0, 0));

        backButton.setOnAction(e -> {
            BorderPane root = (BorderPane) ((Node) e.getSource()).getScene().getRoot();
            root.setCenter(personalInfoForm());
        });
        signUpButton.setOnAction(e -> {
            ArrayList<String> fieldValues = getFieldsValue();
            fieldValues.add("CUSTOMER");

            controller.handleSignUp(fieldValues);
        });

        return buttonBox;
    }

    public VBox createForm(Node bodyBox, Node buttonBox) {
        Label title = new Label("SIGN UP");
        title.setId("signup-title");

        VBox form = new VBox();
        form.getChildren().addAll(title, bodyBox, buttonBox);
        form.setId("signup-form");
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
        HBox buttonBox = firstPageButtonBox();

        VBox form = createForm(personalInfoBox, buttonBox);

        return form;
    }

    public VBox loginDetailsForm() {
        VBox loginDetailsBox = loginDetailsBox();
        HBox buttonBox = finalPageButtonBox();

        VBox form = createForm(loginDetailsBox, buttonBox);

        return form;
    }

    public Scene initialize(){
        initFields();

        BorderPane root = new BorderPane();
        root.setCenter(personalInfoForm());
        Platform.runLater(() -> {root.requestFocus();}); //Remove initial focus on Username TextField
        root.setOnMouseClicked(e -> root.requestFocus()); //Allow unfocus on TextField

        Scene signUpScene = new Scene(root, windowWidth, windowHeight);
        signUpScene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/css/signup-page.css")
                ).toExternalForm()
        );

        return signUpScene;
    }
}
