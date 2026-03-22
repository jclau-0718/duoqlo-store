package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.view.SignUpPage;
import com.duoqlo.duoqlostore.view.UserDashboard;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class SceneManager {
    private static SignUpPage signUpPage;

    public static void switchScene(ActionEvent e, Parent root) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1000, 750);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        SceneManager.class.getResource("/css/home-page.css")
                ).toExternalForm()
        );

        stage.setScene(scene);
    }

    public static Parent createUserDash(){
        UserDashboard userDash = new UserDashboard();

        BorderPane root = new BorderPane();
        root.setTop(userDash.createHeader());
        root.setCenter(userDash.createProductMenu());

        return root;
    }

    public static Parent createAdminDash(){
        return null;
    }

}
