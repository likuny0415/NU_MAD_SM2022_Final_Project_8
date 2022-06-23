package com.example.cs4520teamproject.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs4520teamproject.GroupActivity;
import com.example.cs4520teamproject.Model.Group;
import com.example.cs4520teamproject.Model.User;
import com.example.cs4520teamproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class JoinedGroupListAdapter extends RecyclerView.Adapter<JoinedGroupListAdapter.ViewHolder>{

    private ArrayList<Group> joinedGroups;
    private Group currentGroup;
    User currentUser;
    private FirebaseFirestore db;
    private IQuitButton iQuitButton;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView destination, userName, date, joinedNumber, totalNumber;
        private final ImageView avatar;
        private final Button buttonQuit;
        private final Button buttonDetail;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            destination = itemView.findViewById(R.id.joinedGroupListTextViewMountain);
            userName = itemView.findViewById(R.id.joinedGroupListTextViewName);
            date = itemView.findViewById(R.id.joinedGroupListTextViewDate);
            joinedNumber = itemView.findViewById(R.id.joinedGroupListTextViewJoined);
            totalNumber = itemView.findViewById(R.id.joinedGroupListTextViewTotal);
            avatar = itemView.findViewById(R.id.joinedGroupListImageViewAvatar);
            buttonQuit = itemView.findViewById(R.id.joinedGroupListButtonQuit);
            buttonDetail = itemView.findViewById(R.id.joinedGroupListButtonDetail);
        }

        public Button getButtonDetail() {
            return buttonDetail;
        }

        public TextView getDestination() {
            return destination;
        }

        public TextView getUserName() {
            return userName;
        }

        public TextView getDate() {
            return date;
        }

        public TextView getJoinedNumber() {
            return joinedNumber;
        }

        public TextView getTotalNumber() {
            return totalNumber;
        }

        public ImageView getAvatar() {
            return avatar;
        }

        public Button getButtonQuit() {
            return buttonQuit;
        }
    }

    public JoinedGroupListAdapter(ArrayList<Group> joinedGroups, IQuitButton iQuitButton, Context context) {
        this.joinedGroups = joinedGroups;
        this.iQuitButton = iQuitButton;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_joined_row, parent, false);
        db = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        currentGroup = joinedGroups.get(position);
        holder.getDestination().setText(currentGroup.getDestination());
        holder.getDate().setText(currentGroup.getDate());
        holder.getJoinedNumber().setText("" + currentGroup.getCurNumberOfMembers());
        holder.getTotalNumber().setText("" + currentGroup.getTotalNumberOfMembers());
        holder.getButtonQuit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Confirm Quit")
                        .setMessage("Are you sure you want to quit the group to "+currentGroup.getDestination()+"?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                joinedGroups.remove(holder.getAdapterPosition());
                                iQuitButton.quitGroup(currentGroup);
                                notifyDataSetChanged();
                            }
                        })

                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
        holder.getButtonDetail().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toThisGroup = new Intent(context, GroupActivity.class);
                toThisGroup.putExtra("curGroup", currentGroup);
                toThisGroup.putExtra("type", 2);
                context.startActivity(toThisGroup);
            }
        });


        db.collection("user")
                .document(joinedGroups.get(holder.getAdapterPosition()).getCreateBy())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            currentUser = doc.toObject(User.class);
                            Picasso.get().load(currentUser.getProfile_url()).fit().into(holder.avatar);
                            holder.getUserName().setText(currentUser.getName());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return joinedGroups.size();
    }

    // transfer the delete note to the notes activity
    public interface IQuitButton {
        void quitGroup(Group group);
    }
}
