package com.example.diana.voting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VotingItemActivity extends AppCompatActivity {
    String baseUrl = "http://192.168.37.146:5000/api";
    RequestQueue requestQueue;
    Gson gson = new Gson();

    String votingId = State.getInstance().getCurVotingId();
    Voting votingItem;
    List<Candidate> candidates = new ArrayList<Candidate>();

    LinearLayout candidatesList;
    LinearLayout coefficientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_item);

        requestQueue = Volley.newRequestQueue(this);

        this.getVoting();
        this.getCandidates();
    }

    private String formatDate(Date initDate) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
        return dateFormat.format(initDate);
    }

    private void fillVotingFields(Voting voting){
        TextView topicField = findViewById(R.id.topic);
        topicField.setText(voting.topic);

        TextView dateStartField = findViewById(R.id.dateStart);
        dateStartField.setText(formatDate(voting.dateStart));

        TextView dateEndField = findViewById(R.id.dateEnd);
        dateEndField.setText(formatDate(voting.dateEnd));

        TextView percentField = findViewById(R.id.percent);
        percentField.setText(String.format("%d %%", voting.votersPercent));

        TextView statusField = findViewById(R.id.status);
        statusField.setText(voting.status.toUpperCase());
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
        LinearLayout candidateItem = new LinearLayout(this);
        LinearLayout.LayoutParams candidateItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
        candidateItemParams.setMargins(15,10,0,10);
        candidateItem.setOrientation(LinearLayout.VERTICAL);
        candidateItem.setLayoutParams(candidateItemParams);

        TextView candidateName = new TextView(this);
        LinearLayout.LayoutParams candidateNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60);
        candidateName.setText(candidate.name);
        candidateName.setTypeface(null, Typeface.BOLD);
        candidateName.setTextColor(getResources().getColor(R.color.colorPrimary));
        candidateName.setTextSize(20);
        candidateName.setLayoutParams(candidateNameParams);

        TextView candidateDescription = new TextView(this);
        LinearLayout.LayoutParams candidateDescriptionParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60);
        candidateDescription.setText(candidate.description);
        candidateDescription.setLayoutParams(candidateDescriptionParams);

        candidateItem.addView(candidateName);
        candidateItem.addView(candidateDescription);

        this.candidatesList.addView(candidateItem);
    }

    private void addCoefficientToDialogList(Coefficient coefficient) {
        LinearLayout coefficientItem = new LinearLayout(this);
        LinearLayout.LayoutParams coefficientItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        coefficientItemParams.setMargins(0,20,0,20);
        coefficientItem.setOrientation(LinearLayout.HORIZONTAL);
        coefficientItem.setLayoutParams(coefficientItemParams);

        TextView coeffValue = new TextView(this);
        LinearLayout.LayoutParams coeffValueParams = new LinearLayout.LayoutParams(20, LinearLayout.LayoutParams.MATCH_PARENT, 0.2f);
        coeffValue.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        coeffValue.setText(Integer.toString(coefficient.cost));
        coeffValue.setTextColor(getResources().getColor(R.color.colorPrimary));
        coeffValue.setTextSize(20);
        coeffValue.setBackgroundResource(R.drawable.rounded_textview);
        coeffValue.setTypeface(null, Typeface.BOLD);
        coeffValue.setLayoutParams(coeffValueParams);

        TextView coeffName = new TextView(this);
        LinearLayout.LayoutParams coeffNameParams = new LinearLayout.LayoutParams(242, LinearLayout.LayoutParams.MATCH_PARENT, 0.8f);
        coeffNameParams.setMargins(30,0,0,0);
        coeffName.setGravity(Gravity.CENTER_VERTICAL);
        coeffName.setText(coefficient.name);
        coeffName.setTextSize(18);
        coeffName.setLayoutParams(coeffNameParams);

        coefficientItem.addView(coeffValue);
        coefficientItem.addView(coeffName);

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
