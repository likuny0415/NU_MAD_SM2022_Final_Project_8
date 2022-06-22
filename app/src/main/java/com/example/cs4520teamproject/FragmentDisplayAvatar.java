package com.example.cs4520teamproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDisplayAvatar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDisplayAvatar extends Fragment implements View.OnClickListener {

    private static final String ARG_URI= "avatarUru";

    private Uri avatarUri;
    private IDisplayAvatar mListener;

    private ImageView imageViewAvatar;
    private Button buttonRetake;
    private Button buttonUpload;

    public FragmentDisplayAvatar() {
        // Required empty public constructor
    }


    public static FragmentDisplayAvatar newInstance(Uri imageUri) {
        FragmentDisplayAvatar fragment = new FragmentDisplayAvatar();
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, imageUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            avatarUri = getArguments().getParcelable(ARG_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_avatar, container, false);
        imageViewAvatar = view.findViewById(R.id.displayAvatarImageViewAvatar);
        buttonRetake = view.findViewById(R.id.displayAvatarButtonRetake);
        buttonUpload = view.findViewById(R.id.displayAvatarButtonUpload);

        buttonRetake.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        Glide.with(getContext())
                .load(avatarUri)
                .centerCrop()
                .into(imageViewAvatar);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.displayAvatarButtonRetake:
                mListener.retakeAvatar();
                break;
            case R.id.displayAvatarButtonUpload:
                mListener.uploadAvatar(avatarUri);
                break;
        }
    }

    public interface IDisplayAvatar{
        void retakeAvatar();
        void uploadAvatar(Uri imageUri);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IDisplayAvatar) {
            mListener = (IDisplayAvatar) context;
         } else {
            throw new RuntimeException(context+" must implement IDisplayAvatar");
        }
    }
}