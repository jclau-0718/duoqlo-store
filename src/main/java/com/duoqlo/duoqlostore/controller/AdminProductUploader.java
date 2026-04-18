package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.Product;
import com.duoqlo.duoqlostore.model.ProductDAO;
import com.duoqlo.duoqlostore.model.ProductSize;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.*;

public class AdminProductUploader {
    private ProductDAO productDAO = new ProductDAO();

    // Database connection details
    private static final String DB_URL = "jdbc:sqlite:database.db";

    // Directory Details
    private Properties configProps = new Properties();
    private File configFile = new File("product_uploader_config.properties");
    private static final String LAST_IMAGE_PATH_KEY = "last.image.path";

    // Product Details
    private TextField nameField = new TextField();
    private Label autoSkuLabel = new Label("Select gender and category to generate SKU");
    private TextArea descArea = new TextArea();
    private ComboBox<String> genderCombo = new ComboBox<>();
    private ComboBox<String> categoryCombo = new ComboBox<>();
    private List<File> selectedImageFiles;
    private HBox imageBox = new HBox(10);

    // Product Size Rows
    private VBox sizeRowsContainer = new VBox(10);
    private ObservableList<SizeRow> sizeRows = FXCollections.observableArrayList();

    // Centralized data storage
    private Map<String, String> genderIdMap = new HashMap<>();
    private Map<String, String> categoryIdMap = new HashMap<>();

    // Current selections (initialized once)
    private String currentGender = null;
    private String currentGenderId = null;
    private String currentCategory = null;
    private String currentCategoryId = null;
    private String identifier = "";

    // Navigation
    private Pane parent;
    private VBox productDetailsPane;
    private VBox productSizePane;
    private ScrollPane sizeScrollPane;
    private Button nextButton;
    private Button backButton;
    private Button addProductButton;

    private Runnable showProductPage;

    private boolean isUpdateMode = false;

    private int productId;
    private String originalImagePath;

    public AdminProductUploader(Pane parent) {
        this.parent = parent;
    }

    public void setShowProductPage(Runnable showProductPage) {
        this.showProductPage = showProductPage;
    }

    public VBox show() {
        loadConfig();

        initializeData();

        createProductDetailsPane();
        createProductSizePane();

        return productDetailsPane;
    }

    public VBox show(Product product) {
        loadConfig();

        initializeData();
        if (isUpdateMode) {
            initializeFieldValue(product);
        }

        createProductDetailsPane();
        createProductSizePane();

        return productDetailsPane;
    }

    public void setUpdateMode() {
        this.isUpdateMode = true;
    }

    public void initializeFieldValue(Product product) {
        productId = product.getId();

        nameField.setText(product.getName());
        autoSkuLabel.setText(product.getSku());
        autoSkuLabel.getStyleClass().add("complete");
        identifier = product.getSku().substring(5);
        descArea.setText(product.getDescription());

        currentGender = product.getGender();
        currentCategory = product.getCategory();

        currentGenderId = genderIdMap.get(currentGender);
        currentCategoryId = categoryIdMap.get(currentCategory);

        List<String> categoriesForGender = productDAO.getCategoryNameWithGender(currentGender);
        if (categoriesForGender != null) {
            categoryCombo.setItems(FXCollections.observableArrayList(categoriesForGender));
            categoryCombo.setDisable(false);
        }

        genderCombo.setValue(currentGender);
        categoryCombo.setValue(currentCategory);

        originalImagePath = product.getImagePath();

        loadExistingImages(originalImagePath);

        initSizeRowContainer();
        for (ProductSize productSize : product.getSizes()) {
            String size = productSize.getSize();
            String stock = String.valueOf(productSize.getStockQuantity());
            String price = String.valueOf(productSize.getPrice());

            addSizeRowWithValue(size, stock, price);
        }
    }

    private void initializeData() {
        Map<String, String> genders = productDAO.getAllGenderWithIds();
        if (genders != null) {
            genderIdMap = genders;
        }

        Map<String, String> categories = productDAO.getAllCategoriesWithIds();
        if (categories != null) {
            categoryIdMap = categories;
        }

    }

