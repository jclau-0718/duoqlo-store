package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.controller.Address;

import java.io.*;
import java.util.*;

public class PostcodeService {

    private Map<String, Address> postcodeMap = new HashMap<>();

    public PostcodeService() {
        loadData();
    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        getClass().getResourceAsStream("/postcodes.csv")
                )
        )) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String postcode = parts[0];
                String city = parts[1];
                String state = parts[2];

                postcodeMap.put(postcode, new Address(postcode, city, state));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Address lookup(String postcode) {
        return postcodeMap.get(postcode);
    }
}