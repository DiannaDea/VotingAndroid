package com.example.diana.voting;

public class State {
    public String userId;
    public String curGroupId;
    public String curVotingId;
    public String baseUrl = "https://voting-app-university-server.herokuapp.com/api";

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setCurGroupId(String curGroupId) {
        this.curGroupId = curGroupId;
    }

    public String getCurGroupId() {
        return curGroupId;
    }

    public void setCurVotingId(String curVotingId) {
        this.curVotingId = curVotingId;
    }

    public String getCurVotingId() {
        return curVotingId;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    private static final State state = new State();
    public static State getInstance() {return state;}
}
