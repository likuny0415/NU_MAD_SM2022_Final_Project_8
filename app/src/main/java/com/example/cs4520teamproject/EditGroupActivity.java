package com.example.cs4520teamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cs4520teamproject.Model.Group;
import com.example.cs4520teamproject.Model.User;
import com.example.cs4520teamproject.adapter.MembersAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private Group group;
    private TextView textViewCurrentNumber, textViewDestination, textViewDate;
    private EditText editTextTotal, editTextAvgCost, editTextNote;
    private ImageView imageViewDate, imageViewLocation;
    private RecyclerView recyclerViewMembers;
    private RecyclerView.LayoutManager layoutManager;
    private MembersAdapter membersAdapter;
    private Button buttonSave;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewCurrentNumber = findViewById(R.id.editGroupTextViewCurrentMembers);
        textViewDestination = findViewById(R.id.editGroupTextViewDestination);
        textViewDate = findViewById(R.id.editGroupTextViewDate);
        editTextTotal = findViewById(R.id.editGroupEditTextTotalMembers);
        editTextAvgCost = findViewById(R.id.editGroupEditTextAvgCost);
        editTextNote = findViewById(R.id.editGroupEditTextNote);
        recyclerViewMembers = findViewById(R.id.editGroupRecyclerViewMembers);
        imageViewLocation = findViewById(R.id.editGroupImageViewLocation);
        imageViewDate = findViewById(R.id.editGroupImageViewDate);
        buttonSave = findViewById(R.id.editGroupButtonSave);

        if (getIntent() != null && getIntent().getExtras() != null) {

            group = (Group) getIntent().getSerializableExtra("curGroup");
            setTitle(group.getDestination());
            imageViewLocation.setOnClickListener(this);
            imageViewDate.setOnClickListener(this);
            buttonSave.setOnClickListener(this);

            displayInfo();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editGroupImageViewLocation:
                toMap();
                break;
            case R.id.editGroupButtonSave:
                saveGroupInfo();
                break;
            case R.id.editGroupImageViewDate:
                selectDate();
                break;
        }
    }

    private void selectDate() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(EditGroupActivity.this, new DatePickerDialog.OnDateSetListener() {
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

    private void saveGroupInfo() {
        String totalMembers = editTextTotal.getText().toString();
        String avgCost = editTextAvgCost.getText().toString();
        String note = editTextNote.getText().toString();
        String date = textViewDate.getText().toString();

        if (totalMembers.isEmpty() || avgCost.isEmpty() || note.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
        } else {
            int totalM = Integer.valueOf(totalMembers);
            int avgC = Integer.valueOf(avgCost);
            Date d = new Date();
            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

            try {
                d = formatDate.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (totalM > 10) {
                Toast.makeText(this, "Total number of members must less than 10!", Toast.LENGTH_SHORT).show();
            } else if (totalM < 2) {
                Toast.makeText(this, "A group at least have two members!", Toast.LENGTH_SHORT).show();
            } else if (avgC > 1000) {
                Toast.makeText(this, "Average cost can't more than 1000 dollars", Toast.LENGTH_SHORT).show();
            } else {
                db.collection("group").document(group.getId()).update("averageCost", avgC);
                db.collection("group").document(group.getId()).update("totalNumberOfMembers", totalM);
                db.collection("group").document(group.getId()).update("date", date);
                db.collection("group").document(group.getId()).update("note", note);
                db.collection("group").document(group.getId()).update("groupDate", d);
                finish();
            }
        }
    }

    private void displayInfo() {
        textViewCurrentNumber.setText("" + group.getCurNumberOfMembers());
        editTextTotal.setText("" + group.getTotalNumberOfMembers());
        editTextAvgCost.setText(group.getAverageCost() + "");
        editTextNote.setText(group.getNote());
        textViewDestination.setText(group.getDestination());
        textViewDate.setText(group.getDate());

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
                            layoutManager = new LinearLayoutManager(EditGroupActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            recyclerViewMembers.setLayoutManager(layoutManager);
                            membersAdapter = new MembersAdapter(members, EditGroupActivity.this);
                            recyclerViewMembers.setAdapter(membersAdapter);
                        }
                    });
        }
    }

    private void toMap() {
        Intent toMap = new Intent(EditGroupActivity.this, MapsActivity.class);
        toMap.putExtra("type", 2);
        toMap.putExtra("latitude", group.getLatitude());
        toMap.putExtra("longitude", group.getLongitude());
        startActivity(toMap);
    }
}