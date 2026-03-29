package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.DashboardController;
import com.duoqlo.duoqlostore.model.Product;
import com.duoqlo.duoqlostore.model.ProductDAO;
import com.duoqlo.duoqlostore.model.ProductSize;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.*;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class UserDashboard extends BasePage {
    private ProductDAO productDAO = new ProductDAO();
    private DashboardController controller = new DashboardController();

    private Map<Integer, List<ProductSize>> sizesCache = new HashMap<>();
    private Map<String, List<Image>> imageCache = new ConcurrentHashMap<>();
    private List<Product> allProducts; // Cache all products

    private StackPane body;
    private Rectangle overlay;

    private VBox expandedCardModal;
    private ScaleTransition modalScaleIn;
    private ScaleTransition modalScaleOut;

    static int logoHeight = 50;
    static int iconSize = 19;

    private TilePane productGrid;
    private Label stockLabel;
    private String currentFilter = "ALL";

    private int cardWidth = 200;
    private int cardHeight = cardWidth + 180;
    private int enlargedWidth = cardWidth + 350;
    private int enlargedHeight = enlargedWidth + 180;

    private boolean isSizesSelected = false;

    public UserDashboard() {
        super();
    }

    public HBox buildHeader() {
        //Spacer
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        //Category Buttons
        Button allButton = new Button("ALL");
        Button womenButton = new Button("WOMEN");
        Button menButton = new Button("MEN");

        //Category Menu
        HBox catMenu = new HBox(80);
        catMenu.setId("category-menu");
        catMenu.setAlignment(Pos.BOTTOM_CENTER);
        catMenu.setPadding(new Insets(5));

        catMenu.getChildren().addAll(allButton, womenButton, menButton);

        allButton.setOnAction(e -> {
            currentFilter = "ALL";
            loadAllProducts();
        });

        menButton.setOnAction(e -> {
            currentFilter = "MEN";
            loadProductsByGender("MEN");
        });

        womenButton.setOnAction(e -> {
            currentFilter = "WOMEN";
            loadProductsByGender("WOMEN");
        });

        //Search Button
        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.setIconSize(iconSize);
        searchIcon.setIconColor(Color.web("#EE5702"));
        Button searchButton = new Button("", searchIcon);

        //Cart Button
        FontIcon cartIcon = new FontIcon("fas-shopping-cart");
        cartIcon.setIconSize(iconSize);
        cartIcon.setIconColor(Color.web("#EE5702"));
        Button cartButton = new Button("", cartIcon);
        cartButton.setOnAction(e -> controller.openCartPage(e));

        //Profile Button
        FontIcon profileIcon = new FontIcon("far-user");
        profileIcon.setIconSize(iconSize);
        profileIcon.setIconColor(Color.web("#EE5702"));
        Button profileButton = new Button("", profileIcon);

        //Button Box
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(searchButton, cartButton, profileButton);
        buttonBox.setAlignment(Pos.CENTER);

        HBox header = createHeaderBox(catMenu, buttonBox);

        return header;
    }

    public HBox buildFilterBar() {
        int tbPad = 20;
        int leftPad = 63;

        MenuButton size = new MenuButton("Size");
        MenuButton colour = new MenuButton("Colour");
        MenuButton price = new MenuButton("Price");

        HBox filterMenu = new HBox(10, size, colour, price);
        filterMenu.setId("filter-bar");
        filterMenu.setPadding(new Insets(tbPad, 0, tbPad, leftPad));

        return filterMenu;
    }

    public ScrollPane buildProductGrid() {
        productGrid = new TilePane();
        productGrid.setHgap(15);
        productGrid.setVgap(20);
        productGrid.setPrefColumns(4);
        productGrid.setAlignment(Pos.CENTER);

        VBox productSection = new VBox(buildFilterBar(), productGrid);

        ScrollPane scrollPane = new ScrollPane(productSection);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return scrollPane;
    }

    public Scene initialize() {
        // Load all data once during initialization
        loadAllDataOnce();

        body = new StackPane();

        overlay = new Rectangle();
        overlay.setFill(Color.rgb(0, 0, 0, 0.3));
        overlay.setVisible(false);

        overlay.widthProperty().bind(body.widthProperty());
        overlay.heightProperty().bind(body.heightProperty());

        body.getChildren().addAll(buildProductGrid(), overlay);

        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(body);

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

    private void loadAllDataOnce() {
        // Load all products
        allProducts = productDAO.getAllProducts();

        // Preload all sizes for all products at once
        Task<Void> preloadTask = new Task<>() {
            @Override
            protected Void call() {
                for (Product product : allProducts) {
                    getCachedProductSizes(product.getProductId());
                }
                return null;
            }
        };

        preloadTask.setOnSucceeded(e -> {
            // After sizes are loaded, display products
            displayProducts(allProducts);
        });

        new Thread(preloadTask).start();
    }

    private void displayProducts(List<Product> products) {
        // Clear existing products
        productGrid.getChildren().clear();

        // Create cards for each product
        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productGrid.getChildren().add(productCard);
        }

        // Show message if no products
        if (products.isEmpty()) {
            Label noProductsLabel = new Label("No products available");
            noProductsLabel.getStyleClass().add("no-products");
            productGrid.getChildren().add(noProductsLabel);
        }
    }

    private void loadAllProducts() {
        // Use cached products instead of database query
        displayProducts(allProducts);
    }

    private void loadProductsByGender(String gender) {
        // Filter from cached products instead of database query
        List<Product> filteredProducts = allProducts.stream()
                .filter(product -> gender.equals(product.getGender()))
                .collect(Collectors.toList());
        displayProducts(filteredProducts);
    }

    private VBox createProductCard(Product product) {

        VBox card = new VBox();
        card.setPrefSize(cardWidth, cardHeight);
        card.setMaxSize(cardWidth, cardHeight);
        card.setMinSize(cardWidth, cardHeight);
        card.getStyleClass().add("product-card");
        addToolTip(card, "Click for more details.");

        StackPane imageContainer = createImageContainer(product);

        // Gender Label
        Label genderLabel = new Label(product.getGender());
        genderLabel.getStyleClass().add("gender");

        // Product Name
        Label nameLabel = new Label(product.getProductName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(cardWidth - 10);

        // Product Price
        List<ProductSize> sizes = getCachedProductSizes(product.getProductId());
        double lowestPrice = sizes.stream().mapToDouble(ProductSize::getPrice).min().orElse(0);
        Label priceLabel = new Label(String.format("RM %.2f", lowestPrice));
        priceLabel.getStyleClass().add("price");

        // Buttons
        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #EE5702; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 5;");
        viewButton.setOnAction(e -> showProductDetails(product));

        Button cartButton = new Button("Add to Cart");
        cartButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #EE5702; -fx-border-color: #EE5702; -fx-border-radius: 5; -fx-padding: 5 15;");

        HBox buttonBox = new HBox(10, viewButton, cartButton);
        buttonBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageContainer, genderLabel, nameLabel, priceLabel);

//        VBox.setMargin(productImage, Insets.EMPTY);
        VBox.setMargin(genderLabel, new Insets(8, 0, 0, 8));
        VBox.setMargin(nameLabel, new Insets(5, 8, 0, 8));
        VBox.setMargin(priceLabel, new Insets(5, 8, 5, 8));

        // Simple hover effect - just shadow and slight elevation
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: white; -fx-cursor: hand;");
            card.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.25)));

            // Subtle lift without translate
            TranslateTransition lift = new TranslateTransition(Duration.millis(200), card);
            lift.setToY(-5);
            lift.play();
        });

        card.setOnMouseExited(e -> {
            card.setStyle("");
            card.setEffect(null);

            TranslateTransition lift = new TranslateTransition(Duration.millis(200), card);
            lift.setToY(0);
            lift.play();
        });

        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                showExpandedCard(product);
            }
        });

        return card;

    }

    private StackPane createImageContainer(Product product) {
        ImageCarousel carousel = new ImageCarousel();

        // Product Image
        ImageView productImage = new ImageView();
        productImage.setFitWidth(cardWidth - 3);
        productImage.setPreserveRatio(true);
//        productImage.setSmooth(true);
        productImage.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));


        String imagePath = product.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            loadImageAsync(productImage, imagePath, carousel);
        }

        Button rightButton = new Button();
        rightButton.setGraphic(new FontIcon("fas-chevron-right"));
        rightButton.getStyleClass().add("right-button");
        rightButton.setVisible(false);

        rightButton.setOnAction(e -> {
            Image nextImage = carousel.next();
            if(nextImage != null) {
                productImage.setImage(nextImage);
            }
        });

        rightButton.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> !carousel.hasNext(),
                        productImage.imageProperty()
                )
        );

        Button leftButton = new Button();
        leftButton.setGraphic(new FontIcon("fas-chevron-left"));
        leftButton.getStyleClass().add("left-button");
        leftButton.setVisible(false);

        leftButton.setOnAction(e -> {
            Image prevImage = carousel.previous();
            if(prevImage != null) {
                productImage.setImage(prevImage);
            }
        });

        leftButton.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> !carousel.hasPrevious(),
                        productImage.imageProperty()
                )
        );

        StackPane imageContainer = new StackPane(productImage);
        imageContainer.getChildren().addAll(leftButton, rightButton);
        imageContainer.setAlignment(leftButton, Pos.CENTER_LEFT);
        imageContainer.setAlignment(rightButton, Pos.CENTER_RIGHT);

        imageContainer.setOnMouseEntered(e -> {
            leftButton.setVisible(true);
            rightButton.setVisible(true);
        });

        imageContainer.setOnMouseExited(e -> {
            leftButton.setVisible(false);
            rightButton.setVisible(false);
        });

        return imageContainer;
    }

    private HBox getSizeButtons(List<ProductSize> productSizes) {
        HBox sizeButtons = new HBox(8); // Added spacing
        sizeButtons.setAlignment(Pos.CENTER_LEFT);

        if (productSizes != null && !productSizes.isEmpty()) {
            for (ProductSize size : productSizes) {
                Button button = new Button(size.getSize());
                button.getStyleClass().add("size-button");

                // Store the size data in the button's user data
                button.setUserData(size);

                sizeButtons.getChildren().add(button);
            }
            return sizeButtons;
        }

        return null;
    }

    private void showExpandedCard(Product product) {
        if (expandedCardModal != null) {
            closeExpandedCard();
        }

        // Get product sizes
        List<ProductSize> productSizes = getCachedProductSizes(product.getProductId());

        // Create the enlarged card
        VBox enlargedCard = createExpandedCardContent(product, productSizes);
        expandedCardModal = enlargedCard;

        // Add to body and bring to front
        body.getChildren().add(expandedCardModal);
        expandedCardModal.toFront();

        // Center the card
        centerExpandedCard();

        // Bind centering when window resizes
        body.widthProperty().addListener((obs, oldVal, newVal) -> centerExpandedCard());
        body.heightProperty().addListener((obs, oldVal, newVal) -> centerExpandedCard());

        // Animate modal appearance
        modalScaleIn = new ScaleTransition(Duration.millis(300), expandedCardModal);
        modalScaleIn.setFromX(0.8);
        modalScaleIn.setFromY(0.8);
        modalScaleIn.setToX(1);
        modalScaleIn.setToY(1);
        modalScaleIn.play();

        // Show overlay
        overlay.setVisible(true);

        // Close when clicking on overlay
        overlay.setOnMouseClicked(e -> closeExpandedCard());
    }

    private VBox createExpandedCardContent(Product product, List<ProductSize> productSizes) {
        ImageCarousel carousel = new ImageCarousel();

        VBox card = new VBox();
        card.setPrefSize(enlargedWidth, enlargedHeight);
        card.setMaxSize(enlargedWidth, enlargedHeight);
        card.setMinSize(enlargedWidth, enlargedHeight);
        card.getStyleClass().add("expanded-product-card");
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20;");
        card.setEffect(new DropShadow(25, Color.rgb(0, 0, 0, 0.3)));

        // Close button at top right
        Button closeButton = new Button("✕");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; -fx-font-size: 20px; -fx-cursor: hand; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> closeExpandedCard());

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.TOP_RIGHT);
        headerBox.getChildren().add(closeButton);

        // Product Image - Larger size
        ImageView productImage = new ImageView();
        productImage.setFitWidth(230);
        productImage.setPreserveRatio(true);
        productImage.setSmooth(true);
        productImage.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));

        String imagePath = product.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            loadImageAsync(productImage, imagePath, carousel);
        }

        // Center the image
        StackPane imageContainer = new StackPane(productImage);
        imageContainer.setAlignment(Pos.CENTER);

        // Gender Label
        Label genderLabel = new Label(product.getGender());
        genderLabel.getStyleClass().add("gender");
        genderLabel.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5 10; -fx-background-radius: 5; -fx-font-size: 12px;");

        // Product Name
        Label nameLabel = new Label(product.getProductName());
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);

        // Category
        Label categoryLabel = new Label(product.getCategory() + " - " + product.getSubCategory());
        categoryLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        // Description
        Label descriptionLabel = new Label(product.getDescription() != null ? product.getDescription() : "No description available");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        // Price
        double lowestPrice = productSizes.stream().mapToDouble(ProductSize::getPrice).min().orElse(0);
        Label priceLabel = new Label(String.format("RM %.2f", lowestPrice));
        priceLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #EE5702;");

        // Size buttons section
        Label sizeLabel = new Label("Select Size:");
        sizeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Get size buttons using your existing getSizeButtons method
        HBox sizeButtonBox = getSizeButtons(productSizes);
        if (sizeButtonBox != null) {
            sizeButtonBox.setAlignment(Pos.CENTER_LEFT);
            sizeButtonBox.setPadding(new Insets(5, 0, 5, 0));
        }

        // Stock label (reuse the existing stockLabel but create a new one for expanded view)
        stockLabel = new Label();
        stockLabel.getStyleClass().add("stock");
        stockLabel.setVisible(false);
        stockLabel.setManaged(false);
        stockLabel.setStyle("-fx-text-fill: #28a745; -fx-font-size: 12px; -fx-padding: 5 0;");

        BorderPane sizeStockPane = new BorderPane();
        sizeStockPane.setLeft(sizeButtonBox);
        sizeStockPane.setRight(stockLabel);

        // Buttons
        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.getStyleClass().add("primary-button");
