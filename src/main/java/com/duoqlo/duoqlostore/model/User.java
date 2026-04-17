package com.duoqlo.duoqlostore.model;

import java.util.ArrayList;


public class User {
    private int user_id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String address_line1;
    private String address_line2;
    private String city;
    private int postal_code;
    private String state;
    private String country = "Malaysia";
    private String fullAddress;
    private String role;
    private int is_active;

    private UserDAO userDAO = new UserDAO();

    public User() {

    }
    public User(String username){
        this.username = username; //Mainly used
    }

    public User(int user_id){
        this.user_id = user_id;
    }

    public void setId(int user_id){
        this.user_id = user_id;
    }

    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password){
        this.password = password;
    }

    public void setFullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
    }

    public void setEmail(String email) { this.email = email; }

    public void setRole(String role) {
        this.role = role;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public void setFullAddress(String addressLine1, String addressLine2,
                               String city, int postCode,
                               String state) {

        this.address_line1 = addressLine1;
        this.address_line2 = addressLine2;
        this.city = city;
        this.postal_code = postCode;
        this.state = state;

        //Pack full address
        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(addressLine1);

        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            fullAddress.append(", ").append(addressLine2);
        }

        fullAddress.append(", ").append(postCode)
                .append(" ").append(city)
                .append(", ").append(state)
                .append(", ").append(this.country);

        this.fullAddress = fullAddress.toString();
    }

    public int getId() {
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

    public String getFullName() { return this.lastName + ", " + this.fullName; }

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

    public String getPostalCodeStr() { return String.valueOf(this.postal_code); }

    public String getState() {
        return this.state;
    }

    public String getFullAddress() { return this.fullAddress; }

    public String getRole(){
        return this.role;
    }

    public int getIsActive() { return this.is_active; }

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
