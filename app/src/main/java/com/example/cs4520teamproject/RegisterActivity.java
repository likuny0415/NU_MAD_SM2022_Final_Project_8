package com.example.cs4520teamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextName, editTextMobile, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonSignup, buttonToLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.registerPageEditTextName);
        editTextMobile = findViewById(R.id.registerPageEditTextMobile);
        editTextEmail = findViewById(R.id.registerPageEditTextEmail);
        editTextPassword = findViewById(R.id.registerPageEditTextPassword);
        editTextConfirmPassword = findViewById(R.id.registerPageEditTextConfirmPassword);
        buttonSignup = findViewById(R.id.registerPageButtonSignup);
        buttonToLogin = findViewById(R.id.registerPageButtonToLogin);

        buttonSignup.setOnClickListener(this);
        buttonToLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerPageButtonSignup:
                signUp();
                break;
            case R.id.registerPageButtonToLogin:
                toLoginPage();
                break;
        }
    }

    private void signUp() {
        checkInfo();
    }

    private void checkInfo() {
        String name = editTextName.getText().toString();
        String mobile = editTextMobile.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        if (name.isEmpty() || mobile.isEmpty() || email.isEmpty() || password.isEmpty() ) {
            Toast.makeText(RegisterActivity.this, "Must fulfill all fields!",
                    Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegisterActivity.this, "Enter valid email!", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password length must larger than 6!", Toast.LENGTH_SHORT).show();
        }else if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Password is different!", Toast.LENGTH_SHORT).show();
        } else {
            registerUser(email, password, name, mobile);
        }
    }

    private void registerUser(String email, String password, String name, String mobile) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            registerUserInDB(user.getUid(), email, name, email);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void registerUserInDB(String uid, String email, String name, String mobile) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", uid);
        user.put("email", email);
        user.put("name", name);
        user.put("mobile", mobile);
        user.put("bio", "I am so happy to be here! ^_^");
        user.put("groups", new ArrayList<String>());
        user.put("profile_url", "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg");

        db.collection("user")
                .document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent toChat = new Intent(RegisterActivity.this, GroupsActivity.class);
                        startActivity(toChat);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void toLoginPage() {
        Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(toLogin);
    }


}