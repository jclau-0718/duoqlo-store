package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class AdminDashController {
    private UserDAO userDAO = new UserDAO();
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<User> customers = FXCollections.observableArrayList();
    private ObservableList<User> admins = FXCollections.observableArrayList();
    private ObservableList<Product> products = FXCollections.observableArrayList();
    private ObservableList<Gender> genders = FXCollections.observableArrayList();
    private ObservableList<Category> categories = FXCollections.observableArrayList();
    private ObservableList<Order> orders = FXCollections.observableArrayList();
    private ObservableList<SalesRecord> sales = FXCollections.observableArrayList();

    public void initializeAllData() {
        this.users.setAll(userDAO.getAllUsersObservable());
        setCustomers();
        setAdmins();
        this.products.setAll(productDAO.getAllProductsObservable());
        this.genders.setAll(productDAO.getAllGenders());
        this.categories.setAll(productDAO.getAllCategories());
        this.orders.setAll(orderDAO.getAllOrdersObservable());
    }

    public void refreshProductData() {
        this.products.setAll(productDAO.getAllProductsObservable());
    }

    public void refreshGenderData() {
        this.genders.setAll(productDAO.getAllGenders());
    }

    public void refreshCategoryData() {
        this.categories.setAll(productDAO.getAllCategories());
    }

    public void refreshOrderData() { this.orders.setAll(orderDAO.getAllOrdersObservable()); }

    public ObservableList<User> getCustomers() {
        return this.customers;
    }

    public ObservableList<User> getAdmins() { return this.admins; }

    public ObservableList<Product> getProducts() { return this.products; }

    public ObservableList<Gender> getGenders() { return this.genders; }

    public ObservableList<String> getAllGenders() {
        ObservableList<String> genderList = FXCollections.observableArrayList();
        for(Gender gender : this.genders) {
            genderList.add(gender.getGender());
        }

        return genderList;
    }

    public ObservableList<String> getAllCategories() {
        ObservableList<String> categoryList = FXCollections.observableArrayList();
        for(Category category : this.categories) {
            categoryList.add(category.getCategoryName());
        }

        return categoryList;
    }

    public ObservableList<Category> getCategories() { return this.categories; }

    public ObservableList<Order> getOrders() { return this.orders; }

    public ObservableList<OrderItem> getOrderItems(int orderId) {
        return FXCollections.observableArrayList(orderDAO.getOrderItems(orderId));
    }

    public ObservableList<SalesRecord> getSales() { return this.sales; }

    public ObservableList<SalesRecord> getDailySales() {
        this.sales.setAll(SalesDAO.getDailySales());
        return this.sales;
    }

    public ObservableList<SalesRecord> getWeeklySales() {
        this.sales.setAll(SalesDAO.getWeeklySales());
        return this.sales;
    }

    public ObservableList<SalesRecord> getMonthlySales() {
        this.sales.setAll(SalesDAO.getMonthlySales());
        return this.sales;
    }

    public ObservableList<SalesRecord> getSalesByGenders() {
        this.sales.setAll(SalesDAO.getSalesByGenders());
        return this.sales;
    }

    public ObservableList<SalesRecord> getSalesByCategories() {
        this.sales.setAll(SalesDAO.getSalesByCategories());
        return this.sales;
    }

    public ObservableList<SalesRecord> getSalesByFilterAndFreq(FilterBy filter, Freq freq, String value) {
        this.sales.setAll(SalesDAO.getSalesByFilterAndFrequency(filter, freq, value));
        return this.sales;
    }

    public int getTotalCustomers() {
        return this.customers.size();
    }

    public int getTotalAdmins() {
        return this.admins.size();
    }

    public int getTotalProducts() {
        return this.products.size();
    }

    public int getTotalOrders() {
        return this.orders.size();
    }

    public void setCustomers() {
        this.customers = FXCollections.observableArrayList(
                users.stream()
                        .filter(u -> u.getRole().equals("CUSTOMER"))
                        .toList()
        );
    }

    public void setAdmins() {
        this.admins = FXCollections.observableArrayList(
                users.stream()
                        .filter(u -> u.getRole().equals("ADMIN"))
                        .toList()
        );
    }

    public boolean deactivateCustomers(int userId) {
        try {
            for (User user : users) {
                if (user.getId() == userId) {
                    if (userDAO.deactivateUser(userId)) {
                        user.setIs_active(0);

                        //Update ObservableList
                        int index = users.indexOf(user);
                        if (index >= 0) {
                            users.set(index, user);
                        }

                        setCustomers();

                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deactivateAdmins(int userId) {
        try {
            for (User user : users) {
                if (user.getId() == userId) {
                    if (userDAO.deactivateUser(userId)) {
                        user.setIs_active(0);

                        //Update ObservableList
                        int index = users.indexOf(user);
                        if (index >= 0) {
                            users.set(index, user);
                        }

                        setAdmins();

                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean reactivateUser(int userId) {
        try {
            for (User user : users) {
                if (user.getId() == userId) {
                    if (userDAO.reactivateUser(userId)) {
                        user.setIs_active(1);

                        //Update ObservableList
                        int index = users.indexOf(user);
                        if (index >= 0) {
                            users.set(index, user);
                        }

                        setCustomers();
                        setAdmins();

                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeProduct(Product selectedProd) {
        if(productDAO.deleteProduct(selectedProd.getId()) && productDAO.deleteProductSize(selectedProd.getId())) {
            deleteProdImgDir(selectedProd);
            products.removeIf(product -> product.getId() == selectedProd.getId());

            return true;
        }

        return false;
    }

    private void deleteProdImgDir(Product product) {
        if (product == null) return;

        String imagePath = product.getImagePath();
        if (imagePath == null || imagePath.trim().isEmpty()) return;

        try {
            Path path = Paths.get(imagePath);

            // If relative path, resolve against working directory
            if (!path.isAbsolute()) {
                path = Paths.get(System.getProperty("user.dir")).resolve(path);
            }

            if (!Files.exists(path)) {
                System.out.println("Path does not exist: " + path);
                return;
            }

            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            System.err.println("Failed to delete: " + p + " — " + e.getMessage());
                        }
                    });

            System.out.println("Deleted product directory: " + path);

        } catch (IOException e) {
            System.err.println("Error deleting product directory: " + e.getMessage());
        }
    }

    public boolean addGender(String id, String gender) {
        return productDAO.insertGender(id, gender);
    }

    public boolean updateGender(String id, String gender) {
        return productDAO.updateGender(id, gender);
    }

    public boolean genderInUse(String id) {
        return productDAO.genderIsReferenced(id);
    }

    public boolean removeGender(String id) {
        return productDAO.deleteGender(id);
    }

    public boolean addCategory(String categoryId, String categoryName, String genderId) {
        return productDAO.insertCategory(categoryId, categoryName, genderId);
    }

    public boolean updateCategory(String categoryId, String categoryName, String genderId) {
        return productDAO.updateCategory(categoryId, categoryName, genderId);
    }

    public boolean categoryInUse(String id) {
        return productDAO.categoryIsReferenced(id);
    }

    public boolean removeCategory(String categoryId) {
        return productDAO.deleteCategory(categoryId);
    }

    public boolean setOrderAsDone(int orderId) {
        return orderDAO.setOrderAsDone(orderId);
    }

    public double getTotalRevenue() {
        double totalRevenue = 0;

        for(Order order: orders) {
            if(order.getStatus().equals("DONE")) {
                totalRevenue += order.getTotalPrice();
            }
        }

        return totalRevenue;
    }
}