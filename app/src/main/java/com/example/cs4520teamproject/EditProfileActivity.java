package com.example.cs4520teamproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cs4520teamproject.Model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewEmail, textViewPhone;
    private EditText editTextBio, editTextName;
    private ImageView imageViewAvatar;
    private Button buttonSave;
    private FloatingActionButton buttonEdit;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setTitle("Edit Profile");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewEmail = findViewById(R.id.editProfileTextViewEmail);
        textViewPhone = findViewById(R.id.editProfileTextViewPhone);
        editTextBio = findViewById(R.id.editProfileEditTextBio);
        editTextName = findViewById(R.id.editProfileEditTextName);
        buttonSave = findViewById(R.id.editProfileButtonSave);
        buttonEdit = findViewById(R.id.editProfileFloatingButtonEdit);
        imageViewAvatar = findViewById(R.id.editProfileImageViewAvatar);

        updateUI();

        buttonSave.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);

    }

    private void updateUI() {
        db.collection("user")
                .document(mAuth.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        currentUser = value.toObject(User.class);

                        textViewEmail.setText(currentUser.getEmail());
                        textViewPhone.setText(currentUser.getMobile());
                        editTextBio.setText(currentUser.getBio());
                        editTextName.setText(currentUser.getName());
                        Glide.with(EditProfileActivity.this)
                                .load(currentUser.getProfile_url())
                                .centerCrop()
                                .into(imageViewAvatar);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editProfileButtonSave:
                saveProfile();
                break;
            case R.id.editProfileFloatingButtonEdit:
                chooseAvatar();
                break;
        }
    }


    private void chooseAvatar() {
        Intent toSelectAvatar = new Intent(EditProfileActivity.this, SelectAvatarActivity.class);
        startActivity(toSelectAvatar);
    }

    private void saveProfile() {
        String name = editTextName.getText().toString();
        String bio = editTextBio.getEditableText().toString();
        if (name.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "Name or Bio can't be empty!", Toast.LENGTH_SHORT).show();
        } else {

        }
    }
}