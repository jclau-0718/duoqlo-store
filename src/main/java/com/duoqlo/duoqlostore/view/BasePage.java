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

public abstract class BasePage {
    protected abstract User getCurrentUser();

    private User user;

    protected int windowWidth = 1000;
    protected int windowHeight = 750;

    protected StackPane header;
    protected HBox actionBox;
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

    public StackPane createHeaderBox(HBox middleHBox, HBox rightBox) {
        int sidePad = 35;
        int logoHeight = 35;
        actionBox = rightBox;

        Image logo = new Image(Objects.requireNonNull(UserDashboard.class.getResource("/logo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(logoHeight);
        logoView.setPreserveRatio(true);

        Button logoButton = new Button();
        logoButton.setGraphic(logoView);
        logoButton.setId("logo-button");
        logoButton.setOnAction(e -> {
            DashboardController dashboardController = new DashboardController();
            dashboardController.setUser(getCurrentUser());

            UserDashboard userDash = new UserDashboard(dashboardController);
            Navigator.goTo(userDash.initialize());
        });

        //Profile Button
        FontIcon profileIcon = new FontIcon("fas-user");
        profileIcon.setIconSize(iconSize);
        profileIcon.setIconColor(themeColor);
        Button profileButton = new Button("", profileIcon);

        middleHBox.setMaxWidth(Region.USE_PREF_SIZE);

        actionBox.getChildren().add(profileButton);

        header = new StackPane(); //Button-to-Button space
        header.setId("header-menu");
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
        searchBar.setId("search-bar");
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

    public VBox showSearchBox() {
        ListView<String> suggestionList = new ListView<>();

        ObservableList<String> products = FXCollections.observableArrayList(
                "Phone", "Laptop", "Tablet", "Headphones", "Camera", "Charger"
        );

        VBox searchBox = new VBox();
        searchBox.getChildren().addAll(searchBar, suggestionList);

        for (Node node: actionBox.getChildren()) {
            if(node instanceof HBox) {

            }
        }

        return searchBox;
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

    public BorderPane createNavigationBar(){
        BorderPane navBar = new BorderPane();
        navBar.setLeft(backButton);
        navBar.setRight(forwardButton);

        return navBar;
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
}
