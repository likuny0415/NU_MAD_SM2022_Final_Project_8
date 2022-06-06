package com.example.cs4520teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class Account extends AppCompatActivity {
    private final String TAG = "Demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        android.util.Log.d("TAG", "onCreate: " + 123);

        Log.d("TAG", "onCreate: " + "test steven merge");

        Log.d(TAG, "onCreate: " + " my test");


    }
}