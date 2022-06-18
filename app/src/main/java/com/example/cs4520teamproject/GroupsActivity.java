package com.example.cs4520teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class GroupsActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton buttonAdd;
    private ImageView imageViewRequestLocation;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        setTitle("WeSki Groups");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonAdd = findViewById(R.id.buttonGroupsPageAdd);

        buttonAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonGroupsPageAdd:
                toCreatePage();
                break;

        }
    }

    private void toCreatePage() {
        Intent toCreatePage = new Intent(GroupsActivity.this, CreateGroupActivity.class);
        startActivity(toCreatePage);
    }
}