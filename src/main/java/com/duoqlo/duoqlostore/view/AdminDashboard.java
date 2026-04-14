package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.AdminDashController;
import com.duoqlo.duoqlostore.controller.AdminProductUploader;
import com.duoqlo.duoqlostore.controller.Navigator;
import com.duoqlo.duoqlostore.controller.ProfileController;
import com.duoqlo.duoqlostore.model.Category;
import com.duoqlo.duoqlostore.model.Gender;
import com.duoqlo.duoqlostore.model.Product;
import com.duoqlo.duoqlostore.model.User;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

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
                -fx-font-size: 12;
                """);

        valueLabel = new Label(value);
        valueLabel.setStyle("""
                -fx-text-fill: black;
                -fx-font-size: 24;
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
        StackPane.setMargin(contentBox, new Insets(0, 0, 3, 0));

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

    private <T> void centerAlign(TableColumn<User, T> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }

                setAlignment(Pos.CENTER);
            }
        });
    }

    private void build() {
        // Set up cell value factories
        idCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getId()).asObject());

        usernameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsername()));

        fullNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFirstName() + " " + data.getValue().getLastName()));

        emailCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail()));

        addressCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFullAddress()));

        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getIsActive() == 1 ? "ACTIVE" : "INACTIVE"));

        //Center allign columns
        centerAlign(idCol);
        centerAlign(usernameCol);
        centerAlign(fullNameCol);
        centerAlign(emailCol);
        centerAlign(statusCol);
        centerAlign(actionsCol);

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

                    // Delete button action
                    deactivateButton.setOnAction(e -> {
                        //Show confirmation alert
                        AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
                        confirmAlert.show(body, "Confirm to deactivate?", Pos.TOP_CENTER);
                        confirmAlert.setOnConfirm(() -> {
                            if (controller.deactivateUser(user.getId())) {
                                //Refresh table
                                refresh();

                                //Show success alert
                                AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                                successAlert.show(body, "Deactivated successfully", Pos.TOP_CENTER);
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

        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
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

    private <T> void centerAlign(TableColumn<Product, T> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }

                setAlignment(Pos.CENTER);
            }
        });
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

        //Center allign columnns
        centerAlign(idCol);
        centerAlign(statusCol);

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

        setCompactCol(idCol, 50);
        setCompactCol(nameCol, 200);
        setCompactCol(statusCol, 110);
    }

    private <T> void setCompactCol(TableColumn<Product, T> column, int width) {
        column.setPrefWidth(width);
        column.setMaxWidth(width);
    }
}

public class AdminDashboard extends ApplicationPage {
    private AdminDashController controller;
    private AlertMsg alert;

    private StatCard userStatCard;
    private StatCard adminStatCard;
    private StatCard productStatCard;
    private StatCard orderStatCard;

    private Button addButton = new Button();
    private VBox mainTableBox = new VBox();

    private VBox genderTableBox = new VBox();
    private TableView<Gender> genderTable;
    private TextField genderIdField;
    private TextField genderField;

    private VBox categoryTableBox = new VBox();
    private TableView<Category> categoryTable;
    private TextField categoryIdField;
    private TextField categoryNameField;
    private ComboBox<Gender> categoryGenderCombo;

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

        //Logo button
        Button logoButton = new Button();
        logoButton.setGraphic(logoView);
        logoButton.getStyleClass().add("logo-button");
        logoButton.setOnAction(e -> {
            Navigator.goTo(this.initialize());
        });

