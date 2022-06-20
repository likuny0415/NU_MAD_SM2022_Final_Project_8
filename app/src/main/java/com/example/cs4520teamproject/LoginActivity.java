package com.example.cs4520teamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
        isValidEmailAndPassword();
    }

    private void isValidEmailAndPassword() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email or Password can't be empty", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(LoginActivity.this, "Enter valid email!", Toast.LENGTH_SHORT).show();
        } else {
            loginUser(email, password);
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent toChat = new Intent(LoginActivity.this, GroupsActivity.class);
                            startActivity(toChat);
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void toRegisterPage() {
        Intent toRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(toRegister);
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

    @Override
    public void onBackPressed()
    {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}