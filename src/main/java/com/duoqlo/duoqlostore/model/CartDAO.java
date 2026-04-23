package com.duoqlo.duoqlostore.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            System.err.println(e.getMessage());
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
            System.err.println(e.getMessage());
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
            System.err.println(e.getMessage());
        }

        return null;
    }

    public List<CartItem> getCartItems(int cartId) {
        List<CartItem> cartItemList = new ArrayList<>();

        String sql = "SELECT * FROM cartitem WHERE cart_id = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cartId);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                CartItem cartItem = new CartItem(
                        rs.getInt("cart_id"),
                        rs.getInt("productsize_id"),
                        rs.getInt("product_quantity"),
                        rs.getDouble("sub_total"));

                cartItemList.add(cartItem);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return cartItemList;
    }

    public boolean userCartExists(int userId) {
        String sql = "SELECT * FROM cart WHERE USER_ID = ?;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();


        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public boolean prodSizeExist(int prodSizeId) {
        String sql = """
                SELECT 1 FROM cartitem
                WHERE productsize_id = ?
                LIMIT 1
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prodSizeId);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public boolean updateCartItem(int prodSizeId, int quantity, double subTotal) {
        String sql = """
                UPDATE cartitem
                SET product_quantity = product_quantity + ?, 
                    sub_total = sub_total+ ?, 
                    added_date = datetime('now', 'localtime')
                WHERE productsize_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setDouble(2, subTotal);
            pstmt.setInt(3, prodSizeId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;

    }

    public boolean insertCartItem(CartItem cartItem) {
        int cartId = cartItem.getCartId();
        int productSizeId = cartItem.getProductSizeId();
        int quantity = cartItem.getProductQuantity();
        double subTotal = cartItem.getSubTotal();

        String sql = """
                INSERT INTO cartitem(cart_id, productsize_id, product_quantity, sub_total)
                VALUES (?, ?, ?, ?);
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cartId);
            pstmt.setInt(2, productSizeId);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, subTotal);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public void updateCartLastUpdated(int cartId) {
        String sql = """
            UPDATE cart
            SET last_updated = datetime('now', 'localtime')
            WHERE cart_id = ?
            """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean removeCartItem(int prodSizeId) {
        String sql = " DELETE FROM cartitem WHERE productsize_id = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prodSizeId);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public void clearCart(int cartId) {
        String sql = """
                DELETE FROM cartitem
                WHERE cart_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cartId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
