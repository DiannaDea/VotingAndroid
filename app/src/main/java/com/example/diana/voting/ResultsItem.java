package com.example.diana.voting;

public class ResultsItem {
    Candidate candidate;
    int votesCount;
    double votesValue;

    public ResultsItem(Candidate candidate, int votesCount, int votesValue) {
        this.candidate = candidate;
        this.votesCount = votesCount;
        this.votesValue = votesValue;
    }
}
