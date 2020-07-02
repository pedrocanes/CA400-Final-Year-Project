package com.example.daisyandroidapp;

public class DBUser {

    private String id;

    private String username;

    private String pair_pin;

    private DBUser user;

    public DBUser(String id, String username, String pair_pin) {
        this.id = id;
        this.username = username;
        this.pair_pin = pair_pin;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPair_pin() {
        return pair_pin;
    }

    public DBUser getUser() {
        return user;
    }
}
