package com.duoqlo.duoqlostore.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    private static Connection connection;
    private static final String URL = "jdbc:sqlite:database.db";

    public static Connection connect(){
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                System.out.println("Connection Closed");
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
