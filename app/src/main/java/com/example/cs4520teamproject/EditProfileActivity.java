package com.example.cs4520teamproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_GET_Avatar_URI = 0x001;

    private TextView textViewEmail, textViewPhone;
    private EditText editTextBio, editTextName;
    private ImageView imageViewAvatar;
    private Button buttonSave;
    private FloatingActionButton buttonEdit;


    private Uri avatarUri;

    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setTitle("Edit Profile");

        storage = FirebaseStorage.getInstance("gs://cs4520teamproject.appspot.com");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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
                        Glide.with(getApplicationContext())
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
        startActivityForResult(toSelectAvatar, REQUEST_GET_Avatar_URI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_Avatar_URI && resultCode == RESULT_OK) {
            avatarUri = data.getParcelableExtra("avatarUri");
            Glide.with(this)
                    .load(avatarUri)
                    .centerCrop()
                    .into(imageViewAvatar);
        }
    }

    private void saveProfile() {
        String name = editTextName.getText().toString();
        String bio = editTextBio.getEditableText().toString();
        if (name.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "Name or Bio can't be empty!", Toast.LENGTH_SHORT).show();
        } else if (avatarUri == null) {
            DocumentReference sfDocRef = db.collection("user").document(mAuth.getUid());
            db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(sfDocRef);

                            transaction.update(sfDocRef, "name", name);
                            transaction.update(sfDocRef, "bio", bio);
                            finish();
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else if (avatarUri != null) {
            StorageReference storageReference = storage.getReference()
                    .child("avatars/" + UUID.randomUUID().toString());

            UploadTask uploadTask = storageReference.putFile(avatarUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            // get upload picture url
                            String fileLink = task.getResult().toString();

                            DocumentReference sfDocRef = db.collection("user").document(mAuth.getUid());

                            db.runTransaction(new Transaction.Function<Void>() {
                                        @Override
                                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                            DocumentSnapshot snapshot = transaction.get(sfDocRef);

                                            transaction.update(sfDocRef, "name", name);
                                            transaction.update(sfDocRef, "bio", bio);
                                            transaction.update(sfDocRef, "profile_url", fileLink);
                                            finish();


                                            // Success
                                            return null;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
}