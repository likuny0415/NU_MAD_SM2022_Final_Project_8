package com.example.cs4520teamproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs4520teamproject.Model.Group;
import com.example.cs4520teamproject.Model.User;
import com.example.cs4520teamproject.adapter.CreatedGroupListAdapter;
import com.example.cs4520teamproject.adapter.JoinedGroupListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class JoinedGroupListActivity extends AppCompatActivity implements JoinedGroupListAdapter.IQuitButton {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private JoinedGroupListAdapter joinedGroupListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button buttonDetail;
    User curUser;
    private ImageView created, joined, group, account;
    private TextView noFound;
    private Button joinAGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_group_list);
        setTitle("Joined Groups List");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        account = findViewById(R.id.joinedGroupImageViewAccount);
        created = findViewById(R.id.joinedGroupImageViewCreated);
        group = findViewById(R.id.joinedGroupImageViewGroups);
        joinAGroup = findViewById(R.id.joinedGroupListJoinAGroup);
        noFound = findViewById(R.id.joinedGroupListNoFound);

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAccount();
            }
        });

        created.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCreatedList();
            }
        });

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGroups();
            }
        });

        joinAGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toGroups();
            }
        });


        db.collection("user")
                .document(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            curUser = document.toObject(User.class);

                            db.collection("group")
                                    .whereArrayContains("members", curUser.getId())
                                    .orderBy("groupDate")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Toast.makeText(JoinedGroupListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                ArrayList<Group> groups = new ArrayList<>();
                                                for (DocumentSnapshot document : value.getDocuments()) {
                                                    Group g = document.toObject(Group.class);
                                                    if (!Objects.equals(curUser.getId(), g.getCreateBy())) {
                                                        groups.add(g);
                                                    }
                                                }

                                                if (groups == null || groups.isEmpty()) {
                                                    joinAGroup.setVisibility(View.VISIBLE);
                                                    noFound.setVisibility(View.VISIBLE);
                                                } else {
                                                    joinAGroup.setVisibility(View.GONE);
                                                    noFound.setVisibility(View.GONE);

                                                }
                                                recyclerView = findViewById(R.id.recyclerViewJoinedGroupList);
                                                layoutManager = new LinearLayoutManager(JoinedGroupListActivity.this);
                                                recyclerView.setLayoutManager(layoutManager);
                                                joinedGroupListAdapter = new JoinedGroupListAdapter(groups, JoinedGroupListActivity.this, JoinedGroupListActivity.this);
                                                recyclerView.setAdapter(joinedGroupListAdapter);

                                            }
                                        }
                                    });
//                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
//
//                                                Group g = document.toObject(Group.class);
//
//                                                if (!Objects.equals(curUser.getId(), g.getCreateBy())) {
//                                                    joinedGroups.add(g);
//                                                }
//
//
//                                            }
//
//                                            if (joinedGroups == null || joinedGroups.isEmpty()) {
//                                                joinAGroup.setVisibility(View.VISIBLE);
//                                                noFound.setVisibility(View.VISIBLE);
//                                            } else {
//                                                joinAGroup.setVisibility(View.GONE);
//                                                noFound.setVisibility(View.GONE);
//
//                                            }
//                                            recyclerView = findViewById(R.id.recyclerViewJoinedGroupList);
//                                            layoutManager = new LinearLayoutManager(JoinedGroupListActivity.this);
//                                            recyclerView.setLayoutManager(layoutManager);
//                                            joinedGroupListAdapter = new JoinedGroupListAdapter(joinedGroups, JoinedGroupListActivity.this, JoinedGroupListActivity.this);
//                                            recyclerView.setAdapter(joinedGroupListAdapter);
//                                        }
//                                    });

                        }


                    }


                });
    }

    @Override
    public void quitGroup(Group group) {

        // find current user profile information
        db.collection("user")
                .document(mAuth.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        User currentUser = value.toObject(User.class);
                        db.collection("group")
                                .document(group.getId())
                                .update("curNumberOfMembers", group.getCurNumberOfMembers() - 1);
                        db.collection("group")
                                .document(group.getId())
                                .update("members", FieldValue.arrayRemove(currentUser.getId()));
                    }
                });


    }


    private void toCreatedList() {
        Intent toCreatedList = new Intent(JoinedGroupListActivity.this, CreatedGroupListActivity.class);
        startActivity(toCreatedList);
        overridePendingTransition(0, 0);
    }


    private void toAccount() {
        Intent toAccount = new Intent(JoinedGroupListActivity.this, AccountActivity.class);
        startActivity(toAccount);
        overridePendingTransition(0, 0);
    }

    private void toGroups() {
        Intent toGroups = new Intent(JoinedGroupListActivity.this, GroupsActivity.class);
        startActivity(toGroups);
        overridePendingTransition(0, 0);
    }

}