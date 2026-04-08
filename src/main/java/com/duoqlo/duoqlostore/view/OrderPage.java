package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.DashboardController;
import com.duoqlo.duoqlostore.model.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class OrderCard extends VBox {
    private OrderDAO orderDAO = new OrderDAO();

    private ScrollPane contentPane;

    private Order order;
    private int orderId;

    private int cardWidth = 350;
    private int cardHeight = 800;

    public OrderCard(int orderId) {
        this.orderId = orderId;

        this.order = setOrder(orderId);

        create();
    }

    private ImageView getImageView(OrderItem orderItem) {
        ImageView imageView = new ImageView();

        int psId = orderItem.getProductSizeId();

        String imagePath = orderItem.getImagePath(psId);
        if(imagePath != null && !imagePath.isEmpty()) {
            Image image = getFirstImage(imagePath);
            if(image != null) {
                imageView.setImage(image);
            } else {
                System.err.println("Image not found (Source: OrderCard)");
            }
        } else {
            System.err.println("Image Path not found! (Source: OrderCard)");
        }

        return imageView;
    }

    private Image getFirstImage(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Directory does not exist: " + directoryPath);
            return null;
        }

        // Get all image files
        File[] imageFiles = directory.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".jpg") ||
                    lower.endsWith(".jpeg") ||
                    lower.endsWith(".png") ||
                    lower.endsWith(".gif");
        });

        if (imageFiles == null || imageFiles.length == 0) {
            System.err.println("No images found in directory: " + directoryPath);
            return null;
        }

        // Get the first image
        File firstImage = imageFiles[0];

        // Load and return the image
        return new Image(firstImage.toURI().toString());
    }

    private Order setOrder(int orderId) {
        return orderDAO.getFullOrder(orderId);
    }

    private Region buildLine() {
        Region line = new Region();
        line.setStyle("-fx-background-color: #FE6C01;");
        line.setPrefHeight(5);
        line.setMaxHeight(5);
        line.setMinHeight(5);
        line.setPrefWidth(Double.MAX_VALUE);

        return line;
    }

    private VBox buildHeader() {
        Label orderDateLabel = new Label("Order Date: ");
        orderDateLabel.getStyleClass().add("orderdate");

        Label orderDateValue = new Label(order.getOrderDateString());
        orderDateValue.getStyleClass().add("orderdate-value");

        HBox orderDateBox = new HBox(orderDateLabel, orderDateValue);
        HBox.setMargin(orderDateLabel, new Insets(0, 5, 0, 0));

        Region line = buildLine();

        VBox headerBox = new VBox(orderDateBox, line);

        return headerBox;
    }

    private HBox buildOrderRow(OrderItem orderItem) {
        ImageView productImageView = getImageView(orderItem);
        productImageView.setFitWidth(100);
        productImageView.setPreserveRatio(true);

        Label nameLabel = new Label(orderItem.getProductName());
        nameLabel.getStyleClass().add("name");

        Label quantityLabel = new Label("x"+orderItem.getQuantity());
        quantityLabel.getStyleClass().add("quantity");

        BorderPane nameQuantityPane = new BorderPane();
        nameQuantityPane.setLeft(nameLabel);
        nameQuantityPane.setRight(quantityLabel);

        Label categoryLabel = new Label(orderItem.getCategory());
        categoryLabel.getStyleClass().add("category");

        Label sizeLabel = new Label("Size: ");
        sizeLabel.getStyleClass().add("size");

        Label sizeValueLabel = new Label(orderItem.getSize());
        sizeValueLabel.getStyleClass().add("size-value");
        HBox sizeBox = new HBox(sizeLabel, sizeValueLabel);

        Label subTotalValue = new Label(String.format("RM %.2f", orderItem.getSubTotal()));
        subTotalValue.getStyleClass().add("subtotal-value");

        HBox subTotalBox = new HBox(subTotalValue);
        subTotalBox.setAlignment(Pos.BOTTOM_RIGHT);

        VBox contentBox = new VBox(
                nameQuantityPane,
                categoryLabel,
                sizeBox,
                subTotalBox
        );
        VBox.setVgrow(subTotalBox, Priority.ALWAYS);

        HBox orderRow = new HBox(7, productImageView, contentBox);
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        return orderRow;
    }

    private ScrollPane buildContentBox() {
        VBox contentBox = new VBox(10);
        for (OrderItem orderItem: order.getOrderItemList()) {
            contentBox.getChildren().add(buildOrderRow(orderItem));
        }
        contentBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        this.contentPane = scrollPane;

        return scrollPane;
    }

    private VBox buildFooter() {
        Region line = buildLine();

        Label totalLabel = new Label("Order Total: ");
        totalLabel.getStyleClass().add("total");

        Label totalValue = new Label(String.format("RM %.2f", order.getTotalPrice()));
        totalValue.getStyleClass().add("total-value");

        Label taxLabel = new Label(" (Incl. tax)");
        taxLabel.getStyleClass().add("tax");

        HBox footerLabelBox = new HBox(totalLabel, totalValue, taxLabel);

        VBox footerBox = new VBox(line, footerLabelBox);

        return footerBox;
    }

    private void create() {
        VBox headerBox = buildHeader();

        ScrollPane contentPane = buildContentBox();

        VBox footerBox = buildFooter();

        this.setPadding(new Insets(15));

        this.setMinWidth(cardWidth);
        this.setPrefWidth(cardWidth);
        this.setMaxWidth(cardWidth);

        this.setMinHeight(cardHeight);
        this.setPrefHeight(cardHeight);
        this.setMaxHeight(cardHeight);

        this.getStyleClass().add("order-card");

        this.getChildren().addAll(headerBox, contentPane, footerBox);

        VBox.setVgrow(contentPane, Priority.ALWAYS);
        VBox.setMargin(contentPane, new Insets(15, 0, 15, 0));
    }
}

