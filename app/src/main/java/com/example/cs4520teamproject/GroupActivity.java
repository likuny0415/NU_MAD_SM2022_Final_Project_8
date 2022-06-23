package com.example.cs4520teamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cs4520teamproject.Model.Group;
import com.example.cs4520teamproject.Model.User;
import com.example.cs4520teamproject.adapter.MembersAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity implements View.OnClickListener {

    private Group group;
    private TextView textViewDestination, textViewName, textViewContact, textViewDistance, textViewCurrentNumber, textViewTotalNumber, textViewDate, textViewAvgCost, textViewNote;
    private ImageView imageViewHostAvatar, imageViewLocation;
    private Button buttonJoin;
    private RecyclerView recyclerViewMembers;
    private RecyclerView.LayoutManager layoutManager;
    private MembersAdapter membersAdapter;
    private int type;


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
        textViewContact = findViewById(R.id.groupInformationTextViewContact);
        textViewCurrentNumber = findViewById(R.id.groupInformationTextViewJoinedNumber);
        textViewTotalNumber = findViewById(R.id.groupInformationTextViewJoinedTotalNumber);
        textViewDate = findViewById(R.id.groupInformationTextViewDate);
        textViewAvgCost = findViewById(R.id.groupInformationTextViewAverageCost);
        textViewNote = findViewById(R.id.groupInformationTextViewNote);
        buttonJoin = findViewById(R.id.groupInformationButtonJoin);
        imageViewHostAvatar = findViewById(R.id.groupInformationImageViewHostAvatar);
        imageViewLocation = findViewById(R.id.groupInformationImageViewLocation);
        recyclerViewMembers = findViewById(R.id.groupInformationRecyclerViewMembers);

        if (getIntent() != null && getIntent().getExtras() != null)  {
            group = (Group) getIntent().getSerializableExtra("curGroup");
            type = getIntent().getIntExtra("type", 0);
            displayInfo();
            imageViewLocation.setOnClickListener(this);
            buttonJoin.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.groupInformationImageViewLocation:
                toMap();
                break;
            case R.id.groupInformationButtonJoin:
                joinGroup();
                break;
        }
    }

    private void joinGroup() {
        db.collection("group")
                .document(group.getId())
                .update("members", FieldValue.arrayUnion(mAuth.getUid()));
        db.collection("user")
                .document(mAuth.getUid())
                .update("groups", FieldValue.arrayUnion(group.getId()));

        DocumentReference sfDocRef = db.collection("group").document(group.getId());

        db.runTransaction(new Transaction.Function<Double>() {
                    @Override
                    public Double apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(sfDocRef);
                        double currentNumOfGroupMembers = snapshot.getDouble("curNumberOfMembers") + 1;
                        transaction.update(sfDocRef, "curNumberOfMembers", currentNumOfGroupMembers);
                        if (currentNumOfGroupMembers == group.getTotalNumberOfMembers()) {
                            transaction.update(sfDocRef, "isFull", true);
                        }
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Double>() {
                    @Override
                    public void onSuccess(Double result) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });



        finish();
    }


    private void displayInfo() {
        textViewDestination.setText(group.getDestination());
        textViewNote.setText(group.getNote());
        textViewDate.setText(group.getDate());
        textViewCurrentNumber.setText("" + group.getCurNumberOfMembers());
        textViewTotalNumber.setText("" + group.getTotalNumberOfMembers());
        textViewAvgCost.setText("" + group.getAverageCost());

        if (type == 2) {
            buttonJoin.setVisibility(View.GONE);
        }

        if (group.getMembers().contains(mAuth.getUid())) {
            buttonJoin.setEnabled(false);
        }
        if (group.getCurNumberOfMembers() >= group.getTotalNumberOfMembers()) {
            buttonJoin.setEnabled(false);
        }

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
                            textViewContact.setText(user.getMobile()  + " & " + user.getEmail());
                            Glide.with(GroupActivity.this)
                                    .load(user.getProfile_url())
                                    .centerCrop()
                                    .into(imageViewHostAvatar);

                        }
                    }
                });

        ArrayList<User> members = new ArrayList<>();
        for (String user_id : group.getMembers()) {
            db.collection("user")
                    .document(user_id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                User user = document.toObject(User.class);
                                members.add(user);
                            }
                            layoutManager = new LinearLayoutManager(GroupActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            recyclerViewMembers.setLayoutManager(layoutManager);
                            membersAdapter = new MembersAdapter(members, GroupActivity.this);
                            recyclerViewMembers.setAdapter(membersAdapter);
                        }
                    });
        }


    }




    private void toMap() {
        Intent toMap = new Intent(GroupActivity.this, MapsActivity.class);
        toMap.putExtra("type", 2);
        toMap.putExtra("latitude", group.getLatitude());
        toMap.putExtra("longitude", group.getLongitude());
        startActivity(toMap);
    }
}