package com.example.diana.voting;

import java.util.ArrayList;
import java.util.List;

public class Voting {
    public String _id;
    public String topic;
    public String status;
    public String dateStart;
    public String dateEnd;
    public int votersPercent;
    public List<Coefficient> coefficients = new ArrayList<Coefficient>();

    public Voting(String _id, String topic, String status, String dateStart, String dateEnd, int votersPercent, List<Coefficient> coefficients){
        this._id = _id;
        this.topic = topic;
        this.status = status;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.votersPercent = votersPercent;
        this.coefficients = coefficients;
    }
}
