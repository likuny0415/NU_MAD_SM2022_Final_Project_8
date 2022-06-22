package com.example.cs4520teamproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cs4520teamproject.Model.Group;
import com.example.cs4520teamproject.Model.User;
import com.example.cs4520teamproject.adapter.CreatedGroupListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CreatedGroupListActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private CreatedGroupListAdapter createdGroupListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Group> groups = new ArrayList<>();
    User curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_group_list);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // find current user profile information
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
                                                           .whereEqualTo("createBy", curUser.getId())
                                                           .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                               @Override
                                                               public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                                   if (error != null) {
                                                                       Toast.makeText(CreatedGroupListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                                   } else {
                                                                       for (DocumentSnapshot document : value.getDocuments()) {
                                                                           Group g = document.toObject(Group.class);
                                                                           groups.add(g);
                                                                       }

                                                                       recyclerView = findViewById(R.id.createdGroupListRecyclerView);
                                                                       layoutManager = new LinearLayoutManager(CreatedGroupListActivity.this);
                                                                       recyclerView.setLayoutManager(layoutManager);
                                                                       createdGroupListAdapter = new CreatedGroupListAdapter(groups);
                                                                       recyclerView.setAdapter(createdGroupListAdapter);

                                                                   }
                                                               }
                                                           });

                                               }
                                           }
                                       }
                );


    }
}