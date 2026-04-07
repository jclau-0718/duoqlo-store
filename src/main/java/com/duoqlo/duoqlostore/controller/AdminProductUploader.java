package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.ProductDAO;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.Properties;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class AdminProductUploader extends Application {
    private ProductDAO productDAO = new ProductDAO();

    // Database connection details
    private static final String DB_URL = "jdbc:sqlite:database.db";

    // Directory Details
    private Properties configProps = new Properties();
    private File configFile = new File("product_uploader_config.properties");
    private static final String LAST_IMAGE_PATH_KEY = "last.image.path";

    // Product Details
    private TextField nameField;
    private TextArea descArea;
    private ComboBox<String> genderCombo;
    private ComboBox<String> categoryCombo;
    private List<File> selectedImageFiles;
    private HBox imageBox = new HBox(10);

    // Product Size Rows
    private VBox sizeRowsContainer;
    private ObservableList<SizeRow> sizeRows = FXCollections.observableArrayList();

    // Centralized data storage
    private Map<String, String> genderIdMap = new HashMap<>();
    private Map<String, String> categoryIdMap = new HashMap<>();

    // Current selections (initialized once)
    private String currentGender = null;
    private String currentGenderId = null;
    private String currentCategory = null;
    private String currentCategoryId = null;

    // Navigation
    private BorderPane mainLayout;
    private VBox productDetailsPane;
    private VBox productSizePane;
    private ScrollPane sizeScrollPane;
    private Button nextButton;
    private Button backButton;
    private Button addProductButton;

    private Stage primaryStage;
    private Label autoSkuLabel;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Admin - Add New Product");

        loadConfig();

        // Initialize data from database once
        initializeData();

        // Create main layout
        mainLayout = new BorderPane();

        // Create both panes
        createProductDetailsPane();
        createProductSizePane();

        // Start with product details pane
        mainLayout.setCenter(productDetailsPane);

        Scene scene = new Scene(mainLayout, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeData() {
        // Load all genders and their IDs once
        Map<String, String> genders = productDAO.getAllGenderWithIds();
        if (genders != null) {
            genderIdMap = genders;
        }

        // Load all categories and their IDs once
        Map<String, String> categories = productDAO.getAllCategoriesWithIds();
        if (categories != null) {
            categoryIdMap = categories;
        }

        System.out.println("Data initialized - Genders: " + genderIdMap.size() + ", Categories: " + categoryIdMap.size());
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
        Label titleLabel = new Label("Product Details");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #EE5702;");

        // Form Grid
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        // Row 0: Product Name
        Label nameLabel = new Label("Product Name:*");
        nameField = new TextField();
        nameField.setPrefWidth(400);
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);

        // Row 1: Auto-generated SKU
        Label skuLabel = new Label("SKU (Auto):");
        autoSkuLabel = new Label("Will be auto-generated after selections");
        autoSkuLabel.setStyle("-fx-text-fill: #EE5702; -fx-font-weight: bold;");
        autoSkuLabel.setWrapText(true);
        grid.add(skuLabel, 0, 1);
        grid.add(autoSkuLabel, 1, 1);

        // Row 2: Gender
        Label genderLabel = new Label("Gender:*");
        genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll(genderIdMap.keySet());
        genderCombo.setPrefWidth(400);
        grid.add(genderLabel, 0, 2);
        grid.add(genderCombo, 1, 2);

        // Row 3: Category
        Label categoryLabel = new Label("Category:*");
        categoryCombo = new ComboBox<>();
        categoryCombo.setPrefWidth(400);
        categoryCombo.setDisable(true);
        grid.add(categoryLabel, 0, 3);
        grid.add(categoryCombo, 1, 3);

        // Row 4: Description
        Label descLabel = new Label("Description:");
        descArea = new TextArea();
        descArea.setPrefRowCount(4);
        descArea.setPrefWidth(400);
        grid.add(descLabel, 0, 4);
        grid.add(descArea, 1, 4);

        // Row 5 Product Images
        Label imagesLabel = new Label("Product Images:*");
        Button browseButton = new Button("Browse Images");
        browseButton.setStyle("-fx-background-color: #EE5702; -fx-text-fill: white;");

        VBox imageSection = new VBox(10);
        imageSection.getChildren().addAll(browseButton, imageBox);

        grid.add(imagesLabel, 0, 5);
        grid.add(imageSection, 1, 5);

        // Setup category cascade
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

            selectedImageFiles = fileChooser.showOpenMultipleDialog(primaryStage);

            // Save the parent directory (one level up)
            if (selectedImageFiles != null && !selectedImageFiles.isEmpty()) {
                File firstFile = selectedImageFiles.get(0);
                File imageFolder = firstFile.getParentFile();
                File parentFolder = imageFolder != null ? imageFolder.getParentFile() : null;

                if (parentFolder != null && parentFolder.exists()) {
                    configProps.setProperty(LAST_IMAGE_PATH_KEY, parentFolder.getAbsolutePath());
                    saveConfig();
                    System.out.println("Saved path: " + parentFolder.getAbsolutePath());
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
                        imgView.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px; -fx-padding: 2px;");
                        imageBox.getChildren().add(imgView);
                    } catch (Exception ex) {
                        System.err.println("Error loading image: " + ex.getMessage());
                    }
                }

                Label countLabel = new Label(selectedImageFiles.size() + " images selected");
                countLabel.setStyle("-fx-text-fill: green; -fx-font-size: 10px;");
                imageBox.getChildren().add(countLabel);
            }
        });

        // Next Button
        nextButton = new Button("Next →");
        nextButton.setStyle("-fx-background-color: #EE5702; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        nextButton.setOnAction(e -> goToSizePane());

        productDetailsPane.getChildren().addAll(titleLabel, grid, nextButton);
    }

    private void setupCategoryCascade() {
        genderCombo.setOnAction(e -> {
            currentGender = genderCombo.getValue();
            if (currentGender != null) {
                // Get ID from pre-loaded map
                currentGenderId = genderIdMap.get(currentGender);

                // Filter categories by gender
                List<String> categoriesForGender = productDAO.getCategoryNameWithGender(currentGender);

                if (categoriesForGender != null && !categoriesForGender.isEmpty()) {
                    categoryCombo.setItems(FXCollections.observableArrayList(categoriesForGender));
                    categoryCombo.setDisable(false);
                    categoryCombo.getSelectionModel().clearSelection();
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
                // Get ID from pre-loaded map
                currentCategoryId = categoryIdMap.get(currentCategory);
            }
            updateAutoSku();
        });
    }

    private int getNextProductId() {
        String query = "SELECT MAX(product_id) FROM product";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int maxId = rs.getInt(1);
                return maxId + 1;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 1;
    }

    private void updateAutoSku() {
        if (currentGenderId != null && currentCategoryId != null) {
            int nextId = getNextProductId();
            String sku = currentGenderId + "-" + currentCategoryId + "-" + nextId;
            autoSkuLabel.setText(sku);
            autoSkuLabel.setStyle("-fx-text-fill: #EE5702; -fx-font-weight: bold; -fx-font-size: 12px;");
        } else {
            autoSkuLabel.setText("Select gender and category to generate SKU");
            autoSkuLabel.setStyle("-fx-text-fill: #999999; -fx-font-style: italic;");
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

        mainLayout.setCenter(productSizePane);
    }

    private void goToProductDetailsPane() {
        mainLayout.setCenter(productDetailsPane);
    }

    private void createProductSizePane() {
        productSizePane = new VBox(20);
        productSizePane.setPadding(new Insets(20));
        productSizePane.setAlignment(Pos.TOP_CENTER);

        // Title
        Label titleLabel = new Label("Product Sizes & Pricing");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #EE5702;");

        // Info label
        Label infoLabel = new Label("Add sizes and prices for this product. Start typing in a row to add more sizes.");
        infoLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");

        // Size rows container
        sizeRowsContainer = new VBox(10);
        sizeRowsContainer.setPadding(new Insets(10));
        sizeRowsContainer.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px; -fx-padding: 10;");

        // Add header row
        HBox headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(5));

        Label sizeHeader = new Label("Size");
        sizeHeader.setPrefWidth(150);
        sizeHeader.setStyle("-fx-font-weight: bold;");

        Label stockHeader = new Label("Stock Quantity");
        stockHeader.setPrefWidth(150);
        stockHeader.setStyle("-fx-font-weight: bold;");

        Label priceHeader = new Label("Price (RM)");
        priceHeader.setPrefWidth(150);
        priceHeader.setStyle("-fx-font-weight: bold;");

        Label actionHeader = new Label("");
        actionHeader.setPrefWidth(50);

        headerRow.getChildren().addAll(sizeHeader, stockHeader, priceHeader, actionHeader);
        sizeRowsContainer.getChildren().add(headerRow);

        // Add initial row
        addNewSizeRow();

        // ScrollPane for size rows
        sizeScrollPane = new ScrollPane(sizeRowsContainer);
        sizeScrollPane.setFitToWidth(true);
        sizeScrollPane.setPrefHeight(300);

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        backButton = new Button("← Back");
        backButton.setStyle("-fx-background-color: #666666; -fx-text-fill: white; -fx-padding: 10 20;");
        backButton.setOnAction(e -> goToProductDetailsPane());

        addProductButton = new Button("Add Product");
        addProductButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        addProductButton.setOnAction(e -> addProduct());

        buttonBox.getChildren().addAll(backButton, addProductButton);

        productSizePane.getChildren().addAll(titleLabel, infoLabel, sizeScrollPane, buttonBox);
    }

    private void addNewSizeRow() {
        SizeRow sizeRow = new SizeRow();
        sizeRows.add(sizeRow);

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
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

    private void addProduct() {
        // Validate size rows
        List<SizeRow> validRows = new ArrayList<>();
        for (SizeRow row : sizeRows) {
            String size = row.getSizeField().getText().trim().toUpperCase();
            String stock = row.getStockField().getText().trim();
            String price = row.getPriceField().getText().trim();

            if (!size.isEmpty() || !stock.isEmpty() || !price.isEmpty()) {
                if (size.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Please enter size for all filled rows.");
                    return;
                }
                if (stock.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Please enter stock quantity for size: " + size);
                    return;
                }
                if (price.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Please enter price for size: " + size);
                    return;
                }
                validRows.add(row);
            }
        }

        if (validRows.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please add at least one size variant.");
            return;
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

            // Create directories and save images
            if (!Files.exists(productPath)) {
                Files.createDirectories(productPath);
            }

            for (int i = 0; i < selectedImageFiles.size(); i++) {
                File imgFile = selectedImageFiles.get(i);
                String ext = getFileExtension(imgFile.getName());
                Path target = productPath.resolve("image" + (i + 1) + "." + ext);
                Files.copy(imgFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            }

            // Build success message
            StringBuilder message = new StringBuilder();
            message.append("Product Added Successfully!\n\n");
            message.append("Product ID: ").append(productId).append("\n");
            message.append("Name: ").append(name).append("\n");
            message.append("SKU: ").append(productSKU).append("\n");
            message.append("Gender: ").append(currentGender).append("\n");
            message.append("Category: ").append(currentCategory).append("\n\n");
            message.append("Sizes:\n");

            for (SizeRow row : validRows) {
                String size = row.getSizeField().getText().trim().toUpperCase();
                String stock = row.getStockField().getText().trim();
                String price = row.getPriceField().getText().trim();
                message.append("  - ").append(size).append(" | Stock: ").append(stock).append(" | RM ").append(price).append("\n");
            }

            message.append("\nImages saved to: ").append(productPath.toString());
            showAlert(Alert.AlertType.INFORMATION, message.toString());

            // Reset form
            resetForm();

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to save images: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid number format for stock or price.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error: " + ex.getMessage());
        }
    }

    private Path buildProductPath(String category, String productName, String sku) {
        String sanitizedName = productName.replaceAll("[^a-zA-Z0-9\\s]", "_").replaceAll("\\s+", "_");
        String productFolder = sku + "_" + sanitizedName;
        String projectPath = System.getProperty("user.dir");

        return Paths.get(projectPath, "products", currentGender, category, productFolder);
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
        imageBox.getChildren().clear();
        selectedImageFiles = null;

        // Reset stored values
        currentGender = null;
        currentGenderId = null;
        currentCategory = null;
        currentCategoryId = null;

        autoSkuLabel.setText("Will be auto-generated after selections");

        // Clear size rows
        sizeRows.clear();
        sizeRowsContainer.getChildren().clear();

        // Add header back
        HBox headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(5));
        Label sizeHeader = new Label("Size");
        sizeHeader.setPrefWidth(150);
        sizeHeader.setStyle("-fx-font-weight: bold;");
        Label stockHeader = new Label("Stock Quantity");
        stockHeader.setPrefWidth(150);
        stockHeader.setStyle("-fx-font-weight: bold;");
        Label priceHeader = new Label("Price (RM)");
        priceHeader.setPrefWidth(150);
        priceHeader.setStyle("-fx-font-weight: bold;");
        Label actionHeader = new Label("");
        actionHeader.setPrefWidth(50);
        headerRow.getChildren().addAll(sizeHeader, stockHeader, priceHeader, actionHeader);
        sizeRowsContainer.getChildren().add(headerRow);

        // Add initial row
        addNewSizeRow();

        // Go back to first page
        mainLayout.setCenter(productDetailsPane);
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
        public void setSizeField(TextField sizeField) { this.sizeField = sizeField; }

        public TextField getStockField() { return stockField; }
        public void setStockField(TextField stockField) { this.stockField = stockField; }

        public TextField getPriceField() { return priceField; }
        public void setPriceField(TextField priceField) { this.priceField = priceField; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}