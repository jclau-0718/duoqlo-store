package com.duoqlo.duoqlostore.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.*;

public class ProductDAO {
    public int insertProduct(String sku, String name, String categoryId, String genderId,
                             String imagePath, String description) throws SQLException {
        String sql = """
                INSERT INTO product (product_sku, product_name, category_id, gender_id, image_path, description)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

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

    public boolean updateProduct(int productId, String productSKU, String name,
                                 String categoryId, String genderId, String imagePath,
                                 String description) {

        String sql = """
            UPDATE product
            SET 
                product_sku = ?,
                product_name = ?,
                category_id = ?,
                gender_id = ?,
                image_path = ?,
                description = ?,
                added_at = datetime('now','localtime')
            WHERE product_id = ?;
            """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, productSKU);
            pstmt.setString(2, name);
            pstmt.setString(3, categoryId);
            pstmt.setString(4, genderId);
            pstmt.setString(5, imagePath);
            pstmt.setString(6, description);
            pstmt.setInt(7, productId);

            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0; // true if update successful

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);

            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteProductSize(int productId) {
        String sql = "DELETE FROM productsize WHERE product_id = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);

            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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

    private boolean hasStock(int productId) {
        String sql = """
                SELECT SUM(stock_quantity) as stock
                FROM productsize
                WHERE product_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);) {

            pstmt.setInt(1, productId);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                return rs.getInt("stock") > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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
                int productId = rs.getInt("product_id");

                Product product = new Product(
                        productId,
                        rs.getString("product_sku"),
                        rs.getString("product_name"),
                        rs.getString("gender"),
                        rs.getString("category_name"),
                        rs.getString("description"),
                        rs.getString("image_path"),
                        getProductSizes(productId),
                        hasStock(productId)
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
                   p.image_path, p.description, p.added_at, p.status
            FROM product p
            LEFT JOIN gender g ON p.gender_id = g.gender_id
            LEFT JOIN category c ON p.category_id = c.category_id
            ORDER BY p.product_id;
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
                        getProductSizes(rs.getInt("product_id")),
                        rs.getString("image_path"),
                        rs.getString("description"),
                        rs.getTimestamp("added_at").toLocalDateTime(),
                        rs.getString("status")
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

    public Gender getGender(String genderId) {
        String sql = """
                SELECT * FROM gender
                WHERE gender_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, genderId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Gender(rs.getString("gender_id"), rs.getString("gender"));
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ObservableList<Gender> getAllGenders() {
        ObservableList<Gender> genders = FXCollections.observableArrayList();

        String sql = "SELECT * FROM gender";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                genders.add(new Gender(rs.getString("gender_id"), rs.getString("gender")));
            }

            return genders;

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

    public ObservableList<Category> getAllCategories() {
        ObservableList<Category> categories = FXCollections.observableArrayList();

        String sql = """
                SELECT c.category_id, c.category_name, g.gender_id
                FROM category c
                JOIN gender g ON c.gender_id = g.gender_id;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                categories.add(new Category(
                        rs.getString("category_id"),
                        rs.getString("category_name"),
                        getGender(rs.getString("gender_id"))));
            }

            return categories;

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
            p.product_id,
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
                    int productId = rs.getInt("product_id");
                    String productName = rs.getString("product_name");
                    String categoryName = rs.getString("category_name");
                    String size = rs.getString("size");

                    return new Product(productId, productName, categoryName, size);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private int getNextOrder() {
        String sql = "SELECT MAX(display_order) FROM gender;";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int currentOrder = rs.getInt(1);

                    return currentOrder + 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean insertGender(String id, String gender) {
        int nextOrder = getNextOrder();

        String sql = """
                INSERT INTO gender (gender_id, gender, display_order)
                VALUES (?, ?, ?);
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, gender);
            pstmt.setInt(3, nextOrder);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateGender(String id, String gender) {
        String sql = """
                UPDATE gender
                SET gender = ?
                WHERE gender_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, gender);
            pstmt.setString(2, id);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

//    public boolean genderIsReferenced(String id) {
//        String sql = """
//                SELECT
//                    EXISTS (SELECT 1 FROM category WHERE gender_id = ?) AS in_category,
//                    EXISTS (SELECT 1 FROM product WHERE gender_id = ?) AS in_product;
//                """;
//
//        try (Connection conn = ConnectDB.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, id);
//
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                boolean inCategory = rs.getInt("in_category") == 1;
//                boolean inProduct = rs.getInt("in_product") == 1;
//
//                if (inCategory || inProduct) {
//                    return true;
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }

    public boolean genderIsReferenced(String id) {

        String sqlCategory = "SELECT 1 FROM category WHERE gender_id = ? LIMIT 1";
        String sqlProduct = "SELECT 1 FROM product WHERE gender_id = ? LIMIT 1";

        try (Connection conn = ConnectDB.connect()) {

            try (PreparedStatement ps1 = conn.prepareStatement(sqlCategory)) {
                ps1.setString(1, id);
                ResultSet rs = ps1.executeQuery();
                if (rs.next()) return true;
            }

            try (PreparedStatement ps2 = conn.prepareStatement(sqlProduct)) {
                ps2.setString(1, id);
                ResultSet rs = ps2.executeQuery();
                if (rs.next()) return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteGender(String id) {
        String sql = """
                DELETE FROM gender
                WHERE gender_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

           int affectedRows = pstmt.executeUpdate();

           return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertCategory(String categoryId, String categoryName, String genderId) {
        String sql = """
                INSERT INTO category(category_id, category_name, gender_id)
                VALUES (?, ?, ?);
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoryId);
            pstmt.setString(2, categoryName);
            pstmt.setString(3, genderId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateCategory(String categoryId, String categoryName, String genderId) {
        String sql = """
                UPDATE category
                SET category_name = ? AND gender_id = ?
                WHERE category_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoryName);
            pstmt.setString(2, genderId);
            pstmt.setString(3, categoryId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean categoryIsReferenced(String id) {
        String sql = """
                SELECT 
                    EXISTS (SELECT 1 FROM product WHERE category_id = ?) AS in_category
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean inProduct = rs.getInt("in_category") == 1;

                if (inProduct) {
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteCategory(String id) {
        String sql = """
                DELETE FROM category
                WHERE category_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
