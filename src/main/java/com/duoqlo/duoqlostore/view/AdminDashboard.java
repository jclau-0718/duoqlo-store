package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.AdminDashController;
import com.duoqlo.duoqlostore.controller.Navigator;
import com.duoqlo.duoqlostore.controller.ProfileController;
import com.duoqlo.duoqlostore.model.User;
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

        Label valueLabel = new Label(value);
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
}

public class AdminDashboard extends ApplicationPage {
    private AdminDashController controller = new AdminDashController();
    private AlertMsg alert;

    private Button insertButton = new Button();
    private VBox displayBox = new VBox();
    private VBox bodyVBox;
    private StackPane body;
    private BorderPane root;

    private boolean insertBtnWasVisible = false;

    private int sidePad = 35;

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

    private void showInsertButton(String buttonText) {
        insertButton.setText(buttonText);
        insertButton.setVisible(true);
        insertBtnWasVisible = true;
    }

    private GridPane buildCardGrid() {
        VBox userStatCard = new StatCard("Total Users", String.valueOf(controller.getTotalUsers()));
        userStatCard.setOnMouseClicked(e -> {
            insertButton.setText("+ Insert User");
            insertButton.setVisible(true);
            insertBtnWasVisible = true;
            insertButton.setOnAction(ae -> {
                SignUpPage signUpPage = new SignUpPage();

                signUpPage.setBackToAdminDash(() -> {
                    Navigator.goTo(this.initialize());

                    alert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                    alert.show(body, "New user inserted.", Pos.TOP_CENTER);
                });

                VBox content = signUpPage.getContentForAdmin();

                bodyVBox.getChildren().clear();
                bodyVBox.getChildren().add(content);
            });
            buildUserTable();
        });

        VBox adminStatCard = new StatCard("Total Admins", String.valueOf(controller.getTotalAdmins()));
        VBox productStatCard = new StatCard("Total Products", String.valueOf(controller.getTotalProducts()));
        VBox orderStatCard = new StatCard("Total Orders", String.valueOf(controller.getTotalOrders()));

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

    private void buildUserTable() {
        TableView<User> table = new TableView<>();
        table.setPrefHeight(1000);

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        TableColumn<User, String> fullNameCol = new TableColumn<>("Full Name");
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        TableColumn<User, String> addressCol = new TableColumn<>("Address");
        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        TableColumn<User, Void> actionCol = new TableColumn<>("Actions");

        // Set up cell value factories
        idCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());

        usernameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));

        fullNameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFirstName() + " " + data.getValue().getLastName()));

        emailCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        addressCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFullAddress()));

        statusCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getIsActive() == 1 ? "Active" : "Inactive"));

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
                    if ("Active".equals(item)) {
                        setStyle("""
                    -fx-text-fill: #10A115;
                    """);
                    } else {
                        setStyle("""
                    -fx-text-fill: #D32F2F;
                    """);
                    }
                }
            }
        });

        actionCol.setCellFactory(col -> new TableCell<User, Void>() {
                    private final Button updateButton = new Button("Update");
                    private final Button deactivateButton = new Button("Deactivate");
                    private final Button reactivateButton = new Button("Reactivate");
                    private final HBox buttons = new HBox(10);

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            User user = getTableView().getItems().get(getIndex());

                            buttons.getChildren().clear();

                            buttons.getChildren().add(updateButton);

                            if (user.getIsActive() == 0) { //User is inactive
                                buttons.getChildren().add(reactivateButton);
                            } else {
                                buttons.getChildren().add(deactivateButton);
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
                                alert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
                                alert.show(body, "Confirm to deactivate?", Pos.CENTER);
                                alert.setOnConfirm(() -> {
                                    if (controller.deactivateUser(user.getId())) {
                                        //Refresh table
                                        table.refresh();

                                        //Show success alert
                                        alert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                                        alert.show(body, "Deactivated successfully", Pos.CENTER);
                                    }
                                });
                            });
                            deactivateButton.getStyleClass().add("deactivate-button");

                            reactivateButton.setOnAction(e -> {
                                alert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);
                                alert.show(body, "Confirm to reactivate?", Pos.CENTER);
                                alert.setOnConfirm(() -> {
                                    if (controller.reactivateUser(user.getId())) {
                                        //Refresh table
                                        table.refresh();

                                        //Show success alert
                                        alert = new AlertMsg(AlertMsg.AlertMsgType.SUCCESS);
                                        alert.show(body, "Reactivated successfully", Pos.CENTER);
                                    }
                                });
                            });
                            reactivateButton.getStyleClass().add("reactivate-button");

                            setGraphic(buttons);
                        }
                    }
                });

        // Get users from database (you need to implement this in UserDAO)
        ObservableList<User> users = controller.getUsers();

        table.setItems(users);
        table.getColumns().addAll(idCol, usernameCol, fullNameCol,
                emailCol, addressCol, statusCol, actionCol);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        displayBox.getChildren().clear();
        displayBox.getChildren().add(table);
    }

    public Scene initialize() {
        controller.retrieveAllData();

        GridPane cardGrid = buildCardGrid();

        insertButton.getStyleClass().add("insert-button");
        if (insertBtnWasVisible) {
            insertButton.setVisible(true);
        } else {
            insertButton.setVisible(false);
        }

        HBox insertHBox = new HBox(insertButton);
        insertHBox.setAlignment(Pos.CENTER_RIGHT);
        insertHBox.setPadding(new Insets(0, sidePad+22, 0, 0));

        displayBox.getStyleClass().add("display-box");

        ScrollPane scrollPane = new ScrollPane(displayBox);
        scrollPane.setPadding(new Insets(0));
        scrollPane.getStyleClass().add("display-box");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        bodyVBox = new VBox(cardGrid, insertHBox, scrollPane);
        bodyVBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(displayBox, Priority.ALWAYS);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        VBox.setMargin(scrollPane, new Insets(0, sidePad, 20, sidePad));

        body = new StackPane();
        body.getChildren().add(bodyVBox);
        StackPane.setAlignment(cardGrid, Pos.TOP_CENTER);

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
