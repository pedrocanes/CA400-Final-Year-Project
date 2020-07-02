package com.example.daisyandroidapp;

import com.google.gson.annotations.SerializedName;

public class DBHomeAssistant {

    private String id;

    @SerializedName("serial_key")
    private String serialKey;

    @SerializedName("lat_long")
    private String latLong;

    @SerializedName("user_ID")
    private String userId;

    public String getId() {
        return id;
    }

    public String getSerialKey() {
        return serialKey;
    }

    public String getLatLong() {
        return latLong;
    }

    public String getUserId() {
        return userId;
    }
}