public class OrderPage extends BasePage {
    private DashboardController controller;

    private ComboBox<String> sortCombo;
    private HBox sortBox;
    private ChangeListener<String> sortComboListener;

    @Override
    protected User getCurrentUser() {
        return controller.getUser();
    }

    public void setController(DashboardController controller) {
        this.controller = controller;
    }

    private StackPane buildHeader() {
        Label label = new Label("ORDERS");
        label.getStyleClass().add("order-label");
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);

        FontIcon cartIcon = new FontIcon("fas-shopping-cart");
        cartIcon.setIconSize(iconSize);
        cartIcon.setIconColor(themeColor);
        Button cartButton = new Button("", cartIcon);
        cartButton.setOnAction(e -> controller.openCartPage());

        HBox actionBox = new HBox(10);
        actionBox.setMinWidth(300);
        actionBox.setPrefWidth(300);
        actionBox.setMaxWidth(300);
        actionBox.getChildren().add(cartButton);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        StackPane header = createHeaderBox(labelBox, actionBox);

        return header;
    }

    private HBox buildCardSection() {
        HBox cardBox = new HBox(30);

        for (Order order: controller.getOrders()) {
            cardBox.getChildren().add(new OrderCard(order.getOrderId()));
        }

        cardBox.setPadding(new Insets(30));

        return cardBox;
    }

    public Scene initialize(){
        HBox cardSection = buildCardSection();

        VBox bodyBox = new VBox();
        bodyBox.getChildren().add(cardSection);
        VBox.setMargin(cardSection, new Insets(10, 0, 10, 0));

        ScrollPane contentPane = new ScrollPane(bodyBox);
        contentPane.setFitToWidth(true);
        contentPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        contentPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Hidden by default

        StackPane body = new StackPane();
        body.getChildren().add(contentPane);

        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(body);
        root.setOnMouseClicked(e -> root.requestFocus()); //Allow unfocus on TextField

        Scene scene = new Scene(root,
                Screen.getPrimary().getVisualBounds().getWidth(),
                Screen.getPrimary().getVisualBounds().getHeight());

        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/css/order-page.css")
                ).toExternalForm()
        );

        return scene;
    }
}
