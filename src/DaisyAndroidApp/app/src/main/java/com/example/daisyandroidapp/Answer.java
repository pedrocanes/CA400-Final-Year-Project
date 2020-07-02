package com.example.daisyandroidapp;

public class Answer {
    private String username;
    private String question;
    private String answer;
    private String timeAnswered;
    private String userQuestioned;

    public Answer() {}

    public Answer(String username, String question, String answer, String timeAnswered) {
        this.username = username;
        this.question = question;
        this.answer = answer;
        this.timeAnswered = timeAnswered;
    }

    public String getUsername() {
        return username;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getTimeAnswered() {
        return timeAnswered;
    }

    public String getUserQuestioned() {
        return userQuestioned;
    }
}
