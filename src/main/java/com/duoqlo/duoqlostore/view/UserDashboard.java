package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.CartController;
import com.duoqlo.duoqlostore.controller.DashboardController;
import com.duoqlo.duoqlostore.model.User;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.*;
import java.util.Objects;

import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

public class UserDashboard extends BasePage{
    DashboardController controller = new DashboardController();

    static int logoHeight = 50;
    static int iconSize = 19;

    public UserDashboard(){
        super();
    }

    public HBox createHeader(){
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
        cartButton.setOnAction(e -> controller.openCartPage(e));

        //Profile Button
        FontIcon profileIcon = new FontIcon("far-user");
        profileIcon.setIconSize(iconSize);
        profileIcon.setIconColor(Color.web("#EE5702"));
        Button profileButton = new Button("",profileIcon);

        //Button Box
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(searchButton,cartButton,profileButton);
        buttonBox.setAlignment(Pos.CENTER);

        HBox header = createHeaderBox(catMenu, buttonBox);

        return header;
    }

    public HBox createFilterMenu(){
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

    public ScrollPane createProductMenu(){
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

    public Scene initializeUserDash(){
        UserDashboard userDash = new UserDashboard();

        BorderPane root = new BorderPane();
        root.setTop(userDash.createHeader());
        root.setCenter(userDash.createProductMenu());

        Scene scene = new Scene(root, windowWidth, windowHeight);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/css/home-page.css")
                ).toExternalForm()
        );

        return scene;
    }

    public Scene initializeAdminDash(){
        AdminDashboard adminDash = new AdminDashboard();

        BorderPane root = new BorderPane();
        root.setTop(adminDash.createPage());

        return new Scene(root, windowWidth, windowHeight);
    }
}