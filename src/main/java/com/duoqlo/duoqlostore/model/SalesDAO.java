package com.duoqlo.duoqlostore.model;

import com.duoqlo.duoqlostore.AppConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;

public class SalesDAO {
    public static ObservableList<SalesRecord> getDailySales() {
        ObservableList<SalesRecord> sales = FXCollections.observableArrayList();

        String sql = """
    SELECT 
        o.date,
        o.revenue,
        o.total_orders,
        i.items_sold
    FROM (
        SELECT 
            strftime('%Y-%m-%d', order_date) AS date,
            SUM(total_price) AS revenue,
            COUNT(order_id) AS total_orders
        FROM orders
        WHERE status = 'DONE'
        GROUP BY date
    ) o
    JOIN (
        SELECT 
            strftime('%Y-%m-%d', ord.order_date) AS date,
            SUM(oi.quantity) AS items_sold
        FROM orderitem oi
        JOIN orders ord ON oi.order_id = ord.order_id
        WHERE ord.status = 'DONE'
        GROUP BY date
    ) i ON o.date = i.date
    ORDER BY o.date;
    """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String date = rs.getString("date");

                LocalDate localDate = LocalDate.parse(date);
                String formattedDate = localDate.format(AppConfig.DATE_FORMATTER);

                SalesRecord record = new SalesRecord(
                        formattedDate,
                        rs.getDouble("revenue"),
                        rs.getInt("items_sold"),
                        rs.getInt("total_orders")

                );
                sales.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }

    public static ObservableList<SalesRecord> getWeeklySales() {
        ObservableList<SalesRecord> sales = FXCollections.observableArrayList();

        String sql = """
            SELECT 
                strftime('%Y-%W', o.order_date) AS week,
                SUM(oi.quantity) AS items_sold,
                COUNT(DISTINCT o.order_id) AS total_orders,
                SUM(DISTINCT o.total_price) AS revenue
            FROM orders o
            JOIN orderitem oi ON o.order_id = oi.order_id
            WHERE o.status = 'DONE'
            GROUP BY week
            ORDER BY week;
            """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String rawWeek = rs.getString("week"); // e.g. "2026-15"

                // Parse into start (Monday) and end (Sunday) of that week
                String[] parts = rawWeek.split("-");
                int year = Integer.parseInt(parts[0]);
                int week = Integer.parseInt(parts[1])+1;

                LocalDate startOfWeek = LocalDate.of(year, 1, 1)
                        .with(WeekFields.ISO.weekOfYear(), week)
                        .with(WeekFields.ISO.dayOfWeek(), 1); // Monday

                LocalDate endOfWeek = startOfWeek.plusDays(6); // Sunday

                //Format to 15/04/2026 - 22/04/2026
                String weekLabel = startOfWeek.format(AppConfig.DATE_FORMATTER) +
                        " - " +
                        endOfWeek.format(AppConfig.DATE_FORMATTER);

                SalesRecord record = new SalesRecord(
                        weekLabel,
                        rs.getDouble("revenue"),
                        rs.getInt("items_sold"),
                        rs.getInt("total_orders")
                );
                sales.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }

    public static ObservableList<SalesRecord> getMonthlySales() {
        ObservableList<SalesRecord> sales = FXCollections.observableArrayList();

        String sql = """
        SELECT 
            o.month,
            o.revenue,
            o.total_orders,
            i.items_sold
        FROM (
            SELECT 
                strftime('%Y-%m', order_date) AS month,
                SUM(total_price) AS revenue,
                COUNT(order_id) AS total_orders
            FROM orders
            WHERE status = 'DONE'
            GROUP BY month
        ) o
        JOIN (
            SELECT 
                strftime('%Y-%m', ord.order_date) AS month,
                SUM(oi.quantity) AS items_sold
            FROM orderitem oi
            JOIN orders ord ON oi.order_id = ord.order_id
            WHERE ord.status = 'DONE'
            GROUP BY month
        ) i ON o.month = i.month
        ORDER BY o.month;
        """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String rawMonth = rs.getString("month"); // e.g. "2026-04"

                // Parse into "April 2026" format
                int year = Integer.parseInt(rawMonth.substring(0, 4));
                int month = Integer.parseInt(rawMonth.substring(5));

                LocalDate date = LocalDate.of(year, month, 1);
                String monthLabel = date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                // e.g. "April 2026"

                SalesRecord record = new SalesRecord(
                        monthLabel,
                        rs.getDouble("revenue"),
                        rs.getInt("items_sold"),
                        rs.getInt("total_orders")
                );
                sales.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }

