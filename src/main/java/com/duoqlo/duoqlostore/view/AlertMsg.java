package com.duoqlo.duoqlostore.view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class AlertMsg {
    public enum AlertMsgType {
        INFORMATION,
        WARNING,
        ERROR,
        SUCCESS
        }

    private AlertMsgType type;
    private String text;
    private FontIcon icon;
    private Color color;
    private VBox popupBox = new VBox();

    public AlertMsg() {};

    public AlertMsg(AlertMsgType type) {
        this.type = type;
        setupInfo();
    }

    private void setupInfo() {
        String iconCode = "";
        switch(type) {
            case INFORMATION:
                iconCode = "fas-info-circle";
                color = Color.BLUE;
                break;

            case WARNING:
                iconCode = "fas-exclamation-triangle";
                color = Color.YELLOW;
                break;

            case ERROR:
                iconCode = "far-times-circle";
                color = Color.RED;
                break;

            case SUCCESS:
                iconCode = "far-check-circle";
                color = Color.LIMEGREEN;
                break;
        }

        icon = new FontIcon(iconCode);
        icon.setIconColor(color);
    }

    public void setAlertType(AlertMsgType type) {
        this.type = type;
        setupInfo();
    }

    public void show(StackPane root, String text, Pos pos) {
        if (popupBox.getParent() != null) {
            root.getChildren().remove(popupBox);
        }

        popupBox.getChildren().clear();

        String rgb = String.format(
                "rgb(%d, %d, %d)",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255)
        );

        Button closeButton = new Button("✕");
        closeButton.setStyle("""
                -fx-background-color: transparent;
                    -fx-text-fill: #666;
                    -fx-font-size: 14px;
                    -fx-cursor: hand;
                    -fx-font-weight: bold;
                    -fx-padding: 0
                """);

        HBox closeHBox = new HBox(closeButton);
        closeHBox.setAlignment(Pos.CENTER_RIGHT);

        closeButton.setOnAction(e -> {
            root.getChildren().remove(popupBox);
        });

        Label label = new Label(text);
        label.setStyle("""
                -fx-font-size: 15;
                -fx-text-fill: %s;
                """.formatted(rgb));

        HBox contentBox = new HBox(icon, label);
//        contentBox.setMinHeight(Double.POSITIVE_INFINITY);
        contentBox.setAlignment(Pos.CENTER);
        HBox.setMargin(icon, new Insets(0, 7, 0, 0));

        popupBox.getChildren().addAll(closeHBox, contentBox);
        popupBox.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-padding: 5 10 20 10;
                """);

        popupBox.setMaxHeight(60);
        popupBox.setMaxWidth(250);
        VBox.setVgrow(contentBox, Priority.ALWAYS);
        VBox.setMargin(contentBox, new Insets(10, 0, 0, 0));


        root.getChildren().add(popupBox);
        StackPane.setAlignment(popupBox, pos);
        if (pos.equals(Pos.TOP_CENTER)) {
            StackPane.setMargin(popupBox, new Insets(20, 0, 0, 0));
        }

        popUpAnimation();
    }

    private void popUpAnimation() {
        popupBox.setOpacity(0);     //Start invisible
        popupBox.setTranslateY(-5); //5px above

        //Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), popupBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        //Move down
        TranslateTransition moveDown = new TranslateTransition(Duration.seconds(0.5), popupBox);
        moveDown.setFromY(-50);
        moveDown.setToY(0);

        ParallelTransition enterAnimation = new ParallelTransition(fadeIn, moveDown);

        //Wait for 5 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(5));

        // Fade out animation
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), popupBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        //Remove node after fade out
        fadeOut.setOnFinished(event -> {
            if (popupBox.getParent() != null) {
                ((javafx.scene.layout.Pane) popupBox.getParent()).getChildren().remove(popupBox);
            }
        });

        // Chain all animations
        SequentialTransition fullAnimation = new SequentialTransition(
                enterAnimation,
                pause,
                fadeOut
        );

        fullAnimation.play();
    }
}
