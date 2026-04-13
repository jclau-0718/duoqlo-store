package com.duoqlo.duoqlostore.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.*;

public class ProductDAO {
    public int insertProduct(String sku, String name, String categoryId, String genderId,
                             String imagePath, String description) throws SQLException {
        String sql = "INSERT INTO product (product_sku, product_name, category_id, gender_id, image_path, description) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, sku);
            pstmt.setString(2, name);
            pstmt.setString(3, categoryId);
            pstmt.setString(4, genderId);
            pstmt.setString(5, imagePath);
            pstmt.setString(6, description);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    public void insertProductSize(String sku, int productId, String size, int stock, double price) throws SQLException {
        String sql = "INSERT INTO productsize (productsize_sku, product_id, size, stock_quantity, price) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sku);
            pstmt.setInt(2, productId);
            pstmt.setString(3, size);
            pstmt.setInt(4, stock);
            pstmt.setDouble(5, price);

            pstmt.executeUpdate();
        }
    }

    public List<String> getAllGender() {
        List<String> genderList = new ArrayList<>();

        String sql = "SELECT gender FROM gender";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                genderList.add(rs.getString("gender"));
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return genderList;
    }

    public String getGenderId(String gender) {
        String sql = "SELECT gender_id FROM gender WHERE gender = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, gender);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("gender_id");
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getCategoryNameWithGender(String gender) {
        List<String> categoryList = new ArrayList<>();

        String sql = """
                    SELECT c.category_name
                    FROM category c
                    JOIN gender g ON c.gender_id = g.gender_id
                    WHERE g.gender = ?
                        OR g.gender = 'UNISEX';
                    """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, gender);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                categoryList.add(rs.getString("category_name"));
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return categoryList;
    }

    // In ProductDAO.java
//    public Map<String, String> getSubCategoriesWithNames(String categoryName) {
//        Map<String, String> subCategoriesMap = new LinkedHashMap<>();
//
//        String sql = """
//                    SELECT sc.subcategory_id, sc.subcategory_name
//                    FROM subcategory sc
//                    JOIN category c ON sc.category_id = c.category_id
//                    WHERE c.category_name = ?
//                    ORDER BY sc.display_order
//                    """;
//
//        try (Connection conn = ConnectDB.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, categoryName);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                String id = rs.getString("subcategory_id");
//                String name = rs.getString("subcategory_name");
//                subCategoriesMap.put(name, id);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return subCategoriesMap;
//    }

    public String getCategoryId(String categoryName) {
        String sql = "SELECT category_id FROM category WHERE category_name = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("category_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<String, String> getAllGenderWithIds() {
        Map<String, String> genderMap = new LinkedHashMap<>();

        String sql = "SELECT gender_id, gender FROM gender";

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Store name as key, ID as value (so you can look up ID by name)
                genderMap.put(rs.getString("gender"), rs.getString("gender_id"));
            }

            return genderMap;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HashMap<>(); // Return empty map instead of null
    }

    public Map<String, String> getAllCategoriesWithIds() {
        Map<String, String> categoryMap = new LinkedHashMap<>();

        String sql = "SELECT category_id, category_name FROM category";

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Store name as key, ID as value
                categoryMap.put(rs.getString("category_name"), rs.getString("category_id"));
            }

            return categoryMap;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = """
                SELECT p.product_id, p.product_sku, p.product_name, g.gender, c.category_name,
                       p.description, p.image_path
                FROM product p
                LEFT JOIN gender g ON p.gender_id = g.gender_id
                LEFT JOIN category c ON p.category_id = c.category_id
                ORDER BY g.display_order ASC, p.product_name ASC
                """;

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_sku"),
                        rs.getString("product_name"),
                        rs.getString("gender"),
                        rs.getString("category_name"),
                        rs.getString("description"),
                        rs.getString("image_path")
                );
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public ObservableList<Product> getAllProductsObservable() {
        ObservableList<Product> products = FXCollections.observableArrayList();

        String sql = """
                SELECT p.product_id, p.product_sku, p.product_name, g.gender, c.category_name,
                       p.description, p.image_path
                FROM product p
                LEFT JOIN gender g ON p.gender_id = g.gender_id
                LEFT JOIN category c ON p.category_id = c.category_id
                ORDER BY g.display_order ASC, p.product_name ASC
                """;

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_sku"),
                        rs.getString("product_name"),
                        rs.getString("gender"),
                        rs.getString("category_name"),
                        rs.getString("description"),
                        rs.getString("image_path")
                );
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public List<ProductSize> getProductSizes(int productId) {
        List<ProductSize> productSizeList = new ArrayList<>();

        String sql = "SELECT * FROM productsize WHERE product_id = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ProductSize productSize = new ProductSize(
                        rs.getInt("productsize_id"),
                        rs.getString("productsize_sku"),
                        rs.getInt("product_id"),
                        rs.getString("size"),
                        rs.getInt("stock_quantity"),
                        rs.getDouble("price")
                );
                productSizeList.add(productSize);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productSizeList;
    }

    public List<Product> getProductIdListByName(String name) {
        List<Product> productList = new ArrayList<>();

        String sql = "SELECT product_id,product_name,image_path FROM product WHERE product_name LIKE '%' || ? || '%'";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("image_path"));

                productList.add(product);
            }

            return productList;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getDistinctSizes() {
        List<String> sizeList = new ArrayList<>();

        String sql = "SELECT DISTINCT size FROM productsize;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                sizeList.add(rs.getString("size"));
            }

            return sizeList;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getSizeId(int productId, String size) {
        String sql = """
                SELECT productsize_id FROM productsize 
                WHERE product_id = ? AND size = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            pstmt.setString(2, size);

            ResultSet rs = pstmt.executeQuery();

            return rs.getInt("productsize_id");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public String getProductName(int prodSizeId) {
        String sql = """
                SELECT p.product_name
                FROM productsize ps
                JOIN product p ON p.product_id = ps.product_id
                WHERE ps.productsize_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prodSizeId);

            ResultSet rs = pstmt.executeQuery();

            return rs.getString("product_name");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getCategory(int prodSizeId) {
        String sql = """
                SELECT c.category_name
                FROM productsize ps
                JOIN product p ON ps.product_id = p.product_id
                JOIN category c ON p.category_id = c.category_id
                WHERE ps.productsize_id = ?
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prodSizeId);

            ResultSet rs = pstmt.executeQuery();

            return rs.getString("category_name");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getSize(int prodSizeId) {
        String sql = """
                SELECT size FROM productsize
                WHERE productsize_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prodSizeId);

            ResultSet rs = pstmt.executeQuery();

            return rs.getString("size");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getImagePath(int prodSizeId) {
        String sql = """
                SELECT p.image_path
                FROM productsize ps
                JOIN product p ON p.product_id = ps.product_id
                WHERE ps.productsize_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prodSizeId);

            ResultSet rs = pstmt.executeQuery();

            return rs.getString("image_path");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean deductStock(int productSizeId, int quantity) {
        String sql = """
                UPDATE productsize 
                SET stock_quantity = stock_quantity - ? 
                WHERE productsize_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productSizeId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Product getProduct(int prodSizeId) {
        String sql = """
        SELECT 
            p.product_name,
            c.category_name,
            ps.size
        FROM productsize ps
        JOIN product p ON ps.product_id = p.product_id
        JOIN category c ON p.category_id = c.category_id
        WHERE ps.productsize_id = ?
    """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prodSizeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String productName = rs.getString("product_name");
                    String categoryName = rs.getString("category_name");
                    String size = rs.getString("size");

                    return new Product(productName, categoryName, size);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
