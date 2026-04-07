package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.*;
import com.duoqlo.duoqlostore.view.CartPage;
import com.duoqlo.duoqlostore.view.UserDashboard;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DashboardController {
    private User user;
    private String role;
    private ProductDAO productDAO = new ProductDAO();
    private CartDAO cartDAO = new CartDAO();
    private CartController cartController = new CartController();
    private Cart cart;

    private Map<Integer, List<ProductSize>> sizesCache = new HashMap<>();
    private Map<String, List<Image>> imageCache = new ConcurrentHashMap<>();
    private List<Product> allProducts;
    private List<Product> displayedProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();

    private String sizeSelected = null;
    private String categorySelected = null;
    private String priceSelected = null;
    private String sortingSelected = null;
    private boolean isSorted = false;

    public void setUser(User user){
        this.user = user;
    }

    public void openDashboard(Stage stage){
        UserDashboard userDash = new UserDashboard();

        role = user.getRole(user.getId());

        if(role != null) {
            System.out.println("Role: "+role);
            if (role.equals("CUSTOMER")) {
                stage.setScene(userDash.initialize());
            } else if (role.equals("ADMIN")) {
                stage.setScene(userDash.initialize());
            } else {
                System.out.println("User role invalid");
            }
        } else {
            System.out.println("Role: "+role);
            System.out.println("Error! User not found");
        }

        System.out.println(user.getId());
    }

    public void openCartPage(){
        cartController.setUser(this.user);

        CartPage cartPage = new CartPage(cartController);
        Navigator.goTo(cartPage.initialize());
    }

    public void loadAllDataOnce(Runnable onSuccess) {
        Task<Void> preloadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Fetching products...");
                allProducts = productDAO.getAllProducts();

                updateMessage("Loading product sizes...");
                for (Product product : allProducts) {
                    getCachedProductSizes(product.getProductId());
                }

                updateMessage("Loading product images...");
                preloadAllImages();

                updateMessage("Preparing UI...");
                return null;
            }
        };

        preloadTask.setOnSucceeded(e -> {
            if (onSuccess != null) {
                onSuccess.run();
            }
        });

        new Thread(preloadTask).start();
    }

    // Also add this method to get the Task for message binding:
    public Task<Void> createPreloadTask(Runnable onSuccess) {
        Task<Void> preloadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Fetching products...");
                allProducts = productDAO.getAllProducts();

                updateMessage("Loading product sizes...");
                for (Product product : allProducts) {
                    getCachedProductSizes(product.getProductId());
                }

                updateMessage("Loading product images...");
                preloadAllImages();

                updateMessage("Preparing UI...");
                return null;
            }
        };

        preloadTask.setOnSucceeded(e -> {
            if (onSuccess != null) {
                onSuccess.run();
            }
        });

        return preloadTask;
    }

    private void preloadAllImages() {
        for (Product product : allProducts) {
            String imagePath = product.getImagePath();
            if (imagePath != null && !imagePath.isEmpty() && !imageCache.containsKey(imagePath)) {
                List<Image> images = loadImagesFromPath(imagePath);
                if (!images.isEmpty()) {
                    imageCache.put(imagePath, images);
                }
            }
        }
    }

    private List<Image> loadImagesFromPath(String imagePath) {
        List<Image> images = new ArrayList<>();

        try {
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
        } catch (Exception e) {
            System.err.println("Error loading images from path: " + imagePath);
            e.printStackTrace();
        }

        if (images.isEmpty()) {
            try {
                images.add(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            } catch (Exception e) {
                System.err.println("Could not load placeholder image");
            }
        }

        return images;
    }

    public List<ProductSize> getCachedProductSizes(int productId) {
        if (!sizesCache.containsKey(productId)) {
            sizesCache.put(productId, productDAO.getProductSizes(productId));
        }
        return sizesCache.get(productId);
    }

    public List<Image> getCachedImages(String imagePath) {
        return imageCache.get(imagePath);
    }

    public void loadAllProducts() {
        displayedProducts = new ArrayList<>(allProducts);
    }

    public void loadProductsByGender(String gender) {
        displayedProducts = allProducts.stream()
                .filter(product -> gender.equals(product.getGender()) || "UNISEX".equals(product.getGender()))
                .collect(Collectors.toList());
    }

    public void loadProductsByName(String name) {
        displayedProducts = allProducts.stream()
                .filter(product -> product.getProductName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Product> getDisplayedProducts() {
        return displayedProducts;
    }

    public List<Product> getFilteredProducts() {
        return filteredProducts;
    }

    public void applyFilters() {
        List<Product> filtered = new ArrayList<>(displayedProducts);

        // Filter sizes
        if (sizeSelected != null) {
            filtered = filtered.stream()
                    .filter(product -> {
                        List<ProductSize> sizes = getCachedProductSizes(product.getProductId());
                        return sizes.stream().anyMatch(ps -> ps.getSize().equals(sizeSelected));
                    })
                    .collect(Collectors.toList());
        }

        // Filter category
        if (categorySelected != null) {
            filtered = filtered.stream()
                    .filter(product -> categorySelected.equals(product.getCategory()))
                    .collect(Collectors.toList());
        }


        // Filter prices
        double min = 0;
        double max = 0;

        if (priceSelected != null) {
            switch (priceSelected) {
                case "Below RM30":
                    min = 0;
                    max = 30;
                    break;
                case "RM30 - RM40":
                    min = 30;
                    max = 40;
                    break;
                case "Above RM40":
                    min = 40;
                    max = Double.MAX_VALUE;
                    break;
            }

            final double finalMin = min;
            final double finalMax = max;

            filtered = filtered.stream()
                    .filter(product -> {
                        List<ProductSize> sizes = getCachedProductSizes(product.getProductId());
                        double lowestPrice = sizes.stream().mapToDouble(ProductSize::getPrice).min().orElse(0);
                        return lowestPrice >= finalMin && lowestPrice < finalMax;
                    })
                    .collect(Collectors.toList());
        }

        if (sortingSelected != null) {
            switch (sortingSelected) {
                case "Name (A - Z)":
                    filtered.sort(Comparator.comparing(Product::getProductName,
                            String.CASE_INSENSITIVE_ORDER));
                    break;
                case "Name (Z - A)":
                    filtered.sort(Comparator.comparing(Product::getProductName,
                            String.CASE_INSENSITIVE_ORDER.reversed()));
                    break;
                case "Price (Low - High)":
                    filtered.sort(Comparator.comparingDouble(this::getLowestPrice));
                    break;
                case "Price (High - Low)":
                    filtered.sort(Comparator.comparingDouble(this::getLowestPrice).reversed());
                    break;
            }
        }

        filteredProducts = filtered;
    }

    private double getLowestPrice(Product product) {
        List<ProductSize> sizes = getCachedProductSizes(product.getProductId());
        return sizes.stream()
                .mapToDouble(ProductSize::getPrice)
                .min()
                .orElse(0);
    }

    public String getSizeSelected() {
        return sizeSelected;
    }

    public void setSizeSelected(String sizeSelected) {
        this.sizeSelected = sizeSelected;
    }

    public String getCategorySelected() {
        return categorySelected;
    }

    public void setCategorySelected(String categorySelected) {
        this.categorySelected = categorySelected;
    }

    public String getPriceSelected() {
        return priceSelected;
    }

    public void setPriceSelected(String priceSelected) {
        this.priceSelected = priceSelected;
    }

    public String getSortingSelected() {
        return sortingSelected;
    }

    public void setSortingSelected(String sortingSelected) {
        this.sortingSelected = sortingSelected;
    }

    public boolean isSorted() {
        return isSorted;
    }

    public void setSorted(boolean sorted) {
        isSorted = sorted;
    }

    public List<String> getDistinctSizes() {
        return productDAO.getDistinctSizes();
    }

    public Set<String> getUniqueCategories() {
        Set<String> uniqueCategories = new HashSet<>();
        for (Product product : allProducts) {
            uniqueCategories.add(toTitleCase(product.getCategory()));
        }
        return uniqueCategories;
    }

    private String toTitleCase(String text) {
        if (text == null || text.isEmpty()) return text;

        String[] words = text.toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (word.contains("-")) {
                    String[] parts = word.split("-");
                    for (int i = 0; i < parts.length; i++) {
                        if (!parts[i].isEmpty()) {
                            result.append(Character.toUpperCase(parts[i].charAt(0)))
                                    .append(parts[i].substring(1));
                            if (i < parts.length - 1) {
                                result.append("-");
                            }
                        }
                    }
                    result.append(" ");
                }
                else if (word.contains("'")) {
                    String[] parts = word.split("'");
                    result.append(Character.toUpperCase(parts[0].charAt(0)))
                            .append(parts[0].substring(1))
                            .append("'");
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        result.append(Character.toUpperCase(parts[1].charAt(0)))
                                .append(parts[1].substring(1));
                    }
                    result.append(" ");
                }
                else {
                    result.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1))
                            .append(" ");
                }
            }
        }

        return result.toString().trim();
    }

    public void cleanup() {
        imageCache.clear();
        sizesCache.clear();
        if (allProducts != null) {
            allProducts.clear();
        }
    }

    public int getMaxStock(int productId) {
        int maxStock = sizesCache.get(productId)
                .stream()
                .mapToInt(ProductSize::getStockQuantity)
                .max()
                .orElse(0);

        return maxStock;
    }

    public int getSizeId(int productId, String size) {
        return productDAO.getSizeId(productId, size);
    }

    public boolean addToCart(int productSizeId, int quantity, double subTotal) {
        int userId = this.user.getId();
        Cart cart;

        if (!cartDAO.userCartExists(userId)) {
            cart = cartDAO.createCart(userId);
        } else {
            cart = cartDAO.getUserCart(userId);
        }

        int cartId = cart.getCartId();

        CartItem cartItem = new CartItem(cartId, productSizeId, quantity, subTotal);

        if(cartDAO.insertCartItem(cartItem)) {
            cartController.addCartItem(cartItem);

            return true;
        }

        return false;
    }
}