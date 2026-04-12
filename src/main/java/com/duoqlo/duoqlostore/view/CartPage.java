package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.CartController;
import com.duoqlo.duoqlostore.controller.Navigator;
import com.duoqlo.duoqlostore.model.CartItem;
import com.duoqlo.duoqlostore.model.ProductDAO;
import com.duoqlo.duoqlostore.model.User;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.File;

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

    private Runnable onRemoveCallBack;

    public CartRow(CartItem cartItem, Runnable onRemoveCallBack) {
        this.cartItem = cartItem;
        this.productName = cartItem.getProductName();
        this.category = cartItem.getCategory();
        this.size = cartItem.getSize();
        this.quantity = cartItem.getProductQuantity();
        this.subTotal = cartItem.getSubTotal();
        this.unitPrice = subTotal / quantity;
        this.imageView = new ImageView();
        this.onRemoveCallBack = onRemoveCallBack;

        setImageView();
        create();
    }

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

        Button removeButton = new Button("REMOVE");
        removeButton.getStyleClass().add("remove-button");
        removeButton.setOnAction(e -> {
            if(onRemoveCallBack != null) {
                onRemoveCallBack.run();
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
    }
}

public class CartPage extends BasePage{
    private CartController controller;
    private AlertMsg alert;

    private StackPane body;
    private BorderPane root;

    private int numItems = 0;
    private double total;

    public CartPage(CartController controller) {
        super();
        this.controller = controller;

        numItems = controller.getCartItemList().size();
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

    private StackPane buildHeader(){
        Label label = new Label("CART PAGE");
        label.getStyleClass().add("cart-label");
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);

        StackPane header = createHeaderBox(labelBox, false);

        return header;
    }

    public ScrollPane buildItemBox(){
        VBox itemBox = new VBox();
        itemBox.setPrefWidth(700);
        itemBox.setMaxWidth(700);
        itemBox.setFillWidth(true);
        for (CartItem cartItem: controller.getCartItemList()) {
            CartRow cartRow = new CartRow(cartItem, () -> {
                boolean removed = controller.removeFromCart(cartItem.getProductSizeId());

                if (removed) {
                    refreshPage();
                } else {
                    System.out.println("Error");
                }
            });
            cartRow.getStyleClass().add("cart-row");
            itemBox.getChildren().add(cartRow);
        }
        itemBox.getStyleClass().add("item-box");

        ScrollPane itemContainer = new ScrollPane(itemBox);
        itemContainer.setFitToWidth(true);
        itemContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return itemContainer;
    }

    public Region createLine(){
        Region line = new Region();
        line.setStyle("-fx-background-color: #EBEBEB");
        line.setMaxHeight(3);

        return line;
    }

    private BorderPane buildContentRow(Label leftLabel, Label rightLabel) {
        BorderPane rowPane = new BorderPane();
        rowPane.setLeft(leftLabel);
        rowPane.setRight(rightLabel);
        BorderPane.setAlignment(leftLabel, Pos.CENTER_LEFT);
        BorderPane.setAlignment(rightLabel, Pos.CENTER_RIGHT);

        return rowPane;
    }

    public VBox buildSummaryBox() {
        double itemTotal = controller.getSubTotal();
        double shippingFee = 50;
        total = itemTotal + shippingFee;

        // Section Title
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

        // Total Items
        Label totalItemLabel = new Label(" Total items");
        Label totalItemValue = new Label(String.valueOf(numItems));
        BorderPane totalItemBox = buildContentRow(totalItemLabel, totalItemValue);
        totalItemBox.getStyleClass().add("summary-details-box");

        // Item's total
        Label itemTotalLabel = new Label("Item's total");
        Label itemTotalValue = new Label(showPrice(itemTotal));
        BorderPane itemTotalBox = buildContentRow(itemTotalLabel, itemTotalValue);
        itemTotalBox.getStyleClass().add("summary-details-box");

        // Shipping Fee
        Label shippingLabel = new Label("Shipping Fee");
        Label shippingValue = new Label(showPrice(shippingFee));
        BorderPane shippingBox = buildContentRow(shippingLabel, shippingValue);
        shippingBox.getStyleClass().add("summary-details-box");

        // Total
        Label totalLabel = new Label("TOTAL");
        Label totalValue = new Label(showPrice(total));
        BorderPane totalBox = buildContentRow(totalLabel, totalValue);
        totalBox.getStyleClass().add("total-box");

        // Container
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

    public Button buildCheckoutButton() {
        Button checkoutButton = new Button("CHECKOUT");
        checkoutButton.getStyleClass().add("checkout-button");
        checkoutButton.setOnAction(e -> {
            alert = new AlertMsg(AlertMsg.AlertMsgType.CONFIRMATION);

            alert.setOnConfirm(() -> {

                body.getChildren().add(createLoadingPane(new Label("Processing checkout...")));

                // Wait for 3 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(event -> {
                    if (controller.handleOrder(total)) {
                        controller.handleCheckOut();
                    }
                });
                pause.play();
            });

            alert.setOnCancel(() -> {
                System.out.println("Checkout cancelled");
            });

            alert.show(body, "Confirm to checkout?", Pos.CENTER);
        });

        return checkoutButton;
    }

    private HBox buildContentBox() {
        ScrollPane itemBox = buildItemBox();

        Button checkoutButton = buildCheckoutButton();

        VBox summarySection = new VBox(buildSummaryBox(), checkoutButton);
        summarySection.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(checkoutButton, new Insets(10, 0, 0, 0));

        HBox contentBox = new HBox(itemBox, summarySection);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getStyleClass().add("content-box");
        HBox.setMargin(itemBox, new Insets(0, 10, 0, 30));
        HBox.setMargin(summarySection, new Insets(0, 30, 10, 30));

        return contentBox;
    }

    public HBox buildCartEmptyBox() {
        Label emptyLabel = new Label("Cart is empty");
        emptyLabel.getStyleClass().add("empty-cart");

        HBox emptyLabelBox = new HBox(emptyLabel);
        emptyLabelBox.setAlignment(Pos.TOP_CENTER);
        HBox.setMargin(emptyLabel, new Insets(50, 0, 0, 0));

        return emptyLabelBox;
    }

    private void refreshPage() {
        // Rebuild the content box
        HBox contentBox;
        if (controller.listIsEmpty()) {
            contentBox = buildCartEmptyBox();
        } else {
            contentBox = buildContentBox();
        }

        // Update the UI
        StackPane body = (StackPane) root.getCenter();
        body.getChildren().clear();
        body.getChildren().add(contentBox);
    }

    public Scene initialize(){
        HBox contentBox;

        if (controller.listIsEmpty()) {
            contentBox = buildCartEmptyBox();
        } else {
            contentBox = buildContentBox();
        }

        body = new StackPane();
        body.getChildren().add(contentBox);

        root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(body);
        root.setOnMouseClicked(e -> root.requestFocus()); //Allow unfocus on TextField

        Scene scene = setScene(root, "cart-page");

        return scene;
    }
}
