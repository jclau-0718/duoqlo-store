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
        CONFIRMATION,
        WARNING,
        ERROR,
        SUCCESS
        }

    private AlertMsgType type;

    private String text;
    private FontIcon icon;
    private Color color;

    private Runnable onConfirm;
    private Runnable onCancel;

    private VBox popupBox = new VBox();

    public AlertMsg() {};

    public AlertMsg(AlertMsgType type) {
        this.type = type;
        setupInfo();
    }

    public void setOnConfirm(Runnable onConfirm) {
        this.onConfirm = onConfirm;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    private void setupInfo() {
        String iconCode = "";
        switch(type) {
            case INFORMATION:
                iconCode = "fas-info-circle";
                color = Color.BLUE;
                break;

            case CONFIRMATION:
                iconCode = "fas-question-circle";
                color = Color.BLUE;
                break;

            case WARNING:
                iconCode = "fas-exclamation-triangle";
                color = Color.YELLOW;
                break;

            case ERROR:
                iconCode = "fas-times-circle";
                color = Color.RED;
                break;

            case SUCCESS:
                iconCode = "fas-check-circle";
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

        icon.setIconSize(30);

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
            exitAnimation();

            root.getChildren().remove(popupBox);
        });

        Label label = new Label(text);
        label.setStyle("""
                -fx-font-size: 15;
                -fx-text-fill: %s;
                """.formatted(rgb));

        HBox contentBox = new HBox(icon, label);
        contentBox.setAlignment(Pos.CENTER);
        HBox.setMargin(icon, new Insets(0, 20, 0, 0));

        popupBox.getChildren().addAll(closeHBox, contentBox);
        popupBox.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-padding: 5 10 20 10;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);
                """);

        if (type == AlertMsgType.CONFIRMATION) {
            Button confirmButton = new Button("Confirm");
            confirmButton.setStyle("""
                    -fx-background-color: #FE6C01;
                    -fx-background-radius: 5;
                    -fx-border-color: #FE6C01;
                    -fx-border-radius: 5;
                    -fx-text-fill: white;
                    -fx-font-size: 10;
                    """);
            confirmButton.setOnAction(e -> {
                if (onConfirm != null) {
                    onConfirm.run();
                }

                exitAnimation().play();
            });

            Button cancelButton = new Button("Cancel");
            cancelButton.setStyle("""
                    -fx-background-color: white;
                    -fx-background-radius: 5;
                    -fx-border-color: #FE6C01;
                    -fx-border-radius: 5;
                    -fx-text-fill: #FE6C01;
                    -fx-font-size: 10;
                    """);
            cancelButton.setOnAction(e -> {
                if (onCancel != null) {
                    onCancel.run();
                }

                exitAnimation().play();
            });

            HBox buttonBox = new HBox(7, cancelButton, confirmButton);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            popupBox.getChildren().add(buttonBox);
            VBox.setMargin(buttonBox, new Insets(7, 0, 0, 0));
        }

        popupBox.setMaxHeight(60);
        popupBox.setPrefWidth(250);
        popupBox.setMaxWidth(300);
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

        if(type == AlertMsgType.CONFIRMATION) {
            enterAnimation.play();
            return;
        } else {
            //Wait for 5 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(3));

            // Chain all animations
            SequentialTransition fullAnimation = new SequentialTransition(
                    enterAnimation,
                    pause,
                    exitAnimation()
            );

            fullAnimation.play();
        }
    }

    private FadeTransition exitAnimation() {
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

        return fadeOut;
    }
}
