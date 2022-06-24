package com.example.cs4520teamproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cs4520teamproject.GroupActivity;
import com.example.cs4520teamproject.Model.Group;
import com.example.cs4520teamproject.Model.User;
import com.example.cs4520teamproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class GroupsListAdapter extends RecyclerView.Adapter {

    private static final int FULL_GROUP = 0x001;
    private static final int NOT_FULL_GROUP = 0x002;

    private ArrayList<Group> groups;
    private Context context;


    public GroupsListAdapter(ArrayList<Group> groups, Context context) {
        this.groups = groups;
        this.context = context;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    private class NotFullGroupList extends RecyclerView.ViewHolder {
        private TextView textViewDate, textViewDestination, textViewTotal, textViewCurrent, textViewHostName;
        private ImageView imageViewPhoto;
        private CardView cardViewGroup;
        private FirebaseFirestore db;

        NotFullGroupList(@NonNull View v) {
            super(v);
            textViewDate = v.findViewById(R.id.groupTextViewDate);
            textViewDestination = v.findViewById(R.id.groupTextViewDestination1);
            textViewTotal = v.findViewById(R.id.groupTextViewTotal);
            textViewCurrent = v.findViewById(R.id.groupTextViewCurrent);
            textViewHostName = v.findViewById(R.id.groupTextViewHostName);
            imageViewPhoto = v.findViewById(R.id.groupImageViewPhoto);
            cardViewGroup = v.findViewById(R.id.cardViewGroup);
            db = FirebaseFirestore.getInstance();
        }

        void bind(Group group) {
            textViewCurrent.setText("" + group.getCurNumberOfMembers());
            textViewTotal.setText("" + group.getTotalNumberOfMembers());
            textViewDate.setText(group.getDate());
            textViewDestination.setText(group.getDestination());
           cardViewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toThisGroup = new Intent(context, GroupActivity.class);
                    toThisGroup.putExtra("curGroup", group);
                    toThisGroup.putExtra("type", 1);
                    context.startActivity(toThisGroup);
                }
            });
            db.collection("user")
                    .document(group.getCreateBy())
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
                                        .into(imageViewPhoto);
                               textViewHostName.setText(user.getName());
                            }
                        }
                    });
        }
    }

    private class FullGroupList extends RecyclerView.ViewHolder {
        private TextView textViewDate, textViewDestination, textViewHostName;
        private ImageView imageViewPhoto;
        private CardView cardViewGroup;
        private FirebaseFirestore db;

        FullGroupList(@NonNull View v) {
            super(v);
            textViewDate = v.findViewById(R.id.groupFullTextViewDate);
            textViewDestination = v.findViewById(R.id.groupFullTextViewDestination);
            textViewHostName = v.findViewById(R.id.groupFullTextViewHostName);
            imageViewPhoto = v.findViewById(R.id.groupFullImageViewAvatar);
            cardViewGroup = v.findViewById(R.id.groupFullCardView);
            db = FirebaseFirestore.getInstance();
        }

        void bind(Group group) {
            textViewDate.setText(group.getDate());
            textViewDestination.setText(group.getDestination());
            cardViewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toThisGroup = new Intent(context, GroupActivity.class);
                    toThisGroup.putExtra("curGroup", group);
                    toThisGroup.putExtra("type", 1);
                    context.startActivity(toThisGroup);
                }
            });
            db.collection("user")
                    .document(group.getCreateBy())
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
                                        .into(imageViewPhoto);
                                textViewHostName.setText(user.getName());
                            }
                        }
                    });
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if (viewType == FULL_GROUP) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.groups_list_full, parent, false);
            return new FullGroupList(view);
        } else if (viewType == NOT_FULL_GROUP)  {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.groups_list_not_full, parent, false);
            return new NotFullGroupList(view);
        }

        return null;

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Group group = groups.get(holder.getAdapterPosition());

        if (group.isHasFull()) {
            ((FullGroupList) holder).bind(group);
        } else if (!group.isHasFull()) {
            ((NotFullGroupList) holder).bind(group);
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public int getItemViewType(int position) {
        Group group = groups.get(position);

        if (group.isHasFull()) {
            return FULL_GROUP;
        } else if (!group.isHasFull()) {
            return NOT_FULL_GROUP;
        }
        return - 1;
    }
}
