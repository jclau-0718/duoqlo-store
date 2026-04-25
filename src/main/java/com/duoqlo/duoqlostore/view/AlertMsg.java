package com.duoqlo.duoqlostore.view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class AlertMsg {
    private AlertType type;

    private FontIcon icon;
    private Color color;

    private Runnable onConfirm;
    private Runnable onCancel;

    private VBox popupBox = new VBox();

    public AlertMsg() {}

    public AlertMsg(AlertType type) {
        this.type = type;
        setupInfo();
    }

    public void setOnConfirm(Runnable onConfirm) {
        this.onConfirm = onConfirm;
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

    public void setTitle(Label title) {
        switch (type) {
            case INFORMATION -> title.setText("INFORMATION");
            case CONFIRMATION -> title.setText("CONFIRM?");
            case WARNING -> title.setText("WARNING");
            case ERROR -> title.setText("ERROR");
            case SUCCESS -> title.setText("SUCCESS");
        }
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
                    -fx-font-size: 16;
                    -fx-cursor: hand;
                    -fx-font-weight: bold;
                    -fx-padding: 0
                """);

        Label title = new Label();
        setTitle(title);
        title.setStyle("""
                -fx-font-size: 15;
                -fx-text-fill: %s;
                -fx-font-weight: bold;
                """.formatted(rgb));

        BorderPane topPane = new BorderPane();
        topPane.setLeft(title);
        topPane.setRight(closeButton);

        closeButton.setOnAction(e -> {
            exitAnimation();

            root.getChildren().remove(popupBox);
        });

        Label alertLabel = new Label(text);
        alertLabel.setStyle("""
                -fx-font-size: 15;
                -fx-text-fill: %s;
                """.formatted(rgb));
        alertLabel.setWrapText(true);

        HBox contentBox = new HBox(icon, alertLabel);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setMargin(icon, new Insets(0, 20, 0, 0));

        popupBox.getChildren().addAll(topPane, contentBox);
        popupBox.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-padding: 10 10 20 25;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);
                """);

        if (type == AlertType.CONFIRMATION) {
            PrimaryButton confirmButton = new PrimaryButton("Confirm");
            confirmButton.setFontSize(12);
            confirmButton.setOnAction(e -> {
                if (onConfirm != null) {
                    onConfirm.run();
                }

                exitAnimation().play();
            });

            SecondaryButton cancelButton = new SecondaryButton("Cancel");
            cancelButton.setFontSize(12);
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

        popupBox.setMaxHeight(100);
        popupBox.setPrefWidth(250);
        popupBox.setMaxWidth(300);

        popupBox.setMaxHeight(120);

        VBox.setVgrow(contentBox, Priority.ALWAYS);
        if(type == AlertType.CONFIRMATION) {
            VBox.setMargin(contentBox, new Insets(10, 0, 10, 0));
        } else {
            VBox.setMargin(contentBox, new Insets(10, 0, 0, 0));
        }

        root.getChildren().add(popupBox);
        StackPane.setAlignment(popupBox, pos);
        if (pos.equals(Pos.TOP_CENTER)) {
            StackPane.setMargin(popupBox, new Insets(20, 0, 0, 0));
        }

        popUpAnimation();
    }

    private void popUpAnimation() {
        popupBox.setOpacity(0);     //Start invisible
        popupBox.setTranslateY(-5); //Start at 5px above

        //Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), popupBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        //Move down
        TranslateTransition moveDown = new TranslateTransition(Duration.seconds(0.5), popupBox);
        moveDown.setFromY(-50);
        moveDown.setToY(0);

        ParallelTransition enterAnimation = new ParallelTransition(fadeIn, moveDown);

        if(type == AlertType.CONFIRMATION) {
            enterAnimation.play();
            return;
        } else {
            //Wait for 5 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(3));

            //Chain all animations
            SequentialTransition fullAnimation = new SequentialTransition(
                    enterAnimation,
                    pause,
                    exitAnimation()
            );

            fullAnimation.play();
        }
    }

    private FadeTransition exitAnimation() {
        //Fade out animation
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
