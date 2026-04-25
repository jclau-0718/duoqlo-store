package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.CartController;
import com.duoqlo.duoqlostore.controller.Navigator;
import com.duoqlo.duoqlostore.model.CartItem;
import com.duoqlo.duoqlostore.model.Payment;
import com.duoqlo.duoqlostore.model.ProductDAO;
import com.duoqlo.duoqlostore.model.User;
import javafx.animation.ScaleTransition;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class CartRow extends BorderPane {
    private ProductDAO productDAO = new ProductDAO();

    private CartItem cartItem;
    private String productName;
    private String category;
    private String size;
    private double unitPrice;
    private int quantity;
    private double subTotal;
    private ImageView imageView;

    private Runnable onRemove;

    public CartRow(CartItem cartItem) {
        this.cartItem = cartItem;
        this.productName = cartItem.getProductName();
        this.category = cartItem.getCategory();
        this.size = cartItem.getSize();
        this.quantity = cartItem.getProductQuantity();
        this.subTotal = cartItem.getSubTotal();
        this.unitPrice = subTotal / quantity;
        this.imageView = new ImageView();

        setImageView();
        create();
    }

    public void setOnRemove(Runnable onRemove) { this.onRemove = onRemove; }

    public String getCategory() { return this.category; }

    private void setImageView() {
        int psId = cartItem.getProductSizeId();

        String imagePath = productDAO.getImagePath(psId);
        if(imagePath != null && !imagePath.isEmpty()) {
            Image image = getFirstImage(imagePath);
            if(image != null) {
                imageView.setImage(image);
            } else {
                System.err.println("Image not found (Source: CartRow)");
            }
        } else {
            System.err.println("Image Path not found! (Source: CartRow)");
        }
    }

    private Image getFirstImage(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Directory does not exist: " + directoryPath);
            return null;
        }

        //Get all image files
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

        // et the first image
        File firstImage = imageFiles[0];

        return new Image(firstImage.toURI().toString());
    }

    public void create() {
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(productName);
        nameLabel.getStyleClass().add("name");

        Label categoryLabel = new Label(category);
        categoryLabel.getStyleClass().add("category");

        Label sizeLabel = new Label("Size: ");
        sizeLabel.getStyleClass().add("size");

        Label sizeValueLabel = new Label(size);
        sizeValueLabel.getStyleClass().add("size-value");
        HBox sizeBox = new HBox(sizeLabel, sizeValueLabel);

        Label unitPriceLabel = new Label(String.format("RM%.2f", unitPrice));
        unitPriceLabel.getStyleClass().add("unit-price");

        Label quantityLabel = new Label("Quantity: ");
        quantityLabel.getStyleClass().add("quantity");

        Label quantityValueLabel = new Label(String.valueOf(quantity));
        quantityValueLabel.getStyleClass().add("quantity-value");
        HBox qtyBox = new HBox(quantityLabel, quantityValueLabel);

        VBox contentBox = new VBox(5);
        contentBox.getChildren().addAll(
                nameLabel,
                categoryLabel,
                sizeBox,
                unitPriceLabel,
                qtyBox
        );
        contentBox.setStyle("-fx-padding: 0 0 0 7;");

        for (Node child : contentBox.getChildren()) {
            VBox.setVgrow(child, Priority.ALWAYS);
        }

        Label subTotalLabel = new Label("SUB-TOTAL");
        subTotalLabel.getStyleClass().add("subtotal");

        Label subTotalValueLabel = new Label(String.format("RM %.2f", subTotal));
        subTotalValueLabel.getStyleClass().add("subtotal-value");

        Button removeButton = new PrimaryButton("Remove");
        removeButton.setOnAction(e -> {
            if(onRemove != null) {
                onRemove.run();
            }
        });

        VBox rightBox = new VBox();
        rightBox.getChildren().addAll(
                subTotalLabel,
                subTotalValueLabel,
                removeButton
        );
        rightBox.setAlignment(Pos.BOTTOM_RIGHT);

        this.setLeft(imageView);
        this.setCenter(contentBox);
        this.setRight(rightBox);

        this.getStyleClass().add("cart-row");
    }
}

public class CartPage extends UserPage{
    private CartController controller;
    private AlertMsg alert;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private StackPane body;
    private BorderPane root;

    private Label totalItemValue;
    private Label itemTotalValue;
    private Label shippingValue;
    private Label totalValue;

    private int totalItems = 0;
    private double total;

    private VBox checkoutBox;

    private boolean isBuyNowMode = false;

    public CartPage(CartController controller) {
        this.controller = controller;
    }

    @Override
    public User getUser() { return controller.getUser(); }

    @Override
    public void openCartPage() {
        Navigator.goTo(this.initialize());
    }

    @Override
    public void openOrdersPage() {
        controller.openOrdersPage();
    }

    @Override
    public void openProfilePage() {
        controller.openProfilePage();
    }

    public void setBuyNowMode() { this.isBuyNowMode = true; }

    private HBox buildHeader(){
        Label label = new Label("CART PAGE");
        label.getStyleClass().add("page-label");
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);

