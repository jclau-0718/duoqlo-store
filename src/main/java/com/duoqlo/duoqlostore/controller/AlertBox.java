package com.duoqlo.duoqlostore.controller;

import javafx.scene.control.Alert;

public class AlertBox {
    public static void showError(String title, String content) {
        show(Alert.AlertType.ERROR, title, content);
    }

    public static void showWarning(String title, String content) {
        show(Alert.AlertType.WARNING, title, content);
    }

    private static void show(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
