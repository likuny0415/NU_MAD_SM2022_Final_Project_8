package com.example.cs4520teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        android.util.Log.d(TAG, "onCreate: " + 123);
    }
}