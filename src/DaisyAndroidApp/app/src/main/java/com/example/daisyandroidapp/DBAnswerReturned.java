package com.example.daisyandroidapp;

import com.google.gson.annotations.SerializedName;

public class DBAnswerReturned {

    private String id;

    @SerializedName("answer_ID")
    private String answerId;

    @SerializedName("user_ID")
    private String userId;

    @SerializedName("device_used")
    private String deviceUsed;

    @SerializedName("answer_time")
    private String answerTime;

    public DBAnswerReturned(String id, String answerId, String userId, String deviceUsed, String answerTime) {
        this.id = id;
        this.answerId = answerId;
        this.userId = userId;
        this.deviceUsed = deviceUsed;
        this.answerTime = answerTime;
    }

    public String getId() {
        return id;
    }

    public String getAnswerId() {
        return answerId;
    }

    public String getUserId() {
        return userId;
    }

    public String getDeviceUsed() {
        return deviceUsed;
    }

    public String getAnswerTime() {
        return answerTime;
    }
}
