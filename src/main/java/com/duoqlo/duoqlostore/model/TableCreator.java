package com.duoqlo.duoqlostore.model;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TableCreator {

    public static void createTable(){
        //USERS Table
        final String usersSQL = """
                CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                email TEXT NOT NULL,
                address_line1 TEXT NOT NULL,
                address_line2 TEXT,
                city TEXT,
                postal_code INTEGER,
                state TEXT,
                country TEXT DEFAULT 'MALAYSIA',
                role TEXT CHECK(role IN('CUSTOMER','ADMIN')),
                is_active INTEGER DEFAULT 1 CHECK(is_active IN(0,1))
                );
                """;

        final String genderSQL = """
                CREATE TABLE IF NOT EXISTS gender (
                    gender_id TEXT PRIMARY KEY,
                    gender TEXT NOT NULL,
                    display_order INTEGER NOT NULL
                );
                """;

        final String categorySQL = """
                CREATE TABLE IF NOT EXISTS category (
                    category_id TEXT PRIMARY KEY,
                    category_name TEXT NOT NULL,
                    gender_id TEXT NOT NULL,
                    FOREIGN KEY (gender_id) REFERENCES gender(gender_id) ON DELETE CASCADE
                );
                """;

        //PRODUCT Table
        final String productSQL = """
                CREATE TABLE IF NOT EXISTS product (
                      product_id INTEGER PRIMARY KEY AUTOINCREMENT,
                      product_sku TEXT UNIQUE NOT NULL,
                      product_name TEXT NOT NULL,
                      category_id TEXT NOT NULL,
                      gender_id TEXT NOT NULL,
                      image_path TEXT NOT NULL,
                      description TEXT NOT NULL,
                      added_at TIMESTAMP DEFAULT (datetime('now', 'localtime')),
                      status TEXT DEFAULT 'AVAILABLE' CHECK(status IN('AVAILABLE','OUT OF STOCK')),
                      FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE,
                      FOREIGN KEY (gender_id) REFERENCES gender(gender_id) ON DELETE CASCADE
                  );
                """;

        //PRODUCTSIZE Table
        final String productSizeSQL = """
                CREATE TABLE IF NOT EXISTS productsize (
                      productsize_id INTEGER PRIMARY KEY AUTOINCREMENT,
                      productsize_sku TEXT UNIQUE NOT NULL,
                      product_id INTEGER NOT NULL,
                      size TEXT NOT NULL,
                      stock_quantity INTEGER NOT NULL DEFAULT 0,
                      price REAL NOT NULL,
                      FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
                  );
                """;

        //CART Table
        final String cartSQL = """
                CREATE TABLE IF NOT EXISTS cart (
                cart_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                last_updated TIMESTAMP DEFAULT (datetime('now', 'localtime')),
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
                );
                """;

        final String cartItemSQL = """
                CREATE TABLE IF NOT EXISTS cartitem (
                cart_id INTEGER NOT NULL,
                productsize_id INTEGER NOT NULL,
                product_quantity INTEGER NOT NULL,
                sub_total REAL NOT NULL,
                added_date TIMESTAMP DEFAULT (datetime('now', 'localtime')),
                FOREIGN KEY (cart_id) REFERENCES cart(cart_id),
                FOREIGN KEY (productsize_id) REFERENCES productsize(productsize_id)
                );
                """;

        //ORDERS Table
        final String ordersSQL = """
                CREATE TABLE IF NOT EXISTS orders (
                order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                order_date TIMESTAMP DEFAULT (datetime('now', 'localtime')),
                total_items INT NOT NULL,
                total_price REAL NOT NULL,
                status TEXT DEFAULT 'PENDING' CHECK(status IN('PENDING','DONE')),
                shipping_add TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(user_id)
                );
                """;

        //ORDERITEM Table
        final String orderitemSQL = """
                CREATE TABLE IF NOT EXISTS orderitem (
                orderitem_id INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id INTEGER NOT NULL,
                productsize_id INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                sub_total REAL NOT NULL,
                FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                FOREIGN KEY (productsize_id) REFERENCES productsize(productsize_id)
                );
                """;

        String[] SQLStatements = {
                usersSQL,
                genderSQL,
                categorySQL,
                productSQL,
                productSizeSQL,
                cartSQL,
                cartItemSQL,
                ordersSQL,
                orderitemSQL
        };

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement()){
            stmt.execute("PRAGMA foreign_keys = ON;");

            for(String sql : SQLStatements){
                stmt.execute(sql);
            }

            System.out.println("All tables created successfully!");
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public static void initSequence() {
        String usersSQL = "INSERT OR REPLACE INTO sqlite_sequence(name, seq) VALUES ('users', 999)";                //Next: 1000
        String productSQL = "INSERT OR REPLACE INTO sqlite_sequence(name, seq) VALUES ('product', 4999)";           //Next: 5000
        String productSizeSQL = "INSERT OR REPLACE INTO sqlite_sequence(name, seq) VALUES ('productsize', 5499)";   //Next: 5500
        String ordersSQL = "INSERT OR REPLACE INTO sqlite_sequence(name, seq) VALUES ('orders', 7999)";             //Next: 8000
        String orderItemSQL = "INSERT OR REPLACE INTO sqlite_sequence(name, seq) VALUES ('orderitem', 8999)";       //Next: 9000
        String cartSQL = "INSERT OR REPLACE INTO sqlite_sequence(name, seq) VALUES ('cart', 499)";                  //Next: 500

        String[] SQLStatements = {
                usersSQL,
                productSQL,
                productSizeSQL,
                ordersSQL,
                orderItemSQL,
                cartSQL
        };

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement()){
            stmt.execute("PRAGMA foreign_keys = ON;");

            for(String sql : SQLStatements){
                stmt.execute(sql);
            }

            System.out.println("All table sequence initiated successfully!");
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public static void initTable() {
        String genderSQL = """
                INSERT OR IGNORE INTO gender(gender_id, gender, display_order)
                VALUES
                ('U', 'UNISEX', 1),
                ('W', 'WOMEN', 2),
                ('M', 'MEN', 3);
                """;

        String categorySQL = """
                INSERT OR IGNORE INTO category(category_id, category_name, gender_id)
                VALUES
                ('RN', 'ROUND-NECK SHIRT', 'U'),
                ('BD', 'BUTTON-DOWN SHIRT', 'U'),
                ('PL', 'POLO SHIRT', 'M');
                """;

        String productSQL = """
                INSERT OR IGNORE INTO product(product_id, product_sku, product_name, category_id, gender_id, image_path,
                    description, added_at, status)
                VALUES
                (5001, 'U-RN-5001', 'Plain Round Neck', 'RN', 'U', 'products/U-RN-5001',
                 'A clean, versatile round-neck T-shirt made with comfy, breathable cotton fabric.',
                 datetime('now','localtime'), 'AVAILABLE'),
                
                (5002, 'M-RN-5002', 'Charcoal Acid Wash Tee', 'RN', 'M', 'products/M-RN-5002',
                 'A vintage-inspired heavy cotton shirt with a unique faded texture and relaxed fit.',
                 datetime('now','localtime'), 'AVAILABLE'),
                
                (5003, 'W-RN-5003', 'Cobalt Crest Tee', 'RN', 'W', 'products/W-RN-5003',
                 'A vibrant royal blue round-neck featuring a bold silver lion emblem for an energetic, spirited look.',
                 datetime('now','localtime'), 'AVAILABLE'),
                
                (5004, 'U-RN-5004', 'Srsly Essential Tee', 'RN', 'U', 'products/U-RN-5004',
                 'A clean, breathable white T-shirt with a small, centered block-letter logo for a modern streetwear aesthetic.',
                 datetime('now','localtime'), 'AVAILABLE'),
                
                (5005, 'M-PL-5005', 'Sandstone Mesh Polo', 'PL', 'M', 'products/M-PL-5005',
                 'A modern, textured-knit polo T-shirt featuring a relaxed open-neck collar and a refined, slim-fit silhouette.',
                 datetime('now','localtime'), 'AVAILABLE'),
                
                (5006, 'M-BD-5006', 'Blue Slim-fit Shirt', 'BD', 'M', 'products/M-BD-5006',
                 'A crisp, light blue button-down crafted from smooth poplin fabric with a modern, tailored cut.',
                 datetime('now','localtime'), 'AVAILABLE');
                """;

        String productSizeSQL = """
                INSERT OR IGNORE INTO productsize(productsize_id, productsize_sku, product_id, size, stock_quantity, price)
                VALUES
                (5501, 'U-RN-5001-S', 5001, 'S', 100, 29.9),
                (5502, 'U-RN-5001-M', 5001, 'M', 100, 29.9),
                (5503, 'U-RN-5001-L', 5001, 'L', 100, 29.9),
                (5504, 'U-RN-5001-XL', 5001, 'XL', 100, 30.9),
                
                (5505, 'M-RN-5002-S', 5002, 'S', 100, 32.9),
                (5506, 'M-RN-5002-M', 5002, 'M', 0, 32.9),
                (5507, 'M-RN-5002-L', 5002, 'L', 100, 32.9),
                (5508, 'M-RN-5002-XL', 5002, 'XL', 100, 34.9),
                
                (5509, 'W-RN-5003-S', 5003, 'S', 100, 39.9),
                (5510, 'W-RN-5003-M', 5003, 'M', 100, 39.9),
                (5511, 'W-RN-5003-L', 5003, 'L', 100, 39.9),
                (5512, 'W-RN-5003-XL', 5003, 'XL', 100, 41.9),
                
                (5513, 'U-RN-5004-S', 5004, 'S', 100, 32.9),
                (5514, 'U-RN-5004-M', 5004, 'M', 100, 32.9),
                (5515, 'U-RN-5004-L', 5004, 'L', 100, 32.9),
                (5516, 'U-RN-5004-XL', 5004, 'XL', 0, 33.9),
                (5517, 'U-RN-5004-XXL', 5004, 'XXL', 100, 34.9),
                
                (5518, 'M-PL-5005-S', 5005, 'S', 100, 50.9),
                (5519, 'M-PL-5005-M', 5005, 'M', 100, 50.9),
                (5520, 'M-PL-5005-L', 5005, 'L', 100, 50.9),
                (5521, 'M-PL-5005-XL', 5005, 'XL', 100, 52.9),
                
                (5522, 'M-BD-5006-S', 5006, 'S', 100, 49.9),
                (5523, 'M-BD-5006-M', 5006, 'M', 100, 49.9),
                (5524, 'M-BD-5006-L', 5006, 'L', 100, 49.9),
                (5525, 'M-BD-5006-XL', 5006, 'XL', 100, 51.9),
                (5526, 'M-BD-5006-XXL', 5006, 'XXL', 100, 52.9);
                """;

        String[] SQLStatements = {
                genderSQL,
                categorySQL,
                productSQL,
                productSizeSQL
        };

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement()){
            stmt.execute("PRAGMA foreign_keys = ON;");

            for(String sql : SQLStatements){
                stmt.execute(sql);
            }

            System.out.println("All table data initiated successfully!");
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }
}
