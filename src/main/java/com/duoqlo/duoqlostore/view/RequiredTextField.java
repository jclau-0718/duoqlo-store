package com.duoqlo.duoqlostore.view;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;

public class RequiredTextField extends TextField {
    private String promptText;

    public RequiredTextField(String promptText) {
        this.promptText = promptText;
        create();
    }

    private TextField create() {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setMaxWidth(Double.MAX_VALUE);
        textField.setId("text-field");
        textField.setPadding(new Insets(6,8,8,8));

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal) {
                textField.setId("text-field");
                textField.setStyle("-fx-prompt-text-fill: gray");
            } else {
                textField.setId("text-field-focus");
                textField.setStyle("-fx-prompt-text-fill: transparent");
            }
        });

        if (textField.getText() == null || textField.getText().isEmpty()) {
            textField.getStyleClass().add("required");
        }

        return textField;
    }
}
