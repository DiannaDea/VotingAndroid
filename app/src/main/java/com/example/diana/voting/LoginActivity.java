package com.example.diana.voting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_PASSWORD = "USER_PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, GroupsListActivity.class);

        EditText userEmailField = (EditText) findViewById(R.id.textView_email);
        EditText userPasswordField = (EditText) findViewById(R.id.textView_password);

        String userEmail = userEmailField.getText().toString();
        String userPassword = userPasswordField.getText().toString();

        intent.putExtra(USER_EMAIL, userEmail);
        intent.putExtra(USER_PASSWORD, userPassword);
        startActivity(intent);
    }
}
