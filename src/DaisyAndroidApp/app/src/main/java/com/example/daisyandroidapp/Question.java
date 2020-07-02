package com.example.daisyandroidapp;

import java.util.ArrayList;

public class Question {
    private String title;
    private String id;
    private String researcherId;
    private String userId;
    private ArrayList<String> answers;

    //empty constructor needed
    public Question() {}

    public Question(String title, String id, String researcherId, String userId, ArrayList<String> answers) {
        this.title = title;
        this.id = id;
        this.researcherId = researcherId;
        this.userId = userId;
        this.answers = answers;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getResearcherId() {
        return researcherId;
    }

    public String getUserId() {
        return userId;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }
}
