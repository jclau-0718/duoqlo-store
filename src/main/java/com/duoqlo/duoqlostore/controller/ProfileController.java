package com.duoqlo.duoqlostore.controller;


import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.model.UserDAO;
import com.duoqlo.duoqlostore.view.CartPage;
import com.duoqlo.duoqlostore.view.OrderPage;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class ProfileController {
    private UserDAO userDAO = new UserDAO();
    private PostcodeService postcodeService = new PostcodeService();
    private User user;

    private String menuOpened = "info";

    private List<String> infoList = new ArrayList<>();
    private List<String> addressList = new ArrayList<>();
    private List<String> credentialsList = new ArrayList<>();

    public ProfileController(User user) {
        this.user = user;
    }

    public User getUser() { return this.user; }

    public void openCartPage() {
        CartController cartController = new CartController(this.user);
        CartPage cartPage = new CartPage(cartController);

        Navigator.goTo(cartPage.initialize());
    }

    public void openOrdersPage() {
        OrderController orderController = new OrderController(this.user);
        OrderPage orderPage = new OrderPage(orderController);

        Navigator.goTo(orderPage.initialize());
    }

    public String getUsername() {
        return user != null ? user.getUsername() : "";
    }

    public String getFirstName() {
        return user != null ? user.getFirstName() : "";
    }

    public String getLastName() {
        return user != null ? user.getLastName() : "";
    }

    public String getFullName() {
        System.out.println("In getFullName(): "+user.getFirstName());
        return user != null ? user.getFirstName() + " " + user.getLastName() : "";
    }

    public String getEmail() {
        return user != null ? user.getEmail() : "";
    }

    public String getAddressLine1() {
        return user != null ? user.getAddressLine1() : "";
    }

    public String getAddressLine2() {
        return user != null ? user.getAddressLine2() : "";
    }

    public String getCity() {
        return user != null ? user.getCity() : "";
    }

    public int getPostalCode() {
        return user != null ? user.getPostalCode() : 0;
    }

    public String getPostalCodeStr() {
        return user != null ? user.getPostalCodeStr() : "";
    }

    public String getState() {
        return user != null ? user.getState() : "";
    }

    public String getFullAddress() {
        return user != null ? user.getFullAddress() : "";
    }

    public void setMenuOpened(String menu) {
        this.menuOpened = menu;
    }

    public String getMenuOpened() { return this.menuOpened; }

    public void addNewInfo(String value) {
        infoList.add(value);
    }

    public void addNewAddr(String value) {
        addressList.add(value);
    }

    public void addNewCred(String value) {
        credentialsList.add(value);
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean updateData() {
        int userId = user.getId();

        switch(menuOpened) {
            case "info":
                return userDAO.updateInfo(userId, infoList);

            case "address":
                return userDAO.updateAddress(userId, addressList);

            case "credentials":
                return userDAO.updateCredentials(userId, credentialsList);

            default:
                return false;
        }
    }

    public void setupAddressTracker(TextField postcodeField, TextField cityField, TextField stateField) {
        final boolean[] hasAddr = {false};

        postcodeField.textProperty().addListener((obs, oldVal, newVal) -> {
            Address addr = postcodeService.lookup(newVal);

            if (addr != null) { //Address found
                if (newVal.length() == 5) {
                    cityField.setText(addr.getCity());
                    cityField.setEditable(false);

                    stateField.setText(addr.getState());
                    stateField.setEditable(false);

                    hasAddr[0] = true;
                }
            } else {
                cityField.setText("");
                cityField.setEditable(true);

                stateField.setText("");
                stateField.setEditable(true);

                hasAddr[0] = false;
            }
        });
    }

    public void setNewUser() {
        User newUser = userDAO.getUserById(user.getId());
        System.out.println("newUser firstname: "+newUser.getFirstName());
        if (newUser != null) {
            System.out.println(newUser.getFirstName());
            this.user = newUser;
        }

        clearLists();
    }

    private void clearLists() {
        infoList.clear();
        addressList.clear();
        credentialsList.clear();
    }
}
