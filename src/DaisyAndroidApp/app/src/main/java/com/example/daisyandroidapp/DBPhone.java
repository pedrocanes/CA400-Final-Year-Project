package com.example.daisyandroidapp;

import com.google.gson.annotations.SerializedName;

public class DBPhone {

    private String id;

    //@SerializedName("serial_key")
    private String serial_key;

    //@SerializedName("lat_long")
    private String lat_long;

    //@SerializedName("user_ID")
    private String user_ID;

    public DBPhone(String id, String serial_key, String lat_long, String user_ID) {
        this.id = id;
        this.serial_key = serial_key;
        this.lat_long = lat_long;
        this.user_ID = user_ID;
    }

    public String getId() {
        return id;
    }

    public String getSerial_key() {
        return serial_key;
    }

    public String getLat_long() {
        return lat_long;
    }

    public String getUser_ID() {
        return user_ID;
    }
}
