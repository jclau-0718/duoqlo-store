package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.CartController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class CartPage extends BasePage{
    public CartPage(){
        super();
    }

    public HBox createHeader(){
        Label label = new Label("CART PAGE");
        label.setId("cart-label");
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);

        HBox searchBar = searchBar();

        HBox header = createHeaderBox(labelBox, searchBar);

        return header;
    }

    public HBox createBody(){
        return new HBox(new Label("BODY"));
    }

    public Scene createCartPage(){

        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(createBody());
        root.setOnMouseClicked(e -> root.requestFocus()); //Allow unfocus on TextField

        Scene scene = new Scene(root, windowWidth, windowHeight);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/css/home-page.css")
                ).toExternalForm()
        );

        return scene;
    }
}