        header = createHeaderBox(labelBox, false);

        return header;
    }

    private ScrollPane buildItemBox(){
        VBox itemBox = new VBox();
        itemBox.setPrefWidth(700);
        itemBox.setMaxWidth(700);
        itemBox.setFillWidth(true);
        if(!isBuyNowMode) {
            for (CartItem cartItem : controller.getCartItemList()) {
                CartRow cartRow = new CartRow(cartItem);
                cartRow.setOnRemove(() -> {
                    boolean removed = controller.removeFromCart(cartItem.getProductSizeId());

                    if (removed) {
                        refreshPage();
                    } else {
                        System.out.println("Error");
                    }
                });
                itemBox.getChildren().add(cartRow);
            }
        } else {
            CartRow cartRow = new CartRow(controller.getTempCartItem());
            itemBox.getChildren().add(cartRow);
        }

        itemBox.getStyleClass().add("item-box");

        ScrollPane itemContainer = new ScrollPane(itemBox);
        itemContainer.setFitToWidth(true);
        itemContainer.setFitToHeight(true);
        itemContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return itemContainer;
    }

    private Region createLine(){
        Region line = new Region();
        line.setStyle("-fx-background-color: #EBEBEB");
        line.setMaxHeight(3);

        return line;
    }

    private BorderPane buildSumContentRow(Label leftLabel, Label rightLabel) {
        BorderPane rowPane = new BorderPane();
        rowPane.setLeft(leftLabel);
        rowPane.setRight(rightLabel);
        BorderPane.setAlignment(leftLabel, Pos.CENTER_LEFT);
        BorderPane.setAlignment(rightLabel, Pos.CENTER_RIGHT);

        return rowPane;
    }

    private void setSummaryValue() {
        double itemTotal;
        double shippingFee = 50;

        if(!isBuyNowMode) {
            totalItems = controller.getTotalItems();
            itemTotal = controller.getSubTotal();
            total = itemTotal + shippingFee;
        } else {
            totalItems = 1;
            itemTotal = controller.getTempCartItem().getSubTotal();
            total = itemTotal + shippingFee;
        }

        totalItemValue.setText(String.valueOf(totalItems));
        itemTotalValue.setText(showPrice(itemTotal));
        shippingValue.setText(showPrice(shippingFee));
        totalValue.setText(showPrice(total));
    }

    private VBox buildSummaryBox() {
        Region leftLine = createLine();
        Region rightLine = createLine();

        Label title = new Label("Order Summary");
        title.getStyleClass().add("section-title");

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(leftLine, title, rightLine);

        HBox.setHgrow(leftLine, Priority.ALWAYS);
        HBox.setHgrow(rightLine, Priority.ALWAYS);
        HBox.setMargin(leftLine, new Insets(0,5,0,0));
        HBox.setMargin(title, new Insets(0,5,0,5));
        HBox.setMargin(rightLine, new Insets(0,0,0,5));

        Label totalItemLabel = new Label(" Total items");
        totalItemValue = new Label();
        BorderPane totalItemBox = buildSumContentRow(totalItemLabel, totalItemValue);
        totalItemBox.getStyleClass().add("summary-details-box");

        Label itemTotalLabel = new Label("Item's total");
        itemTotalValue = new Label();
        BorderPane itemTotalBox = buildSumContentRow(itemTotalLabel, itemTotalValue);
        itemTotalBox.getStyleClass().add("summary-details-box");

        Label shippingLabel = new Label("Shipping Fee");
        shippingValue = new Label();
        BorderPane shippingBox = buildSumContentRow(shippingLabel, shippingValue);
        shippingBox.getStyleClass().add("summary-details-box");

        Label totalLabel = new Label("TOTAL");
        totalValue = new Label();
        BorderPane totalBox = buildSumContentRow(totalLabel, totalValue);
        totalBox.getStyleClass().add("total-box");

        setSummaryValue();

        VBox container = new VBox(20);
        container.setPrefWidth(300);
        container.getChildren().addAll(
                titleBox,
                totalItemBox,
                itemTotalBox,
                shippingBox,
                totalBox
        );
        container.getStyleClass().add("order-summary-box");

        return container;
    }

    private VBox buildCheckoutBox() {
        Button closeButton = new Button("✕");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> closeCheckoutBox());

        HBox closeButtonBox = new HBox(closeButton);
        closeButtonBox.setAlignment(Pos.CENTER_RIGHT);

        Label paymentLabel = new Label("Payment Method: ");
        paymentLabel.setStyle("-fx-font-size: 14");

        ComboBox<Payment> paymentCombo = new ComboBox<>();
        paymentCombo.getItems().addAll(Payment.values());
        paymentCombo.getStyleClass().add("payment-combo");

        HBox paymentBox = new HBox(5);
        paymentBox.getChildren().addAll(paymentLabel, paymentCombo);
        paymentBox.setAlignment(Pos.CENTER);

        Button confirmButton = new SecondaryButton("Confirm");
        confirmButton.setOnAction(e -> {
            Payment paymentMethod = paymentCombo.getValue();

            if (controller.handleOrder(total, totalItems, paymentMethod)) {
                controller.handleCheckOut();
            }
        });

        Button cancelButton = new PrimaryButton("Cancel");
        cancelButton.setOnAction(e -> closeCheckoutBox());

        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox checkoutBox = new VBox(10);
        checkoutBox.getStyleClass().add("checkout-box");
        checkoutBox.setAlignment(Pos.CENTER);
        checkoutBox.getChildren().addAll(closeButtonBox, paymentBox, buttonBox);
        checkoutBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        VBox.setMargin(buttonBox, new Insets(5, 0, 0, 0));

        return checkoutBox;
    }

    private void showCheckoutBox() {
        checkoutBox = buildCheckoutBox();

        body.getChildren().add(checkoutBox);
        checkoutBox.toFront();

        ScaleTransition modalScaleIn = new ScaleTransition(Duration.millis(300), checkoutBox);
        modalScaleIn.setFromX(0.8);
        modalScaleIn.setFromY(0.8);
        modalScaleIn.setToX(1);
        modalScaleIn.setToY(1);
        modalScaleIn.play();
    }

    private void closeCheckoutBox() {
        ScaleTransition modalScaleOut = new ScaleTransition(Duration.millis(200), checkoutBox);
        modalScaleOut.setToX(0);
        modalScaleOut.setToY(0);
        modalScaleOut.setOnFinished(e -> {
            body.getChildren().remove(checkoutBox);
            checkoutBox = null;
        });
        modalScaleOut.play();
    }

    private Button buildCheckoutButton() {
        Button checkoutButton = new PrimaryButton("Checkout");
        checkoutButton.setOnAction(e -> showCheckoutBox());

        return checkoutButton;
    }

    private HBox buildContentBox() {
        String dateText = "Last updated: " + controller.getLastUpdatedDate();
        Label dateLabel = new Label(dateText);
        dateLabel.setStyle("-fx-font-size: 13");

        Button emptyCartButton = new PrimaryButton("Empty Cart");
        emptyCartButton.setOnAction(e -> {
            controller.clearCart();

            refreshPage();
        });

        BorderPane topPane = new BorderPane();
        topPane.setLeft(dateLabel);
        topPane.setRight(emptyCartButton);

        ScrollPane itemBox = buildItemBox();

        VBox itemSection = new VBox(15);
        itemSection.getChildren().addAll(topPane, itemBox);

        Button checkoutButton = buildCheckoutButton();

        VBox summarySection = new VBox(buildSummaryBox(), checkoutButton);
        summarySection.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(checkoutButton, new Insets(10, 0, 0, 0));

        HBox contentBox = new HBox(itemSection, summarySection);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getStyleClass().add("content-box");
        HBox.setMargin(itemSection, new Insets(0, 10, 0, 30));
        HBox.setMargin(summarySection, new Insets(0, 30, 10, 30));

        return contentBox;
    }

    private HBox buildEmptyCartBox() {
        Label emptyLabel = new Label("Cart is empty");
        emptyLabel.getStyleClass().add("empty-cart");

        HBox emptyLabelBox = new HBox(emptyLabel);
        emptyLabelBox.setAlignment(Pos.TOP_CENTER);
        HBox.setMargin(emptyLabel, new Insets(50, 0, 0, 0));

        return emptyLabelBox;
    }

    private Rectangle buildOverlay() {
        Rectangle overlay = new Rectangle();
        overlay.setFill(Color.rgb(0, 0, 0, 0.3));

        overlay.widthProperty().bind(body.widthProperty());
        overlay.heightProperty().bind(body.heightProperty());

        return overlay;
    }

    private void loadCartAsync() {
        body.getChildren().clear();
        body.getChildren().addAll(buildOverlay(), createLoadingPane(new Label()));

        Task<List<CartItem>> task = new Task<>() {
            @Override
            protected List<CartItem> call() {
                controller.refreshCart();
                return controller.getCartItemList();
            }
        };

        task.setOnSucceeded(e -> {
            List<CartItem> cartItems = task.getValue();

            body.getChildren().clear();

            if ((cartItems == null || cartItems.isEmpty()) && !isBuyNowMode) {
                body.getChildren().add(buildEmptyCartBox());
            } else {
                totalItems = controller.getTotalItems();
                body.getChildren().add(buildContentBox());
            }
        });

        task.setOnFailed(e -> {
            body.getChildren().clear();
            body.getChildren().add(buildEmptyCartBox());
        });

        executor.submit(task);
    }

    private void refreshPage() {
        StackPane body = (StackPane) root.getCenter();
        body.getChildren().clear();
        loadCartAsync();
    }

    public void exit() {
        executor.shutdownNow();
        controller.cleanup();
        controller = null;
    }

    public Scene initialize(){
        body = new StackPane();

        root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(body);
        root.setOnMouseClicked(e -> root.requestFocus()); //Allow unfocus on TextField

        loadCartAsync();

        Scene scene = setScene(root, "cart-page");

        return scene;
    }
}
