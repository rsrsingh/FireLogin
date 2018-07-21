package com.example.randeepsingh.firelogin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ViewHolder> {

    public static Context context;
    FirebaseFirestore firebaseFirestore;
    ArrayList<ProfileViewList> postList;
    private String postURL;
    private String postID;
    FirebaseAuth auth;

    public ProfileRecyclerAdapter(ArrayList<ProfileViewList> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_row, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = parent.getContext();
        auth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        postID = postList.get(position).getBlogPostrID();
        postURL = postList.get(position).getThumb_imageUrl();
        holder.setImage(postURL);
        final String post_id = postList.get(position).getBlogPostrID();

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "image is clicked at position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage(String url) {
            imageView = mView.findViewById(R.id.profileRow_image);
            Glide.with(context).load(url).into(imageView);
        }
    }
}
