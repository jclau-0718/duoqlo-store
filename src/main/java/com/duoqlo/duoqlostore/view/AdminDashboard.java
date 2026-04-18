package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.AdminDashController;
import com.duoqlo.duoqlostore.controller.AdminProductUploader;
import com.duoqlo.duoqlostore.controller.Navigator;
import com.duoqlo.duoqlostore.controller.ProfileController;
import com.duoqlo.duoqlostore.model.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Objects;

class StatCard extends VBox {
    private String title;
    private String value;
    private Label valueLabel;

    public StatCard(String title, String value) {
        this.title = title;
        this.value = value;

        create();
    }

    private VBox buildOrangeBackground() {
        VBox background = new VBox();
        background.setStyle("""
                -fx-background-color: #FE6C01;
                -fx-background-radius: 10;
                -fx-border-radus: 10;
                """);

        return background;
    }

    private VBox buildContentBox() {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("""
                -fx-text-fill: black;
                -fx-font-size: 15;
                """);

        valueLabel = new Label(value);
        valueLabel.setStyle("""
                -fx-text-fill: black;
                -fx-font-size: 26;
                """);

        VBox contentBox = new VBox(titleLabel, valueLabel);
        contentBox.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-border-color: white;
                -fx-border-radius: 10;
                -fx-padding: 5 10 5 10;
                """);

        return contentBox;
    }

    private void create() {
        VBox orangeBox = buildOrangeBackground();

        VBox contentBox = buildContentBox();

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(orangeBox, contentBox);
        stackPane.setAlignment(Pos.TOP_CENTER);
        StackPane.setMargin(contentBox, new Insets(0, 0, 5, 0));
        stackPane.setPrefHeight(80);

        this.getChildren().add(stackPane);
        this.getStyleClass().add("stat-card");
        this.setStyle("");
    }

    public void update(String value) {
        this.value = value;
        if (valueLabel != null) {
            valueLabel.setText(value);
        }
    }
}

class UserTableView extends TableView<User> {
    private AdminDashController controller;
    private StackPane body;
    private BorderPane root;

    private TableColumn<User, Integer> idCol = new TableColumn<>("User ID");
    private TableColumn<User, String> usernameCol = new TableColumn<>("Username");
    private TableColumn<User, String> fullNameCol = new TableColumn<>("Full Name");
    private TableColumn<User, String> emailCol = new TableColumn<>("Email");
    private TableColumn<User, String> addressCol = new TableColumn<>("Address");
    private TableColumn<User, String> statusCol = new TableColumn<>("Status");
    private TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");

    public UserTableView(AdminDashController controller, StackPane body, BorderPane root) {
        this.controller = controller;
        this.body = body;
        this.root = root;

        build();
    }

    private void build() {
        // Set up cell value factories
        idCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getId()).asObject());

        usernameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsername()));

        fullNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFullName()));

        emailCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail()));

        addressCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFullAddress()));

        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getIsActive() == 1 ? "ACTIVE" : "INACTIVE"));

        //Setting up status column style
        statusCol.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    // Apply different styles based on status
                    if (item.equals("ACTIVE")) {
                        setStyle("-fx-text-fill: #10A115;");
                    } else {
                        setStyle("-fx-text-fill: #D32F2F;");
                    }
                }
            }
        });

        //Setting up actions column
        actionsCol.setCellFactory(col -> new TableCell<User, Void>() {
            private final Button updateButton = new Button("Update");
            private final Button deactivateButton = new Button("Deactivate");
            private final Button reactivateButton = new Button("Reactivate");
            private final HBox buttonBox = new HBox(10);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());

                    final boolean[] isAdmin = {false};
                    if(user.getRole().equals("ADMIN")) isAdmin[0] = true;

                    double buttonWidth = 100;
                    updateButton.setPrefWidth(buttonWidth);
                    deactivateButton.setPrefWidth(buttonWidth);
                    reactivateButton.setPrefWidth(buttonWidth);

                    buttonBox.getStyleClass().add("action-box");
                    buttonBox.setAlignment(Pos.CENTER);
                    buttonBox.getChildren().clear();
                    buttonBox.getChildren().add(updateButton);

                    if (user.getIsActive() == 0) { //User is inactive
                        buttonBox.getChildren().add(reactivateButton);
                    } else {
                        buttonBox.getChildren().add(deactivateButton);
                    }

                    // Update button action
                    updateButton.setOnAction(e -> {
                        ProfileController profileController = new ProfileController(user);
                        ProfilePage profilePage = new ProfilePage(profileController);

                        StackPane content = profilePage.getContent();

                        root.setCenter(content);
                    });
                    updateButton.getStyleClass().add("update-button");

                    // Deactivate button action
                    deactivateButton.setOnAction(e -> {
                        if (isAdmin[0] && controller.getTotalAdmins() == 1) {
                            AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                            errorAlert.show(body, "Minimum one admin must remain active.", Pos.TOP_CENTER);
                            return;
                        }

                        AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
                        confirmAlert.show(body, "Confirm to deactivate?", Pos.TOP_CENTER);
                        confirmAlert.setOnConfirm(() -> {
                            boolean success = isAdmin[0]
                                    ? controller.deactivateAdmins(user.getId())
                                    : controller.deactivateCustomers(user.getId());

                            if (success) {
                                refresh();
                                AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                                successAlert.show(body, "Deactivated successfully", Pos.TOP_CENTER);
                            } else {
                                AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                                errorAlert.show(body, "Error. Failed to deactivate.", Pos.TOP_CENTER);
                            }
                        });
                    });
                    deactivateButton.getStyleClass().add("deactivate-button");

                    reactivateButton.setOnAction(e -> {
                        AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
                        confirmAlert.show(body, "Confirm to reactivate?", Pos.TOP_CENTER);
                        confirmAlert.setOnConfirm(() -> {
                            if (controller.reactivateUser(user.getId())) {
                                //Refresh table
                                refresh();

                                //Show success alert
                                AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                                successAlert.show(body, "Reactivated successfully", Pos.TOP_CENTER);
                            }
                        });
                    });
                    reactivateButton.getStyleClass().add("reactivate-button");

                    setGraphic(buttonBox);
                }
            }
        });

        getColumns().addAll(idCol, usernameCol, fullNameCol,
                emailCol, addressCol, statusCol, actionsCol);

        TableUtils.addColToolTip(fullNameCol);
        TableUtils.addColToolTip(emailCol);
        TableUtils.addColToolTip(addressCol);

        setColWidth(statusCol, 100);

        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private <T> void setColWidth(TableColumn<User, T> column, int width) {
        column.setMinWidth(width);
        column.setPrefWidth(width);
        column.setMaxWidth(width);
    }
}

class ProductTableView extends TableView<Product> {
    private AdminDashController controller;
    private StackPane body;
    private BorderPane root;
    private Pane parent;

    private Runnable refreshStatCard;
    private Runnable showProductPage;

    private TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
    private TableColumn<Product, String> skuCol = new TableColumn<>("Product SKU");
    private TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
    private TableColumn<Product, String> genderCol = new TableColumn<>("Gender");
    private TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
    private TableColumn<Product, String> sizesCol = new TableColumn<>("Sizes");
    private TableColumn<Product, String> priceRangeCol = new TableColumn<>("Price Range(RM)");
    private TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
    private TableColumn<Product, String> addedDateCol = new TableColumn<>("Added Date");
    private TableColumn<Product, String> statusCol = new TableColumn<>("Status");
    private TableColumn<Product, Void> actionsCol = new TableColumn<>("Actions");

    public ProductTableView(AdminDashController controller, StackPane body, BorderPane root) {
        this.controller = controller;
        this.body = body;
        this.root = root;

        build();
    }

    public void setParent(Pane parent) {
        this.parent = parent;
    }

    public void setRefreshStatCard(Runnable refresh) {
        this.refreshStatCard = refresh;
    }

    public void setShowProductPage(Runnable show) {
        this.showProductPage = show;
    }

    private void build() {
        idCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getId()).asObject());

        skuCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSku()));

        nameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));

        genderCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getGender()));

        categoryCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategory()));

        sizesCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSizeRange()));

        priceRangeCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPriceRange()));

        stockCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getStock()).asObject());

        addedDateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAddedDateStr()));

        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));

        statusCol.setCellFactory(column -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    // Apply different styles based on status
                    if (item.equals("AVAILABLE")) {
                        setStyle("-fx-text-fill: #10A115;");
                    } else {
                        setStyle("-fx-text-fill: #D32F2F;");
                    }
                }
            }
        });

        //Setting up actions column
        actionsCol.setCellFactory(column -> new TableCell<Product, Void>() {
            private final Button viewButton = new Button("View");
            private final Button removeButton = new Button("Remove");
            private final HBox buttonBox = new HBox(10);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());

                    double buttonWidth = 100;
                    viewButton.setPrefWidth(buttonWidth);
                    removeButton.setPrefWidth(buttonWidth);

                    buttonBox.getStyleClass().add("action-box");
                    buttonBox.getChildren().clear();
                    buttonBox.getChildren().addAll(viewButton, removeButton);
                    buttonBox.setAlignment(Pos.CENTER);

                    viewButton.getStyleClass().add("view-button");
                    viewButton.setOnAction(e -> {
                        AdminProductUploader uploader = new AdminProductUploader(parent);
                        uploader.setUpdateMode();
                        uploader.setShowProductPage(() -> showProductPage.run());

                        parent.getChildren().clear();
                        parent.getChildren().add(uploader.show(product));
                    });

                    removeButton.setOnAction(e -> {
                        AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
                        confirmAlert.show(body, "Confirm to remove product?", Pos.TOP_CENTER);
                        confirmAlert.setOnConfirm(() -> {
                            getTableView().setItems(FXCollections.observableArrayList());
                            getTableView().refresh();

                            if (controller.removeProduct(product)) {
                                refreshStatCard.run();

                                getTableView().setItems(controller.getProducts());
                                refresh();

                                AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                                successAlert.show(body, "Successfully removed product", Pos.TOP_CENTER);
                            }
                        });
                    });
                    removeButton.getStyleClass().add("remove-button");

                    setGraphic(buttonBox);
                }
            }
        });

        getColumns().addAll(idCol, skuCol, nameCol, genderCol, categoryCol,
                sizesCol, priceRangeCol, stockCol, addedDateCol, statusCol, actionsCol);

        TableUtils.addColToolTip(nameCol);
        TableUtils.addColToolTip(categoryCol);
        TableUtils.addColToolTip(priceRangeCol);
        TableUtils.addColToolTip(addedDateCol);

        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        //Make the columns compact
        skinProperty().addListener((obs, oldSkin, newSkin) -> {
            lookupAll(".column-header").forEach(header -> {
                if (header instanceof Region region) {
                    region.setPadding(new Insets(2));
                }
            });
        });

        getColumns().forEach(col -> col.setStyle("-fx-padding: 2;"));

        setColWidth(idCol, 50);
        setColWidth(nameCol, 200);
        setColWidth(priceRangeCol, 150);
        setColWidth(statusCol, 110);
    }

    private <T> void setColWidth(TableColumn<Product, T> column, int width) {
        column.setMinWidth(width);
        column.setPrefWidth(width);
        column.setMaxWidth(width);
    }
}

public class AdminDashboard extends ApplicationPage {
    private enum ActiveView {
        CUSTOMERS, ADMINS, PRODUCTS, NONE
    }

    private AdminDashController controller;
    private AlertMsg alert;

    private ActiveView currentActiveView = ActiveView.NONE;

    private StatCard customerStatCard;
    private StatCard adminStatCard;
    private StatCard productStatCard;
    private StatCard orderStatCard;
    private StatCard revenueStatCard;

    private BorderPane buttonPane;
    private Button addButton = new Button();
    private Button backButton;
    private VBox mainTableBox = new VBox();

    private Label titleLabel;
    private HBox titleBox;

    private GridPane genderCategoryGrid;
    private VBox genderTableBox = new VBox();
    private TableView<Gender> genderTable;
    private TextField genderIdField;
    private TextField genderField;

    private VBox categoryTableBox = new VBox();
    private TableView<Category> categoryTable;
    private TextField categoryIdField;
    private TextField categoryNameField;
    private ComboBox<Gender> categoryGenderCombo;

    private TableView<Order> orderTable;
    private VBox detailBox;

    private HBox salesFilterSection;

    private VBox bodyVBox;
    private StackPane body;
    private BorderPane root;

    private boolean addBtnWasVisible = false;

    private int sidePad = 35;

    public AdminDashboard(AdminDashController controller) {
        this.controller = controller;
    }

    private StackPane buildHeader() {
        int logoHeight = 35;

        Image logo = new Image(Objects.requireNonNull(UserDashboard.class.getResource("/logo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(logoHeight);
        logoView.setPreserveRatio(true);

        //LEFT - Logo button
        Button logoButton = new Button();
        logoButton.setGraphic(logoView);
        logoButton.getStyleClass().add("logo-button");
        logoButton.setOnAction(e -> {
            Navigator.goTo(this.initialize());
        });

        //MIDDLE
        Label label = new Label("ADMIN DASHBOARD");
        label.getStyleClass().add("admin-dash-label");
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);
        labelBox.setMaxWidth(Region.USE_PREF_SIZE);

        //RIGHT
        Button logOutButton = new Button("LOG OUT");
        logOutButton.getStyleClass().add("logout-button");
        System.out.println(logOutButton.getStyleClass());
        logOutButton.setOnAction(e -> Navigator.goTo(new LogInPage().initialize()));

        StackPane header = new StackPane(); //Button-to-Button space
        header.getStyleClass().add("header");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setPrefHeight(10);
        header.setPadding(new Insets(20)); //Space between children edge and HBox edge

        header.getChildren().addAll(logoButton, labelBox, logOutButton);

        StackPane.setAlignment(logoButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(labelBox, Pos.CENTER);
        StackPane.setAlignment(logOutButton, Pos.CENTER_RIGHT);

        StackPane.setMargin(logoButton, new Insets(0, 0, 0, sidePad));
        StackPane.setMargin(logOutButton, new Insets(0, sidePad, 0, 0));


        return header;
    }

    private GridPane buildCardGrid() {
        customerStatCard = new StatCard("Total Customers", String.valueOf(controller.getTotalCustomers()));
        customerStatCard.setOnMouseClicked(e -> buildCustomerPage());

        adminStatCard = new StatCard("Total Admins", String.valueOf(controller.getTotalAdmins()));
        adminStatCard.setOnMouseClicked(e -> buildAdminPage());

        productStatCard = new StatCard("Total Products", String.valueOf(controller.getTotalProducts()));
        productStatCard.setOnMouseClicked(e -> buildProductPage());

        orderStatCard = new StatCard("Total Orders", String.valueOf(controller.getTotalOrders()));
        orderStatCard.setOnMouseClicked(e -> buildOrderPage());

        revenueStatCard = new StatCard("Total Revenue (RM)", String.format("%.2f", controller.getTotalRevenue()));
        revenueStatCard.setOnMouseClicked(e -> buildSalesPage());

        GridPane cardGrid = new GridPane();
        cardGrid.add(customerStatCard, 0, 0);
        cardGrid.add(adminStatCard, 1, 0);
        cardGrid.add(productStatCard, 2, 0);
        cardGrid.add(orderStatCard, 3, 0);
        cardGrid.add(revenueStatCard, 4, 0);

        cardGrid.setPadding(new Insets(30, 35, 15, 35));
        cardGrid.setAlignment(Pos.CENTER);
        cardGrid.setHgap(80);
        cardGrid.setMaxHeight(Region.USE_PREF_SIZE);

        //Add column constraints
        for (int i = 0; i < cardGrid.getChildren().size(); i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(14);
            cardGrid.getColumnConstraints().add(col);
        }

        return cardGrid;
    }

    private void buildSalesPage() {
        setTitleLabel("SALES");

        SalesPage salesPage = new SalesPage(this.controller);

        salesFilterSection = salesPage.getFilterSection();

        hideAddButton();
        buttonPane.setLeft(salesFilterSection);

        mainTableBox.getChildren().clear();
        mainTableBox.getChildren().add(salesPage.getContent());

        removeGendersCategories();
    }

    private void removeSalesFilter() {
        if(buttonPane.getLeft() != null) {
            buttonPane.setLeft(null);
        }
    }

    private HBox buildTableTitle(Label titleLabel) {
        titleLabel.getStyleClass().add("title");

        Region rightLine = new Region();
        rightLine.setStyle("-fx-background-color: #A1A1A1");
        rightLine.setMaxHeight(3);

        Region leftLine = new Region();
        leftLine.setStyle("-fx-background-color: #A1A1A1");
        leftLine.setMaxHeight(3);

        HBox titleBox = new HBox(20, leftLine, titleLabel, rightLine);
        titleBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(rightLine, Priority.ALWAYS);
        HBox.setHgrow(leftLine, Priority.ALWAYS);

        return titleBox;
    }

    private void setTitleLabel(String title) {
        titleBox.setVisible(true);
        titleBox.setManaged(true);
        titleLabel.setText(title);
    }

    private void showButtonBox(String buttonText) {
        addButton.setText(buttonText);
        addButton.setVisible(true);
        addBtnWasVisible = true;
    }

    private void hideAddButton() {
        addButton.setVisible(false);
        addBtnWasVisible = false;
    }

    private void showCustomerTable() {
        TableView<User> customerTable = new UserTableView(this.controller, body, root);

        // Get users from database
        ObservableList<User> customers = controller.getCustomers();

        customerTable.setItems(customers);

        mainTableBox.getChildren().clear();
        mainTableBox.getChildren().add(customerTable);
        VBox.setVgrow(customerTable, Priority.ALWAYS);
    }

    private void buildCustomerPage() {
        currentActiveView = ActiveView.CUSTOMERS;

        setTitleLabel("CUSTOMERS");

        removeSalesFilter();

        showButtonBox("+ Add Customer");
        addButton.setOnAction(ae -> {
            backButton.setVisible(true);

            SignUpPage signUpPage = new SignUpPage();
            signUpPage.setIsAdminMode();

            signUpPage.setBackToAdminDash(() -> {
                Navigator.goTo(this.initialize());

                alert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                alert.show(body, "New user added.", Pos.TOP_CENTER);
            });

            VBox content = signUpPage.getContentForAdmin();

            switchDisplayBox(content);
        });

        showCustomerTable();

        removeGendersCategories();
    }

    private void showAdminTable() {
        TableView<User> adminTable = new UserTableView(this.controller, body, root);

        ObservableList<User> admins = controller.getAdmins();

        adminTable.setItems(admins);

        mainTableBox.getChildren().clear();
        mainTableBox.getChildren().add(adminTable);
        VBox.setVgrow(adminTable, Priority.ALWAYS);
    }

    private void buildAdminPage() {
        currentActiveView = ActiveView.ADMINS;

        setTitleLabel("ADMINS");

        removeSalesFilter();

        showButtonBox("+ Add Admin");
        addButton.setOnAction(ae -> {
            backButton.setVisible(true);

            SignUpPage signUpPage = new SignUpPage();
            signUpPage.setIsAdminMode();

            signUpPage.setBackToAdminDash(() -> {
                Navigator.goTo(this.initialize());

                alert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                alert.show(body, "New admin added.", Pos.TOP_CENTER);
            });

            VBox content = signUpPage.getContentForAdmin();

            switchDisplayBox(content);
        });

        showAdminTable();

        removeGendersCategories();
    }

    private void showProductTable() {
        TableView<Product> productTable = new ProductTableView(this.controller, body, root);
        ((ProductTableView) productTable).setRefreshStatCard(() -> {
            refreshStatCards();
        });

        ObservableList<Product> products = controller.getProducts();

        productTable.setItems(products);

        mainTableBox.getChildren().clear();
        mainTableBox.getChildren().add(productTable);
        VBox.setVgrow(productTable, Priority.ALWAYS);

        ((ProductTableView) productTable).setParent(mainTableBox);

        ((ProductTableView) productTable).setShowProductPage(() -> {
            refreshProductPage();
            controller.refreshProductData();

            AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
            successAlert.show(body, "Successfully updated product.", Pos.TOP_CENTER);
        });
    }

    private GridPane buildGenderForm() {
        Label idLabel = new Label("Gender ID");
        idLabel.getStyleClass().add("form-header");

        Label genderLabel = new Label("Gender");
        genderLabel.getStyleClass().add("form-header");

        genderIdField = new TextField();
        genderIdField.textProperty().addListener((obs, oldval, newVal) -> {
            if (newVal.isEmpty()) {
                return;
            }

            if (!newVal.matches("^[A-Za-z]+$") || newVal.length() > 1) {
                genderIdField.setText(oldval);
            }
        });
        genderField = new TextField();
        genderField.textProperty().addListener((obs, oldval, newVal) -> {
            if (newVal.isEmpty()) {
                return;
            }

            if (!newVal.matches("^[A-Za-z]+$")) {
                genderField.setText(oldval);
            }
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(30);
        grid.add(idLabel, 0, 0);
        grid.add(genderLabel, 0, 1);
        grid.add(genderIdField, 1, 0);
        grid.add(genderField, 1, 1);

        return grid;
    }

    private void showAddGenderForm() {
        Label titleLabel = new Label("Enter New Gender Details");
        titleLabel.getStyleClass().add("title");

        GridPane grid = buildGenderForm();

        Button addButton = new Button("Add");
        addButton.getStyleClass().add("orange-button");
        addButton.setOnAction(e -> {
            AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
            confirmAlert.show(body, "Confirm to add?", Pos.TOP_CENTER);
            confirmAlert.setOnConfirm(() -> {
                String id = genderIdField.getText().trim().toUpperCase();
                String gender = genderField.getText().trim().toUpperCase();

                if (controller.addGender(id, gender)) {
                    AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                    successAlert.show(body, "Successfully add new gender", Pos.TOP_CENTER);

                    refreshProductPage();
                } else {
                    AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                    errorAlert.show(body, "Error adding gender. Try again.", Pos.TOP_CENTER);
                }
            });
        });

        VBox vbox = new VBox(20, titleLabel, grid, addButton);
        vbox.setAlignment(Pos.CENTER);

        genderTableBox.getChildren().clear();
        genderTableBox.getChildren().add(vbox);
    }

    private void showUpdateGenderForm() {
        Label titleLabel = new Label("Enter New Gender Details");
        titleLabel.getStyleClass().add("title");

        GridPane grid = buildGenderForm();
        genderIdField.setDisable(true);

        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("orange-button");
        updateButton.setOnAction(e -> {
            AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
            confirmAlert.show(body, "Confirm to update?", Pos.TOP_CENTER);
            confirmAlert.setOnConfirm(() -> {
                String currentId = genderIdField.getText().trim().toUpperCase();
                String currentGender = genderField.getText().trim().toUpperCase();

                if (controller.updateGender(currentId, currentGender)) {
                    AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                    successAlert.show(body, "Successfully updated gender", Pos.TOP_CENTER);

                    genderIdField.setText("");
                    genderField.setText("");

                    refreshProductPage();
                } else {
                    AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                    errorAlert.show(body, "Error updating gender. Try again.", Pos.TOP_CENTER);
                }
            });
        });

        VBox vbox = new VBox(20, titleLabel, grid, updateButton);
        vbox.setAlignment(Pos.CENTER);

        genderTableBox.getChildren().clear();
        genderTableBox.getChildren().add(vbox);
    }

    private void buildGenderTable() {
        genderTable = new TableView<>();

        TableColumn<Gender, String> idCol = new TableColumn<>("ID");
        TableColumn<Gender, String> genderCol = new TableColumn<>("Gender");
        TableColumn<Gender, Void> actionsCol = new TableColumn<>("Actions");

        idCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getId()));

        genderCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getGender()));

        actionsCol.setCellFactory(column -> new TableCell<Gender, Void>() {
            private final Button updateButton = new Button("Update");
            private final Button removeButton = new Button("Remove");
            private final HBox buttonBox = new HBox(10);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {

                    buttonBox.setAlignment(Pos.CENTER);
                    buttonBox.getChildren().clear();
                    buttonBox.getChildren().addAll(updateButton, removeButton);

                    double buttonWidth = 100;
                    updateButton.setPrefWidth(buttonWidth);
                    removeButton.setPrefWidth(buttonWidth);

                    updateButton.setOnAction(e -> {
                        String oldId = getTableView().getItems().get(getIndex()).getId();
                        String oldGender = getTableView().getItems().get(getIndex()).getGender();

                        showUpdateGenderForm();
                        genderIdField.setText(oldId);
                        genderField.setText(oldGender);
                    });
                    updateButton.getStyleClass().add("update-button");

                    removeButton.setOnAction(e -> {
                        String id = getTableView().getItems().get(getIndex()).getId();

                        if(controller.genderInUse(id)) {
                            AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                            errorAlert.show(body, "Gender is in use!", Pos.TOP_CENTER);
                            return;
                        }

                        AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
                        confirmAlert.show(body, "Confirm to remove?", Pos.TOP_CENTER);
                        confirmAlert.setOnConfirm(() -> {
                            if (controller.removeGender(id)) {
                                AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                                successAlert.show(body, "Successfully removed gender.", Pos.TOP_CENTER);

                                //Refresh table content
                                controller.refreshGenderData();
                                genderTable.setItems(controller.getGenders());

                            } else {
                                AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                                errorAlert.show(body, "Error removing gender. Try again.", Pos.TOP_CENTER);
                                return;
                            }
                        });
                    });
                    removeButton.getStyleClass().add("remove-button");

                    setGraphic(buttonBox);
                }
            }
        });

        genderTable.getColumns().addAll(idCol, genderCol, actionsCol);

        TableUtils.addColToolTip(genderCol);

        genderTable.setItems(controller.getGenders());

        genderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private VBox buildGenderSection() {
        Label genderTitleLabel = new Label("GENDERS");

        HBox genderTitleBox = buildTableTitle(genderTitleLabel);

        Button addButton = new Button("+ Add Gender");
        addButton.setOnAction(e -> showAddGenderForm());
        addButton.getStyleClass().add("orange-button");

        HBox buttonBox = new HBox(addButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        buildGenderTable();

        genderTableBox = new VBox(genderTable);
        genderTableBox.setPrefHeight(300);
        genderTableBox.getStyleClass().add("display-box");

        ScrollPane tableScrollPane = new ScrollPane(genderTableBox);
        tableScrollPane.setFitToWidth(true);

        VBox genderSection = new VBox(10);
        genderSection.getChildren().addAll(genderTitleBox, buttonBox, tableScrollPane);

        return genderSection;
    }

    private GridPane buildCategoryForm() {
        Label idLabel = new Label("Category ID");
        idLabel.getStyleClass().add("form-header");

        Label categoryLabel = new Label("Category Name");
        categoryLabel.getStyleClass().add("form-header");

        Label genderLabel = new Label("Gender");
        genderLabel.getStyleClass().add("form-header");

        categoryIdField = new TextField();
        categoryIdField.textProperty().addListener((obs, oldval, newVal) -> {
            if (newVal.isEmpty()) {
                return;
            }

            if (!newVal.matches("^[A-Za-z]+$") || newVal.length() > 2) {
                categoryIdField.setText(oldval);
            }
        });

        categoryNameField = new TextField();
        categoryNameField.textProperty().addListener((obs, oldval, newVal) -> {
            if (newVal.isEmpty()) {
                return;
            }

            if (!newVal.matches("^[A-Za-z- ]+$")) {
                categoryNameField.setText(oldval);
            }
        });

        categoryGenderCombo = new ComboBox<>();
        categoryGenderCombo.setItems(controller.getGenders());
        categoryGenderCombo.setCellFactory(list -> new ListCell<>() {   //Output gender name only
            @Override
            protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getGender());
            }
        });

        categoryGenderCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getGender());
            }
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(30);
        grid.add(idLabel, 0, 0);
        grid.add(categoryLabel, 0, 1);
        grid.add(genderLabel, 0, 2);
        grid.add(categoryIdField, 1, 0);
        grid.add(categoryNameField, 1, 1);
        grid.add(categoryGenderCombo, 1, 2);

        return grid;
    }

    private void showAddCategoryForm() {
        Label titleLabel = new Label("Enter New Category Details");
        titleLabel.getStyleClass().add("title");

        GridPane grid = buildCategoryForm();

        Button addButton = new Button("Add");
        addButton.getStyleClass().add("orange-button");
        addButton.setOnAction(e -> {
            AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
            confirmAlert.show(body, "Confirm to add?", Pos.TOP_CENTER);
            confirmAlert.setOnConfirm(() -> {
                String categoryId = categoryIdField.getText().trim().toUpperCase();
                String categoryName = categoryNameField.getText().trim().toUpperCase();
                String genderId = categoryGenderCombo.getValue().getId();

                if (controller.addCategory(categoryId, categoryName, genderId)) {
                    AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                    successAlert.show(body, "Successfully add new category", Pos.TOP_CENTER);

                    refreshProductPage();
                } else {
                    AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                    errorAlert.show(body, "Error adding category. Try again.", Pos.TOP_CENTER);
                }
            });
        });

        VBox vbox = new VBox(20, titleLabel, grid, addButton);
        vbox.setAlignment(Pos.CENTER);

        categoryTableBox.getChildren().clear();
        categoryTableBox.getChildren().add(vbox);
    }


    private void showUpdateCategoryForm() {
        Label titleLabel = new Label("Enter New Category Details");
        titleLabel.getStyleClass().add("title");

        GridPane grid = buildCategoryForm();
        categoryIdField.setDisable(true);

        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("orange-button");
        updateButton.setOnAction(e -> {
            AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
            confirmAlert.show(body, "Confirm to update?", Pos.TOP_CENTER);
            confirmAlert.setOnConfirm(() -> {
                String categoryId = categoryIdField.getText();
                String newCategoryName = categoryNameField.getText().trim().toUpperCase();
                String newGenderId = categoryGenderCombo.getValue().getId();

                if (controller.updateCategory(categoryId, newCategoryName, newGenderId)) {
                    AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                    successAlert.show(body, "Successfully updated category", Pos.TOP_CENTER);

                    categoryIdField.setText("");
                    categoryNameField.setText("");
                    categoryGenderCombo.setValue(null);

                    refreshProductPage();
                } else {
                    AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                    errorAlert.show(body, "Error updating category. Try again.", Pos.TOP_CENTER);
                }
            });
        });

        VBox vbox = new VBox(20, titleLabel, grid, updateButton);
        vbox.setAlignment(Pos.CENTER);

        categoryTableBox.getChildren().clear();
        categoryTableBox.getChildren().add(vbox);
    }

    private void buildCategoryTable() {
        categoryTable = new TableView<>();

        TableColumn<Category, String> idCol = new TableColumn<>("ID");
        TableColumn<Category, String> categoryCol = new TableColumn<>("Category");
        TableColumn<Category, String> genderCol = new TableColumn<>("Gender");
        TableColumn<Category, Void> actionsCol = new TableColumn<>("Actions");

        idCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getId()));

        categoryCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCategoryName()));

        genderCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getGenderName()));

        actionsCol.setCellFactory(column -> new TableCell<Category, Void>() {
            private final Button updateButton = new Button("Update");
            private final Button removeButton = new Button("Remove");
            private final HBox buttonBox = new HBox(10);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Category category = getTableView().getItems().get(getIndex());

                    double buttonWidth = 100;
                    updateButton.setPrefWidth(buttonWidth);
                    removeButton.setPrefWidth(buttonWidth);

                    buttonBox.getStyleClass().add("action-box");
                    buttonBox.getChildren().clear();
                    buttonBox.getChildren().addAll(updateButton, removeButton);

                    updateButton.setOnAction(e -> {
                        showUpdateCategoryForm();

                        categoryIdField.setText(category.getId());
                        categoryNameField.setText(category.getCategoryName());
                        categoryGenderCombo.setValue(category.getGender());
                    });
                    updateButton.getStyleClass().add("update-button");

                    removeButton.setOnAction(e -> {
                        String id = getTableView().getItems().get(getIndex()).getId();

                        if (controller.categoryInUse(id)) {
                            AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                            errorAlert.show(body, "Category is in use!", Pos.TOP_CENTER);
                            return;
                        }

                        AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
                        confirmAlert.show(body, "Confirm to remove?", Pos.TOP_CENTER);
                        confirmAlert.setOnConfirm(() -> {
                             if (controller.removeCategory(id)) {
                                AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                                successAlert.show(body, "Successfully removed category.", Pos.TOP_CENTER);

                                //Refresh table content
                                controller.refreshCategoryData();
                                categoryTable.setItems(controller.getCategories());
                            } else {
                                AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                                errorAlert.show(body, "Error removing category. Try again.", Pos.TOP_CENTER);
                            }
                        });
                    });
                    removeButton.getStyleClass().add("remove-button");

                    setGraphic(buttonBox);
                }
            }
        });

        categoryTable.getColumns().addAll(idCol, categoryCol, genderCol, actionsCol);

        TableUtils.addColToolTip(categoryCol);
        TableUtils.addColToolTip(genderCol);

        categoryTable.setItems(controller.getCategories());

        categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private VBox buildCategorySection() {
        Label categoryTitleLabel = new Label("CATEGORIES");

        HBox categoryTitleBox = buildTableTitle(categoryTitleLabel);

        Button addButton = new Button("+ Add Category");
        addButton.getStyleClass().add("orange-button");
        addButton.setOnAction(e -> showAddCategoryForm());

        HBox buttonBox = new HBox(addButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        buildCategoryTable();

        categoryTableBox = new VBox(categoryTable);
        categoryTableBox.setPrefHeight(300);
        categoryTableBox.getStyleClass().add("display-box");

        ScrollPane tableScrollPane = new ScrollPane(categoryTableBox);
        tableScrollPane.setFitToWidth(true);

        VBox categorySection = new VBox(10);
        categorySection.getChildren().addAll(categoryTitleBox, buttonBox, tableScrollPane);

        return categorySection;
    }

    private void showGendersCategories() {
        VBox genderSection = buildGenderSection();

        VBox categorySection = buildCategorySection();

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        genderCategoryGrid = new GridPane();
        genderCategoryGrid.setHgap(50);
        genderCategoryGrid.add(genderSection, 0, 0);
        genderCategoryGrid.add(categorySection, 1, 0);

        genderCategoryGrid.getColumnConstraints().addAll(col1, col2);

        bodyVBox.getChildren().add(genderCategoryGrid);
        VBox.setMargin(genderCategoryGrid, new Insets(10, 22, 0, 22));
    }

    private void removeGendersCategories() {
        bodyVBox.getChildren().remove(genderCategoryGrid);
    }

    private void buildProductPage() {
        currentActiveView = ActiveView.PRODUCTS;

        setTitleLabel("PRODUCTS");

        removeSalesFilter();

        showButtonBox("+ Add Product");
        addButton.setOnAction(ae -> {
            backButton.setVisible(true);

            AdminProductUploader uploader = new AdminProductUploader(mainTableBox);
            uploader.setShowProductPage(() -> {
                refreshProductPage();

                AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                successAlert.show(body, "Successfully added product.", Pos.TOP_CENTER);
            });
            switchDisplayBox(uploader.show());
        });

        showGendersCategories();

        showProductTable();
    }

    private void buildOrderTable() {
        orderTable = new TableView<>();
        orderTable.getStyleClass().add("order-table");

        TableColumn<Order, Integer> orderIdCol = new TableColumn<>("ID");
        TableColumn<Order, Integer> userIdCol = new TableColumn<>("User ID");
        TableColumn<Order, String> usernameCol = new TableColumn<>("Username");
        TableColumn<Order, String> fullNameCol = new TableColumn<>("Full Name");
        TableColumn<Order, String> shipAddrCol = new TableColumn<>("Shipping Address");
        TableColumn<Order, String> orderDateCol = new TableColumn<>("Order Date");
        TableColumn<Order, Integer> totalItemsCol = new TableColumn<>("Total Items");
        TableColumn<Order, Double> totalPriceCol = new TableColumn<>("Total Price (RM)");
        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        TableColumn<Order, Void> actionsCol = new TableColumn<>("Actions");

        orderIdCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getOrderId()).asObject());

        userIdCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getUserId()).asObject());

        usernameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsername()));

        fullNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFullName()));

        shipAddrCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getShippingAddress()));

        orderDateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOrderDateString()));

        totalItemsCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getTotalItems()).asObject());

        totalPriceCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getTotalPrice()).asObject());

        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));

        TableUtils.addTwoDecimalFormatting(totalPriceCol);

        //Setting up status column style
        statusCol.setCellFactory(column -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    // Apply different styles based on status
                    if (item.equals("DONE")) {
                        setStyle("-fx-text-fill: #10A115;");
                    } else {
                        setStyle("-fx-text-fill: #F59E0B;");
                    }
                }
            }
        });

        actionsCol.setCellFactory(column -> new TableCell<Order, Void>() {
            private final Button setAsDoneButton = new Button("Set As DONE");
            private final Button viewDetailsButton = new Button("View Details");
            private final HBox buttonBox = new HBox(10);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());

                    double buttonWidth = 100;
                    viewDetailsButton.setPrefWidth(buttonWidth);
                    setAsDoneButton.setPrefWidth(buttonWidth);

                    buttonBox.getStyleClass().add("action-box");
                    buttonBox.setAlignment(Pos.CENTER);
                    buttonBox.getChildren().clear();
                    buttonBox.getChildren().addAll(viewDetailsButton, setAsDoneButton);
                    if(order.getStatus().equals("DONE")) {
                        setAsDoneButton.setDisable(true);
                    }

                    setAsDoneButton.setOnAction(e -> {
                        if(controller.setOrderAsDone(order.getOrderId())) {
                            AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                            successAlert.show(body, "Order set as done.", Pos.TOP_CENTER);

                            refreshStatCards();

                            refreshOrderPage();
                        } else {
                            AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                            errorAlert.show(body, "Error! Please try again.", Pos.TOP_CENTER);
                        }
                    });
                    setAsDoneButton.getStyleClass().add("set-done-button");

                    viewDetailsButton.setOnAction(e -> {
                        showOrderDetails(order);
                    });
                    viewDetailsButton.getStyleClass().add("view-button");

                    setGraphic(buttonBox);
                }
            }
        });

        setOrderColWidth(orderIdCol, 80);
        setOrderColWidth(fullNameCol, 150);
        setOrderColWidth(shipAddrCol, 200);
        setOrderColWidth(orderDateCol, 110);
        setOrderColWidth(totalPriceCol, 150);

        orderTable.getColumns().addAll(orderIdCol, userIdCol, usernameCol,
                fullNameCol, shipAddrCol, orderDateCol, totalItemsCol,
                totalPriceCol, statusCol, actionsCol
        );

        TableUtils.addColToolTip(usernameCol);
        TableUtils.addColToolTip(fullNameCol);
        TableUtils.addColToolTip(shipAddrCol);
        TableUtils.addColToolTip(orderDateCol);

        orderTable.setItems(controller.getOrders());

        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private <T> void setOrderColWidth(TableColumn<Order, T> column, int width) {
        column.setPrefWidth(width);
        column.setMaxWidth(width);
    }

    private void buildOrderPage() {
        setTitleLabel("ORDERS");

        hideAddButton();
        removeSalesFilter();

        buildOrderTable();

        mainTableBox.getChildren().clear();
        mainTableBox.getChildren().add(orderTable);

        removeGendersCategories();
    }

    private TableView<OrderItem> buildOrderItemTable(int orderId) {
        TableView<OrderItem> orderItemTable = new TableView<>();

        TableColumn<OrderItem, Integer> productIdCol = new TableColumn<>("Product ID");
        TableColumn<OrderItem, String> productNameCol = new TableColumn<>("Product Name");
        TableColumn<OrderItem, String> categoryCol = new TableColumn<>("Category");
        TableColumn<OrderItem, String> sizeCol = new TableColumn<>("Size");
        TableColumn<OrderItem, Integer> quantityCol = new TableColumn<>("Quantity");
        TableColumn<OrderItem, Double> subtotalCol = new TableColumn<>("Sub-Total (RM)");

        productIdCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getProductId()).asObject());

        productNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProductName()));

        categoryCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategory()));

        sizeCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProductSize()));

        quantityCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());

        subtotalCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getSubTotal()).asObject());

        orderItemTable.getColumns().addAll(
                productIdCol, productNameCol, categoryCol,
                sizeCol, quantityCol, subtotalCol);

        orderItemTable.setItems(controller.getOrderItems(orderId));

        orderItemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        return orderItemTable;
    }

    private void showOrderDetails(Order order) {
        Button closeButton = new Button("✕");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> body.getChildren().remove(detailBox));

        HBox closeHBox = new HBox(closeButton);
        closeHBox.setAlignment(Pos.TOP_RIGHT);

        String orderIdText = "Order ID: " + String.valueOf(order.getOrderId());
        Label orderIdLabel = new Label(orderIdText);
        orderIdLabel.getStyleClass().add("order-id");

        Label statusLabel = new Label(order.getStatus());
        statusLabel.getStyleClass().add("status");
        if(statusLabel.getText().equals("DONE")) {
            statusLabel.setStyle("-fx-text-fill: #10A115;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #F59E0B;");
        }

        BorderPane idStatusSection = new BorderPane();
        idStatusSection.setLeft(orderIdLabel);
        idStatusSection.setRight(statusLabel);

        Label customerDetailsLabel = new Label("Customer Details");
        customerDetailsLabel.getStyleClass().add("section-title");

        Region firstSeparator = new Region();
        firstSeparator.setStyle("-fx-background-color: #FE6C01");
        firstSeparator.setMaxHeight(2);

        HBox firstSepBox = new HBox(firstSeparator);
        HBox.setHgrow(firstSeparator, Priority.ALWAYS);
        firstSepBox.setPrefWidth(Double.MAX_VALUE);

        String userIdText = "User ID: " + String.valueOf(order.getUserId());
        Label userIdLabel = new Label(userIdText);

        String usernameText = "Username: " + order.getUsername();
        Label usernameLabel = new Label(usernameText);

        String fullNameText = "Full Name: " + order.getFullName();
        Label fullNameLabel = new Label(fullNameText);

        Label orderDetailsLabel = new Label("Order Details");
        orderDetailsLabel.getStyleClass().add("section-title");

        Region secondSeparator = new Region();
        secondSeparator.setStyle("-fx-background-color: #FE6C01");
        secondSeparator.setMaxHeight(2);

        HBox secondSepBox = new HBox(secondSeparator);
        HBox.setHgrow(secondSeparator, Priority.ALWAYS);
        secondSepBox.setPrefWidth(Double.MAX_VALUE);

        firstSeparator.setMinHeight(3);
        firstSeparator.setPrefHeight(3);
        firstSepBox.setMinHeight(3);
        firstSepBox.setMaxWidth(Double.MAX_VALUE); // make HBox stretch full width

        secondSeparator.setMinHeight(3);
        secondSeparator.setPrefHeight(3);
        secondSepBox.setMinHeight(3);
        secondSepBox.setMaxWidth(Double.MAX_VALUE);

        String shipAddrText = "Shipping Address: " + order.getShippingAddress();
        Label shipAddrLabel = new Label(shipAddrText);

        String orderDateText = "Order Date: " + order.getOrderDateString();
        Label orderDateLabel = new Label(orderDateText);

        String totalItemText = "Total Items: " + String.valueOf(order.getTotalItems());
        Label totalItemLabel = new Label(totalItemText);

        Label orderItemsLabel = new Label("Order Items:");

        TableView<OrderItem> orderItemTable = buildOrderItemTable(order.getOrderId());

        String totalText = "Total: " + showPrice(order.getTotalPrice());
        Label totalLabel = new Label(totalText);
        totalLabel.getStyleClass().add("total");

        int height = 710;
        int width = height * 3/2;

        detailBox = new VBox(5);
        detailBox.getStyleClass().add("detail-box");
        detailBox.setAlignment(Pos.TOP_LEFT);
        detailBox.setPrefHeight(height);
        detailBox.setMaxHeight(height);
        detailBox.setPrefWidth(width);
        detailBox.setMaxWidth(width);
        detailBox.getChildren().addAll(
                closeHBox, idStatusSection,
                customerDetailsLabel, firstSepBox,
                userIdLabel, usernameLabel, fullNameLabel,
                orderDetailsLabel, secondSepBox,
                shipAddrLabel, orderDateLabel, totalItemLabel,
                orderItemsLabel, orderItemTable,
                totalLabel
        );

        body.getChildren().add(detailBox);
        StackPane.setAlignment(detailBox, Pos.CENTER);
    }

    private void switchDisplayBox(Pane pane) {
        mainTableBox.getChildren().clear();
        mainTableBox.getChildren().add(pane);
    }

    private void showEmptyDisplayBox() {
        Label startLabel = new Label("Click a statistic box above to view data.");
        startLabel.getStyleClass().add("start-label");

        mainTableBox.getChildren().clear();
        mainTableBox.getChildren().add(startLabel);
    }

    private void refreshStatCards() {
        customerStatCard.update(String.valueOf(controller.getTotalCustomers()));
        adminStatCard.update(String.valueOf(controller.getTotalAdmins()));
        productStatCard.update(String.valueOf(controller.getTotalProducts()));
        orderStatCard.update(String.valueOf(controller.getTotalOrders()));
        revenueStatCard.update(String.format("%.2f", controller.getTotalRevenue()));
    }

    private void refreshProductPage() {
        controller.refreshProductData();
        controller.refreshGenderData();
        controller.refreshCategoryData();

        mainTableBox.getChildren().clear();
        showProductTable();

        genderTableBox.getChildren().clear();
        buildGenderTable();
        genderTableBox.getChildren().add(genderTable);

        categoryTableBox.getChildren().clear();
        buildCategoryTable();
        categoryTableBox.getChildren().add(categoryTable);

        refreshStatCards();
    }

    private void refreshOrderPage() {
        controller.refreshOrderData();

        buildOrderPage();

        refreshStatCards();
    }

    private void backToTable() {
        switch(currentActiveView) {
            case ActiveView.NONE -> showEmptyDisplayBox();
            case ActiveView.CUSTOMERS -> showCustomerTable();
            case ActiveView.ADMINS -> showAdminTable();
            case ActiveView.PRODUCTS -> showProductTable();
        }

        backButton.setVisible(false);
    }

    public Scene initialize() {
        controller.initializeAllData();

        GridPane cardGrid = buildCardGrid();

        StackPane topStackPane = new StackPane();
        topStackPane.getChildren().add(cardGrid);

        titleLabel = new Label("");

        titleBox = buildTableTitle(titleLabel);
        titleBox.setVisible(false);
        titleBox.setManaged(false);

        addButton.getStyleClass().add("orange-button");
        if (addBtnWasVisible) {
            addButton.setVisible(true);
        } else {
            addButton.setVisible(false);
        }

        FontIcon backIcon = new FontIcon("far-caret-square-left");
        backIcon.setIconSize(16);
        backIcon.setIconColor(Color.web("#A1A1A1"));
        backButton = new Button("Back To Table", backIcon);
        backButton.getStyleClass().add("back-button");
        backButton.setVisible(false);
        backButton.setOnAction(e -> backToTable());

        buttonPane = new BorderPane();
        buttonPane.setLeft(backButton);
        buttonPane.setRight(addButton);
        buttonPane.setPadding(new Insets(0, sidePad-15, 0, sidePad-15));

        mainTableBox.setAlignment(Pos.TOP_CENTER);
        mainTableBox.getStyleClass().add("display-box");
        showEmptyDisplayBox();

        ScrollPane mainTableScrollPane = new ScrollPane(mainTableBox);
        mainTableScrollPane.setPadding(new Insets(0));
        mainTableScrollPane.getStyleClass().add("display-box");
        mainTableScrollPane.setFitToWidth(true);
        mainTableScrollPane.setFitToHeight(true);
        mainTableScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        bodyVBox = new VBox(topStackPane, titleBox, buttonPane, mainTableScrollPane);
        bodyVBox.setAlignment(Pos.CENTER);
        bodyVBox.setPadding(new Insets(0, sidePad, 20, sidePad));
        VBox.setVgrow(mainTableBox, Priority.ALWAYS);
        VBox.setVgrow(mainTableScrollPane, Priority.ALWAYS);
        VBox.setMargin(titleBox, new Insets(10, 22, 10, 22));

        ScrollPane bodyScrollPane = new ScrollPane(bodyVBox);
        bodyScrollPane.setPadding(new Insets(0));
        bodyScrollPane.setFitToWidth(true);
        bodyScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        bodyScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        bodyScrollPane.setMinHeight(600);

        body = new StackPane();
        body.getChildren().add(bodyScrollPane);
        StackPane.setAlignment(cardGrid, Pos.TOP_CENTER);
        StackPane.setMargin(bodyScrollPane, new Insets(0, 0, 20, 0));

        root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(body);

        Scene scene = setScene(root, "admin-dash");

        scene.getStylesheets().addAll(
                getClass().getResource("/css/profile-page.css").toExternalForm(),
                getClass().getResource("/css/signup-page.css").toExternalForm()
        );

        return scene;
    }
}