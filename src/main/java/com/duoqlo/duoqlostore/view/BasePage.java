package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.DashboardController;
import com.duoqlo.duoqlostore.controller.Navigator;
import com.duoqlo.duoqlostore.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Objects;
import java.util.Stack;

public abstract class BasePage extends ApplicationPage {
    public abstract void openCartPage();
    public abstract void openOrdersPage();
    public abstract void openProfilePage();

    private User user;

    protected StackPane header;
    protected TextField searchField;
    protected HBox searchBar;
    protected Button enterButton;
    protected Button backButton;
    protected Button forwardButton;

    protected int iconSize = 19;

    protected Color themeColor = Color.web("FE6C01");

    public BasePage(){
        this.searchField = createSearchField();
        this.searchBar = createSearchBar();
        this.backButton = createBackButton();
        this.forwardButton = createForwardButton();
    }

    public void addToolTip(Node node, String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(javafx.util.Duration.seconds(2));
        tooltip.setStyle("""
                -fx-background-color: #C7C4C3;
                -fx-border-color: #9A9593;
                -fx-text-fill: black;
                """);
        Tooltip.install(node, tooltip);
    }

    public StackPane createHeaderBox(HBox middleHBox) {
        int sidePad = 35;
        int logoHeight = 35;

        Image logo = new Image(Objects.requireNonNull(UserDashboard.class.getResource("/logo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(logoHeight);
        logoView.setPreserveRatio(true);

        //Logo button
        Button logoButton = new Button();
        logoButton.setGraphic(logoView);
        logoButton.getStyleClass().add("logo-button");
        logoButton.setOnAction(e -> {
            Navigator.openUserDashboard();
        });

        middleHBox.setMaxWidth(Region.USE_PREF_SIZE);

        //Cart Button
        FontIcon cartIcon = new FontIcon("fas-shopping-cart");
        cartIcon.setIconSize(iconSize);
        cartIcon.setIconColor(themeColor);
        Button cartButton = new Button("", cartIcon);
        cartButton.setOnAction(e -> openCartPage());

        //Orders Button
        FontIcon receiptIcon = new FontIcon("fas-receipt");
        receiptIcon.setIconSize(iconSize);
        receiptIcon.setIconColor(themeColor);
        Button ordersButton = new Button("", receiptIcon);
        ordersButton.setOnAction(e -> openOrdersPage());

        //Profile Button
        FontIcon profileIcon = new FontIcon("fas-user");
        profileIcon.setIconSize(iconSize);
        profileIcon.setIconColor(themeColor);
        Button profileButton = new Button("", profileIcon);
        profileButton.setOnAction(e -> openProfilePage());

        HBox actionBox = new HBox(10);
        actionBox.setMinWidth(300);
        actionBox.setPrefWidth(300);
        actionBox.setMaxWidth(300);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.getChildren().addAll(this.searchBar, cartButton, ordersButton, profileButton);

        header = new StackPane(); //Button-to-Button space
        header.getStyleClass().add("header");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setPrefHeight(10);
        header.setPadding(new Insets(20)); //Space between all button and HBox edge

        header.getChildren().addAll(logoButton, middleHBox, actionBox);

        StackPane.setAlignment(logoButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(middleHBox, Pos.CENTER);
        StackPane.setAlignment(actionBox, Pos.CENTER_RIGHT);

        StackPane.setMargin(logoButton, new Insets(0, 0, 0, sidePad));
        StackPane.setMargin(actionBox, new Insets(0, sidePad, 0, 0));

        return header;
    }

    public TextField createSearchField() {
        TextField searchField = new TextField();
        searchField.getStyleClass().add("search-field");
        searchField.setPromptText("Type to search");

        return searchField;
    }

    public HBox createSearchBar(){
        int searchBarLength = 250;
        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.setIconColor(themeColor);
        searchIcon.setIconSize(iconSize);

        FontIcon rightIcon = new FontIcon("fas-arrow-right");
        rightIcon.setIconSize(iconSize);
        rightIcon.setIconColor(Color.web("ADADAD"));

        enterButton = new Button("", rightIcon);
        enterButton.setPadding(Insets.EMPTY);

        HBox searchBar = new HBox();
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(Insets.EMPTY);
        searchBar.getStyleClass().add("search-bar");
        searchBar.setMaxHeight(10);
        searchBar.setMinWidth(searchBarLength);
        searchBar.setPrefWidth(searchBarLength);
        searchBar.setMaxWidth(searchBarLength);

        searchBar.getChildren().addAll(searchIcon, searchField, enterButton);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox.setMargin(searchIcon, new Insets(0, 3, 0, 5));
        HBox.setMargin(enterButton, new Insets(0, 5, 0, 3));
        ListView<String> suggestionList = new ListView<>();

        ObservableList<String> products = FXCollections.observableArrayList(
                "Phone", "Laptop", "Tablet", "Headphones", "Camera", "Charger"
        );

        VBox searchVBox = new VBox();
        searchVBox.getChildren().addAll(searchBar, suggestionList);

        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal) {
                searchBar.getStyleClass().add("focused");
                enterButton.setStyle("-fx-opacity: 1");
            } else {
                searchBar.getStyleClass().remove("focused");
                enterButton.setStyle("-fx-opacity: 0.5");
            }
        });

        return searchBar;
    }

    public Button createBackButton(){
        FontIcon backIcon = new FontIcon("fas-arrow-left");
        backIcon.setIconColor(themeColor); //Orange color

        Button backButton = new Button("", backIcon);
        backButton.setStyle("""
                -fx-background-color: transparent;
                -fx-border-color: transparent;
                """);
        if(Navigator.backIsEmpty()){
            backButton.setDisable(true);
        } else {
            backButton.setDisable(false);
        }

        return backButton;
    }

    public Button createForwardButton(){
        FontIcon forwardIcon = new FontIcon("fas-arrow-right");
        forwardIcon.setIconColor(themeColor); //Orange color

        Button forwardButton = new Button("", forwardIcon);
        forwardButton.setStyle("""
                -fx-background-color: transparent;
                -fx-border-color: transparent;
                """);
        if(Navigator.forwardIsEmpty()){
            forwardButton.setDisable(true);
        } else {
            forwardButton.setDisable(false);
        }

        return forwardButton;
    }

    public StackPane createLoadingPane(Label loadingLabel) {
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: rgba(0,0,0,0.6);");

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        ProgressIndicator spinner = new ProgressIndicator();

        loadingLabel = new Label("Loading...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        box.getChildren().addAll(spinner, loadingLabel);
        pane.getChildren().add(box);

        pane.setVisible(false);

        return pane;
    }

    public String showPrice(double amount) {
        return String.format("RM %.2f", amount);
    }

    public ComboBox<String> createSortCombo() {
        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.setPromptText("Sort by");
        sortCombo.setPrefWidth(60);
        sortCombo.setMaxWidth(175);
        sortCombo.getStyleClass().add("sort-combo");
        sortCombo.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Sort by");
                } else {
                    setText("Sort by: " + item);
                }
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        // Changes the width accordingly
        sortCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                String fullText = "Sort by: " + newVal;
                javafx.scene.text.Text textHelper = new javafx.scene.text.Text(fullText);
                textHelper.setFont(sortCombo.getButtonCell().getFont());
                double textWidth = textHelper.getLayoutBounds().getWidth();
                double neededWidth = Math.min(textWidth + 40, 250);
                sortCombo.setPrefWidth(neededWidth);
            }
        });

        return sortCombo;
    }

   public HBox createSortBox(ComboBox<String> sortCombo) {
       FontIcon sortIcon = new FontIcon("fas-sort");
       sortIcon.setIconSize(16);
       sortIcon.setIconColor(themeColor);

       HBox sortBox = new HBox(sortIcon, sortCombo);
       sortBox.setAlignment(Pos.CENTER_LEFT);

       return sortBox;
   }

    public TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setMaxWidth(Double.MAX_VALUE);
        textField.setPadding(new Insets(6,8,8,8));

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal) {
                textField.setStyle("-fx-prompt-text-fill: gray");
            } else {
                textField.setStyle("-fx-prompt-text-fill: transparent");
            }
        });

        return textField;
    }

    public Label createErrorLabel() {
        Label label = new Label();
        label.setVisible(false);
        label.setManaged(true);
        label.getStyleClass().add("error-msg");

        return label;
    }

    public VBox createTextFieldBox(TextField textField, Label errorLabel) {
        VBox box = new VBox(textField, errorLabel);
        box.setFillWidth(true);

        VBox.setMargin(errorLabel, new Insets(3, 0, 0, 0));

        return box;
    }

    public PasswordField createPasswordField(String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);

        return passwordField;
    }

    public HBox createPasswordHBox(PasswordField passwordField, TextField visiblePassField) {
        FontIcon eyeSlashIcon = new FontIcon("far-eye-slash");
        eyeSlashIcon.setIconColor(Color.LIGHTGRAY);
        eyeSlashIcon.setIconSize(16);

        FontIcon eyeIcon = new FontIcon("far-eye");
        eyeIcon.setIconColor(Color.LIGHTGRAY);
        eyeIcon.setIconSize(16);

        visiblePassField.getStyleClass().add("visible-pass");
        visiblePassField.setVisible(false);
        visiblePassField.setManaged(false);

        Button toggleButton = new Button("",eyeSlashIcon);
        toggleButton.getStyleClass().add("showpass-button");
        toggleButton.setFocusTraversable(false); // Prevent stealing focus
        toggleButton.setVisible(false);

        final boolean[] passwordVisible = {false};

        toggleButton.setOnAction(e -> {
            if (!passwordVisible[0]) {
                //Make password visible
                //Update toggleButton icon
                toggleButton.setGraphic(eyeIcon);

                visiblePassField.setText(passwordField.getText());
                visiblePassField.setVisible(true);
                visiblePassField.setManaged(true);
                visiblePassField.requestFocus();
                visiblePassField.positionCaret(visiblePassField.getText().length());

                passwordField.setVisible(false);
                passwordField.setManaged(false);

                passwordVisible[0] = true;
            } else {
                //Hide password
                //Update toggleButton icon
                toggleButton.setGraphic(eyeSlashIcon);

                passwordField.setText(visiblePassField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                passwordField.requestFocus();
                passwordField.positionCaret(passwordField.getText().length());

                visiblePassField.setVisible(false);
                visiblePassField.setManaged(false);

                passwordVisible[0] = false;
            }
        });

        HBox passwordHBox = new HBox();
        passwordHBox.getChildren().addAll(passwordField, visiblePassField, toggleButton);
        passwordHBox.setFillHeight(true); //Allow node to expand height until HBox height
        passwordHBox.getStyleClass().add("password-box");
        passwordHBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(passwordField, Priority.ALWAYS);
        HBox.setHgrow(visiblePassField, Priority.ALWAYS);
        HBox.setHgrow(toggleButton, Priority.ALWAYS);

        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { //If password field is focused
                passwordField.setStyle("-fx-prompt-text-fill: transparent");
                passwordHBox.getStyleClass().add("focused");
                toggleButton.setVisible(true);
            } else {
                if (!passwordField.getText().isEmpty()) {
                    passwordField.setStyle("-fx-prompt-text-fill: transparent");
                    passwordHBox.getStyleClass().add("focused");
                    toggleButton.setVisible(true);
                } else {
                    passwordField.setStyle("-fx-prompt-text-fill: gray");
                    passwordHBox.getStyleClass().remove("focused");
                    toggleButton.setVisible(false);
                }
            }
        });

        visiblePassField.textProperty().addListener((obs, oldVal, newVal) -> {
            passwordField.setText(visiblePassField.getText());
        });

        visiblePassField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { //If text field is focused
                visiblePassField.setStyle("-fx-prompt-text-fill: transparent");
                passwordHBox.getStyleClass().add("focused");
                toggleButton.setVisible(true);
            } else {
                if (!visiblePassField.getText().isEmpty()) {
                    visiblePassField.setStyle("-fx-prompt-text-fill: transparent");
                    passwordHBox.getStyleClass().add("focused");
                    toggleButton.setVisible(true);
                } else {
                    visiblePassField.setStyle("-fx-prompt-text-fill: gray");
                    passwordHBox.getStyleClass().remove("focused");
                    toggleButton.setVisible(false);
                }
            }
        });

        return passwordHBox;
    }

    public VBox createPasswordVBox(HBox passwordHBox, Label errorLabel) {
        VBox passwordVBox = new VBox(passwordHBox, errorLabel);
        passwordVBox.setFillWidth(true);

        VBox.setMargin(errorLabel, new Insets(3, 0, 0, 0));

        return passwordVBox;
    }
}
