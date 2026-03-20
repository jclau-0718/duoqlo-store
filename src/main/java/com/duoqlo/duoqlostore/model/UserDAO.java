package com.duoqlo.duoqlostore.model;

import javax.xml.crypto.Data;
import java.sql.*;

public class UserDAO extends DataAccessObject<User> {
    @Override
    public void insert(User user){
        String sql = """
                INSERT INTO users\s
                (username, password, first_name, last_name, email, address, role)
                VALUES (?,?,?,?,?,?,?)
                """;
        try(Connection conn = ConnectDB.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getAddress());
            pstmt.setString(7, user.getRole());

            //Insert data
            pstmt.executeUpdate();

            //Get generated id
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                user.setID(generatedId); // if you allow setter
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(User user){
        String sql = "UPDATE users SET is_active = 0 WHERE user_id = ?";

        try(Connection conn = ConnectDB.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, user.getID());
            pstmt.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getID(User user) {

        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = ConnectDB.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                int userID = rs.getInt("user_id");
                return userID;
            }

            return -1; //user_id not found

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; //user_id not found
        }
    }

    public String getRole(int userID){
        String sql = "SELECT role FROM users WHERE user_id = ?";

        try (Connection conn = ConnectDB.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                String role = rs.getString("role");
                return role;
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getIDByUsername(String username) {

        String sql = "SELECT user_id from users where username = ?";

        try (Connection conn = ConnectDB.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                int userID = rs.getInt("user_id");
                return userID;
            }

            return -1; //user_id not found

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; //user_id not found
        }
    }

    public User getUserByUsername(String username){
        User user = new User(username);

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            if(rs.next()){


                int id = rs.getInt("user_id");
                String pass = rs.getString("password");
                String fn = rs.getString("first_name");
                String ln = rs.getString("last_name");
                String e = rs.getString("email");
                String add = rs.getString("address");
                String r = rs.getString("role");
                int is_act = rs.getInt("is_active");

                user.setInfo(id, username, pass, fn, ln, e, add, r, is_act);
            }

            return user;

        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean usernameExists(String username){
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = ConnectDB.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkCredentials(String username, String password){
        String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";

        try (Connection conn = ConnectDB.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

//    public User getUserByUsername(String username){
//        String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";
//    }
}
