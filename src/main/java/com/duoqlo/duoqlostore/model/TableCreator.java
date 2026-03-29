package com.duoqlo.duoqlostore.model;

import java.sql.Connection;
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
                    gender TEXT NOT NULL
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

        final String subCategorySQL = """
                CREATE TABLE IF NOT EXISTS subcategory (
                    subcategory_id TEXT PRIMARY KEY,
                    subcategory_name TEXT NOT NULL,
                    category_id TEXT NOT NULL,
                    display_order INTEGER NOT NULL,
                    FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE
                );
                """;

        //PRODUCT Table
        final String productSQL = """
                CREATE TABLE IF NOT EXISTS product (
                      product_id INTEGER PRIMARY KEY AUTOINCREMENT,
                      product_sku TEXT UNIQUE NOT NULL,
                      product_name TEXT NOT NULL,
                      subcategory_id TEXT NOT NULL,
                      gender_id TEXT NOT NULL,
                      image_path TEXT NOT NULL,
                      description TEXT NOT NULL,
                      added_at TIMESTAMP DEFAULT (datetime('now', 'localtime')),
                      FOREIGN KEY (subcategory_id) REFERENCES subcategory(subcategory_id) ON DELETE CASCADE,
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
                productsize_id INTEGER NOT NULL,
                product_quantity INTEGER NOT NULL,
                added_date TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                FOREIGN KEY (productsize_id) REFERENCES productsize(productsize_id)
                );
                """;

        //ORDERS Table
        final String ordersSQL = """
                CREATE TABLE IF NOT EXISTS orders (
                order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                order_date TEXT NOT NULL,
                total_price INTEGER NOT NULL,
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
                product_size_id INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                price INTEGER NOT NULL,
                FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                FOREIGN KEY (productsize_id) REFERENCES productsize(productsize_id)
                );
                """;

        final String insertCatSQL = """
                INSERT OR IGNORE INTO category (category_id, category_name, gender_id) VALUES
                ('ST', 'SHIRTS', 'U'),
                ('PT', 'PANTS', 'U'),
                ('SK', 'SOCKS', 'U'),
                ('DS', 'DRESSES', 'W');
                """;

        final String insertSubCatSQL = """
                INSERT OR IGNORE INTO subcategory (subcategory_id, subcategory_name, category_id, display_order) VALUES
                ('RN', 'ROUND-NECK SHIRT', 'ST', 1),
                ('PT', 'POLO T-SHIRT', 'ST', 2),
                ('BD', 'BUTTON-DOWN SHIRT', 'ST', 3),
                ('HD', 'HOODIE', 'ST', 4),
                ('SH', 'SHORTS', 'PT', 5),
                ('TR', 'TROUSERS', 'PT', 6),
                ('WL', 'WOOL SOCKS', 'SK', 7),
                ('CT', 'COTTON SOCKS', 'SK', 8),
                ('BF', 'BAMBOO FIBRE SOCKS', 'SK', 9),
                ('NY', 'NYLON SOCKS', 'SK', 10),
                ('PE', 'POLYESTER SOCKS', 'SK', 11),
                ('MS', 'MIDI SKIRT', 'DS', 12);
                """;

        String[] SQLStatements = {
                usersSQL,
                genderSQL,
                categorySQL,
                subCategorySQL,
                productSQL,
                productSizeSQL,
                cartSQL,
                ordersSQL,
                orderitemSQL,
                insertCatSQL,
                insertSubCatSQL
        };

        try (Connection conn = ConnectDB.connect();
             Statement stmt = conn.createStatement()){
            stmt.execute("PRAGMA foreign_keys = ON;");

            for(String sql : SQLStatements){
                stmt.execute(sql);
            }

            System.out.println("All tables created successfully!");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