        Label label = new Label("ADMIN DASHBOARD");
        label.getStyleClass().add("admindash-label");
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);

        labelBox.setMaxWidth(Region.USE_PREF_SIZE);

        StackPane header = new StackPane(); //Button-to-Button space
        header.getStyleClass().add("header");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setPrefHeight(10);
        header.setPadding(new Insets(20)); //Space between children edge and HBox edge

        header.getChildren().addAll(logoButton, labelBox);

        StackPane.setAlignment(logoButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(labelBox, Pos.CENTER);

        StackPane.setMargin(logoButton, new Insets(0, 0, 0, sidePad));

        return header;
    }

    private void showAddButton(String buttonText) {
        addButton.setText(buttonText);
        addButton.setVisible(true);
        addBtnWasVisible = true;
    }

    private GridPane buildCardGrid() {
        userStatCard = new StatCard("Total Users", String.valueOf(controller.getTotalUsers()));
        userStatCard.setOnMouseClicked(e -> {
            showAddButton("+ Add User");
            addButton.setOnAction(ae -> {
                SignUpPage signUpPage = new SignUpPage();
                signUpPage.setIsAdminMode();

                signUpPage.setBackToAdminDash(() -> {
                    Navigator.goTo(this.initialize());

                    alert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                    alert.show(body, "New user added.", Pos.TOP_CENTER);
                });

                VBox content = signUpPage.getContentForAdmin();

                bodyVBox.getChildren().clear();
                bodyVBox.getChildren().add(content);
            });
            showUserTable();
        });

        adminStatCard = new StatCard("Total Admins", String.valueOf(controller.getTotalAdmins()));
        adminStatCard.setOnMouseClicked(e -> {
            showAddButton("+ Add Admin");
            addButton.setOnAction(ae -> {
                SignUpPage signUpPage = new SignUpPage();
                signUpPage.setIsAdminMode();

                signUpPage.setBackToAdminDash(() -> {
                    Navigator.goTo(this.initialize());

                    alert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                    alert.show(body, "New admin added.", Pos.TOP_CENTER);
                });

                VBox content = signUpPage.getContentForAdmin();

                bodyVBox.getChildren().clear();
                bodyVBox.getChildren().add(content);
            });
            showAdminTable();
        });

        productStatCard = new StatCard("Total Products", String.valueOf(controller.getTotalProducts()));
        productStatCard.setOnMouseClicked(e -> {
            showGendersCategories();
            showAddButton("+ Add Product");
            addButton.setOnAction(ae -> {
                AdminProductUploader uploader = new AdminProductUploader(mainTableBox);
                uploader.setShowProductPage(() -> {
                    refreshProductPage();

                    AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                    successAlert.show(body, "Successfully added product.", Pos.TOP_CENTER);
                });
                switchDisplayBox(uploader.show());
            });
            showProductTable();
        });

        orderStatCard = new StatCard("Total Orders", String.valueOf(controller.getTotalOrders()));

        GridPane cardGrid = new GridPane();
        cardGrid.add(userStatCard, 0, 0);
        cardGrid.add(adminStatCard, 1, 0);
        cardGrid.add(productStatCard, 2, 0);
        cardGrid.add(orderStatCard, 3, 0);

        cardGrid.setPadding(new Insets(30, 35, 15, 35));
        cardGrid.setAlignment(Pos.CENTER);
        cardGrid.setHgap(80);
        cardGrid.setMaxHeight(Region.USE_PREF_SIZE);

        //Add column constraints
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(10);
            cardGrid.getColumnConstraints().add(col);
        }

        return cardGrid;
    }

    private void switchDisplayBox(Pane pane) {
        mainTableBox.getChildren().clear();
        mainTableBox.getChildren().add(pane);
    }

    private void showEmptyDisplayBox() {
        Label clickButtonLabel = new Label("Click a statistic box above to view data.");
        clickButtonLabel.getStyleClass().add("click-button");

        mainTableBox.getChildren().clear();
        mainTableBox.getChildren().add(clickButtonLabel);
    }

    private void showUserTable() {
        TableView<User> userTable = new UserTableView(this.controller, body, root);

        // Get users from database
        ObservableList<User> users = controller.getUsers();

        userTable.setItems(users);

        mainTableBox.getChildren().clear();
        mainTableBox.getChildren().add(userTable);
        VBox.setVgrow(userTable, Priority.ALWAYS);
    }

    private void showAdminTable() {
        TableView<User> adminTable = new UserTableView(this.controller, body, root);

        ObservableList<User> admins = controller.getAdmins();

        adminTable.setItems(admins);

        mainTableBox.getChildren().clear();
        mainTableBox.getChildren().add(adminTable);
        VBox.setVgrow(adminTable, Priority.ALWAYS);
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

    private void refreshStatCards() {
        userStatCard.update(String.valueOf(controller.getTotalUsers()));
        adminStatCard.update(String.valueOf(controller.getTotalAdmins()));
        productStatCard.update(String.valueOf(controller.getTotalProducts()));
        orderStatCard.update(String.valueOf(controller.getTotalOrders()));
    }

    private GridPane buildGenderForm() {
        Label idLabel = new Label("Gender ID");
        idLabel.getStyleClass().add("header");

        Label genderLabel = new Label("Gender");
        genderLabel.getStyleClass().add("header");

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

    private TableView<Gender> buildGenderTable() {
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

                        AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
                        confirmAlert.show(body, "Confirm to remove?", Pos.TOP_CENTER);
                        confirmAlert.setOnConfirm(() -> {
                            if (controller.genderInUse(id)) {
                                AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                                errorAlert.show(body, "Gender is in use!", Pos.TOP_CENTER);
                                return;
                            } else if (controller.removeGender(id)) {
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

        ObservableList<Gender> genders = controller.getGenders();

        genderTable.setItems(genders);

        genderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        return genderTable;
    }

    private VBox buildGenderSection() {
        Label genderTitle = new Label("Genders");
        genderTitle.getStyleClass().add("title");

        HBox genderTitleBox = new HBox(genderTitle);
        genderTitleBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("+ Add Gender");
        addButton.setOnAction(e -> showAddGenderForm());
        addButton.getStyleClass().add("orange-button");

        HBox buttonBox = new HBox(addButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        genderTableBox = new VBox(buildGenderTable());
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
        idLabel.getStyleClass().add("header");

        Label categoryLabel = new Label("Category Name");
        categoryLabel.getStyleClass().add("header");

        Label genderLabel = new Label("Gender");
        genderLabel.getStyleClass().add("header");

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


    private void showUpdateCategoryrForm() {
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

    private TableView<Category> buildCategoryTable() {
        TableView<Category> categoryTable = new TableView<>();

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

                    buttonBox.getChildren().clear();
                    buttonBox.getChildren().addAll(updateButton, removeButton);

                    updateButton.setOnAction(e -> {
                        showUpdateCategoryrForm();

                        categoryIdField.setText(category.getId());
                        categoryNameField.setText(category.getCategoryName());
                        categoryGenderCombo.setValue(category.getGender());
                    });
                    updateButton.getStyleClass().add("update-button");

                    removeButton.setOnAction(e -> {
                        String id = getTableView().getItems().get(getIndex()).getId();

                        AlertMsg confirmAlert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
                        confirmAlert.show(body, "Confirm to remove?", Pos.TOP_CENTER);
                        confirmAlert.setOnConfirm(() -> {
                            if (controller.categoryInUse(id)) {
                                AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                                errorAlert.show(body, "Category is in use!", Pos.TOP_CENTER);
                                return;
                            } else if (controller.removeCategory(id)) {
                                AlertMsg successAlert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                                successAlert.show(body, "Successfully removed category.", Pos.TOP_CENTER);

                                //Refresh table content
                                controller.refreshCategoryData();
                                categoryTable.setItems(controller.getCategories());

                            } else {
                                AlertMsg errorAlert = new AlertMsg(AlertMsg.AlertMsgType.ERROR);
                                errorAlert.show(body, "Error removing category. Try again.", Pos.TOP_CENTER);
                                return;
                            }
                        });
                    });
                    removeButton.getStyleClass().add("remove-button");

                    setGraphic(buttonBox);
                }
            }
        });

        categoryTable.getColumns().addAll(idCol, categoryCol, genderCol, actionsCol);

        ObservableList<Category> categories = controller.getCategories();
        categoryTable.setItems(categories);

        categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        return categoryTable;
    }

    private VBox buildCategorySection() {
        Label categoryTitle = new Label("Categories");
        categoryTitle.getStyleClass().add("title");

        HBox categoryTitleBox = new HBox(categoryTitle);
        categoryTitleBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("+ Add Category");
        addButton.getStyleClass().add("orange-button");
        addButton.setOnAction(e -> showAddCategoryForm());

        HBox buttonBox = new HBox(addButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        categoryTableBox = new VBox(buildCategoryTable());
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

        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.add(genderSection, 0, 0);
        grid.add(categorySection, 1, 0);

        grid.getColumnConstraints().addAll(col1, col2);

        bodyVBox.getChildren().add(grid);
        VBox.setMargin(grid, new Insets(10, 22, 0, 22));
    }

    private void refreshProductPage() {
        controller.initializeAllData();

        mainTableBox.getChildren().clear();
        showProductTable();

        genderTableBox.getChildren().clear();
        genderTableBox.getChildren().add(buildGenderTable());

        categoryTableBox.getChildren().clear();
        categoryTableBox.getChildren().add(buildCategoryTable());

        refreshStatCards();
    }

    public Scene initialize() {
        controller.initializeAllData();

        GridPane cardGrid = buildCardGrid();

        addButton.getStyleClass().add("orange-button");
        if (addBtnWasVisible) {
            addButton.setVisible(true);
        } else {
            addButton.setVisible(false);
        }

        HBox addHBox = new HBox(addButton);
        addHBox.setAlignment(Pos.CENTER_RIGHT);
        addHBox.setPadding(new Insets(0, sidePad+22, 0, 0));

        mainTableBox.setAlignment(Pos.CENTER);
        mainTableBox.getStyleClass().add("display-box");
        showEmptyDisplayBox();

        ScrollPane mainTableScrollPane = new ScrollPane(mainTableBox);
        mainTableScrollPane.setPadding(new Insets(0));
        mainTableScrollPane.getStyleClass().add("display-box");
        mainTableScrollPane.setFitToWidth(true);
        mainTableScrollPane.setFitToHeight(true);
        mainTableScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        bodyVBox = new VBox(cardGrid, addHBox, mainTableScrollPane);
        bodyVBox.setAlignment(Pos.CENTER);
        bodyVBox.setPadding(new Insets(0, sidePad, 20, sidePad));
        VBox.setVgrow(mainTableBox, Priority.ALWAYS);
        VBox.setVgrow(mainTableScrollPane, Priority.ALWAYS);

        ScrollPane bodyScrollPane = new ScrollPane(bodyVBox);
        bodyScrollPane.setPadding(new Insets(0));
        bodyScrollPane.setFitToWidth(true);
        bodyScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

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