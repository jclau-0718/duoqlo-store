package com.duoqlo.duoqlostore.view;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import org.kordamp.ikonli.javafx.FontIcon;

public class SecondaryButton extends Button {

    private int fontSize = 14; // default

    public SecondaryButton() {
        super();
        applyStyle();
    }

    public SecondaryButton(String text) {
        super(text);
        applyStyle();
    }

    public SecondaryButton(String text, FontIcon icon) {
        super(text, icon);
        applyStyle();
    }

    public void setFontSize(int size) {
        this.fontSize = size;
        applyStyle();
    }

    private void applyStyle() {
        this.setStyle(String.format("""
                -fx-font-family: Arial;
                -fx-font-size: %dpx;
                -fx-font-weight: bold;
                -fx-border-color: #FE6C01;
                -fx-border-radius: 5;
                -fx-background-color: white;
                -fx-background-radius: 5;
                -fx-text-fill: #FE6C01;
                -fx-cursor: hand;
                """, fontSize)
        );
    }
}
