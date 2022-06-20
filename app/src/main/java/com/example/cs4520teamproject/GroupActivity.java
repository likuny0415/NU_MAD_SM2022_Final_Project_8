package com.example.cs4520teamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cs4520teamproject.Model.Group;
import com.example.cs4520teamproject.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GroupActivity extends AppCompatActivity {

    private Group group;
    private TextView textViewDestination, textViewName, textViewContact, textViewDistance, textViewCurrentNumber, textViewTotalNumber, textViewDate, textViewAvgCost, textViewNote;
    private ImageView imageViewHostAvatar;
    private Button buttonJoin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        setTitle("Current Group");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewDestination = findViewById(R.id.groupInformationTextViewDestination);
        textViewName = findViewById(R.id.groupInformationTextViewHostName);
        textViewContact = findViewById(R.id.groupInformationTextViewEmail);
        textViewDistance = findViewById(R.id.groupInformationTextViewDistance);
        textViewCurrentNumber = findViewById(R.id.groupInformationTextViewJoinedNumber);
        textViewTotalNumber = findViewById(R.id.groupInformationTextViewJoinedTotalNumber);
        textViewDate = findViewById(R.id.groupInformationTextViewDate);
        textViewAvgCost = findViewById(R.id.groupInformationTextViewAverageCost);
        textViewNote = findViewById(R.id.groupInformationTextViewNote);
        buttonJoin = findViewById(R.id.groupInformationButtonJoin);
        imageViewHostAvatar = findViewById(R.id.groupInformationImageViewHostAvatar);

        if (getIntent() != null && getIntent().getExtras() != null)  {
            group = (Group) getIntent().getSerializableExtra("curGroup");

            displayInfo();
        }
    }

    private void displayInfo() {
        textViewDestination.setText(group.getDestination());
        textViewNote.setText(group.getNote());
        textViewDate.setText(group.getDate());
        textViewCurrentNumber.setText("" + group.getCurNumberOfMembers());
        textViewTotalNumber.setText("" + group.getTotalNumberOfMembers());
        textViewAvgCost.setText("" + group.getAverageCost());


        db.collection("user")
                .document(group.getCreateBy())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            User user = document.toObject(User.class);
                            textViewName.setText(user.getName());
                            textViewContact.setText(user.getEmail());
                            Glide.with(GroupActivity.this)
                                    .load(user.getProfile_url())
                                    .centerCrop()
                                    .into(imageViewHostAvatar);

                        }
                    }
                });

    }
}