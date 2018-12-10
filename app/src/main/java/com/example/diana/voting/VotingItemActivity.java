package com.example.diana.voting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        TextView statusField = findViewById(R.id.status);
        statusField.setText(voting.status);
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

    private void addCandidateToDialogList(Candidate candidate) {
        TextView candidateItem = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,20);

        candidateItem.setLayoutParams(params);
        candidateItem.setText(candidate.name);
        candidateItem.setBackgroundColor(0xffffdbdb);
        candidateItem.setPadding(20, 20, 20, 20);

        this.candidatesList.addView(candidateItem);
    }

    private void addCoefficientToDialogList(Coefficient coefficient) {
        TextView coefficientItem = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,20);

        coefficientItem.setLayoutParams(params);
        coefficientItem.setText(coefficient.name);
        coefficientItem.setBackgroundColor(0xffffdbdb);
        coefficientItem.setPadding(20, 20, 20, 20);

        this.coefficientsList.addView(coefficientItem);
    }

    private void fillDialogWithCandidates(){
        for (Candidate candidate: candidates) {
            addCandidateToDialogList(candidate);
        }
    }

    private void fillDialogWithCoefficients(){
        for (Coefficient coefficient: votingItem.coefficients) {
            addCoefficientToDialogList(coefficient);
        }
    }

    public void viewCandidates(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Voting candidates");

        final View customLayout = getLayoutInflater().inflate(R.layout.candidates_dialog, null);
        builder.setView(customLayout);

        this.candidatesList = (LinearLayout) customLayout.findViewById(R.id.candidatesList);

        fillDialogWithCandidates();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog candidatesDialog = builder.create();
        candidatesDialog.show();
    }

    public void viewCoefficients(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Voting coefficients");

        final View customLayout = getLayoutInflater().inflate(R.layout.coefficients_dialog, null);
        builder.setView(customLayout);

        this.coefficientsList = (LinearLayout) customLayout.findViewById(R.id.coefficientsList);

        fillDialogWithCoefficients();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog coefficientsDialog = builder.create();
        coefficientsDialog.show();
    }
}
