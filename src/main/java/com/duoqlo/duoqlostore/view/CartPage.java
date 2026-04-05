package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.CartController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Objects;

class CartItem {
}

public class CartPage extends BasePage{
    private CartController controller;

    public void setController (CartController controller) {
        this.controller = controller;
    }

    public CartPage() { super(); }

    private StackPane buildHeader(){
        Label label = new Label("CART PAGE");
        label.setId("cart-label");
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);

//        FontIcon profileIcon = new FontIcon("far-user");
//        profileIcon.setIconSize(iconSize);
//        profileIcon.setIconColor(Color.web("#EE5702"));
//        Button profileButton = new Button("", profileIcon);F

        HBox actionBox = new HBox(10);
        actionBox.setMinWidth(300);
        actionBox.setPrefWidth(300);
        actionBox.setMaxWidth(300);
        actionBox.getChildren().add(searchBar);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        StackPane header = createHeaderBox(labelBox, actionBox);

        return header;
    }

    public HBox createBody(){
        return new HBox(new Label("BODY"));
    }

    public Scene initialize(){
        BorderPane root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(createBody());
        root.setOnMouseClicked(e -> root.requestFocus()); //Allow unfocus on TextField

        Scene scene = new Scene(root,
                Screen.getPrimary().getVisualBounds().getWidth(),
                Screen.getPrimary().getVisualBounds().getHeight());

        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/css/cart-page.css")
                ).toExternalForm()
        );

        return scene;
    }
}
