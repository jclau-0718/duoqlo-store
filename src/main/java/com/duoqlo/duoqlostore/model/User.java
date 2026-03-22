package com.duoqlo.duoqlostore.model;

import java.util.ArrayList;

public class User {
    private int user_id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String address_line1;
    private String address_line2;
    private String city;
    private int postal_code;
    private String state;
    private String country;
    private String role;
    private int is_active;

    private UserDAO userDAO = new UserDAO();

    public User() {

    }
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
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getAddressLine1() {
        return this.address_line1;
    }

    public String getAddressLine2() {
        return this.address_line2;
    }

    public String getCity() {
        return this.city;
    }

    public int getPostalCode() {
        return this.postal_code;
    }

    public String getState() {
        return this.state;
    }

    public String getRole(){
        return this.role;
    }

    public String getRole(int user_id){
        return userDAO.getRole(user_id);
    }

    public void setInfo(ArrayList<String> info){

        this.username = info.get(0);
        this.password = info.get(1);
        this.firstName = info.get(2);
        this.lastName = info.get(3);
        this.email = info.get(4);
        this.address_line1 = info.get(5);
        this.address_line2 = info.get(6);
        this.city = info.get(7);
        this.postal_code = Integer.parseInt(info.get(8));
        this.state = info.get(9);
        this.role = info.get(10);
    }

}
