package com.example.cs4520teamproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cs4520teamproject.Model.User;
import com.example.cs4520teamproject.R;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder>{

    private ArrayList<User> members;
    private Context context;

    public MembersAdapter(ArrayList<User> members, Context context) {
        this.members = members;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView photo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewMemberName);
            photo = itemView.findViewById(R.id.imageViewMemberPhoto);
        }

        public TextView getName() {
            return name;
        }

        public ImageView getPhoto() {
            return photo;
        }
    }

    @NonNull
    @Override
    public MembersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.members_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersAdapter.ViewHolder holder, int position) {
        holder.getName().setText(members.get(holder.getAdapterPosition()).getName());
        Glide.with(context)
                .load(members.get(holder.getAdapterPosition()).getProfile_url())
                .centerCrop()
                .into(holder.getPhoto());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }


}
