package com.example.daisyandroidapp;

import com.google.gson.annotations.SerializedName;

public class DBAnswer {

    private String id;

    private String answer;

    @SerializedName("question_ID")
    private String questionId;

    public DBAnswer(String id, String answer, String questionId) {
        this.id = id;
        this.answer = answer;
        this.questionId = questionId;
    }

    public String getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestionId() {
        return questionId;
    }
}
