package com.duoqlo.duoqlostore;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Test extends Application {
    private String selectedSort = "Name (A-Z)";

    public ComboBox<String> createSortBox() {
        ComboBox<String> sortBox = new ComboBox<>();

        sortBox.getItems().addAll(
                "Name (A-Z)",
                "Name (Z-A)",
                "Price (Low to High)",
                "Price (High to Low)"
        );

        sortBox.setValue(selectedSort); // restore previous selection

        sortBox.setOnAction(e -> {
            selectedSort = sortBox.getValue(); // remember selection
        });

        return sortBox;
    }

    @Override
    public void start(Stage primaryStage) {

        StackPane root = new StackPane();

        // Add ONLY searchField to root
        root.getChildren().add(createSortBox());

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Search Demo");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}