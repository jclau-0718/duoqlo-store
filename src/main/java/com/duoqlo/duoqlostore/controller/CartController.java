package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.view.CartPage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class CartController {
    private User user;
    private CartPage cartPage = new CartPage();

    public void setUser(User user){
        this.user = user;
    }

    public void initCartPage(ActionEvent e){
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(cartPage.createCartPage());
    }
}
