package com.duoqlo.duoqlostore.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    public static void drop(String table){

        String sql = "DROP TABLE IF EXISTS " + table;

        try (Connection conn = ConnectDB.connect(); Statement stmt = conn.createStatement()){
            stmt.executeUpdate(sql);
            System.out.println(table + "table dropped");

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
