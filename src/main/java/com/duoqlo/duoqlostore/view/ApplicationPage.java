package com.duoqlo.duoqlostore.view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;

import java.util.Objects;

public class ApplicationPage {
    protected String showPrice(double amount) {
        return String.format("RM %.2f", amount);
    }

    protected Scene setScene(Parent root, String cssFileName) {
        Scene scene = new Scene(root,
                Screen.getPrimary().getVisualBounds().getWidth(),
                Screen.getPrimary().getVisualBounds().getHeight());

        String cssPath = "/css/"+cssFileName+".css";

        scene.getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/css/app-page.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource(cssPath)).toExternalForm()
        );

        root.setOnMouseClicked(e -> root.requestFocus()); //Allow unfocus on TextField

        return scene;
    }
}
