package com.example.cs4520teamproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cs4520teamproject.Model.Group;
import com.example.cs4520teamproject.Model.User;
import com.example.cs4520teamproject.adapter.CreatedGroupListAdapter;
import com.example.cs4520teamproject.adapter.GroupsListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CreatedGroupListActivity extends AppCompatActivity implements CreatedGroupListAdapter.IRemoveButton{


    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private CreatedGroupListAdapter createdGroupListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView created, joined, group, account;
    private Button createMyGroup;
    private TextView noFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_group_list);
        setTitle("Created Groups List");


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        account = findViewById(R.id.createdGroupImageViewAccount);
        joined = findViewById(R.id.createdGroupImageViewJoined);
        group = findViewById(R.id.createdGroupImageViewGroups);
        createMyGroup = findViewById(R.id.createdGroupListCreateMyGroup);
        noFound = findViewById(R.id.createdGroupListNotFound);

        createMyGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCreateMyOwn();
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAccount();
            }
        });

        joined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toJoinedList();
            }
        });

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGroups();
            }
        });

        display();

    }


    private void display() {
        db.collection("group")
                .whereEqualTo("createBy", mAuth.getUid())
                .orderBy("groupDate")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(CreatedGroupListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayList<Group> groups = new ArrayList<>();
                            for (DocumentSnapshot document: value.getDocuments()) {
                                Group g = document.toObject(Group.class);
                                groups.add(g);
                            }

                            if (groups == null || groups.isEmpty()) {
                                createMyGroup.setVisibility(View.VISIBLE);
                                noFound.setVisibility(View.VISIBLE);
                            } else {
                                createMyGroup.setVisibility(View.GONE);
                                noFound.setVisibility(View.GONE);
                            }

                            recyclerView = findViewById(R.id.createdGroupListRecyclerView);
                            layoutManager = new LinearLayoutManager(CreatedGroupListActivity.this);
                            recyclerView.setLayoutManager(layoutManager);
                            createdGroupListAdapter = new CreatedGroupListAdapter(groups, CreatedGroupListActivity.this, CreatedGroupListActivity.this);
                            recyclerView.setAdapter(createdGroupListAdapter);

                            updateUI();
                        }
                    }
                });

    }

    private void updateUI() {
        db.collection("group")
                .whereEqualTo("createBy", mAuth.getUid())
                .orderBy("groupDate")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(CreatedGroupListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayList<Group> groups= new ArrayList<>();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                Group g = document.toObject(Group.class);
                                groups.add(g);
                            }
                            createdGroupListAdapter.setCreatedGroups(groups);
                            createdGroupListAdapter.notifyDataSetChanged();

                        }
                    }
                });

    }


    @Override
    public void removeGroup(Group group) {

        db.collection("group")
                .document(group.getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        db.collection("user")
                                .whereArrayContains("groups", group.getId())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                            User u = document.toObject(User.class);
                                            db.collection("user")
                                                    .document(u.getId())
                                                    .update("groups", FieldValue.arrayRemove(group.getId()));

                                        }
                                    }
                                });
                    }
                });
    }


    private void toJoinedList() {
        Intent toCreatedList = new Intent(CreatedGroupListActivity.this, JoinedGroupListActivity.class);
        startActivity(toCreatedList);
        overridePendingTransition(0, 0);
    }


    private void toAccount() {
        Intent toAccount = new Intent(CreatedGroupListActivity.this, AccountActivity.class);
        startActivity(toAccount);
        overridePendingTransition(0, 0);
    }

    private void toGroups() {
        Intent toGroups = new Intent(CreatedGroupListActivity.this, GroupsActivity.class);
        startActivity(toGroups);
        overridePendingTransition(0, 0);
    }

    private void toCreateMyOwn() {
        Intent toGroups = new Intent(CreatedGroupListActivity.this, CreateGroupActivity.class);
        startActivity(toGroups);
        overridePendingTransition(0, 0);
    }

}