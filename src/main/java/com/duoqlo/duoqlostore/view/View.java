package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.DashboardController;
import com.duoqlo.duoqlostore.model.Product;
import com.duoqlo.duoqlostore.model.ProductDAO;
import com.duoqlo.duoqlostore.model.ProductSize;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.*;
import java.io.File;
import java.util.List;
import java.util.Objects;

import org.kordamp.ikonli.javafx.FontIcon;

public class View extends BasePage {
    DashboardController controller = new DashboardController();
    ProductDAO productDao = new ProductDAO();

    static int logoHeight = 50;
    static int iconSize = 19;

    private TilePane productGrid;
    private String currentFilter = "ALL"; // ALL, MEN, WOMEN, KIDS

    public View() {
        super();
        productGrid = createProductGrid();
    }

    public HBox buildHeader() {
        // Spacer
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // Category Buttons
        Button womenButton = new Button("WOMEN");
        Button menButton = new Button("MEN");
        Button kidsButton = new Button("KIDS");
        Button allButton = new Button("ALL");

        // Style buttons
        String buttonStyle = "-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-font-size: 14px;";
        String activeStyle = "-fx-background-color: #EE5702; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 15; -fx-background-radius: 5;";

        womenButton.setStyle(buttonStyle);
        menButton.setStyle(buttonStyle);
        kidsButton.setStyle(buttonStyle);
        allButton.setStyle(activeStyle); // ALL active by default

        // Button actions
        womenButton.setOnAction(e -> {
            currentFilter = "WOMEN";
            loadProductsByGender("WOMEN");
            updateButtonStyles(womenButton, menButton, kidsButton, allButton);
            womenButton.setStyle(activeStyle);
        });

        menButton.setOnAction(e -> {
            currentFilter = "MEN";
            loadProductsByGender("MEN");
            updateButtonStyles(womenButton, menButton, kidsButton, allButton);
            menButton.setStyle(activeStyle);
        });

        kidsButton.setOnAction(e -> {
            currentFilter = "KIDS";
            loadProductsByGender("KIDS");
            updateButtonStyles(womenButton, menButton, kidsButton, allButton);
            kidsButton.setStyle(activeStyle);
        });

        allButton.setOnAction(e -> {
            currentFilter = "ALL";
            loadAllProducts();
            updateButtonStyles(womenButton, menButton, kidsButton, allButton);
            allButton.setStyle(activeStyle);
        });

        // Category Menu
        HBox catMenu = new HBox(80);
        catMenu.setId("category-menu");
        catMenu.setAlignment(Pos.BOTTOM_CENTER);
        catMenu.setPadding(new Insets(5));
        catMenu.getChildren().addAll(allButton, womenButton, menButton, kidsButton);

        // Search Button
        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.setIconSize(iconSize);
        searchIcon.setIconColor(Color.web("#EE5702"));
        Button searchButton = new Button("", searchIcon);

        // Cart Button
        FontIcon cartIcon = new FontIcon("fas-shopping-cart");
        cartIcon.setIconSize(iconSize);
        cartIcon.setIconColor(Color.web("#EE5702"));
        Button cartButton = new Button("", cartIcon);
        cartButton.setOnAction(e -> controller.openCartPage(e));

        // Profile Button
        FontIcon profileIcon = new FontIcon("far-user");
        profileIcon.setIconSize(iconSize);
        profileIcon.setIconColor(Color.web("#EE5702"));
        Button profileButton = new Button("", profileIcon);

        // Button Box
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(searchButton, cartButton, profileButton);
        buttonBox.setAlignment(Pos.CENTER);

        HBox header = createHeaderBox(catMenu, buttonBox);
        return header;
    }