    public static ObservableList<SalesRecord> getSalesByGenders() {
        ObservableList<SalesRecord> sales = FXCollections.observableArrayList();

        String sql = """
         SELECT
             g.gender AS label,
             COALESCE(SUM(oi.quantity), 0) AS items_sold,
             COALESCE(COUNT(DISTINCT o.order_id), 0) AS total_orders,
             COALESCE(SUM(oi.sub_total), 0) AS revenue
         FROM gender g
         LEFT JOIN product p ON g.gender_id = p.gender_id
         LEFT JOIN productsize ps ON p.product_id = ps.product_id
         LEFT JOIN orderitem oi ON ps.productsize_id = oi.productsize_id
         LEFT JOIN orders o ON oi.order_id = o.order_id AND o.status = 'DONE'
         GROUP BY g.gender
         ORDER BY g.display_order;
         """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SalesRecord record = new SalesRecord(
                        rs.getString("label"),
                        rs.getDouble("revenue"),
                        rs.getInt("items_sold"),
                        rs.getInt("total_orders")

                );
                sales.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }

    public static ObservableList<SalesRecord> getSalesByCategories() {
        ObservableList<SalesRecord> sales = FXCollections.observableArrayList();

        String sql = """
          SELECT
              c.category_name AS label,
              COALESCE(SUM(oi.quantity), 0) AS items_sold,
              COALESCE(COUNT(DISTINCT o.order_id), 0) AS total_orders,
              COALESCE(SUM(oi.sub_total), 0) AS revenue
          FROM category c
          LEFT JOIN product p ON c.category_id = p.category_id
          LEFT JOIN productsize ps ON p.product_id = ps.product_id
          LEFT JOIN orderitem oi ON ps.productsize_id = oi.productsize_id
          LEFT JOIN orders o ON oi.order_id = o.order_id AND o.status = 'DONE'
          GROUP BY c.category_name
          ORDER BY revenue DESC;
          """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SalesRecord record = new SalesRecord(
                        rs.getString("label"),
                        rs.getDouble("revenue"),
                        rs.getInt("items_sold"),
                        rs.getInt("total_orders")

                );
                sales.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }

    public static ObservableList<SalesRecord> getSalesByFilter(FilterBy filter, String value) {
        ObservableList<SalesRecord> list = FXCollections.observableArrayList();

        String sql = """
            SELECT 
                strftime('%Y-%m-%d', o.order_date) AS label,
                SUM(oi.quantity) AS items_sold,
                COUNT(DISTINCT o.order_id) AS total_orders,
                SUM(oi.sub_total) AS revenue
            FROM orders o
            JOIN orderitem oi ON o.order_id = oi.order_id
            JOIN productsize ps ON oi.productsize_id = ps.productsize_id
            JOIN product p ON ps.product_id = p.product_id
            JOIN gender g ON p.gender_id = g.gender_id
            JOIN category c ON p.category_id = c.category_id
            WHERE o.status = 'DONE'
        """;

        if (filter == FilterBy.GENDER) {
            sql += " AND g.gender = ? ";
        } else if (filter == FilterBy.CATEGORY) {
            sql += " AND c.category_name = ? ";
        }

        sql += """
            GROUP BY strftime('%Y-%m-%d', o.order_date)
            ORDER BY label;
        """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Set parameter
            if (filter != FilterBy.NONE) {
                ps.setString(1, value);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SalesRecord record = new SalesRecord(
                        rs.getString("label"),
                        rs.getDouble("revenue"),
                        rs.getInt("items_sold"),
                        rs.getInt("total_orders")
                );
                list.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
