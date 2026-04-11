package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.DashboardController;
import com.duoqlo.duoqlostore.model.*;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.*;

class ImageCarousel {
    private int currentIndex = 0;
    private List<Image> images = new ArrayList<>();

    public void setImages(List<Image> images) {
        this.images = images;
        this.currentIndex = 0;
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

public class UserDashboard extends BasePage {
    private DashboardController controller;
    private AlertMsg alert = new AlertMsg();

    private StackPane body;
    private Rectangle overlay;

    private VBox expandedCardModal;
    private ScaleTransition modalScaleIn;
    private ScaleTransition modalScaleOut;

    private Label loadingLabel = new Label("");
    private StackPane loadingPane = createLoadingPane(loadingLabel);

    private TilePane productGrid;
    private Label stockLabel;

    private ComboBox<String> sizeCombo;
    private ComboBox<String> categoryCombo;
    private ComboBox<String> priceCombo;
    private ComboBox<String> sortCombo;
    private ChangeListener<String> sortComboListener;

    private Button minusQtyBtn;
    private Button plusQtyBtn;

    private HBox sortBox;

    private int cardWidth = 200;
    private int cardHeight = cardWidth + 180;
    private int enlargedWidth = cardWidth + 350;
    private int enlargedHeight = enlargedWidth + 180;

    private Label qtyErrorLabel = new Label("Maximum stock reached.");
    private Label priceLabel;
    private double unitPrice;

    private TextField quantityField;
    private int productQuantity = 1;

    private String currentFilter = "ALL";
    private String sizeSelected;
    private boolean isSizesSelected = false;
    private boolean[] isDisabled = {false};

    public UserDashboard() {
        super();
    }

    public UserDashboard(DashboardController controller) {
        super();
        this.controller = controller;
    }

    public StackPane getBody() { return this.body; };

    @Override
    public void openCartPage() {
        controller.openCartPage();
    }

    @Override
    public void openOrdersPage() {
        controller.openOrdersPage();
    }

    @Override
    public void openProfilePage() {
        controller.openProfilePage();
    }

    public StackPane buildHeader() {
        //Category Buttons
        ToggleButton allButton = new ToggleButton("ALL");
        ToggleButton womenButton = new ToggleButton("WOMEN");
        ToggleButton menButton = new ToggleButton("MEN");

        ToggleGroup categoryGroup = new ToggleGroup();
        allButton.setToggleGroup(categoryGroup);
        womenButton.setToggleGroup(categoryGroup);
        menButton.setToggleGroup(categoryGroup);

        allButton.setSelected(true);

        //Category Menu
        HBox catMenu = new HBox(80);
        catMenu.getStyleClass().add("category-menu");
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

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal.isEmpty()) {
                loadAllProducts();
            }
        });

        searchField.setOnAction(e -> {
            enterButton.fire();
        });

        enterButton.setOnAction(e -> {
            String text = searchField.getText();
            loadProductsByName(text);
        });

        header = createHeaderBox(catMenu);

