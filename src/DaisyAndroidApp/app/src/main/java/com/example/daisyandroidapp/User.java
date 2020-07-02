package com.example.daisyandroidapp;

import com.google.firebase.database.Exclude;

public class User {
    private String userId;
    private String type;
    private String name;
    private String email;
    private Boolean paired = false;

    public User(String type, String name, String email) {
        this.type = type;
        this.name = name;
        this.email = email;
    }

    public User() {
        //publin no-arg constructor needed
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getPaired() {
        return paired;
    }

    public void setPaired(Boolean paired) {
        this.paired = paired;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
