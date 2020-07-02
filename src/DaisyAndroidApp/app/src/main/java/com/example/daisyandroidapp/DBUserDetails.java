package com.example.daisyandroidapp;

import com.google.gson.annotations.SerializedName;

public class DBUserDetails {

    private String id;

    @SerializedName("user_ID")
    private String userId;

    @SerializedName("question_ID")
    private String questionId;

    @SerializedName("ask_question")
    private Boolean askQuestion;

    @SerializedName("user_available")
    private String userAvailable;

    @SerializedName("device_to_use")
    private String deviceToUse;

    public DBUserDetails(String id, String userId, String questionId, Boolean askQuestion, String userAvailable, String deviceToUse) {
        this.id = id;
        this.userId = userId;
        this.questionId = questionId;
        this.askQuestion = askQuestion;
        this.userAvailable = userAvailable;
        this.deviceToUse = deviceToUse;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public Boolean getAskQuestion() {
        return askQuestion;
    }

    public String getUserAvailable() {
        return userAvailable;
    }

    public String getDeviceToUse() {
        return deviceToUse;
    }
}
