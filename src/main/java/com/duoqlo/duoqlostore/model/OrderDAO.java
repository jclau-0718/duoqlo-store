package com.duoqlo.duoqlostore.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public Order insertOrder(Order order) {
        String sql = """
                INSERT INTO orders(user_id, total_items, total_price, shipping_add) 
                VALUES (?, ?, ?, ?);
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, order.getUserId());
            pstmt.setInt(2, order.getTotalItems());
            pstmt.setDouble(3, order.getTotalPrice());
            pstmt.setString(4, order.getShippingAddress());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);

                    return getOrderById(orderId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertOrderItem(int orderId, CartItem cartItem) {
        String sql = """
                INSERT INTO orderitem(order_id, productsize_id, quantity, sub_total)
                VALUES (?, ?, ?, ?);
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, cartItem.getProductSizeId());
            pstmt.setInt(3, cartItem.getProductQuantity());
            pstmt.setDouble(4, cartItem.getSubTotal());

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setStatus(rs.getString("status"));
                order.setShippingAddress(rs.getString("shipping_add"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> orderItemList = new ArrayList<>();

        String sql = """
            SELECT oi.*, p.product_name, ps.size
            FROM orderitem oi
            JOIN productsize ps ON oi.productsize_id = ps.productsize_id
            JOIN product p ON ps.product_id = p.product_id
            WHERE oi.order_id = ?
            """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderItemId(rs.getInt("orderitem_id"));
                orderItem.setOrderId(rs.getInt("order_id"));
                orderItem.setProductSizeId(rs.getInt("productsize_id"));
                orderItem.setQuantity(rs.getInt("quantity"));
                orderItem.setSubTotal(rs.getDouble("sub_total"));

                orderItemList.add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderItemList;
    }

    public Order getFullOrder(int orderId) {
        Order order = getOrderById(orderId);

        order.setOrderItemList(getOrderItems(orderId));

        return order;

    }

    public List<Order> getAllOrders(int userId) {
        List<Order> orders = new ArrayList<>();

        String sql = """
                SELECT * FROM orders
                WHERE user_id = ?
                ORDER BY order_date ASC;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getDouble("total_price"),
                        rs.getString("status"),
                        rs.getString("shipping_add")
                );

                orders.add(order);
            }

            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public ObservableList<Order> getAllOrdersObservable() {
        UserDAO userDAO = new UserDAO();

        ObservableList<Order> orders = FXCollections.observableArrayList();

        String sql = """
                SELECT * FROM orders
                ORDER BY order_date ASC;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        userDAO.getUserById(rs.getInt("user_id")),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getInt("total_items"),
                        rs.getDouble("total_price"),
                        rs.getString("status"),
                        rs.getString("shipping_add")
                );

                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public boolean setOrderAsDone(int orderId) {
        String sql = """
                UPDATE orders
                SET status = 'DONE'
                WHERE order_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);

            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
