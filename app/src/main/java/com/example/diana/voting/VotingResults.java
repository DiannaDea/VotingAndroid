package com.example.diana.voting;

import java.util.ArrayList;
import java.util.List;

public class VotingResults {
    List<ResultsItem> results = new ArrayList<ResultsItem>();
    String votingId;

    public VotingResults(List<ResultsItem> results, String votingId) {
        this.results = results;
        this.votingId = votingId;
    }

}
