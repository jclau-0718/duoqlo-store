package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.CartController;
import com.duoqlo.duoqlostore.model.CartItem;
import com.duoqlo.duoqlostore.model.ProductDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.Objects;

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

    public Image getFirstImage(String directoryPath) {
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
        System.out.println("Loading first image: " + firstImage.getName());

        // Load and return the image
        return new Image(firstImage.toURI().toString());
    }

    public BorderPane create() {
        imageView.setFitWidth(100);

        Label nameLabel = new Label(productName);

        Label categoryLabel = new Label(category);

        Label sizeLabel = new Label("Size: ");
        Label sizeValueLabel = new Label(size);
        sizeValueLabel.setStyle("-fx-font-weight: bold; ");
        HBox sizeBox = new HBox(sizeLabel, sizeValueLabel);

        Label unitPriceLabel = new Label(String.format("RM%.2f", unitPrice));

        Label quantityLabel = new Label("Quantity: ");
        Label quantityValueLabel = new Label(String.valueOf(quantity));
        quantityValueLabel.setStyle("-fx-font-weight: bold");
        HBox qtyBox = new HBox(quantityLabel, quantityValueLabel);

        VBox contentBox = new VBox(5);
        contentBox.getChildren().addAll(
                nameLabel,
                categoryLabel,
                sizeBox,
                unitPriceLabel,
                qtyBox
        );

        Label subTotalLabel = new Label("SUB-TOTAL");
        Label subTotalValueLabel = new Label(String.format("RM%.2f", subTotal));
        Button removeButton = new Button("REMOVE");
        removeButton.getStyleClass().add("primary-button");

        VBox rightBox = new VBox();
        rightBox.getChildren().addAll(
                subTotalLabel,
                subTotalValueLabel,
                removeButton
        );
        rightBox.setAlignment(Pos.BOTTOM_RIGHT);

        BorderPane itemRow = new BorderPane();
        itemRow.setLeft(imageView);
        itemRow.setCenter(contentBox);
        itemRow.setRight(rightBox);

        return itemRow;
    }

}

public class CartPage extends BasePage{
    private CartController controller;

    private int numItems = 0;

    public CartPage(CartController controller) {
        super();
        this.controller = controller;

        numItems = controller.getCartItemList().size();
    }

    private StackPane buildHeader(){
        Label label = new Label("CART PAGE");
        label.setId("cart-label");
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);

        FontIcon profileIcon = new FontIcon("far-user");
        profileIcon.setIconSize(iconSize);
        profileIcon.setIconColor(Color.web("#EE5702"));
        Button profileButton = new Button("", profileIcon);

        HBox actionBox = new HBox(10);
        actionBox.setMinWidth(300);
        actionBox.setPrefWidth(300);
        actionBox.setMaxWidth(300);
        actionBox.getChildren().addAll(searchBar, profileButton);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        StackPane header = createHeaderBox(labelBox, actionBox);

        return header;
    }

    public ScrollPane buildItemBox(){
        VBox itemBox = new VBox();
        itemBox.setPrefWidth(300);
        itemBox.setPrefHeight(400);
        System.out.println("List is empty: "+controller.listIsEmpty());
        for (CartItem cartItem: controller.getCartItemList()) {
            itemBox.getChildren().add(new CartRow(cartItem));
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
        double total = itemTotal + shippingFee;

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

        // Item's total
        Label itemTotalLabel = new Label("Item's total");
        Label itemTotalValue = new Label(showPrice(itemTotal));
        BorderPane itemTotalBox = buildContentRow(itemTotalLabel, itemTotalValue);

        // Shipping Fee
        Label shippingLabel = new Label("Shipping Fee");
        Label shippingValue = new Label(showPrice(shippingFee));
        BorderPane shippingBox = buildContentRow(shippingLabel, shippingValue);

        // Total
        Label totalLabel = new Label("TOTAL");
        Label totalValue = new Label(showPrice(total));
        BorderPane totalBox = buildContentRow(totalLabel, totalValue);
        totalBox.getStyleClass().add("total-box");

        // Container
        VBox container = new VBox(20);
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

    public Button checkoutButton() {
        Button checkoutBt = new Button("CHECKOUT");
        checkoutBt.getStyleClass().add("primary-button");

        return checkoutBt;
    }

    public Scene initialize(){
        VBox summarySection = new VBox(buildSummaryBox(), checkoutButton());
        summarySection.setAlignment(Pos.TOP_CENTER);

        HBox contentBox = new HBox(buildItemBox(), summarySection);
        HBox.setMargin(buildItemBox(), new Insets(0, 10, 0, 30));
        HBox.setMargin(summarySection, new Insets(0, 30, 10, 30));

        StackPane body = new StackPane();
        body.getChildren().add(contentBox);
        StackPane.setAlignment(contentBox, Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(body);
        root.setOnMouseClicked(e -> root.requestFocus()); //Allow unfocus on TextField

        Scene scene = new Scene(root,
                Screen.getPrimary().getVisualBounds().getWidth(),
                Screen.getPrimary().getVisualBounds().getHeight());

        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/css/cart-page.css")
                ).toExternalForm()
        );

        return scene;
    }
}
