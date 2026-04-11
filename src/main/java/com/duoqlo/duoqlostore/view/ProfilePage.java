package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.InfoValidation;
import com.duoqlo.duoqlostore.controller.Navigator;
import com.duoqlo.duoqlostore.controller.ProfileController;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProfilePage extends BasePage {
    private ProfileController controller;
    private InfoValidation validator = new InfoValidation();

    private AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
    private AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);

    private StackPane body;
    private VBox profileBox;
    private VBox editBox;

    // Info form
    private TextField firstNameField, lastNameField, emailField;
    private Label firstNameError, lastNameError, emailError;
    private VBox firstNameBox, lastNameBox, emailBox;

    //Address Form
    private TextField address1Field, address2Field,
            cityField, postcodeField, stateField;
    private Label address1Error, address2Error,
            cityError, postcodeError, stateError;
    private VBox address1Box, address2Box,
            cityBox, postcodeBox, stateBox;

    //Credentials form
    private TextField usernameField, visiblePassField, visibleConfirmPassField;
    private PasswordField passwordField, confirmPassField;
    private Label usernameError, passwordError, confirmPassError;
    private HBox passwordHBox, confirmPassHBox;
    private VBox usernameBox, passwordBox, confirmPassBox;

    private Map<TextField, Label> fieldErrorMap = new LinkedHashMap<>();

    private int profileBoxWidth = 100;

    public ProfilePage(ProfileController controller) {
        this.controller = controller;
    }

    @Override
    public void openCartPage() {

    }

    @Override
    public void openOrdersPage() {

    }

    @Override
    public void openProfilePage() {
        Navigator.goTo(this.initialize());
    }

    private StackPane buildHeader() {
        Label label = new Label("PROFILE");
        label.getStyleClass().add("profile-label");

        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);

        StackPane header = createHeaderBox(labelBox);

        return header;
    }

    private void initFieldBoxes() {
        // First Name
        firstNameField = createTextField("First Name");
        firstNameField.getStyleClass().add("valid");
        firstNameError = createErrorLabel();
        firstNameBox = createTextFieldBox(firstNameField, firstNameError);

        // Last Name
        lastNameField = createTextField("Last Name");
        lastNameField.getStyleClass().add("valid");
        lastNameError = createErrorLabel();
        lastNameBox = createTextFieldBox(lastNameField, lastNameError);

        // Email
        emailField = createTextField("Email (hello@example.com)");
        emailField.getStyleClass().add("valid");
        emailError = createErrorLabel();
        emailBox = createTextFieldBox(emailField, emailError);

        // Address 1
        address1Field = createTextField("Address Line 1");
        address1Field.getStyleClass().add("valid");
        address1Error = createErrorLabel();
        address1Box = createTextFieldBox(address1Field, address1Error);

        // Address 2 (optional)
        address2Field = createTextField("Address Line 2 (Optional)");
        address2Field.getStyleClass().add("valid");
        address2Error = createErrorLabel();
        address2Box = createTextFieldBox(address2Field, address2Error);

        // City
        cityField = createTextField("City");
        cityField.getStyleClass().add("valid");
        cityError = createErrorLabel();
        cityBox = createTextFieldBox(cityField, cityError);

        // Postcode
        postcodeField = createTextField("Postcode");
        postcodeField.getStyleClass().add("valid");
        postcodeError = createErrorLabel();
        postcodeBox = createTextFieldBox(postcodeField, postcodeError);

        // State
        stateField = createTextField("State");
        stateField.getStyleClass().add("valid");
        stateError = createErrorLabel();
        stateBox = createTextFieldBox(stateField, stateError);

        // Username
        usernameField = createTextField("Username");
        usernameField.getStyleClass().add("valid");
        usernameError = createErrorLabel();
        usernameBox = createTextFieldBox(usernameField, usernameError);

        // Password
        passwordField = createPasswordField("New Password");

        visiblePassField = new TextField();

        passwordHBox = createPasswordHBox(passwordField, visiblePassField);
        passwordHBox.getStyleClass().add("valid");
        passwordError = createErrorLabel();
        passwordBox = createPasswordVBox(passwordHBox, passwordError);

        // Confirm Password
        confirmPassField = createPasswordField("Confirm New Password");

        visibleConfirmPassField = new TextField();

        confirmPassHBox = createPasswordHBox(confirmPassField, visibleConfirmPassField);
        confirmPassHBox.getStyleClass().add("valid");
        confirmPassError = createErrorLabel();
        confirmPassBox = createPasswordVBox(confirmPassHBox, confirmPassError);

        setTextFieldVal();
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

    private void setTextFieldVal() {
        // Info form
        firstNameField.setText(controller.getFirstName());
        lastNameField.setText(controller.getLastName());
        emailField.setText(controller.getEmail());

        // Address form
        address1Field.setText(controller.getAddressLine1());
        address2Field.setText(controller.getAddressLine2());
        cityField.setText(controller.getCity());
        postcodeField.setText(controller.getPostalCodeStr());
        stateField.setText(controller.getState());

        // Credentials form
        usernameField.setText(controller.getUsername());
    }

    private void getNewInfo() {
        controller.addNewInfo(firstNameField.getText());
        controller.addNewInfo(lastNameField.getText());
        controller.addNewInfo(emailField.getText());
    }

    private void getNewAddr() {
        controller.addNewAddr(address1Field.getText());
        controller.addNewAddr(address2Field.getText());
        controller.addNewAddr(cityField.getText());
        controller.addNewAddr(postcodeField.getText());
        controller.addNewAddr(stateField.getText());
    }

    private void getNewCred() {
        controller.addNewCred(usernameField.getText());
        controller.addNewCred(controller.hashPassword(passwordField.getText()));
    }

    private VBox buildProfileBox() {
        Label nameLabel = new Label(controller.getFullName());
        nameLabel.getStyleClass().add("name");

        Label usernameLabel = new Label(controller.getUsername());
        usernameLabel.getStyleClass().add("username");

        HBox nameBox = new HBox(7, nameLabel, usernameLabel);

        Label emailLabel = new Label(controller.getEmail());
        emailLabel.getStyleClass().add("email");

        Label addressLabel = new Label("Address: ");
        addressLabel.getStyleClass().add("address");

        Label addressValue = new Label(controller.getFullAddress());
        addressValue.getStyleClass().add("address-value");

        HBox addressBox = new HBox(addressLabel, addressValue);

        VBox profileBox = new VBox(5, nameBox, emailLabel, addressBox);
        profileBox.setPadding(new Insets(20));
        profileBox.getStyleClass().add("profile-box");

        return profileBox;
    }

    private FontIcon createCaretRight() {
        FontIcon rightCaret = new FontIcon("fas-caret-right");
        rightCaret.setIconColor(themeColor);
        rightCaret.setIconSize(18);

        return rightCaret;
    }

    private VBox buildOptionBox() {
        FontIcon infoRightCaret = createCaretRight();

        ToggleButton editInfoButton = new ToggleButton("Edit Info", infoRightCaret);
        editInfoButton.setPrefWidth(Double.MAX_VALUE);
        editInfoButton.setAlignment(Pos.CENTER_LEFT);
        editInfoButton.setOnAction(e -> {
            controller.setMenuOpened("info");
            updateEditBox(buildInfoBox());
        });

        FontIcon addrRightCaret = createCaretRight();
        ToggleButton editAddrButton = new ToggleButton("Edit Address", addrRightCaret);
        editAddrButton.setPrefWidth(Double.MAX_VALUE);
        editAddrButton.setAlignment(Pos.CENTER_LEFT);
        editAddrButton.setOnAction(e -> {
            controller.setMenuOpened("address");
            updateEditBox(buildAddressBox());
        });

        FontIcon credRightCaret = createCaretRight();
        ToggleButton editCredButton = new ToggleButton("Edit Credentials", credRightCaret);
        editCredButton.setPrefWidth(Double.MAX_VALUE);
        editCredButton.setAlignment(Pos.CENTER_LEFT);
        editCredButton.setOnAction(e -> {
            controller.setMenuOpened("credentials");
            updateEditBox(buildCredentialsBox());
        });

        ToggleGroup categoryGroup = new ToggleGroup();
        editInfoButton.setToggleGroup(categoryGroup);
        editAddrButton.setToggleGroup(categoryGroup);
        editCredButton.setToggleGroup(categoryGroup);

        VBox optionBox = new VBox(30, editInfoButton, editAddrButton, editCredButton);
        optionBox.getStyleClass().add("option-box");
        optionBox.setFillWidth(true);
        optionBox.setPadding(new Insets(0, 20, 0, 20));

        return optionBox;
    }

    private void updateEditBox(VBox textfieldBox) {
        VBox newEditBox = textfieldBox;
        newEditBox.getStyleClass().add("edit-box");

        if (editBox.getParent() instanceof GridPane) {
            GridPane parent = (GridPane) editBox.getParent();
            int row = GridPane.getRowIndex(editBox);
            int col = GridPane.getColumnIndex(editBox);

            //Remove old and add new
            parent.getChildren().remove(editBox);
            parent.add(newEditBox, col, row);

            //Update reference
            editBox = newEditBox;
        }
    }

    private void handleUpdate() {
        if (controller.updateData()) {
            controller.setNewUser();

            updateProfileBox();

            successAlert.show(body, "Successfully updated.", Pos.TOP_CENTER);

        } else {
            errorAlert.show(body, "Error. Please try again.", Pos.TOP_CENTER);
        }
    }

    private void updateProfileBox() {
        VBox newProfileBox = buildProfileBox();
        newProfileBox.getStyleClass().add("profile-box");

        if (profileBox.getParent() instanceof GridPane) {
            GridPane parent = (GridPane) profileBox.getParent();
            int row = GridPane.getRowIndex(profileBox);
            int col = GridPane.getColumnIndex(profileBox);

            //Remove old and add new
            parent.getChildren().remove(profileBox);
            parent.add(newProfileBox, col, row, 2, 1);

            //Update reference
            profileBox = newProfileBox;
        }
    }

    private VBox buildInfoBox() {

        Button updateInfoButton = new Button("UPDATE");
        updateInfoButton.getStyleClass().add("primary-button");
        updateInfoButton.setDefaultButton(true);
        updateInfoButton.setOnAction(e -> {
            getNewInfo();

            handleUpdate();
        });

        HBox buttonBox = new HBox(updateInfoButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

        VBox infoBox = new VBox(7);
        infoBox.getChildren().addAll(firstNameBox, lastNameBox, emailBox);
        infoBox.getChildren().add(buttonBox);
        infoBox.setPadding(new Insets(20));

        return infoBox;
    }

    private VBox buildAddressBox() {
        VBox textfieldBox = new VBox(7, address1Box, address2Box, cityBox, postcodeBox, stateBox);

        Button updateAddressButton = new Button("UPDATE");
        updateAddressButton.getStyleClass().add("primary-button");
        updateAddressButton.setDefaultButton(true);
        updateAddressButton.setOnAction(e -> {
            getNewAddr();

            handleUpdate();
        });
        HBox buttonBox = new HBox(updateAddressButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

        VBox addressBox = new VBox(textfieldBox, buttonBox);
        addressBox.setPadding(new Insets(20));

        return addressBox;
    }

    private VBox buildCredentialsBox() {
        VBox textfieldBox = new VBox(7, usernameBox, passwordBox, confirmPassBox);

        Button updateCredButton = new Button("UPDATE");
        updateCredButton.getStyleClass().add("primary-button");
        updateCredButton.setDefaultButton(true);
        updateCredButton.setOnAction(e -> {
            getNewCred();

            handleUpdate();
        });
        HBox buttonBox = new HBox(updateCredButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

        VBox credentialsBox = new VBox(textfieldBox, buttonBox);
        credentialsBox.setPadding(new Insets(20));

        return credentialsBox;
    }

    private StackPane buildBody() {
        int rowHeight = 395;

        profileBox = buildProfileBox();
        profileBox.setPrefWidth(profileBoxWidth);

        VBox optionBox = buildOptionBox();
        optionBox.setAlignment(Pos.CENTER);

        optionBox.setMinHeight(rowHeight);
        optionBox.setPrefHeight(rowHeight);
        optionBox.setMaxHeight(rowHeight);

        editBox = buildInfoBox();
        editBox.getStyleClass().add("edit-box");

        editBox.setMinHeight(rowHeight);
        editBox.setPrefHeight(rowHeight);
        editBox.setMaxHeight(rowHeight);

        GridPane boxGrid = new GridPane();
        boxGrid.setVgap(35);
        boxGrid.setHgap(35);
        boxGrid.add(profileBox, 0, 0, 2, 1);
        boxGrid.add(optionBox, 0, 1, 1, 1);
        boxGrid.add(editBox, 1, 1, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setFillWidth(true);
        col2.setPercentWidth(50);

        boxGrid.getColumnConstraints().addAll(col1, col2);

        VBox vbox = new VBox(boxGrid);
        vbox.setMaxWidth(700);
        vbox.setAlignment(Pos.CENTER);

        StackPane body = new StackPane(vbox);

        return body;
    }

    public Scene initialize() {
        initFieldBoxes();
        packFieldError();

        body = buildBody();

        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(body);

        Scene scene = setScene(root, "profile-page");

        return scene;
    }
}
