package com.example.diana.voting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VotingsListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String VOTING_ID = "VOTING_ID";

    RequestQueue requestQueue;

    String baseUrl = State.getInstance().getBaseUrl();
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

    private void addVotingToList(final Voting voting){
        LinearLayout votingItem = new LinearLayout(this);
        LinearLayout.LayoutParams votingItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 90);
        votingItemParams.setMargins(0,20,0,0);
        votingItem.setOrientation(LinearLayout.HORIZONTAL);
        votingItem.setBackgroundColor(Color.parseColor("#eaeaea"));
        votingItem.setLayoutParams(votingItemParams);

        votingItem.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(VotingsListActivity.this, VotingItemActivity.class);

                State.getInstance().setCurVotingId(voting._id);
                startActivity(intent);
            }
        });

        TextView groupName = new TextView(this);
        LinearLayout.LayoutParams groupNameParams = new LinearLayout.LayoutParams(242, LinearLayout.LayoutParams.MATCH_PARENT, 0.6f);
        groupNameParams.setMargins(30,0,0,0);
        groupName.setGravity(Gravity.CENTER_VERTICAL);
        groupNameParams.setMargins(20, 20, 0, 20);
        groupName.setText("# ".concat(voting.topic));
        groupName.setTextSize(getResources().getDimension(R.dimen.h3_font_size));
        groupName.setTypeface(null, Typeface.BOLD);
        groupName.setTextColor(getResources().getColor(R.color.colorPrimary));
        groupName.setLayoutParams(groupNameParams);

        Button btnTag = new Button(this);
        LinearLayout.LayoutParams resultButtonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f);
        resultButtonParams.setMargins(20, 20, 20, 20);
        btnTag.setText("Results");
        btnTag.setLayoutParams(resultButtonParams);
        btnTag.setBackgroundColor(Color.parseColor("#ffffff"));
        btnTag.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                viewResults(v, voting._id);
            }
        });

        votingItem.addView(groupName);

        if (voting.status.equals("finished")) {
            votingItem.addView(btnTag);
        }

        this.votingsList.addView(votingItem);
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

    private LinearLayout addResultDescription(ResultsItem result) {
        LinearLayout candidateItem = new LinearLayout(this);
        LinearLayout.LayoutParams candidateItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.2f);
        candidateItemParams.setMargins(50,0,0,20);
        candidateItem.setOrientation(LinearLayout.VERTICAL);
        candidateItem.setLayoutParams(candidateItemParams);


        TextView resultName = new TextView(this);
        LinearLayout.LayoutParams resultNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f);
        resultName.setGravity(Gravity.FILL_VERTICAL);
        resultName.setText(result.candidate.name);
        resultName.setTypeface(null, Typeface.BOLD);
        resultName.setTextSize(getResources().getDimension(R.dimen.h2_font_size));
        resultName.setLayoutParams(resultNameParams);

        TextView votesValue = new TextView(this);
        LinearLayout.LayoutParams votesValueParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f);
        votesValue.setGravity(Gravity.FILL_VERTICAL);
        votesValue.setText("result: ".concat(Double.toString(result.votesValue)));
        votesValue.setTextSize(getResources().getDimension(R.dimen.h3_font_size));
        votesValue.setLayoutParams(votesValueParams);

        candidateItem.addView(resultName);
        candidateItem.addView(votesValue);

        return candidateItem;
    }

    private void addResultToDialogList(ResultsItem result, LinearLayout resultsListContainer, int count) {
        LinearLayout resultItem = new LinearLayout(this);
        LinearLayout.LayoutParams resultItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        resultItemParams.setMargins(0,20,0,0);
        resultItem.setOrientation(LinearLayout.HORIZONTAL);
        resultItem.setLayoutParams(resultItemParams);

        TextView positionValue = new TextView(this);
        LinearLayout.LayoutParams positionValueParams = new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.MATCH_PARENT, 0.3f);
        positionValue.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        positionValue.setText("#".concat(Integer.toString(count)));
        positionValue.setTextColor(getResources().getColor(R.color.colorPrimary));
        positionValue.setTextSize(20);
        positionValueParams.setMargins(20, 20, 0, 20);
        positionValue.setBackgroundResource(R.drawable.rounded_textview);
        positionValue.setTypeface(null, Typeface.BOLD);
        positionValue.setLayoutParams(positionValueParams);


        LinearLayout candidateItem = addResultDescription(result);
        
        resultItem.addView(positionValue);
        resultItem.addView(candidateItem);

        resultsListContainer.addView(resultItem);
    }

    public class ResultsComparator implements Comparator<ResultsItem> {
        @Override
        public int compare(ResultsItem o1, ResultsItem o2) {
            return Double.compare(o2.votesValue, o1.votesValue);
        }
    }

    public void fillDialogsWithResults(VotingResults votingResults, View customLayout) {
        LinearLayout resultsListContainer = (LinearLayout) customLayout.findViewById(R.id.votingResultsDialog);

        Collections.sort(votingResults.results, new ResultsComparator());
        int count = 1;

        for(ResultsItem result: votingResults.results) {
            addResultToDialogList(result, resultsListContainer, count);
            count++;
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
