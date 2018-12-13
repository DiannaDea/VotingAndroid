package com.example.diana.voting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class VotingsListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String VOTING_ID = "VOTING_ID";

    RequestQueue requestQueue;

    String baseUrl = "http://192.168.37.146:5000/api";
    Gson gson = new Gson();

    LinearLayout votingsList;

    private String groupId = State.getInstance().getCurGroupId();
    private String userId = State.getInstance().getUserId();

    private List<Voting> newVotings = new ArrayList<Voting>();
    private List<Voting> recentVotings = new ArrayList<Voting>();
    private List<Voting> allVotings = new ArrayList<Voting>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votings_list);

        this.setVotingFilter();

        this.votingsList = (LinearLayout) findViewById(R.id.votingsList);

        requestQueue = Volley.newRequestQueue(this);

        this.performInitialRequests();
    }

    private void performInitialRequests(){
        getVotingListByState("new");
        getVotingListByState("recent");
    }

    private void setVotingFilter(){
        Spinner votingFilter = findViewById(R.id.selectType);

        String[] items = new String[]{"View all", "New votings", "Already voted"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        votingFilter.setAdapter(adapter);
        votingFilter.setOnItemSelectedListener(this);
    }

    private void renderVotingsList(List<Voting> votings){
        for(Voting voting : votings) {
            addVotingToList(voting);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        votingsList.removeAllViews();
        switch (position){
            case 0:
                renderVotingsList(allVotings);
                break;
            case 1:
                renderVotingsList(newVotings);
                break;
            case 2:
                renderVotingsList(recentVotings);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void addButtonToContainer(final Voting voting, LinearLayout votingContainer) {
        Button btnTag = new Button(this);
        btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnTag.setText("Results");

        if (voting.status.equals("finished")) {
            votingContainer.addView(btnTag);
        }

        btnTag.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                viewResults(v, voting._id);
            }
        });
    }

    private void addTextToContainer(final Voting voting, LinearLayout votingContainer) {
        TextView votingItem = new TextView(this);

        votingItem.setText(voting.topic);
        votingItem.setBackgroundColor(0xffffdbdb);
        votingItem.setPadding(20, 20, 20, 20);

        votingItem.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(VotingsListActivity.this, VotingItemActivity.class);

                State.getInstance().setCurVotingId(voting._id);
                startActivity(intent);
            }
        });

        votingContainer.addView(votingItem);
    }

    private void addVotingToList(final Voting voting){
        LinearLayout votingItemContainer = new LinearLayout(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(16,0,16,20);

        votingItemContainer.setLayoutParams(params);

        this.addTextToContainer(voting, votingItemContainer);
        this.addButtonToContainer(voting, votingItemContainer);

        this.votingsList.addView(votingItemContainer);
    }

    private void getVotingListByState(final String state) {
        String url = String.format("%s/groups/%s/users/%s/votings?state=%s", this.baseUrl, this.groupId, this.userId, state);

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    Voting voting = gson.fromJson(jsonObj.toString(), Voting.class);

                                    if ((state.equals("new"))) {
                                        newVotings.add(voting);
                                    } else {
                                        recentVotings.add(voting);
                                    }

                                    allVotings.add(voting);
                                    addVotingToList(voting);
                                } catch (JSONException e) {
                                    Log.e("Volley", "Invalid JSON Object.");
                                }
                            }
                        } else {
                            //setRepoListText("No repos found.");
                        }
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

    public void viewResults(View view, String votingId) {
        String url = String.format("%s/votings/%s/results", this.baseUrl, votingId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                (String)null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObj) {
                        VotingResults votingResults = gson.fromJson(jsonObj.toString(), VotingResults.class);
                        showResultsDialog(votingResults);
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

    private void addResultToDialogList(ResultsItem result, LinearLayout resultsListContainer) {
        TextView resultItemText = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,20);

        resultItemText.setLayoutParams(params);
        resultItemText.setText(String.format("%s - %.1f", result.candidate.name, result.votesValue));

        resultItemText.setPadding(20, 20, 20, 20);

        resultsListContainer.addView(resultItemText);
    }

    public void fillDialogsWithResults(VotingResults votingResults, View customLayout) {
        LinearLayout resultsListContainer = (LinearLayout) customLayout.findViewById(R.id.votingResultsDialog);

        for(ResultsItem result: votingResults.results) {
            addResultToDialogList(result, resultsListContainer);
        }
    }

    public void showResultsDialog(VotingResults votingResults) {
        AlertDialog.Builder builder = new AlertDialog.Builder(VotingsListActivity.this);
        builder.setTitle("Results");

        final View customLayout = getLayoutInflater().inflate(R.layout.voting_results_dialog, null);
        builder.setView(customLayout);

        fillDialogsWithResults(votingResults, customLayout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog resultsDialog = builder.create();
        resultsDialog.show();
    }
}
