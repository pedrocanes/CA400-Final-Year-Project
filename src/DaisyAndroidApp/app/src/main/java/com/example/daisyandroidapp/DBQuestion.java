package com.example.daisyandroidapp;

import com.google.gson.annotations.SerializedName;

public class DBQuestion {

    private String id;

    private String question;

    @SerializedName("researcher_ID")
    private String researcherId;

    public DBQuestion() {}

    public DBQuestion(String id, String question, String researcherId) {
        this.id = id;
        this.question = question;
        this.researcherId = researcherId;
    }

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getResearcherId() {
        return researcherId;
    }
}
