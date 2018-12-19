package com.example.diana.voting;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class GroupsListActivity extends AppCompatActivity {
    public static final String GROUP_ID = "GROUP_ID";
    public static final String USER_ID = "USER_ID";

    private String userId = "5bf2918d77167f22f6f3471e";

    LinearLayout groupsList;
    RequestQueue requestQueue;

    String baseUrl = "http://192.168.37.146:5000/api";
    String url;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);

        this.groupsList = (LinearLayout) findViewById(R.id.groupsList);
        //this.groupsList.setMovementMethod(new ScrollingMovementMethod());

        requestQueue = Volley.newRequestQueue(this);
        getGroupsList();
    }

    private void addGroupToList(final Group group){
        LinearLayout groupItem = new LinearLayout(this);
        LinearLayout.LayoutParams groupItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        groupItemParams.setMargins(0,20,0,20);
        groupItem.setOrientation(LinearLayout.HORIZONTAL);
        groupItem.setBackgroundColor(Color.parseColor("#eaeaea"));
        groupItem.setLayoutParams(groupItemParams);

        TextView abbrValue = new TextView(this);
        LinearLayout.LayoutParams coeffValueParams = new LinearLayout.LayoutParams(20, LinearLayout.LayoutParams.MATCH_PARENT, 0.2f);
        abbrValue.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        abbrValue.setText(group.name.substring(0, 2).toUpperCase());
        abbrValue.setTextColor(getResources().getColor(R.color.colorPrimary));
        abbrValue.setTextSize(20);
        coeffValueParams.setMargins(20, 20, 0, 20);
        abbrValue.setBackgroundResource(R.drawable.rounded_abbr_group);
        abbrValue.setTypeface(null, Typeface.BOLD);
        abbrValue.setLayoutParams(coeffValueParams);

        TextView groupName = new TextView(this);
        LinearLayout.LayoutParams groupNameParams = new LinearLayout.LayoutParams(242, LinearLayout.LayoutParams.MATCH_PARENT, 0.8f);
        groupNameParams.setMargins(30,0,0,0);
        groupName.setGravity(Gravity.CENTER_VERTICAL);
        groupNameParams.setMargins(20, 20, 0, 20);
        groupName.setText(group.name);
        groupName.setTextSize(18);
        groupName.setLayoutParams(groupNameParams);

        groupItem.addView(abbrValue);
        groupItem.addView(groupName);

        groupItem.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                State.getInstance().setCurGroupId(group._id);

                Intent intent = new Intent(GroupsListActivity.this, VotingsListActivity.class);
                startActivity(intent);
            }
        });

        this.groupsList.addView(groupItem);
    }

    private void getGroupsList() {
        this.url = this.baseUrl + "/users/5bf2918d77167f22f6f3471e/groups";

        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    Group group = gson.fromJson(jsonObj.toString(), Group.class);

                                    addGroupToList(group);
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
}
