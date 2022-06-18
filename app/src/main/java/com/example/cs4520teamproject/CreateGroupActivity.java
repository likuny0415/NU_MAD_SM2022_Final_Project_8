package com.example.cs4520teamproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView imageViewRequestLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setTitle("Create your group!");

        imageViewRequestLocation = findViewById(R.id.imageViewRequestDestination);
        imageViewRequestLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewRequestDestination:
                toMaps();
                break;
        }
    }

    private void toMaps() {

        Intent toCreatePage = new Intent(CreateGroupActivity.this, MapsActivity.class);
        startActivity(toCreatePage);
    }



}