        return header;
    }

    public BorderPane buildFilterBar() {
        int tbPad = 20;
        int sidePad = 63;

        sizeCombo = new ComboBox<>();
        sizeCombo.setPromptText("All sizes");
        sizeCombo.setPrefWidth(120);
        sizeCombo.getStyleClass().add("filter-combo");

        categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("All categories");
        categoryCombo.setPrefWidth(150);
        categoryCombo.getStyleClass().add("filter-combo");

        priceCombo = new ComboBox<>();
        priceCombo.setPromptText("All prices");
        priceCombo.setPrefWidth(150);
        priceCombo.getStyleClass().add("filter-combo");

        FontIcon sortIcon = new FontIcon("fas-sort");
        sortIcon.setIconSize(16);
        sortIcon.setIconColor(themeColor);
        sortCombo = createSortCombo();

        sortBox = createSortBox(sortCombo);

        HBox filterBox = new HBox(10, sizeCombo, categoryCombo, priceCombo);
        filterBox.getStyleClass().add("filter-box");

        BorderPane filterBar = new BorderPane();
        filterBar.setLeft(filterBox);
        filterBar.setRight(sortBox);

        filterBar.setPadding(new Insets(tbPad, sidePad, tbPad, sidePad));
        return filterBar;
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

    private void loadAllDataOnce() {
        loadingPane.setVisible(true);

        Task<Void> preloadTask = controller.createPreloadTask(() -> {
            loadAllProducts();
            setupSizeMenu();
            setupCategoryMenu();
            setupPriceMenu();
            setupSortingMenu();
            loadingPane.setVisible(false);
        });

        loadingLabel.textProperty().bind(preloadTask.messageProperty());

        new Thread(preloadTask).start();
    }

    private void setupSizeMenu() {
        List<String> sizeList = controller.getDistinctSizes();
        sizeCombo.getItems().clear();
        sizeCombo.getItems().add("All sizes");
        sizeCombo.getItems().addAll(sizeList);
        sizeCombo.getSelectionModel().clearSelection();
        sizeCombo.setValue(null);
        sizeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.toLowerCase().contains("all")) {
                controller.setSizeSelected(null);
                sizeCombo.getStyleClass().remove("selected");
            } else {
                controller.setSizeSelected(newVal);
                sizeCombo.getStyleClass().add("selected");
            }
            applyFilters();
        });
    }

    private void setupCategoryMenu() {
        Set<String> uniqueCategories = controller.getUniqueCategories();

        categoryCombo.getItems().clear();
        categoryCombo.getItems().add("All categories");
        categoryCombo.getItems().addAll(uniqueCategories);
        categoryCombo.getSelectionModel().clearSelection();
        categoryCombo.setValue(null);

        categoryCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.toLowerCase().contains("all")) {
                controller.setCategorySelected(null);
                categoryCombo.getStyleClass().remove("selected");
            } else {
                controller.setCategorySelected(newVal.toUpperCase());
                categoryCombo.getStyleClass().add("selected");
            }
            applyFilters();
        });
    }

    private void setupPriceMenu() {
        List<String> priceRanges = Arrays.asList("Below RM30", "RM30 - RM40", "Above RM40");
        priceCombo.getItems().clear();
        priceCombo.getItems().add("All prices");
        priceCombo.getItems().addAll(priceRanges);
        priceCombo.getSelectionModel().clearSelection();
        priceCombo.setValue(null);
        priceCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal.toLowerCase().contains("all")) {
                controller.setPriceSelected(null);
                priceCombo.getStyleClass().remove("selected");
            } else {
                controller.setPriceSelected(newVal);
                priceCombo.getStyleClass().add("selected");
            }
            applyFilters();
        });
    }

    private void setupSortingMenu() {
        List<String> sortingList = new ArrayList<>();

        sortingList.add("Name (A - Z)");
        sortingList.add("Name (Z - A)");
        sortingList.add("Price (Low - High)");
        sortingList.add("Price (High - Low)");

        sortCombo.getItems().addAll(sortingList);

        sortComboListener = (obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                controller.setSorted(true);

                boolean hasResetButton = sortBox.getChildren().stream()
                        .anyMatch(node -> node.getStyleClass().contains("reset-button"));

                if (!hasResetButton) {
                    sortBox.getChildren().add(createResetButton());
                }

                controller.setSortingSelected(newVal);
                sortCombo.getStyleClass().add("selected");
                applyFilters();
            }
        };

        sortCombo.valueProperty().addListener(sortComboListener);
    }

    private void applyFilters() {
        controller.applyProdFilters();
        displayProducts(controller.getFilteredProducts());
    }

    private Button createResetButton() {
        FontIcon xIcon = new FontIcon("fas-times");
        xIcon.setIconSize(16);
        Button resetButton = new Button("", xIcon);
        resetButton.getStyleClass().add("reset-button");
        resetButton.setOnAction(e -> {
            sortCombo.valueProperty().removeListener(sortComboListener);

            sortCombo.setValue(null);
            sortCombo.setPromptText("Sort by");
            sortCombo.setPrefWidth(60);
            sortCombo.getStyleClass().remove("selected");

            controller.setSortingSelected(null);
            controller.setSorted(false);

            sortBox.getChildren().remove(resetButton);

            applyFilters();

            sortCombo.valueProperty().addListener(sortComboListener);
        });

        return resetButton;
    }

    private void displayProducts(List<Product> products) {
        productGrid.getChildren().clear();

        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productGrid.getChildren().add(productCard);
        }

        if (products.isEmpty()) {
            Label noProductsLabel = new Label("No products available");
            noProductsLabel.getStyleClass().add("no-products");
            productGrid.getChildren().add(noProductsLabel);
        }
    }

    private void loadAllProducts() {
        controller.loadAllProducts();
        displayProducts(controller.getDisplayedProducts());
    }

    private void loadProductsByGender(String gender) {
        controller.loadProductsByGender(gender);
        displayProducts(controller.getDisplayedProducts());
    }

    private void loadProductsByName(String name) {
        controller.loadProductsByName(name);
        displayProducts(controller.getDisplayedProducts());
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox();
        card.setPrefSize(cardWidth, cardHeight);
        card.setMaxSize(cardWidth, cardHeight);
        card.setMinSize(cardWidth, cardHeight);
        card.getStyleClass().add("product-card");
        addToolTip(card, "Click for more details.");

        ImageView productImage = new ImageView();
        productImage.setFitWidth(cardWidth - 3);
        productImage.setPreserveRatio(true);

        StackPane imageContainer = createImageContainer(product, productImage);

        Label genderLabel = new Label(product.getGender());
        genderLabel.getStyleClass().add("gender");

        Label nameLabel = new Label(product.getProductName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(cardWidth - 10);

        List<ProductSize> sizes = controller.getCachedProductSizes(product.getProductId());
        double lowestPrice = sizes.stream().mapToDouble(ProductSize::getPrice).min().orElse(0);
        Label priceLabel = new Label(showPrice(lowestPrice));
        priceLabel.getStyleClass().add("price");

        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #FE6C01; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 5;");
        viewButton.setOnAction(e -> showProductDetails(product));

        Button cartButton = new Button("Add to Cart");
        cartButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #FE6C01; -fx-border-color: #FE6C01; -fx-border-radius: 5; -fx-padding: 5 15;");

        HBox buttonBox = new HBox(10, viewButton, cartButton);
        buttonBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageContainer, genderLabel, nameLabel, priceLabel);

        VBox.setMargin(genderLabel, new Insets(8, 0, 0, 8));
        VBox.setMargin(nameLabel, new Insets(5, 8, 0, 8));
        VBox.setMargin(priceLabel, new Insets(5, 8, 5, 8));

        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: white; -fx-cursor: hand;");
            card.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.25)));

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

    private StackPane createImageContainer(Product product, ImageView productImage) {
        ImageCarousel carousel = new ImageCarousel();

        String imagePath = product.getImagePath();
        List<Image> cachedImages = controller.getCachedImages(imagePath);
        if (imagePath != null && !imagePath.isEmpty() && cachedImages != null) {
            carousel.setImages(cachedImages);
            Image firstImage = carousel.getCurrentImage();
            if (firstImage != null) {
                productImage.setImage(firstImage);
            }
        } else {
            productImage.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            if (imagePath != null && !imagePath.isEmpty()) {
                loadImageAsync(productImage, imagePath, carousel);
            }
        }

        Button rightButton = new Button();
        rightButton.setGraphic(new FontIcon("fas-chevron-right"));
        rightButton.getStyleClass().add("right-button");
        rightButton.setVisible(false);
        rightButton.setMaxHeight(Double.MAX_VALUE);

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
        leftButton.setMaxHeight(Double.MAX_VALUE);

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

    private void loadImageAsync(ImageView imageView, String imagePath, ImageCarousel carousel) {
        Task<List<Image>> loadTask = new Task<>() {
            @Override
            protected List<Image> call() throws Exception {
                List<Image> images = new ArrayList<>();

                if (imagePath != null && !imagePath.isEmpty()) {
                    File folder = new File(imagePath);

                    if (folder.exists() && folder.isDirectory()) {
                        File[] files = folder.listFiles((dir, name) ->
                                name.toLowerCase().endsWith(".jpg") ||
                                        name.toLowerCase().endsWith(".png") ||
                                        name.toLowerCase().endsWith(".jpeg")
                        );

                        if (files != null) {
                            for (File file : files) {
                                Image img = new Image(file.toURI().toString(), true);
                                images.add(img);
                            }
                        }
                    }
                }

                if (images.isEmpty()) {
                    images.add(new Image(
                            getClass().getResourceAsStream("/images/placeholder.png")
                    ));
                }

                return images;
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

    private HBox getSizeButtons(List<ProductSize> productSizes) {
        HBox sizeButtons = new HBox(8);
        sizeButtons.setAlignment(Pos.CENTER_LEFT);

        if (productSizes != null && !productSizes.isEmpty()) {
            for (ProductSize size : productSizes) {
                Button button = new Button(size.getSize());
                if (size.getStockQuantity() <= 0) {
                    isDisabled[0] = true;
                    button.addEventFilter(ActionEvent.ACTION, e -> e.consume());
                    button.getStyleClass().add("disabled");
                }

                button.getStyleClass().add("size-button");
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

        List<ProductSize> productSizes = controller.getCachedProductSizes(product.getProductId());

        VBox enlargedCard = createExpandedCardContent(product, productSizes);
        expandedCardModal = enlargedCard;

        body.getChildren().add(expandedCardModal);
        expandedCardModal.toFront();

        centerExpandedCard();

        body.widthProperty().addListener((obs, oldVal, newVal) -> centerExpandedCard());
        body.heightProperty().addListener((obs, oldVal, newVal) -> centerExpandedCard());

        modalScaleIn = new ScaleTransition(Duration.millis(300), expandedCardModal);
        modalScaleIn.setFromX(0.8);
        modalScaleIn.setFromY(0.8);
        modalScaleIn.setToX(1);
        modalScaleIn.setToY(1);
        modalScaleIn.play();

        overlay.setVisible(true);

        overlay.setOnMouseClicked(e -> closeExpandedCard());
    }

    private VBox createExpandedCardContent(Product product, List<ProductSize> productSizes) {
        ImageCarousel carousel = new ImageCarousel();

        VBox card = new VBox();
        card.setPrefSize(enlargedWidth, enlargedHeight);
        card.setMaxSize(enlargedWidth, enlargedHeight);
        card.setMinSize(enlargedWidth, enlargedHeight);
        card.getStyleClass().add("expanded-product-card");

        Button closeButton = new Button("✕");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> closeExpandedCard());

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.TOP_RIGHT);
        headerBox.getChildren().add(closeButton);

        ImageView productImage = new ImageView();
        productImage.setFitWidth(230);
        productImage.setPreserveRatio(true);
        productImage.setSmooth(true);

        String imagePath = product.getImagePath();
        List<Image> cachedImages = controller.getCachedImages(imagePath);
        if (imagePath != null && !imagePath.isEmpty() && cachedImages != null) {
            carousel.setImages(cachedImages);
            Image firstImage = carousel.getCurrentImage();
            if (firstImage != null) {
                productImage.setImage(firstImage);
            }
        } else {
            productImage.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            if (imagePath != null && !imagePath.isEmpty()) {
                loadImageAsync(productImage, imagePath, carousel);
            }
        }

        StackPane imageContainer = createImageContainer(product, productImage);
        imageContainer.setAlignment(Pos.CENTER);

        Label genderLabel = new Label(product.getGender());
        genderLabel.getStyleClass().add("gender");
        genderLabel.setStyle("");

        Label nameLabel = new Label(product.getProductName());
        nameLabel.getStyleClass().add("expanded-product-name");
        nameLabel.setWrapText(true);

        Label descriptionLabel = new Label(product.getDescription() != null ? product.getDescription() : "No description available");
        descriptionLabel.setWrapText(true);
        descriptionLabel.getStyleClass().add("description");

        unitPrice = productSizes.stream().mapToDouble(ProductSize::getPrice).min().orElse(0);
        priceLabel = new Label(showPrice(unitPrice));
        priceLabel.getStyleClass().add("expanded-price");

        Label sizeLabel = new Label("Select size:");
        sizeLabel.getStyleClass().add("select-size");

        HBox sizeButtonBox = getSizeButtons(productSizes);
        if (sizeButtonBox != null) {
            sizeButtonBox.setAlignment(Pos.CENTER_LEFT);
            sizeButtonBox.setPadding(new Insets(5, 0, 5, 0));
        }

        stockLabel = new Label();
        stockLabel.getStyleClass().add("stock");
        stockLabel.setVisible(false);
        stockLabel.setManaged(false);
        stockLabel.setStyle("");

        BorderPane sizeStockPane = new BorderPane();
        sizeStockPane.setLeft(sizeButtonBox);
        sizeStockPane.setRight(stockLabel);

        Label selectQuantityLabel = new Label("Select quantity: ");
        selectQuantityLabel.getStyleClass().add("select-quantity");

        quantityField = new TextField();
        quantityField.setMaxWidth(30);
        quantityField.setText("1");
        quantityField.getStyleClass().add("quantity-field");
        quantityField.setAlignment(Pos.CENTER);

        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty() && !newVal.matches("\\d*")) {
                quantityField.setText(oldVal);

                checkQuantity(product.getProductId());
            } else if (newVal != null && !newVal.isEmpty()) {
                checkQuantity(product.getProductId());
            } else {
                productQuantity = 0;
            }

            updatePriceLabel();
        });

        quantityField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {  // Focus lost
                String text = quantityField.getText();
                if (text == null || text.isEmpty()) {
                    quantityField.setText("1");
                    checkQuantity(product.getProductId());
                }
            }
        });

        minusQtyBtn = new Button("–");
        minusQtyBtn.setDisable(true);
        minusQtyBtn.setOnAction(e -> {
            quantityField.setFocusTraversable(false);

            productQuantity--;
            quantityField.setText(String.valueOf(productQuantity));
            updatePriceLabel();
            checkQuantity(product.getProductId());

            minusQtyBtn.requestFocus();

        });

        plusQtyBtn = new Button("+");
        plusQtyBtn.setOnAction(e -> {
            quantityField.setFocusTraversable(false);

            productQuantity++;
            quantityField.setText(String.valueOf(productQuantity));
            updatePriceLabel();
            checkQuantity(product.getProductId());

            plusQtyBtn.requestFocus();

        });

        HBox qtyActionBox = new HBox();
        qtyActionBox.getStyleClass().add("quantity-box");
        qtyActionBox.getChildren().addAll(minusQtyBtn, quantityField, plusQtyBtn);
        qtyActionBox.setAlignment(Pos.CENTER);

        HBox quantityBox = new HBox(5);
        quantityBox.getChildren().addAll(selectQuantityLabel, qtyActionBox);
        quantityBox.setAlignment(Pos.CENTER_LEFT);

        qtyErrorLabel.getStyleClass().add("quantity-error");
        qtyErrorLabel.setVisible(false);
        qtyErrorLabel.setManaged(false);

        BorderPane quantityPane = new BorderPane();
        quantityPane.setLeft(quantityBox);
        quantityPane.setRight(qtyErrorLabel);

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.getStyleClass().add("primary-button");
        addToCartButton.setOnAction(e -> {
            if(!isSizesSelected) {
                stockLabel.setText("Size is required.");
                stockLabel.setStyle("-fx-text-fill: red");
                stockLabel.setVisible(true);
                stockLabel.setManaged(true);
                return;
            } else {
                int productSizeId = controller.getSizeId(product.getProductId(), sizeSelected);
                productQuantity = Integer.parseInt(quantityField.getText());
                double subTotal = Double.parseDouble(priceLabel.getText().substring(3));

                if (controller.addToCart(productSizeId, productQuantity, subTotal)) {
                    alert.setAlertType(AlertMsg.AlertMsgType.SUCCESS);
                    alert.show(body, "Added to cart!", Pos.CENTER);
                    closeExpandedCard();
                }
            }

            closeExpandedCard();
        });

        Button continueButton = new Button("Continue Shopping");
        continueButton.getStyleClass().add("secondary-button");
        continueButton.setOnAction(e -> closeExpandedCard());

        HBox buttonBox = new HBox(15, addToCartButton, continueButton);
        buttonBox.setAlignment(Pos.CENTER);

        if (sizeButtonBox != null) {
            for (javafx.scene.Node node : sizeButtonBox.getChildren()) {
                if (node instanceof Button) {
                    Button sizeButton = (Button) node;
                    String sizeText = sizeButton.getText();

                    for (ProductSize ps : productSizes) {
                        if (ps.getSize().equals(sizeText)) {
                            int stock = ps.getStockQuantity();

                            sizeButton.setOnMouseEntered(e -> {
                                updateStockLabel(stock);
                                unitPrice = ps.getPrice();
                                priceLabel.setText(showPrice(unitPrice));
                            });

                            sizeButton.setOnMouseExited(e -> {
                                if (!isSizesSelected) {
                                    stockLabel.setVisible(false);
                                    stockLabel.setManaged(false);
                                }
                            });

                            sizeButton.setOnAction(e -> {
                                isSizesSelected = true;
                                sizeSelected = sizeButton.getText();
                                updateStockLabel(stock);
                                unitPrice = ps.getPrice();
                                priceLabel.setText(showPrice(unitPrice));
                                resetQtyField();

                                for (javafx.scene.Node n : sizeButtonBox.getChildren()) {
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

        VBox contentBox = new VBox(5);
        contentBox.getChildren().addAll(
                genderLabel,
                nameLabel,
                descriptionLabel,
                priceLabel,
                sizeLabel,
                sizeStockPane,
                quantityPane,
                buttonBox
        );
        VBox.setMargin(genderLabel, new Insets(10, 0, 0, 0));
        VBox.setMargin(buttonBox, new Insets(10, 0, 0, 0));

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(headerBox, imageContainer, contentBox);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(headerBox, new Insets(0, 10, 10, 0));

        card.getChildren().add(mainLayout);

        return card;
    }

    private boolean updateStockLabel(int stock) {
        boolean hasStock = false;

        if (stock > 0) {
            stockLabel.setText("In-stock");
            stockLabel.setStyle("-fx-text-fill: #28a745; ");
            hasStock = true;
        } else {
            stockLabel.setText("Out of stock");
            stockLabel.setStyle("-fx-text-fill: red");
            hasStock = false;
        }

        stockLabel.setVisible(true);
        stockLabel.setManaged(true);

        return hasStock;
    }

    private void updatePriceLabel() {
        int qty = Integer.parseInt(quantityField.getText());

        double totalPrice = unitPrice * qty;

        priceLabel.setText(showPrice(totalPrice));
    }

    private void resetQtyField() {
        quantityField.setText("1");
        productQuantity = 1;
    }

    private void checkQuantity(int productId) {
        productQuantity = Integer.parseInt(quantityField.getText());

        if (productQuantity == 1) {
            minusQtyBtn.setDisable(true);
        } else {
            minusQtyBtn.setDisable(false);
        }

        if (productQuantity == controller.getMaxStock(productId)) {
            plusQtyBtn.setDisable(true);
            qtyErrorLabel.setVisible(true);
            qtyErrorLabel.setManaged(true);
        } else {
            plusQtyBtn.setDisable(false);
            qtyErrorLabel.setVisible(false);
            qtyErrorLabel.setManaged(false);
        }
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
                overlay.setOnMouseClicked(null);
            });
            modalScaleOut.play();
        }
    }

    private void showProductDetails(Product product) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Product Details");
        dialog.setHeaderText(product.getProductName());

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        Label descriptionLabel = new Label("Description: " + (product.getDescription() != null ? product.getDescription() : "No description available"));
        descriptionLabel.setWrapText(true);

        Label categoryLabel = new Label("Category: " + product.getCategory());

        List<ProductSize> sizes = controller.getCachedProductSizes(product.getProductId());

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
            sizeGrid.add(new Label(showPrice(size.getPrice())), 1, row);
            sizeGrid.add(new Label(String.valueOf(size.getStockQuantity())), 2, row);
        }

        content.getChildren().addAll(descriptionLabel, categoryLabel, sizesLabel, sizeGrid);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    public Scene initialize() {
        body = new StackPane();

        overlay = new Rectangle();
        overlay.setFill(Color.rgb(0, 0, 0, 0.3));
        overlay.setVisible(false);

        overlay.widthProperty().bind(body.widthProperty());
        overlay.heightProperty().bind(body.heightProperty());

        loadingPane = createLoadingPane(loadingLabel);

        body.getChildren().addAll(buildProductGrid(), overlay, loadingPane);

        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(body);

        Scene scene = setScene(root, "home-page");

        loadAllDataOnce();

        return scene;
    }
}

