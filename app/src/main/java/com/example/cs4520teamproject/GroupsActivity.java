package com.example.cs4520teamproject;

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
        buttonToAccount = findViewById(R.id.accountPageButtonToAccount);
        buttonAdd.setOnClickListener(this);
        buttonToAccount.setOnClickListener(this);

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

                            recyclerView = findViewById(R.id.groupsRecyclerView);
                            recyclerViewLayoutManager = new LinearLayoutManager(GroupsActivity.this);
                            recyclerView.setLayoutManager(recyclerViewLayoutManager);
                            groupsListAdapter = new GroupsListAdapter(groups, GroupsActivity.this);
                            recyclerView.setAdapter(groupsListAdapter);
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
            case R.id.accountPageButtonToAccount:
                toAccount();
                break;
        }
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