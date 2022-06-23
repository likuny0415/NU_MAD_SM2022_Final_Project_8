package com.example.cs4520teamproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs4520teamproject.EditGroupActivity;
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

public class CreatedGroupListAdapter extends RecyclerView.Adapter<CreatedGroupListAdapter.ViewHolder> {

    private ArrayList<Group> createdGroups;
    private Group currentGroup;
    User currentUser;
    private FirebaseFirestore db;
    private IRemoveButton iRemoveButton;
    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView destination, userName, date, joinedNumber, totalNumber;
        private final ImageView avatar;
        private final Button buttonDelete, buttonEdit;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            destination = itemView.findViewById(R.id.createdGroupListTextViewMountain);
            userName = itemView.findViewById(R.id.createdGroupListTextViewName);
            date = itemView.findViewById(R.id.createdGroupListTextViewDate);
            joinedNumber = itemView.findViewById(R.id.createdGroupListTextViewJoined);
            totalNumber = itemView.findViewById(R.id.createdGroupListTextViewTotal);
            avatar = itemView.findViewById(R.id.createdGroupListImageViewAvatar);
            buttonDelete = itemView.findViewById(R.id.createdGroupListButtonDelete);
            buttonEdit = itemView.findViewById(R.id.createdGroupListButtonEdit);

        }

        public Button getButtonEdit() {
            return buttonEdit;
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

        public Button getButtonDelete() {
            return buttonDelete;
        }
    }

    public CreatedGroupListAdapter(ArrayList<Group> createdGroups, IRemoveButton iRemoveButton, Context context) {
        this.createdGroups = createdGroups;
        this.iRemoveButton = iRemoveButton;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_created_row, parent, false);
        db = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        currentGroup = createdGroups.get(position);
        holder.getDestination().setText(currentGroup.getDestination());
        holder.getDate().setText(currentGroup.getDate());
        holder.getJoinedNumber().setText("" + currentGroup.getCurNumberOfMembers());
        holder.getTotalNumber().setText("" + currentGroup.getTotalNumberOfMembers());
        holder.getButtonDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdGroups.remove(holder.getAdapterPosition());
                iRemoveButton.removeGroup(currentGroup);
                notifyDataSetChanged();
            }
        });

        holder.getButtonEdit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toThisGroup = new Intent(context, EditGroupActivity.class);
                toThisGroup.putExtra("curGroup", currentGroup);
                context.startActivity(toThisGroup);
            }
        });


        db.collection("user")
                .document(createdGroups.get(holder.getAdapterPosition()).getCreateBy())
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
        return createdGroups.size();
    }


    // transfer the delete note to the notes activity
    public interface IRemoveButton {
        void removeGroup(Group group);
    }
}
