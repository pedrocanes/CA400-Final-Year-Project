package com.example.daisyandroidapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DBAnswerReturnedList {

    @SerializedName("data")
    private List<DBAnswerReturned> dbAnswerReturnedList;

    public List<DBAnswerReturned> getDbAnswerReturnedList() {
        return dbAnswerReturnedList;
    }
}
