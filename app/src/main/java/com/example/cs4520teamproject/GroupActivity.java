package com.example.cs4520teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cs4520teamproject.Model.Group;

public class GroupActivity extends AppCompatActivity {

    private Group group;
    private TextView textViewDestination, textViewName, textViewContact, textViewDistance, textViewCurrentNumber, textViewTotalNumber, textViewDate, textViewAvgCost, textViewNote;
    private ImageView imageViewHostAvatar;
    private Button buttonJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        setTitle("Current Group");

//        textViewDestination = findViewById(R.id.groupInformationTextViewDestination);
        textViewName = findViewById(R.id.groupInformationTextViewHostName);
        textViewContact = findViewById(R.id.groupInformationTextViewEmail);
        textViewDistance = findViewById(R.id.groupInformationTextViewDistance);
        textViewCurrentNumber = findViewById(R.id.groupInformationTextViewJoinedNumber);
        textViewTotalNumber = findViewById(R.id.groupInformationTextViewJoinedTotalNumber);
        textViewDate = findViewById(R.id.groupInformationTextViewDate);
        textViewAvgCost = findViewById(R.id.groupInformationTextViewAverageCost);
        textViewNote = findViewById(R.id.groupInformationTextViewNote);
//        buttonJoin = findViewById(R.id.groupInformationButtonJoin);

        if (getIntent() != null && getIntent().getExtras() != null)  {
            group = (Group) getIntent().getSerializableExtra("curGroup");

            displayInfo();
        }
    }

    private void displayInfo() {

    }
}