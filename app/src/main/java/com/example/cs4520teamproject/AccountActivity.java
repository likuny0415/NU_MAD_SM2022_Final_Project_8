package com.example.cs4520teamproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cs4520teamproject.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {
    private final String TAG = "Demo";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    User currentUser;
    private TextView userName, emailAddress, phoneNumber, bioText;
    private Button joinedList, createdList, editProfile, logOut, groups, account;
    private ImageView userAvatar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle("My Account");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.accountPageTextViewName);
        emailAddress = findViewById(R.id.accountPageTextViewEmailDetail);
        phoneNumber = findViewById(R.id.accountPageTextViewPhoneDetail);
        bioText = findViewById(R.id.accountPageTextViewBioDetail);
        joinedList = findViewById(R.id.accountPageButtonJoinedList);
        createdList = findViewById(R.id.accountPageButtonCreatedList);
        editProfile = findViewById(R.id.accountPageButtonEdit);
        logOut = findViewById(R.id.accountPageButtonLogOut);
//        groups = findViewById(R.id.accountPageButtonGroups);
        account = findViewById(R.id.accountPageButtonAccount);
        userAvatar = findViewById(R.id.accountPageImageViewAvatar);



        // find current user profile information
        db.collection("user")
                .document(mAuth.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        currentUser = value.toObject(User.class);
                        updateUI();
                    }
                });



        // logout
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent finish = new Intent(AccountActivity.this, LoginActivity.class);
                startActivity(finish);
            }
        });

        // got to groups
//        groups.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent goToGroups = new Intent(AccountActivity.this, GroupsActivity.class);
//                startActivity(goToGroups);
//                overridePendingTransition(0, 0);
//            }
//        });


        createdList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCreatedGroups = new Intent(AccountActivity.this, CreatedGroupListActivity.class);
                startActivity(goToCreatedGroups);
            }
        });

        joinedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToJoinedGroups = new Intent(AccountActivity.this, JoinedGroupListActivity.class);
                startActivity(goToJoinedGroups);
            }
        });


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEditProfile = new Intent(AccountActivity.this, EditProfileActivity.class);
                startActivity(toEditProfile);
                overridePendingTransition(0, 0);
            }
        });


    }


    // update UI according to user information
    private void updateUI() {
        emailAddress.setText(currentUser.getEmail());
        userName.setText(currentUser.getName());
        phoneNumber.setText(currentUser.getMobile());
        bioText.setText(currentUser.getBio());
        Picasso.get().load(currentUser.getProfile_url()).fit().into(userAvatar);


    }
}