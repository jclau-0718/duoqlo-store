package com.duoqlo.duoqlostore.view;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;

import java.util.Objects;

public class ApplicationPage {

    public Scene setScene(Parent root, String cssFileName) {
        Scene scene = new Scene(root,
                Screen.getPrimary().getVisualBounds().getWidth(),
                Screen.getPrimary().getVisualBounds().getHeight());

        String cssPath = "/css/"+cssFileName+".css";

        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(cssPath)
                ).toExternalForm()
        );

        Platform.runLater(() -> {root.requestFocus();}); //Remove initial focus on Username TextField
        root.setOnMouseClicked(e -> root.requestFocus()); //Allow unfocus on TextField

        return scene;
    }
}
