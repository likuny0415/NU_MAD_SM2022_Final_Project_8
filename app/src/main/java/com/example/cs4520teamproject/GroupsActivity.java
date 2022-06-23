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
import android.widget.Toast;

import com.example.cs4520teamproject.Model.Group;
import com.example.cs4520teamproject.adapter.GroupsListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroupsActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton buttonAdd;
    private ImageView imageViewRequestLocation;
    private Button buttonToGroups, buttonToAccount;

    private ImageView imageViewGroups, imageViewCreated, imageViewJoined, imageViewAccount;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private GroupsListAdapter groupsListAdapter;
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
        imageViewAccount = findViewById(R.id.groupsImageViewAccount);
        imageViewGroups = findViewById(R.id.groupsImageViewGroups);
        imageViewJoined = findViewById(R.id.groupsImageViewJoined);
        imageViewCreated = findViewById(R.id.groupsImageViewCreated);

        buttonAdd.setOnClickListener(this);
        imageViewAccount.setOnClickListener(this);
        imageViewCreated.setOnClickListener(this);
        imageViewJoined.setOnClickListener(this);


        display();
    }

    private void display() {
        Date time = Calendar.getInstance().getTime();
        db.collection("group")
                .whereEqualTo("isFull", false)
                .whereGreaterThan("groupDate", time)
                .orderBy("groupDate")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Group> curGroups= new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Group g = document.toObject(Group.class);
                                curGroups.add(g);
                            }
                            recyclerView = findViewById(R.id.groupsRecyclerView);
                            recyclerViewLayoutManager = new LinearLayoutManager(GroupsActivity.this);
                            recyclerView.setLayoutManager(recyclerViewLayoutManager);
                            groupsListAdapter = new GroupsListAdapter(curGroups, GroupsActivity.this);
                            recyclerView.setAdapter(groupsListAdapter);

                            updateUI();

                        }
                    }
                });


    }

    private void updateUI() {
        Date time = Calendar.getInstance().getTime();
        db.collection("group")
                .whereEqualTo("isFull", false)
                .whereGreaterThan("groupDate", time)
                .orderBy("groupDate")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(GroupsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayList<Group> groups = new ArrayList<>();
                            for (DocumentSnapshot document: value.getDocuments()) {
                                Group g = document.toObject(Group.class);
                                groups.add(g);
                            }

                            groupsListAdapter.setGroups(groups);
                            groupsListAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonGroupsPageAdd:
                toCreatePage();
                break;
            case R.id.groupsImageViewCreated:
                toCreatedList();
                break;
            case R.id.groupsImageViewJoined:
                toJoinedList();
                break;
            case R.id.groupsImageViewAccount:
                toAccount();
                break;
        }
    }

    private void toJoinedList() {
        Intent toCreatedList = new Intent(GroupsActivity.this, JoinedGroupListActivity.class);
        startActivity(toCreatedList);
        overridePendingTransition(0, 0);
    }

    private void toCreatedList() {
        Intent toJoinedList = new Intent(GroupsActivity.this, CreatedGroupListActivity.class);
        startActivity(toJoinedList);
        overridePendingTransition(0, 0);
    }


    private void toAccount() {
        Intent toAccount = new Intent(GroupsActivity.this, AccountActivity.class);
        startActivity(toAccount);
        overridePendingTransition(0, 0);
    }

    private void toCreatePage() {
        Intent toCreatePage = new Intent(GroupsActivity.this, CreateGroupActivity.class);
        startActivity(toCreatePage);
    }
}