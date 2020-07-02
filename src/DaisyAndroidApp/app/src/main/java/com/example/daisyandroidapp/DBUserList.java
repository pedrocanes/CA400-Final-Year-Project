package com.example.daisyandroidapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DBUserList {

    @SerializedName("data")
    private List<DBUser> dbUserList;

    public List<DBUser> getDbUserList() {
        return dbUserList;
    }
}