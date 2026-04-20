package com.duoqlo.duoqlostore.model;

public class Category {
    private String id;
    private String categoryName;
    private Gender gender;

    public Category(String id, String categoryName, Gender gender) {
        this.id = id;
        this.categoryName = categoryName;
        this.gender = gender;
    }

    public String getId() { return this.id; }

    public String getCategoryName() { return this.categoryName; }

    public Gender getGender() { return this.gender; }

    public String getGenderId() { return this.gender.getId(); }

    public String getGenderName() { return this.gender.getGender(); }
}
