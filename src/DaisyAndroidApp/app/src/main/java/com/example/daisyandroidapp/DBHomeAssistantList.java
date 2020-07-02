package com.example.daisyandroidapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DBHomeAssistantList {

    @SerializedName("data")
    private List<DBHomeAssistant> dbHomeAssistantList;

    public List<DBHomeAssistant> getDbHomeAssistantList() {
        return dbHomeAssistantList;
    }
}
