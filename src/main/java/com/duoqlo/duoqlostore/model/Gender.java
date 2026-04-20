package com.duoqlo.duoqlostore.model;

public class Gender {
    private String id;
    private String gender;

    public Gender(String id, String gender) {
        this.id = id;
        this.gender = gender;
    }

    @Override
    public String toString() {
        return getGender();
    }

    public String getId() { return this.id; }

    public String getGender() { return this.gender; }
}
