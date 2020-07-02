package com.example.daisyandroidapp;

public class DBResearcher {

    private String id;

    private String username;

    private String email;

    private DBResearcher user;

    public DBResearcher(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public DBResearcher getUser() {
        return user;
    }
}