    private void loadConfig() {
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                configProps.load(fis);
            } catch (IOException e) {
                System.err.println("Error loading config: " + e.getMessage());
            }
        }
    }

    private void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            configProps.store(fos, "Product Uploader Configuration");
        } catch (IOException e) {
            System.err.println("Error saving config: " + e.getMessage());
        }
    }

    private void createProductDetailsPane() {
        productDetailsPane = new VBox(20);
        productDetailsPane.setPadding(new Insets(20));
        productDetailsPane.setAlignment(Pos.TOP_CENTER);

        // Title
        Label titleLabel = new Label("Enter Product Details");
        titleLabel.getStyleClass().add("title");

        // Form Grid
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(30);
        grid.setAlignment(Pos.CENTER);

        // Row 0: Product Name
        Label nameLabel = new Label("Product Name:*");
        nameLabel.getStyleClass().add("name");
        nameField.setPrefWidth(400);
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        // Row 1: Auto-generated SKU
        Label skuLabel = new Label("SKU (Auto):");
        skuLabel.getStyleClass().add("sku");
        autoSkuLabel.getStyleClass().addAll("auto-sku");
        autoSkuLabel.setWrapText(true);
        grid.add(skuLabel, 0, 1);
        grid.add(autoSkuLabel, 1, 1);

        // Row 2: Gender
        Label genderLabel = new Label("Gender:*");
        genderLabel.getStyleClass().add("gender");
        genderCombo.getItems().addAll(genderIdMap.keySet());
        genderCombo.setPrefWidth(400);
        grid.add(genderLabel, 0, 2);
        grid.add(genderCombo, 1, 2);

        // Row 3: Category
        Label categoryLabel = new Label("Category:*");
        categoryLabel.getStyleClass().add("category");
        categoryCombo.setPrefWidth(400);
        grid.add(categoryLabel, 0, 3);
        grid.add(categoryCombo, 1, 3);

        // Row 4: Description
        Label descLabel = new Label("Description:");
        descLabel.getStyleClass().add("description");
        descArea.setPrefRowCount(4);
        descArea.setPrefWidth(400);
        grid.add(descLabel, 0, 4);
        grid.add(descArea, 1, 4);

        // Row 5 Product Images
        Label imagesLabel = new Label("Product Images:*");
        imagesLabel.getStyleClass().add("images");
        Button browseButton = new Button("Browse Images");

        VBox imageSection = new VBox(10);
        imageSection.getChildren().addAll(browseButton, imageBox);

        grid.add(imagesLabel, 0, 5);
        grid.add(imageSection, 1, 5);

        // Setup category cascade
        if(!isUpdateMode) {
            categoryCombo.setDisable(true);
        }

        setupCategoryCascade();

        // Browse button action
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Product Images");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp")
            );

            //Load last used directory
            String lastPath = configProps.getProperty(LAST_IMAGE_PATH_KEY, System.getProperty("user.home"));
            File lastDirectory = new File(lastPath);
            if (lastDirectory.exists() && lastDirectory.isDirectory()) {
                fileChooser.setInitialDirectory(lastDirectory);
            }
            Window window = browseButton.getScene().getWindow();
            selectedImageFiles = fileChooser.showOpenMultipleDialog(window);


            // Save the parent directory (one level up)
            if (selectedImageFiles != null && !selectedImageFiles.isEmpty()) {
                File firstFile = selectedImageFiles.get(0);
                File imageFolder = firstFile.getParentFile();
                File parentFolder = imageFolder != null ? imageFolder.getParentFile() : null;

                if (parentFolder != null && parentFolder.exists()) {
                    configProps.setProperty(LAST_IMAGE_PATH_KEY, parentFolder.getAbsolutePath());
                    saveConfig();
                } else if (imageFolder != null && imageFolder.exists()) {
                    // Fallback to image folder if parent doesn't exist
                    configProps.setProperty(LAST_IMAGE_PATH_KEY, imageFolder.getAbsolutePath());
                    saveConfig();
                }
            }

            imageBox.getChildren().clear();
            if (selectedImageFiles != null && !selectedImageFiles.isEmpty()) {
                for (File file : selectedImageFiles) {
                    try {
                        Image img = new Image(file.toURI().toString(), 100, 100, true, true);
                        ImageView imgView = new ImageView(img);
                        imageBox.getChildren().add(imgView);
                    } catch (Exception ex) {
                        System.err.println("Error loading image: " + ex.getMessage());
                    }
                }

                Label countLabel = new Label(selectedImageFiles.size() + " images selected");
                countLabel.getStyleClass().add("count");
                imageBox.getChildren().add(countLabel);
            }
        });

        //Right allign the first column
        ColumnConstraints col = new ColumnConstraints();
        col.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().add(col);

        // Next Button
        nextButton = new Button("Next");
        nextButton.setOnAction(e -> goToSizePane());

        productDetailsPane.getChildren().addAll(titleLabel, grid, nextButton);
        productDetailsPane.getStylesheets().add(getClass().getResource("/css/uploader.css").toExternalForm());
    }

    private void setupCategoryCascade() {
        genderCombo.setOnAction(e -> {
            currentGender = genderCombo.getValue();
            if (currentGender != null) {
                currentGenderId = genderIdMap.get(currentGender);

                List<String> categoriesForGender = productDAO.getCategoryNameWithGender(currentGender);
                if (categoriesForGender != null && !categoriesForGender.isEmpty()) {
                    categoryCombo.setItems(FXCollections.observableArrayList(categoriesForGender));
                    categoryCombo.setDisable(false);
                    if (!isUpdateMode) {
                        categoryCombo.getSelectionModel().clearSelection();
                    }
                } else {
                    categoryCombo.getItems().clear();
                    categoryCombo.setDisable(true);
                    showAlert(Alert.AlertType.WARNING, "No categories available for " + currentGender);
                }
            } else {
                categoryCombo.setDisable(true);
            }
            updateAutoSku();
        });

        categoryCombo.setOnAction(e -> {
            currentCategory = categoryCombo.getValue();
            if (currentCategory != null) {
                currentCategoryId = categoryIdMap.get(currentCategory);
            }
            updateAutoSku();
        });
    }

    private void updateAutoSku() {
        if (currentGenderId != null && currentCategoryId != null) {
            if (!isUpdateMode) {
                identifier = String.valueOf(System.currentTimeMillis()).substring(9);
            }
            String sku = currentGenderId + "-" + currentCategoryId + "-" + identifier;
            autoSkuLabel.setText(sku);
            autoSkuLabel.getStyleClass().add("complete");
        } else {
            autoSkuLabel.setText("Select gender and category to generate SKU");
            autoSkuLabel.getStyleClass().remove("complete");
        }
    }

    private void goToSizePane() {
        // Validate product details using the stored current values
        if (nameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter product name.");
            return;
        }

        if (currentGender == null) {
            showAlert(Alert.AlertType.ERROR, "Please select gender.");
            return;
        }

        if (currentCategory == null) {
            showAlert(Alert.AlertType.ERROR, "Please select category.");
            return;
        }

        if (selectedImageFiles == null || selectedImageFiles.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please select at least one product image.");
            return;
        }

        parent.getChildren().setAll(productSizePane);
    }

    private void goToProductDetailsPane() {
        parent.getChildren().setAll(productDetailsPane);
    }

    private void createProductSizePane() {
        productSizePane = new VBox(20);
        productSizePane.setPadding(new Insets(20));
        productSizePane.setAlignment(Pos.TOP_CENTER);

        // Title
        Label titleLabel = new Label("Product Sizes & Pricing");
        titleLabel.getStyleClass().add("title");

        // Info label
        Label infoLabel = new Label("Add sizes and prices for this product. Start typing in a row to add more sizes.");
        infoLabel.getStyleClass().add("info");

        if (!isUpdateMode) {
            initSizeRowContainer();
        }

        // Add initial row
        addNewSizeRow();

        // ScrollPane for size rows
        sizeScrollPane = new ScrollPane(sizeRowsContainer);
        sizeScrollPane.setFitToWidth(true);
        sizeScrollPane.setPrefHeight(300);

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        backButton = new Button("Back");
        backButton.setOnAction(e -> goToProductDetailsPane());

        addProductButton = new Button("Add Product");
        addProductButton.setOnAction(e -> {
            if (addProduct()) {
                resetForm();

                showProductPage.run();
            } else {
                return;
            }
        });


        buttonBox.getChildren().addAll(backButton, addProductButton);

        productSizePane.setSpacing(20);
        productSizePane.getChildren().addAll(titleLabel, infoLabel, sizeScrollPane, buttonBox);
        productSizePane.getStylesheets().add(getClass().getResource("/css/uploader.css").toExternalForm());
    }

    private void initSizeRowContainer() {
        Label sizeHeader = new Label("Size");
        sizeHeader.setPrefWidth(150);
        sizeHeader.getStyleClass().add("header");

        Label stockHeader = new Label("Stock Quantity");
        stockHeader.setPrefWidth(150);
        stockHeader.getStyleClass().add("header");

        Label priceHeader = new Label("Price (RM)");
        priceHeader.setPrefWidth(150);
        priceHeader.getStyleClass().add("header");

        Label actionHeader = new Label("Actions");
        actionHeader.setPrefWidth(50);
        actionHeader.getStyleClass().add("header");

        HBox headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER);
        headerRow.setPadding(new Insets(5));
        headerRow.getChildren().addAll(sizeHeader, stockHeader, priceHeader, actionHeader);

        // Size rows container
        sizeRowsContainer.setAlignment(Pos.CENTER);
        sizeRowsContainer.setPadding(new Insets(10));
        sizeRowsContainer.getChildren().add(headerRow);
    }

    private void addSizeRowWithValue(String size, String stock, String price) {
        SizeRow sizeRow = new SizeRow();
        sizeRows.add(sizeRow);

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(5));

        TextField sizeField = new TextField(size);
        sizeField.setPromptText("e.g., S, M, L, XL");
        sizeField.setPrefWidth(150);

        TextField stockField = new TextField(stock);
        stockField.setPromptText("Quantity");
        stockField.setPrefWidth(150);
        stockField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                stockField.setText(oldVal);
            }
        });

        TextField priceField = new TextField(price);
        priceField.setPromptText("0.00");
        priceField.setPrefWidth(150);
        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                priceField.setText(oldVal);
            }
        });

        Button deleteButton = new Button("✕");
        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setPrefWidth(50);
        deleteButton.setOnAction(e -> {
            sizeRowsContainer.getChildren().remove(row);
            sizeRows.remove(sizeRow);
        });

        row.getChildren().addAll(sizeField, stockField, priceField, deleteButton);

        sizeField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused && isLastRow(row)) {
                addNewSizeRow();
            }
        });
        stockField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused && isLastRow(row)) {
                addNewSizeRow();
            }
        });
        priceField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused && isLastRow(row)) {
                addNewSizeRow();
            }
        });

        sizeRowsContainer.getChildren().add(row);

        sizeRow.setSizeField(sizeField);
        sizeRow.setStockField(stockField);
        sizeRow.setPriceField(priceField);
    }

    private void addNewSizeRow() {
        SizeRow sizeRow = new SizeRow();
        sizeRows.add(sizeRow);

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(5));

        TextField sizeField = new TextField();
        sizeField.setPromptText("e.g., S, M, L, XL");
        sizeField.setPrefWidth(150);

        TextField stockField = new TextField();
        stockField.setPromptText("Quantity");
        stockField.setPrefWidth(150);
        stockField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                stockField.setText(oldVal);
            }
        });

        TextField priceField = new TextField();
        priceField.setPromptText("0.00");
        priceField.setPrefWidth(150);
        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                priceField.setText(oldVal);
            }
        });

        Button deleteButton = new Button("✕");
        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setPrefWidth(50);
        deleteButton.setOnAction(e -> {
            sizeRowsContainer.getChildren().remove(row);
            sizeRows.remove(sizeRow);
        });

        row.getChildren().addAll(sizeField, stockField, priceField, deleteButton);

        sizeField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused && isLastRow(row)) {
                addNewSizeRow();
            }
        });
        stockField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused && isLastRow(row)) {
                addNewSizeRow();
            }
        });
        priceField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused && isLastRow(row)) {
                addNewSizeRow();
            }
        });

        sizeRowsContainer.getChildren().add(row);

        sizeRow.setSizeField(sizeField);
        sizeRow.setStockField(stockField);
        sizeRow.setPriceField(priceField);
    }

    private boolean isLastRow(HBox row) {
        return sizeRowsContainer.getChildren().indexOf(row) == sizeRowsContainer.getChildren().size() - 1;
    }

    private boolean addProduct() {
        // Validate size rows
        List<SizeRow> validRows = new ArrayList<>();
        for (SizeRow row : sizeRows) {
            String size = row.getSizeField().getText().trim().toUpperCase();
            String stock = row.getStockField().getText().trim();
            String price = row.getPriceField().getText().trim();

            if (!size.isEmpty() || !stock.isEmpty() || !price.isEmpty()) {
                if (size.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Please enter size for all filled rows.");
                    return false;
                }
                if (stock.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Please enter stock quantity for size: " + size);
                    return false;
                }
                if (price.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Please enter price for size: " + size);
                    return false;
                }
                validRows.add(row);
            }
        }

        if (validRows.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please add at least one size variant.");
            return false;
        }

        try {
            // Get form values using the stored current selections
            String name = toTitleCase(nameField.getText().trim());
            String productSKU = autoSkuLabel.getText();
            String description = descArea.getText().trim();

            // Build product folder path using stored values
            Path productPath = buildProductPath(currentCategory, name, productSKU);
            String imagePath = productPath.toString();

            // Save to database
            if (!isUpdateMode) {
                int productId = productDAO.insertProduct(productSKU, name, currentCategoryId, currentGenderId, imagePath, description);

                if (productId > 0) {
                    // Save size variants
                    for (SizeRow row : validRows) {
                        String size = row.getSizeField().getText().trim().toUpperCase();
                        int stock = Integer.parseInt(row.getStockField().getText().trim());
                        double price = Double.parseDouble(row.getPriceField().getText().trim());

                        String productsizeSKU = productSKU + "-" + size;
                        productDAO.insertProductSize(productsizeSKU, productId, size, stock, price);
                    }
                }
            } else {
                if (!originalImagePath.equals(imagePath)) {
                    Path oldPath = Paths.get(originalImagePath);

                    try {
                        Files.move(oldPath, productPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Failed to rename product directory.");
                        return false;
                    }
                }

                productDAO.updateProduct(productId, productSKU, name, currentCategoryId, currentGenderId, imagePath, description);

                productDAO.deleteProductSize(productId);
                // Save size variants
                for (SizeRow row : validRows) {
                    String size = row.getSizeField().getText().trim().toUpperCase();
                    int stock = Integer.parseInt(row.getStockField().getText().trim());
                    double price = Double.parseDouble(row.getPriceField().getText().trim());

                    String productsizeSKU = productSKU + "-" + size;
                    productDAO.insertProductSize(productsizeSKU, productId, size, stock, price);
                }
            }

            // Create directories and save images
            if (!Files.exists(productPath)) {
                Files.createDirectories(productPath);
            }

            //Insert images into product path
            for (int i = 0; i < selectedImageFiles.size(); i++) {
                File imgFile = selectedImageFiles.get(i);
                String ext = getFileExtension(imgFile.getName());
                Path target = productPath.resolve("image" + (i + 1) + "." + ext);
                Files.copy(imgFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            }


            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to save images: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format for stock or price.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error: " + ex.getMessage());
        }

        return false;
    }

    private Path buildProductPath(String category, String productName, String sku) {
        String productFolder = sku;
        String projectPath = System.getProperty("user.dir");

        return Paths.get(projectPath, "products", productFolder);
    }

    private void loadExistingImages(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) return;

        try {
            Path path = Paths.get(imagePath);
            if (!path.isAbsolute()) {
                path = Paths.get(System.getProperty("user.dir")).resolve(path);
            }

            if (!Files.exists(path)) return;

            imageBox.getChildren().clear();
            List<File> existingImages = new ArrayList<>();

            Files.list(path)
                    .filter(p -> {
                        String name = p.getFileName().toString().toLowerCase();
                        return name.endsWith(".jpg") || name.endsWith(".jpeg")
                                || name.endsWith(".png") || name.endsWith(".gif")
                                || name.endsWith(".webp");
                    })
                    .sorted() // image1, image2, image3...
                    .forEach(p -> {
                        try {
                            Image img = new Image(p.toUri().toString(), 100, 100, true, true);
                            ImageView imgView = new ImageView(img);
                            imageBox.getChildren().add(imgView);
                            existingImages.add(p.toFile());
                        } catch (Exception ex) {
                            System.err.println("Error loading image: " + ex.getMessage());
                        }
                    });

            selectedImageFiles = existingImages;

            if (!existingImages.isEmpty()) {
                Label countLabel = new Label(existingImages.size() + " images loaded");
                countLabel.getStyleClass().add("count");
                imageBox.getChildren().add(countLabel);
            }

        } catch (IOException e) {
            System.err.println("Error loading existing images: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index > 0 && index < filename.length() - 1) {
            return filename.substring(index + 1).toLowerCase();
        }
        return "jpg";
    }

    private void resetForm() {
        nameField.clear();
        descArea.clear();
        genderCombo.getSelectionModel().clearSelection();
        categoryCombo.getItems().clear();
        categoryCombo.setDisable(true);
        for (Node node : imageBox.getChildren()) {
            if (node instanceof ImageView iv) {
                iv.setImage(null); // 🔥 releases file lock
            }
        }
        if (imageBox != null) {
            for (Node node : imageBox.getChildren()) {
                if (node instanceof ImageView) {
                    ImageView iv = (ImageView) node;
                    iv.setImage(null); // Release the image
                }
            }
            imageBox.getChildren().clear();
        }
        selectedImageFiles = null;

        // Reset stored values
        currentGender = null;
        currentGenderId = null;
        currentCategory = null;
        currentCategoryId = null;

        autoSkuLabel.setText("Select gender and category to generate SKU");

        // Clear size rows
        sizeRows.clear();
        sizeRowsContainer.getChildren().clear();

        // Add header back
        HBox headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(5));

        Label sizeHeader = new Label("Size");
        sizeHeader.setPrefWidth(150);

        Label stockHeader = new Label("Stock Quantity");
        stockHeader.setPrefWidth(150);

        Label priceHeader = new Label("Price (RM)");
        priceHeader.setPrefWidth(150);

        Label actionHeader = new Label("Actions");
        actionHeader.setPrefWidth(50);

        headerRow.getChildren().addAll(sizeHeader, stockHeader, priceHeader, actionHeader);
        sizeRowsContainer.getChildren().add(headerRow);

        // Add initial row
        addNewSizeRow();

        // Go back to first page
        parent.getChildren().setAll(productDetailsPane);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Success");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private String toTitleCase(String text) {
        if (text == null || text.isEmpty()) return text;

        String[] words = text.toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    // Inner class to hold size row data
    private class SizeRow {
        private TextField sizeField;
        private TextField stockField;
        private TextField priceField;

        public TextField getSizeField() { return sizeField; }
        public TextField getStockField() { return stockField; }
        public TextField getPriceField() { return priceField; }

        public void setSizeField(TextField sizeField) { this.sizeField = sizeField; }
        public void setStockField(TextField stockField) { this.stockField = stockField; }
        public void setPriceField(TextField priceField) { this.priceField = priceField; }
    }
}