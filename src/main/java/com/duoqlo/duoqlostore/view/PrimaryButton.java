package com.duoqlo.duoqlostore.view;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import org.kordamp.ikonli.javafx.FontIcon;

public class PrimaryButton extends Button {

    private int fontSize = 14; // default
    private int radius = 5;

    public PrimaryButton() {
        super();
        applyStyle();
    }

    public PrimaryButton(String text) {
        super(text);
        applyStyle();
    }

    public PrimaryButton(String text, FontIcon icon) {
        super(text, icon);
        applyStyle();
    }

    public void setFontSize(int size) {
        this.fontSize = size;
        applyStyle();
    }

    public void setRadius(int radius) {
        this.radius = radius;
        applyStyle();
    }

    private void applyStyle() {
        this.setStyle(String.format("""
                -fx-font-family: Arial;
                -fx-font-size: %d;
                -fx-font-weight: bold;
                -fx-border-color: #FE6C01;
                -fx-border-radius: %d;
                -fx-background-color: #FE6C01;
                -fx-background-radius: %d;
                -fx-text-fill: white;
                -fx-cursor: hand;
                """, fontSize, radius, radius)
        );
    }
}
