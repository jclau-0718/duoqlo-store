package com.duoqlo.duoqlostore.model;

import javax.xml.crypto.Data;
import java.sql.*;

public class UserDAO extends DataAccessObject<User> {
    @Override
    public void insert(User user){
        String sql = """
                INSERT INTO users
                (username, password, first_name, last_name, email, address_line1, address_line2, city, postal_code, state, role)
                VALUES (?,?,?,?,?,?,?,?,?,?,?)
                """;
        try(Connection conn = ConnectDB.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getAddressLine1());
            pstmt.setString(7, user.getAddressLine2());
            pstmt.setString(8, user.getCity());
            pstmt.setInt(9, user.getPostalCode());
            pstmt.setString(10, user.getState());
            pstmt.setString(11, user.getRole());

            //Insert data
            pstmt.executeUpdate();

            //Get generated id
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                user.setId(generatedId); // if you allow setter
            }

            System.out.println("User added!");

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(User user){
        String sql = "UPDATE users SET is_active = 0 WHERE user_id = ?";

        try(Connection conn = ConnectDB.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, user.getId());
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

        String sql = "SELECT user_id FROM users WHERE username = ?";

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
        if(!usernameExists(username)) {
            return null;
        }


        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            User user = new User(username);

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                user.setId(rs.getInt("user_id"));
                user.setRole(rs.getString("role"));
                user.setIs_active(rs.getInt("is_active"));
            }

            return user;

        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public String getPasswordByUsername(String username) {
        if(!usernameExists(username)) {
            return null;
        }

        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            User user = new User(username);

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                return rs.getString("password");
            }

            return null;

        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public User getUserByCredentials(String username, String password){
        if(!usernameExists(username)) {
            return null;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            User user = new User(username);

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            if(rs.next()){
                user.setId(rs.getInt("user_id"));
                user.setRole(rs.getString("role"));
                user.setIs_active(rs.getInt("is_active"));

                return user;
            } else {
                return null;
            }



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

    public boolean emailExists(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";

        try (Connection conn = ConnectDB.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean duplicateExists(String username, String firstname, String lastname, String email) {
        String sql = "SELECT user_id FROM users WHERE username = ? AND first_name = ? AND last_name = ? AND email = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, username);
            pstmt.setString(2, firstname);
            pstmt.setString(3, lastname);
            pstmt.setString(4, email);

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
