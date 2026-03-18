package com.duoqlo.duoqlostore.view;

import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.*;
import java.util.Objects;
import org.kordamp.ikonli.javafx.FontIcon;

public class HomePage {
    static int logoHeight = 50;
    static int iconSize = 19;

    public static HBox createHeader(){
        HBox header = new HBox(5); //Button-to-Button space
        header.setId("header-menu");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setPrefHeight(10);
        header.setPadding(new Insets(13)); //Space between all button and HBox edge
        header.setAlignment(Pos.CENTER); // vertically centers all children

        //Add Logo
        Image logo = new Image(Objects.requireNonNull(HomePage.class.getResource("/logo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(logoHeight);
        logoView.setPreserveRatio(true);

        //Spacer
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        //Category Buttons
        Button womenButton = new Button("WOMEN");
        Button menButton = new Button("MEN");
        Button kidsButton = new Button("KIDS");

        //Category Menu
        HBox catMenu = new HBox(80);
        catMenu.setId("category-menu");
        catMenu.setAlignment(Pos.BOTTOM_CENTER);
        catMenu.setPadding(new Insets(5));

        catMenu.getChildren().addAll(womenButton,menButton,kidsButton);

        //Search Button
        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.setIconSize(iconSize);
        searchIcon.setIconColor(Color.web("#EE5702"));
        Button searchButton = new Button("",searchIcon);

        //Cart Button
        FontIcon cartIcon = new FontIcon("fas-shopping-cart");
        cartIcon.setIconSize(iconSize);
        cartIcon.setIconColor(Color.web("#EE5702"));
        Button cartButton = new Button("",cartIcon);

        //Profile Button
        FontIcon profileIcon = new FontIcon("far-user");
        profileIcon.setIconSize(iconSize);
        profileIcon.setIconColor(Color.web("#EE5702"));
        Button profileButton = new Button("",profileIcon);

        header.getChildren().addAll(logoView,spacer1,catMenu,spacer2,searchButton,cartButton,profileButton);

        return header;
    }

    public static HBox createFilterMenu(){
        int tbPad = 20;
        int leftPad = 63;
        MenuButton size = new MenuButton("Size");
        MenuButton colour = new MenuButton("Colour");
        MenuButton price = new MenuButton("Price");

        HBox filterMenu = new HBox(10,size,colour,price);
        filterMenu.setId("filter-menu");
        filterMenu.setPadding(new Insets(tbPad,0,tbPad,leftPad));

        return filterMenu;
    }

    public static ScrollPane createProductMenu(){
        TilePane productGrid = new TilePane();
        productGrid.setHgap(20);
        productGrid.setVgap(20);
        productGrid.setPrefColumns(3);
        productGrid.setAlignment(Pos.CENTER);

        VBox productSection = new VBox(createFilterMenu(),productGrid);

        ScrollPane scrollPane = new ScrollPane(productSection);
        scrollPane.setFitToWidth(true);

        for (int i = 1; i <= 20; i++) {
            VBox item = new VBox();
            item.setPrefSize(200, 300);
            item.setStyle("-fx-border-color: black;");

            Label label = new Label("Label"+i);
            label.setStyle("-fx-text-fill: black");

            Button addCartButton = new Button("Add to Cart");
            Button buyNowButton = new Button("Buy Now");

            HBox buttonBox = new HBox(5,addCartButton,buyNowButton);

            item.getChildren().addAll(label, buttonBox);
            productGrid.getChildren().add(item);
        }

        return scrollPane;
    }
}