//        addToCartButton.setStyle("-fx-background-color: #EE5702; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        addToCartButton.setOnAction(e -> {
            // Add to cart logic
            System.out.println("Added to cart: " + product.getProductName());
            showToast("Added to cart!");
            closeExpandedCard();
        });

        Button continueButton = new Button("Continue Shopping");
        continueButton.getStyleClass().add("secondary-button");
//        continueButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #EE5702; -fx-border-color: #EE5702; -fx-border-radius: 5; -fx-padding: 10 20; -fx-cursor: hand;");
        continueButton.setOnAction(e -> closeExpandedCard());

        HBox buttonBox = new HBox(15, addToCartButton, continueButton);
        buttonBox.setAlignment(Pos.CENTER);

        if (sizeButtonBox != null) {
            for (Node node : sizeButtonBox.getChildren()) {
                if (node instanceof Button) {
                    Button sizeButton = (Button) node;
                    String sizeText = sizeButton.getText();

                    // Find the corresponding ProductSize
                    for (ProductSize ps : productSizes) {
                        if (ps.getSize().equals(sizeText)) {
                            int stock = ps.getStockQuantity();

                            sizeButton.setOnMouseEntered(e -> {
                                updateStockLabel(stock);

                                // Update price when size is hovered
                                priceLabel.setText(String.format("RM %.2f", ps.getPrice()));
                            });

                            sizeButton.setOnMouseExited( e -> {
                                if (!isSizesSelected) {
                                    stockLabel.setVisible(false);
                                    stockLabel.setManaged(false);
                                }
                            });

                            sizeButton.setOnAction(e -> {
                                isSizesSelected = true;

                                updateStockLabel(stock);

                                // Update price when size is selected
                                priceLabel.setText(String.format("RM %.2f", ps.getPrice()));

                                // Update button style to show selection
                                for (Node n : sizeButtonBox.getChildren()) {
                                    if (n instanceof Button) {
                                        n.getStyleClass().remove("selected");
                                    }
                                }
                                sizeButton.getStyleClass().add("selected");
                            });
                            break;
                        }
                    }
                }
            }
        }

        // Layout all components
        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(
                genderLabel,
                nameLabel,
                categoryLabel,
                descriptionLabel,
                priceLabel,
                sizeLabel,
                sizeStockPane,
//                sizeButtonBox != null ? sizeButtonBox : new Label("No sizes available"),
//                stockLabel,
                buttonBox
        );
        VBox.setMargin(genderLabel, new Insets(5, 0, 0, 0));
        VBox.setMargin(buttonBox, new Insets(10, 0, 0, 0));

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(headerBox, imageContainer, contentBox);
        mainLayout.setAlignment(Pos.TOP_CENTER);

        card.getChildren().add(mainLayout);

        // Make card draggable
        makeDraggable(card);

        return card;
    }

    private boolean updateStockLabel(int stock) {
        boolean hasStock = false;

        if (stock > 0) {
            stockLabel.setText("In-stock");
            hasStock = true;
        } else {
            stockLabel.setText("Out of stock");
            hasStock = false;
        }

        stockLabel.setVisible(true);
        stockLabel.setManaged(true);

        return hasStock;
    }

    private void centerExpandedCard() {
        if (expandedCardModal != null && body != null) {
            double centerX = (body.getWidth() - expandedCardModal.getWidth()) / 2;
            double centerY = (body.getHeight() - expandedCardModal.getHeight()) / 2;
            expandedCardModal.setLayoutX(centerX);
            expandedCardModal.setLayoutY(centerY);
        }
    }

    private void closeExpandedCard() {
        if (expandedCardModal != null) {
            modalScaleOut = new ScaleTransition(Duration.millis(200), expandedCardModal);
            modalScaleOut.setToX(0);
            modalScaleOut.setToY(0);
            modalScaleOut.setOnFinished(e -> {
                body.getChildren().remove(expandedCardModal);
                expandedCardModal = null;
                overlay.setVisible(false);
                overlay.setOnMouseClicked(null); // Remove overlay click handler
            });
            modalScaleOut.play();
        }
    }

    private void makeDraggable(VBox card) {
        final double[] dragDelta = new double[2];

        card.setOnMousePressed(event -> {
            dragDelta[0] = card.getLayoutX() - event.getSceneX();
            dragDelta[1] = card.getLayoutY() - event.getSceneY();
            card.setCursor(Cursor.MOVE);
        });

        card.setOnMouseDragged(event -> {
            double newX = event.getSceneX() + dragDelta[0];
            double newY = event.getSceneY() + dragDelta[1];

            // Keep within bounds
            newX = Math.max(0, Math.min(newX, body.getWidth() - card.getWidth()));
            newY = Math.max(0, Math.min(newY, body.getHeight() - card.getHeight()));

            card.setLayoutX(newX);
            card.setLayoutY(newY);
        });

        card.setOnMouseReleased(event -> {
            card.setCursor(Cursor.HAND);
        });
    }

    private void showToast(String message) {
        Label toast = new Label(message);
        toast.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5; -fx-font-size: 14px;");
        toast.setOpacity(0);

        StackPane toastPane = new StackPane(toast);
        toastPane.setMouseTransparent(true);
        body.getChildren().add(toastPane);

        toastPane.setLayoutX((body.getWidth() - 200) / 2);
        toastPane.setLayoutY(body.getHeight() - 100);

        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(Duration.millis(300), toast);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(Duration.millis(300), toast);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> body.getChildren().remove(toastPane));

        fadeIn.play();
        pause.play();
        fadeOut.play();
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

        // Size and price table - use cached sizes
        List<ProductSize> sizes = getCachedProductSizes(product.getProductId());

        Label sizesLabel = new Label("Available Sizes:");
        sizesLabel.setStyle("-fx-font-weight: bold; -fx-margin-top: 10;");

        GridPane sizeGrid = new GridPane();
        sizeGrid.setHgap(10);
        sizeGrid.setVgap(5);

        int row = 0;
        Label sizeHeader = new Label("Size");
        sizeHeader.setStyle("-fx-font-weight: bold;");
        Label priceHeader = new Label("Price (RM)");
        priceHeader.setStyle("-fx-font-weight: bold;");
        Label stockHeader = new Label("Stock");
        stockHeader.setStyle("-fx-font-weight: bold;");

        sizeGrid.add(sizeHeader, 0, row);
        sizeGrid.add(priceHeader, 1, row);
        sizeGrid.add(stockHeader, 2, row);

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

    private void loadImageAsync(ImageView imageView, String imagePath, ImageCarousel carousel) {
        Task<List<Image>> loadTask = new Task<>() {
            @Override
            protected List<Image> call() throws Exception {

                if (imageCache.containsKey(imagePath)) {
                    return imageCache.get(imagePath);
                }

                List<Image> images = new ArrayList<>();

                if (imagePath != null && !imagePath.isEmpty()) {
                    File folder = new File(imagePath);

                    if (folder.exists() && folder.isDirectory()) {
                        File[] files = folder.listFiles((dir, name) ->
                                name.toLowerCase().endsWith(".jpg") ||
                                        name.toLowerCase().endsWith(".png") ||
                                        name.toLowerCase().endsWith(".jpeg")
                        );

                        for (File file : files) {
                                Image img = new Image(file.toURI().toString(), true);
                                images.add(img);
                        }

//                        if (files != null && files.length > 0) {
//
//                            Arrays.sort(files, Comparator.comparing(File::getName));
//
//                            for (File file : files) {
//                                Image img = new Image(file.toURI().toString(), true);
//                                images.add(img);
//                            }
//                        }
                    }
                }

                if (images.isEmpty()) {
                    images.add(new Image(
                            getClass().getResourceAsStream("/images/placeholder.png")
                    ));
                }

                List<Image> finalList = List.copyOf(images);
                imageCache.put(imagePath, finalList);

                return finalList;
            }
        };

        loadTask.setOnSucceeded(e -> {
            carousel.setImages(loadTask.getValue());

            Image firstImage = carousel.getCurrentImage();
            if (firstImage != null) {
                imageView.setImage(firstImage);
            }
        });

        new Thread(loadTask).start();

    }

    private List<ProductSize> getCachedProductSizes(int productId) {
        if (!sizesCache.containsKey(productId)) {
            sizesCache.put(productId, productDAO.getProductSizes(productId));
        }
        return sizesCache.get(productId);
    }

    public void cleanup() {
        imageCache.clear();
        sizesCache.clear();
        if (allProducts != null) {
            allProducts.clear();
        }
    }
}

class ImageCarousel {
    private int currentIndex = 0;
    private String imagePath = null;
    private List<Image> images = new ArrayList<>();
    private Map<String, List<Image>> imageMap = new ConcurrentHashMap<>();

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setImages(List<Image> images) {
        this.images = images;
        this.currentIndex = 0; // reset when new images loaded
    }

    public List<Image> getImages() {
        return images;
    }

    public Image getCurrentImage() {
        if (images == null || images.isEmpty()) return null;
        return images.get(currentIndex);
    }

    public boolean hasNext() {
        return images != null && currentIndex < images.size() - 1;
    }

    public boolean hasPrevious() {
        return images != null && currentIndex > 0;
    }

    public Image next() {
        if (hasNext()) {
            currentIndex++;
        }
        return getCurrentImage();
    }

    public Image previous() {
        if (hasPrevious()) {
            currentIndex--;
        }
        return getCurrentImage();
    }
}