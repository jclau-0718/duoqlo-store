package com.duoqlo.duoqlostore.controller;

public class Address {
    private String postcode;
    private String city;
    private String state;

    public Address(String postcode, String city, String state) {
        this.postcode = postcode;
        this.city = city;
        this.state = state;
    }

    public String getCity() { return city; }
    public String getState() { return state; }
}
