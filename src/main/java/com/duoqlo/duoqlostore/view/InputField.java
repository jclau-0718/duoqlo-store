package com.duoqlo.duoqlostore.view;

import javafx.scene.control.TextField;

public class InputField extends TextField {
    public InputField() {
        applyStyle();
    }

    public InputField(String promptText) {
        this.setPromptText(promptText);
        applyStyle();
    }

    private void applyStyle() {
        if (getStylesheets().isEmpty()) {
            getStylesheets().add(
                    getClass().getResource("/css/text-field.css").toExternalForm()
            );
        }
    }

    public void setFontSize(int size) {
        String style = "-fx-font-size: " + size + ";";
        setStyle(style);

        getStyleClass().addListener((javafx.collections.ListChangeListener<String>) change -> {
            setStyle(style);
        });
    }
}
