package com.example.diana.voting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class VotingsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votings_list);

        Intent intent = getIntent();
        String groupId = intent.getStringExtra(GroupsListActivity.GROUP_ID);
    }
}
