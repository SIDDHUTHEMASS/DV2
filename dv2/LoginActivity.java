package com.runtimetitans.dv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout username;
    private TextInputLayout password;
    private MaterialButton login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getEditText().getText().toString().contentEquals("admin") & password.getEditText().getText().toString().contentEquals("administrator")){
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}