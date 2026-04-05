package com.duoqlo.duoqlostore.model;

import java.sql.*;
import java.util.*;

public class ProductDAO {
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

        String sql = "SELECT category_id, category_name FROM category ORDER BY display_order";

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

//    public List<Product> getProductsByGender(String genderName) {
//        List<Product> products = new ArrayList<>();
//        String sql = """
//        SELECT
//            p.product_id,
//            p.product_sku,
//            p.product_name,
//            g.gender,
//            c.category_name,
//            s.subcategory_name,
//            p.description,
//            p.image_path
//        FROM product p
//        LEFT JOIN gender g ON p.gender_id = g.gender_id
//        LEFT JOIN subcategory s ON p.subcategory_id = s.subcategory_id
//        LEFT JOIN category c ON s.category_id = c.category_id
//        WHERE g.gender = ?
//        ORDER BY p.product_id DESC
//    """;
//
//        try (Connection conn = ConnectDB.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, genderName);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                Product product = new Product(
//                        rs.getInt("product_id"),
//                        rs.getString("product_sku"),
//                        rs.getString("product_name"),
//                        rs.getString("gender") != null ? rs.getString("gender") : "",
//                        rs.getString("category_name") != null ? rs.getString("category_name") : "",
//                        rs.getString("description"),
//                        rs.getString("image_path")
//                );
//                products.add(product);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return products;
//    }

//    public List<Product> getProductsByCategory(String categoryName) {
//        List<Product> products = new ArrayList<>();
//        String sql = """
//        SELECT
//            p.product_id,
//            p.product_sku,
//            p.product_name,
//            g.gender,
//            c.category_name,
//            s.subcategory_name,
//            p.description,
//            p.image_path
//        FROM product p
//        LEFT JOIN gender g ON p.gender_id = g.gender_id
//        LEFT JOIN subcategory s ON p.subcategory_id = s.subcategory_id
//        LEFT JOIN category c ON s.category_id = c.category_id
//        WHERE c.category_name = ?
//        ORDER BY p.product_id DESC
//    """;
//
//        try (Connection conn = ConnectDB.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, categoryName);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                Product product = new Product(
//                        rs.getInt("product_id"),
//                        rs.getString("product_sku"),
//                        rs.getString("product_name"),
//                        rs.getString("gender") != null ? rs.getString("gender") : "",
//                        rs.getString("category_name") != null ? rs.getString("category_name") : "",
//                        rs.getString("subcategory_name") != null ? rs.getString("subcategory_name") : "",
//                        rs.getString("description"),
//                        rs.getString("image_path")
//                );
//                products.add(product);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return products;
//    }

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
}
