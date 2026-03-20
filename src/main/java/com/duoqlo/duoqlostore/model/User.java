package com.duoqlo.duoqlostore.model;

public class User {
    private int user_id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String role;
    private int is_active;

    private UserDAO userDAO = new UserDAO();

    public User(String username){
        this.username = username;
    }

    public User(int user_id,String username){
        this.user_id = user_id;
        this.username = username;
    }

    public User(int user_id){
        this.user_id = user_id;
    }
    public void setID(int user_id){
        this.user_id = user_id;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public int getID() {
        return this.user_id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getRole(){
        return this.role;
    }

    public String getRole(int user_id){
        return userDAO.getRole(user_id);
    }

    public void setInfo(int user_id, String username, String password, String first_name, String last_name, String email, String address, String role, int is_active){
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.firstName = first_name;
        this.lastName = last_name;
        this.email = email;
        this.address = address;
        this.role = role;
        this.is_active = is_active;
    }

}
