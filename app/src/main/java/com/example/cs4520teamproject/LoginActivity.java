package com.example.cs4520teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "Demo";

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonToSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.loginPageEditTextEmail);
        editTextPassword = findViewById(R.id.loginPageEditTextPassword);
        buttonLogin = findViewById(R.id.loginPageButtonLogin);
        buttonToSignup = findViewById(R.id.loginPageButtonToRegister);

        buttonLogin.setOnClickListener(this);
        buttonToSignup.setOnClickListener(this);

    }

    private void login() {

    }

    private void toRegisterPage() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginPageButtonLogin:
                login();
                break;
            case R.id.loginPageButtonToRegister:
                toRegisterPage();
                break;
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent toChat = new Intent(LoginActivity.this, GroupsActivity.class);
            startActivity(toChat);
        }
    }
}