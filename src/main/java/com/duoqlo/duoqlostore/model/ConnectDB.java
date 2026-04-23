package com.duoqlo.duoqlostore.model;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    private static Connection connection;
    private static final String URL = "jdbc:sqlite:database.db";

    public static Connection connect(){
        try {
            connection = DriverManager.getConnection(URL);

            return connection;
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Connection Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

        return null;
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                System.out.println("Connection Closed");
                connection.close();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Connection Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
