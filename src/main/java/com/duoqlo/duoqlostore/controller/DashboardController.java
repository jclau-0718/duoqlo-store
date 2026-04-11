package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.*;
import com.duoqlo.duoqlostore.view.*;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DashboardController {
    private User user;
    private CartController cartController;
    private OrderController orderController;
    private ProfileController profileController;
    private ProductDAO productDAO = new ProductDAO();

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
        cartController = new CartController(this.user);
        orderController = new OrderController(this.user);
        profileController = new ProfileController(this.user);
    }

    public void openCartPage(){
        CartPage cartPage = new CartPage(this.cartController);

        Navigator.goTo(cartPage.initialize());
    }

    public void openOrdersPage() {
        OrderPage orderPage = new OrderPage(this.orderController);

        Navigator.goTo(orderPage.initialize());
    }

    public void openProfilePage() {
        ProfilePage profilePage = new ProfilePage(this.profileController);

        Navigator.goTo(profilePage.initialize());
    }

    public void deleteAccount() {
        // Code to delete account
    }

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
        displayedProducts = filterAndSortByGender(allProducts, gender);
    }

    public void loadProductsByName(String name, String currentFilter) {
        // Filter by name
        List<Product> nameFiltered = allProducts.stream()
                .filter(product -> product.getProductName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());

        // Then apply gender filter and sort
        switch (currentFilter) {
            case "ALL":
                displayedProducts = nameFiltered;
                break;
            case "WOMEN":
                displayedProducts = filterAndSortWomenFirst(nameFiltered);
                break;
            case "MEN":
                displayedProducts = filterAndSortMenFirst(nameFiltered);
                break;
            default:
                displayedProducts = nameFiltered;
                break;
        }
    }



    private List<Product> filterAndSortByGender(List<Product> productList, String gender) {
        List<Product> sortedList = productList.stream()
                .filter(product -> gender.equals(product.getGender()) || "UNISEX".equals(product.getGender()))
                .sorted((p1, p2) -> {
                    if (p1.getGender().equals(gender) && p2.getGender().equals("UNISEX")) return -1;
                    if (p1.getGender().equals("UNISEX") && p2.getGender().equals(gender)) return 1;
                    return 0;
                })
                .collect(Collectors.toList());

        return sortedList;
    }

    private List<Product> filterAndSortWomenFirst(List<Product> productList) {
        List<Product> sortedList = productList.stream()
                .filter(product -> "WOMEN".equals(product.getGender()) || "UNISEX".equals(product.getGender()))
                .sorted((p1, p2) -> {
                    if (p1.getGender().equals("WOMEN") && p2.getGender().equals("UNISEX")) return -1;
                    if (p1.getGender().equals("UNISEX") && p2.getGender().equals("WOMEN")) return 1;
                    return 0;
                })
                .collect(Collectors.toList());

        return sortedList;
    }

    private List<Product> filterAndSortMenFirst(List<Product> productList) {
        List<Product> sortedList = productList.stream()
                .filter(product -> "MEN".equals(product.getGender()) || "UNISEX".equals(product.getGender()))
                .sorted((p1, p2) -> {
                    if (p1.getGender().equals("MEN") && p2.getGender().equals("UNISEX")) return -1;
                    if (p1.getGender().equals("UNISEX") && p2.getGender().equals("MEN")) return 1;
                    return 0;
                })
                .collect(Collectors.toList());

        return sortedList;

    }

    public List<Product> getDisplayedProducts() {
        return displayedProducts;
    }

    public List<Product> getFilteredProducts() {
        return filteredProducts;
    }

    public void applyProdFilters() {
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


    public void setSizeSelected(String sizeSelected) {
        this.sizeSelected = sizeSelected;
    }


    public void setCategorySelected(String categorySelected) {
        this.categorySelected = categorySelected;
    }

    public void setPriceSelected(String priceSelected) {
        this.priceSelected = priceSelected;
    }

    public void setSortingSelected(String sortingSelected) {
        this.sortingSelected = sortingSelected;
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
        return this.cartController.addCartItem(productSizeId, quantity, subTotal);
    }
}