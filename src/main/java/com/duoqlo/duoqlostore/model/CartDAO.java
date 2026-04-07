package com.duoqlo.duoqlostore.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartDAO {
    public Cart createCart(int userId) {
        String sql = """
                INSERT INTO cart(user_id) 
                VALUES (?);
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int cartId = generatedKeys.getInt(1);

                        return getCart(cartId);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Cart getCart(int cartId) {
        String sql = "SELECT * FROM cart WHERE cart_id = ?;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cartId);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                return new Cart(rs.getInt("cart_id"),
                                rs.getInt("user_id"),
                                rs.getString("last_updated"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Cart getUserCart(int userId) {
        String sql = "SELECT * FROM cart WHERE user_id = ?;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                return new Cart(rs.getInt("cart_id"),
                        rs.getInt("user_id"),
                        rs.getString("last_updated"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean userCartExists(int userId) {
        String sql = "SELECT * FROM cart WHERE USER_ID = ?;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertCartItem(CartItem cartItem) {
        int cartId = cartItem.getCartId();
        int productSizeId = cartItem.getProductSizeId();
        int quantity = cartItem.getProductQuantity();

        String sql = """
                INSERT INTO cartitem(cart_id, productsize_id, product_quantity)
                VALUES (?, ?, ?);
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cartId);
            pstmt.setInt(2, productSizeId);
            pstmt.setInt(3, quantity);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
