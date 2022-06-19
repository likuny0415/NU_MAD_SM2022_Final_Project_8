package com.example.cs4520teamproject.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cs4520teamproject.Model.Group;
import com.example.cs4520teamproject.Model.User;
import com.example.cs4520teamproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.ViewHolder> {

    private ArrayList<Group> groups;
    private Context context;
    private FirebaseFirestore db;

    public GroupsListAdapter(ArrayList<Group> groups, Context context) {
        this.groups = groups;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDate, textViewDestination, textViewTotal, textViewCurrent;
        private ImageView imageViewPhoto;

        public ViewHolder(@NonNull View v) {
            super(v);
            textViewDate = v.findViewById(R.id.groupTextViewDate);
            textViewDestination = v.findViewById(R.id.groupTextViewDestination);
            textViewTotal = v.findViewById(R.id.groupTextViewTotal);
            textViewCurrent = v.findViewById(R.id.groupTextViewCurrent);
            imageViewPhoto = v.findViewById(R.id.groupImageViewPhoto);
        }

        public TextView getTextViewDate() {
            return textViewDate;
        }

        public TextView getTextViewDestination() {
            return textViewDestination;
        }

        public TextView getTextViewTotal() {
            return textViewTotal;
        }

        public TextView getTextViewCurrent() {
            return textViewCurrent;
        }

        public ImageView getImageViewPhoto() {
            return imageViewPhoto;
        }
    }

    @NonNull
    @Override
    public GroupsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_list, parent, false);
        db = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsListAdapter.ViewHolder holder, int position) {
        holder.getTextViewCurrent().setText("" + groups.get(holder.getAdapterPosition()).getCurNumberOfMembers());
        holder.getTextViewTotal().setText("" + groups.get(holder.getAdapterPosition()).getTotalNumberOfMembers());
        holder.getTextViewDate().setText(groups.get(holder.getAdapterPosition()).getDate());
        holder.getTextViewDestination().setText(groups.get(holder.getAdapterPosition()).getDestination());
        db.collection("user")
                .document(groups.get(holder.getAdapterPosition()).getCreateBy())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            User user = doc.toObject(User.class);
                            Glide.with(context)
                                    .load(user.getProfile_url())
                                    .centerCrop()
                                    .into(holder.getImageViewPhoto());


                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


}
