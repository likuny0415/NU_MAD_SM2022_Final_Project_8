package com.example.cs4520teamproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.location.Address;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_GET_MAP_LOCATION = 0;
    private ImageView imageViewRequestLocation, imageViewDate;
    private EditText editTextTotalMembers, editTextAvgCost, editTextNote;
    private Button buttonPost;
    private TextView textViewDestination, textViewDate;

    private Address address;
    private String destination;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setTitle("Create your group!");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        imageViewRequestLocation = findViewById(R.id.imageViewRequestDestination);
        imageViewDate = findViewById(R.id.createGroupImageViewDate);
        textViewDestination = findViewById(R.id.createGroupTextViewDestination);
        textViewDate = findViewById(R.id.createGroupTextViewDate);
        editTextTotalMembers = findViewById(R.id.createGroupEditTextTotalNumbers);
        editTextAvgCost = findViewById(R.id.createGroupEditTextAvgCost);
        editTextNote = findViewById(R.id.createGroupEditTextNote);
        buttonPost = findViewById(R.id.createGroupButtonPost);


        imageViewRequestLocation.setOnClickListener(this);
        buttonPost.setOnClickListener(this);
        imageViewDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewRequestDestination:
                toMaps();
                break;
            case R.id.createGroupButtonPost:
                createGroup();
                break;
            case R.id.createGroupImageViewDate:
                selectDate();
                break;
        }
    }

    private void selectDate() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(CreateGroupActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + monthTransfrom(month) + "/" + year;
                textViewDate.setText(date);
            }
        }, year, month, day);
        dialog.show();
    }

    private String monthTransfrom(int month) {
        if (month < 10) {
            return "0" + month;
        } else {
            return "" + month;
        }
    }

    private void createGroup() {
        String totalMembers = editTextTotalMembers.getText().toString();
        String avgCost = editTextAvgCost.getText().toString();
        String note = editTextNote.getText().toString();
        String selectDestination = textViewDestination.getText().toString();
        String selectDate = textViewDate.getText().toString();

        if (totalMembers.isEmpty() ||avgCost.isEmpty() || note.isEmpty()
                || selectDestination.equals("Find destination") || selectDate.equals("Select Date")) {
            Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
        } else {
            int totalM = Integer.valueOf(totalMembers);
            int avgC = Integer.valueOf(avgCost);

            if (totalM > 10) {
                Toast.makeText(this, "Total number of members must less than 10!", Toast.LENGTH_SHORT).show();
            } else if (totalM < 2) {
                Toast.makeText(this, "A group at least have two members!", Toast.LENGTH_SHORT).show();
            }
            else if (avgC > 1000) {
                Toast.makeText(this, "Average cost can't more than 1000 dollars", Toast.LENGTH_SHORT).show();
            } else {
                Map<String, Object> curGroup = new HashMap<>();

                Date d = new Date();

                SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    d = formatDate.parse(selectDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                curGroup.put("createBy", mAuth.getUid());
                curGroup.put("members", Arrays.asList(mAuth.getUid()));
                curGroup.put("groupDate", d);
                curGroup.put("date", selectDate);
                curGroup.put("totalNumberOfMembers", totalM);
                curGroup.put("curNumberOfMembers", 1);
                curGroup.put("note", note);
                curGroup.put("destination", selectDestination);
                curGroup.put("latitude", address.getLatitude());
                curGroup.put("longitude", address.getLongitude());
                curGroup.put("hasFull", false);
                curGroup.put("averageCost", avgC);

                db.collection("group")
                        .add(curGroup)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                String groupId = documentReference.getId();
                                curGroup.put("id", groupId);
                                db.collection("group").document(groupId).set(curGroup, SetOptions.merge());
                                db.collection("user")
                                        .document(mAuth.getUid())
                                        .update("groups", FieldValue.arrayUnion(groupId));
                                finish();
                            }
                        });
            }

        }
    }

    private void toMaps() {
        Intent toMaps = new Intent(CreateGroupActivity.this, MapsActivity.class);
        startActivityForResult(toMaps, REQUEST_GET_MAP_LOCATION);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_MAP_LOCATION && resultCode == RESULT_OK) {
            address = data.getParcelableExtra("address");
            destination = data.getStringExtra("destination");
            setDestination();
        }
    }

    private void setDestination() {
        textViewDestination.setText(destination);
    }
}