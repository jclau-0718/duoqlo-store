package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.Navigator;
import com.duoqlo.duoqlostore.controller.OrderController;
import com.duoqlo.duoqlostore.model.*;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class OrderCard extends VBox {
    private Stage stage;

    private Order order;
    private User user;
    private ExecutorService executor;

    private int cardWidth = 350;
    private int cardHeight = 750    ;

    private Runnable cancelOrder;

    public OrderCard(Order order, ExecutorService executor) {
        this.order = order;
        this.executor = executor;

        create();
    }

    public void setUser(User user) { this.user = user; }

    public void setStage(Stage stage) { this.stage = stage; }

    public void setCancelOrder(Runnable cancelOrder) {
        this.cancelOrder = cancelOrder;
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
                    lower.endsWith(".png");
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

    private void loadImageAsync(ImageView imageView, String path) {
        Task<Image> task = new Task<>() {
            @Override
            protected Image call() {
                return getFirstImage(path);
            }
        };

        task.setOnSucceeded(e -> {
            Image img = task.getValue();
            if (img != null) {
                imageView.setImage(img);
            }
        });

        executor.submit(task);
    }

    private ImageView getImageView(OrderItem orderItem) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        int psId = orderItem.getProductSizeId();

        String imagePath = orderItem.getImagePath(psId);
        if(imagePath != null && !imagePath.isEmpty()) {
            loadImageAsync(imageView, imagePath);
        } else {
            System.out.println("error");
        }

        return imageView;
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

        Label statusLabel = new Label(order.getStatus());
        statusLabel.getStyleClass().add("status");
        if(statusLabel.getText().equals("DONE")) {
            statusLabel.setStyle("-fx-text-fill: #10A115;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #F59E0B;");
        }

        BorderPane dateStatusPane = new BorderPane();
        dateStatusPane.setLeft(orderDateBox);
        dateStatusPane.setRight(statusLabel);

        VBox headerBox = new VBox(dateStatusPane, line);

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

        return scrollPane;
    }

    private VBox buildFooter() {
        Region line = buildLine();

        Label shippingFeeLabel = new Label("Shipping Fee: ");
        shippingFeeLabel.getStyleClass().add("ship-fee");

        Label shippingFeeValue = new Label("RM 50");
        shippingFeeValue.getStyleClass().add("ship-fee-value");

        Label totalLabel = new Label("Order Total: ");
        totalLabel.getStyleClass().add("total");

        Label totalValue = new Label(String.format("RM %.2f", order.getTotalPrice()));
        totalValue.getStyleClass().add("total-value");

        HBox footerShipFeeBox = new HBox(shippingFeeLabel, shippingFeeValue);
        HBox footerTotalBox = new HBox(totalLabel, totalValue);

        Button downloadButton = new PrimaryButton("Download Receipt");
        downloadButton.setOnAction(e -> {
            ReceiptExporter exporter = new ReceiptExporter(user ,order);
            exporter.export(stage);
        });

        Button cancelButton = new SecondaryButton("Cancel Order");
        cancelButton.setOnAction(e -> cancelOrder.run());

        HBox buttonBox = new HBox(8);
        buttonBox.getChildren().add(downloadButton);
        buttonBox.setAlignment(Pos.CENTER);

        if(order.getStatus().equals("PENDING")) {
            buttonBox.getChildren().add(cancelButton);
        }

        VBox footerBox = new VBox();
        footerBox.getChildren().addAll(line, footerShipFeeBox, footerTotalBox, buttonBox);

        return footerBox;
    }

    private void create() {
        VBox headerBox = buildHeader();

        ScrollPane contentPane = buildContentBox();

        VBox footerBox = buildFooter();

        setPadding(new Insets(15));

        setMinWidth(cardWidth);
        setPrefWidth(cardWidth);
        setMaxWidth(cardWidth);

        setMinHeight(cardHeight);
        setPrefHeight(cardHeight);
        setMaxHeight(cardHeight);

        getStyleClass().add("order-card");

        getChildren().addAll(headerBox, contentPane, footerBox);

        VBox.setVgrow(contentPane, Priority.ALWAYS);
        VBox.setMargin(contentPane, new Insets(15, 0, 8, 0));
        VBox.setVgrow(this, Priority.ALWAYS);
    }
}

public class OrderPage extends UserPage {
    private OrderController controller;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private StackPane body;

    private HBox cardSection;

    public OrderPage(OrderController controller) {
        this.controller = controller;
    }

    @Override
    public User getUser() { return controller.getUser(); }

    @Override
    public void openCartPage() {
        if(!controller.openCartPage()) {
            AlertMsg errorAlert = new AlertMsg(AlertType.ERROR);
            errorAlert.show(body, "Unable to fetch cart. Please try again.", Pos.TOP_CENTER);
        }
    }

    @Override
    public void openOrdersPage() {
        Navigator.goTo(this.initialize());
    }

    @Override
    public void openProfilePage() {
        controller.openProfilePage();
    }

    private HBox buildHeader() {
        Label label = new Label("ORDERS");
        label.getStyleClass().add("page-label");
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);

        HBox actionBox = new HBox(10);
        actionBox.setMinWidth(300);
        actionBox.setPrefWidth(300);
        actionBox.setMaxWidth(300);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        header = createHeaderBox(labelBox, false);

        return header;
    }

    private HBox buildOrderEmptyBox() {
        Label emptyLabel = new Label("No orders found");
        emptyLabel.getStyleClass().add("no-orders");

        HBox emptyLabelBox = new HBox(emptyLabel);
        emptyLabelBox.setAlignment(Pos.TOP_CENTER);
        HBox.setMargin(emptyLabel, new Insets(10, 0, 0, 0));

        return emptyLabelBox;
    }

    private void cancelOrder(int orderId) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return controller.orderCancelled(orderId);
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                loadOrdersAsync();
            }
        });

        executor.submit(task);
    }

    private void loadOrdersAsync() {
        Task<List<Order>> task = new Task<>() {
            @Override
            protected List<Order> call() {
                return controller.getOrders();
            }
        };

        task.setOnSucceeded(e -> {
            List<Order> orders = task.getValue();

            cardSection.getChildren().clear();

            if (orders != null && !orders.isEmpty()) {
                for (Order order : orders) {
                    Stage stage = (Stage) body.getScene().getWindow();

                    OrderCard card = new OrderCard(order, executor);
                    card.setUser(controller.getUser());
                    card.setStage(stage);
                    card.setCancelOrder(() -> cancelOrder(order.getOrderId()));
                    cardSection.getChildren().add(card);
                }
            } else {
                HBox emptyOrderBox = buildOrderEmptyBox();
                cardSection.getChildren().add(emptyOrderBox);
                HBox.setHgrow(emptyOrderBox, Priority.ALWAYS);
            }
        });

        executor.submit(task);
    }

    public void exit() {
        executor.shutdownNow();
        controller.cleanup();
        controller = null;
    }

    public Scene initialize(){
        cardSection = new HBox(30);
        cardSection.setPadding(new Insets(30));
        loadOrdersAsync();

        VBox bodyBox = new VBox();
        bodyBox.getChildren().add(cardSection);

        ScrollPane contentPane = new ScrollPane(bodyBox);
        contentPane.setFitToWidth(true);
        contentPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        contentPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        body = new StackPane();
        body.getChildren().add(contentPane);

        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(body);

        Scene scene = setScene(root, "order-page");

        return scene;
    }
}