    private void updateButtonStyles(Button women, Button men, Button kids, Button all) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-font-size: 14px;";
        women.setStyle(defaultStyle);
        men.setStyle(defaultStyle);
        kids.setStyle(defaultStyle);
        all.setStyle(defaultStyle);
    }

    public HBox buildFilterMenu() {
        int tbPad = 20;
        int leftPad = 63;

        MenuButton size = new MenuButton("Size");
        MenuButton colour = new MenuButton("Colour");
        MenuButton price = new MenuButton("Price");

        HBox filterMenu = new HBox(10, size, colour, price);
        filterMenu.setId("filter-menu");
        filterMenu.setPadding(new Insets(tbPad, 0, tbPad, leftPad));

        return filterMenu;
    }

    private TilePane createProductGrid() {
        TilePane grid = new TilePane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPrefColumns(4);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private void loadAllProducts() {
        List<Product> products = productDao.getAllProducts();
        displayProducts(products);
    }

    private void loadProductsByGender(String gender) {
        List<Product> products = productDao.getProductsByGender(gender);
        displayProducts(products);
    }

    private void displayProducts(List<Product> products) {
        productGrid.getChildren().clear();

        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productGrid.getChildren().add(productCard);
        }

        // Show message if no products
        if (products.isEmpty()) {
            Label noProductsLabel = new Label("No products available");
            noProductsLabel.setStyle("-fx-text-fill: #999999; -fx-font-size: 16px;");
            productGrid.getChildren().add(noProductsLabel);
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setPrefSize(250, 350);
        card.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 8px; -fx-background-color: white; -fx-background-radius: 8px; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-border-color: #EE5702; -fx-border-radius: 8px; -fx-background-color: white; -fx-background-radius: 8px; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 8px; -fx-background-color: white; -fx-background-radius: 8px; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"));

        // Product Image
        ImageView productImage = new ImageView();
        productImage.setFitWidth(230);
        productImage.setFitHeight(200);
        productImage.setPreserveRatio(true);

        try {
            // Try to load the first image from product folder
            String imagePath = product.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image img = new Image(imageFile.toURI().toString(), 230, 200, true, true);
                    productImage.setImage(img);
                } else {
                    // Load placeholder image
                    productImage.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                }
            } else {
                productImage.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            }
        } catch (Exception e) {
            // Load placeholder on error
            productImage.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
        }

        // Product Name
        Label nameLabel = new Label(product.getProductName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
        nameLabel.setWrapText(true);

        // Product Price (get lowest price from sizes)
        List<ProductSize> sizes = productDao.getProductSizes(product.getProductId());
        double lowestPrice = sizes.stream().mapToDouble(ProductSize::getPrice).min().orElse(0);
        Label priceLabel = new Label(String.format("RM %.2f", lowestPrice));
        priceLabel.setStyle("-fx-text-fill: #EE5702; -fx-font-weight: bold; -fx-font-size: 16px;");

        // Product Category
        Label categoryLabel = new Label(product.getCategory() + " - " + product.getSubCategory());
        categoryLabel.setStyle("-fx-text-fill: #999999; -fx-font-size: 11px;");

        // Buttons
        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #EE5702; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 5;");
        viewButton.setOnAction(e -> showProductDetails(product));

        Button cartButton = new Button("Add to Cart");
        cartButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #EE5702; -fx-border-color: #EE5702; -fx-border-radius: 5; -fx-padding: 5 15;");

        HBox buttonBox = new HBox(10, viewButton, cartButton);
        buttonBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(productImage, nameLabel, priceLabel, categoryLabel, buttonBox);

        return card;
    }

    private void showProductDetails(Product product) {
        // Create a dialog to show product details
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Product Details");
        dialog.setHeaderText(product.getProductName());

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        // Product details
        Label descriptionLabel = new Label("Description: " + (product.getDescription() != null ? product.getDescription() : "No description available"));
        descriptionLabel.setWrapText(true);

        Label categoryLabel = new Label("Category: " + product.getCategory() + " - " + product.getSubCategory());

        // Size and price table
        List<ProductSize> sizes = productDao.getProductSizes(product.getProductId());

        Label sizesLabel = new Label("Available Sizes:");
        sizesLabel.setStyle("-fx-font-weight: bold; -fx-margin-top: 10;");

        GridPane sizeGrid = new GridPane();
        sizeGrid.setHgap(10);
        sizeGrid.setVgap(5);

        int row = 0;
        sizeGrid.add(new Label("Size"), 0, row);
        sizeGrid.add(new Label("Price (RM)"), 1, row);
        sizeGrid.add(new Label("Stock"), 2, row);

        for (ProductSize size : sizes) {
            row++;
            sizeGrid.add(new Label(size.getSize()), 0, row);
            sizeGrid.add(new Label(String.format("%.2f", size.getPrice())), 1, row);
            sizeGrid.add(new Label(String.valueOf(size.getStockQuantity())), 2, row);
        }

        content.getChildren().addAll(descriptionLabel, categoryLabel, sizesLabel, sizeGrid);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    public ScrollPane buildProductGrid() {
        // Load all products initially
        loadAllProducts();

        VBox productSection = new VBox(buildFilterMenu(), productGrid);

        ScrollPane scrollPane = new ScrollPane(productSection);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white;");

        return scrollPane;
    }

    public Scene initialize() {
        UserDashboard userDash = new UserDashboard();

        BorderPane root = new BorderPane();
        root.setTop(userDash.buildHeader());
        root.setCenter(userDash.buildProductGrid());

        Scene scene = new Scene(root, windowWidth, windowHeight);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/css/home-page.css")
                ).toExternalForm()
        );

        return scene;
    }

    public Scene initializeAdminDash() {
        AdminDashboard adminDash = new AdminDashboard();

        BorderPane root = new BorderPane();
        root.setTop(adminDash.createPage());

        return new Scene(root, windowWidth, windowHeight);
    }
}