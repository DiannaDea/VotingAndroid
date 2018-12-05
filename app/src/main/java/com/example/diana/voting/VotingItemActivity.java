package com.example.diana.voting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VotingItemActivity extends AppCompatActivity {
    String baseUrl = "http://192.168.37.146:5000/api";
    RequestQueue requestQueue;
    Gson gson = new Gson();

    String votingId;
    Voting votingItem;
    List<Candidate> candidates = new ArrayList<Candidate>();

    LinearLayout candidatesList;
    LinearLayout coefficientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_item);

        Intent intent = getIntent();
        this.votingId = intent.getStringExtra(VotingsListActivity.VOTING_ID);

        requestQueue = Volley.newRequestQueue(this);

        this.getVoting();
        this.getCandidates();
    }


    private void fillVotingFields(Voting voting){
        TextView topicField = findViewById(R.id.topic);
        topicField.setText(voting.topic);

        TextView dateStartField = findViewById(R.id.dateStart);
        dateStartField.setText(voting.dateStart);

        TextView dateEndField = findViewById(R.id.dateEnd);
        dateEndField.setText(voting.dateEnd);

        TextView percentField = findViewById(R.id.percent);
        percentField.setText(String.format("%d %%", voting.votersPercent));
    }


    private void getVoting(){
        String url = String.format("%s/votings/%s", this.baseUrl, this.votingId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                (String)null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObj) {
                        Voting voting = gson.fromJson(jsonObj.toString(), Voting.class);
                        votingItem = voting;
                        fillVotingFields(voting);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e("Volley", error.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void getCandidates() {
        String url = String.format("%s/votings/%s/candidates", this.baseUrl, this.votingId);

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    Candidate candidate = gson.fromJson(jsonObj.toString(), Candidate.class);

                                    candidates.add(candidate);
                                } catch (JSONException e) {
                                    Log.e("Volley", "Invalid JSON Object.");
                                }
                            }
                        } else {
                            //setRepoListText("No repos found.");
                        }
                        System.out.println(candidates);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        //setRepoListText("Error while calling REST API");
                        Log.e("Volley", error.toString());
                    }
                }
        );
        requestQueue.add(arrReq);
    }
}
