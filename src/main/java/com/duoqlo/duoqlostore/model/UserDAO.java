package com.duoqlo.duoqlostore.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.List;

public class UserDAO {
    public void insert(User user) {
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

        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public boolean updateInfo(int userId, List<String> infoList) {
        String firstName = infoList.get(0);
        String lastName = infoList.get(1);
        String email = infoList.get(2);

        String sql = """
                UPDATE users
                SET first_name = ?,
                last_name = ?,
                email = ?
                WHERE user_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setInt(4, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public boolean updateAddress(int userId, List<String> addresList) {
        String addressLine1 = addresList.get(0);
        String addressLine2 = addresList.get(1);
        String city = addresList.get(2);
        int postalCode = Integer.parseInt(addresList.get(3));
        String state = addresList.get(4);

        String sql = """
                UPDATE users
                SET address_line1 = ?,
                    address_line2 = ?,
                    city = ?,
                    postal_code = ?,
                    state = ?
                WHERE user_id = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, addressLine1);
            pstmt.setString(2, addressLine2);
            pstmt.setString(3, city);
            pstmt.setInt(4, postalCode);
            pstmt.setString(5, state);
            pstmt.setInt(6, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public boolean updateCredentials(int userId, List<String> credentialList) {
        String username = credentialList.get(0);
        String password = credentialList.get(1);

        String sql = """
                UPDATE users
                SET username = ?,
                    password = ?
                WHERE user_id = ?;
                """;
        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        User user = new User();

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("first_name"), rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setFullAddress(
                        rs.getString("address_line1"),
                        rs.getString("address_line2"),
                        rs.getString("city"),
                        rs.getInt("postal_code"),
                        rs.getString("state")
                );
                user.setRole(rs.getString("role"));
                user.setIs_active(rs.getInt("is_active"));
            }

        } catch (SQLException e){
            System.err.println(e.getMessage());
        }

        return user;
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
                user.setFullName(rs.getString("first_name"), rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setFullAddress(
                        rs.getString("address_line1"),
                        rs.getString("address_line2"),
                        rs.getString("city"),
                        rs.getInt("postal_code"),
                        rs.getString("state")
                );
                user.setRole(rs.getString("role"));
                user.setIs_active(rs.getInt("is_active"));
            }

            return user;

        } catch (SQLException e){
            System.err.println(e.getMessage());
        }

        return null;
    }

    public String getPasswordByUsername(String username) {
        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                return rs.getString("password");
            }

            return null;

        } catch (SQLException e){
            System.err.println(e.getMessage());
        }

        return null;
    }

    public ObservableList<User> getAllUsersObservable() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String query = """
                SELECT * FROM users;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("first_name"), rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setIs_active(rs.getInt("is_active"));

                // Set address fields
                user.setFullAddress(
                        rs.getString("address_line1"),
                        rs.getString("address_line2"),
                        rs.getString("city"),
                        rs.getInt("postal_code"),
                        rs.getString("state")
                );

                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return users;
    }

    public boolean isActive(String username) {
        String sql = """
                SELECT is_active FROM users
                WHERE username = ?;
                """;

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                if(rs.getInt("is_active") == 1) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (SQLException e){
            System.err.println(e.getMessage());
        }

        return false;
    }

    public boolean adminExists() {
        String sql = "SELECT 1 FROM users WHERE role = 'ADMIN';";

        try(Connection conn = ConnectDB.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public void initAdmin() {
        String sql = """
                INSERT INTO 
                users(user_id, username, password, first_name, 
                last_name, email, address_line1, city, postal_code, state, role)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try(Connection conn = ConnectDB.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, 1000); //user id
            pstmt.setString(2, "admin123"); //username
            pstmt.setString(3, BCrypt.hashpw("Admin@123", BCrypt.gensalt())); //password
            pstmt.setString(4, "Admin"); //first_name
            pstmt.setString(5, "Admin"); //last_name
            pstmt.setString(6, "admin123@gmail.com"); //email
            pstmt.setString(7, "address"); //address line 1
            pstmt.setString(8, "Iskandar Puteri"); //city
            pstmt.setString(9, "79100"); //postal_code
            pstmt.setString(10, "Johor"); //state
            pstmt.setString(11, "ADMIN"); //role

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean usernameExists(String username){
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e){
            System.err.println(e.getMessage());
        }

        return false;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";

        try (Connection conn = ConnectDB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e){
            System.err.println(e.getMessage());
        }

        return false;
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
            System.err.println(e.getMessage());
        }

        return false;
    }

    public boolean deactivateUser(int userid) {
        String sql = """
                UPDATE users
                SET is_active = 0
                WHERE user_id = ?;
                """;

        try (Connection conn = ConnectDB.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, userid);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e){
            System.err.println(e.getMessage());
        }

        return false;
    }

    public boolean reactivateUser(int userid) {
        String sql = """
                UPDATE users
                SET is_active = 1
                WHERE user_id = ?;
                """;

        try (Connection conn = ConnectDB.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, userid);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e){
            System.err.println(e.getMessage());
        }

        return false;
    }
}
