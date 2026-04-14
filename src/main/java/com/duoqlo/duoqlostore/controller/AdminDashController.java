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
    private ObservableList<User> admins = FXCollections.observableArrayList();
    private ObservableList<Product> products = FXCollections.observableArrayList();
    private ObservableList<Gender> genders = FXCollections.observableArrayList();
    private ObservableList<Category> categories = FXCollections.observableArrayList();
    private ObservableList<Order> orders = FXCollections.observableArrayList();

    public void initializeAllData() {
        this.users.setAll(userDAO.getAllUsersObservable());
        this.admins.setAll(userDAO.getAllAdminsObservable());
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

    public ObservableList<User> getUsers() {
        return this.users;
    }

    public ObservableList<User> getAdmins() {
        return this.admins;
    }

    public ObservableList<Product> getProducts() {
        return this.products;
    }

    public ObservableList<Gender> getGenders() { return this.genders; }

    public ObservableList<Category> getCategories() { return this.categories; }

    public int getTotalUsers() {
        return this.users.size();
    }

    public int getTotalProducts() {
        return this.products.size();
    }

    public int getTotalOrders() {
        return this.orders.size();
    }

    public int getTotalAdmins() {
        return this.admins.size();
    }

    public boolean deactivateUser(int userId) {
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
}