package com.randeepsingh.blogfeed.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.randeepsingh.blogfeed.Home.UserPost;
import com.randeepsingh.blogfeed.ProfileViewList;
import com.randeepsingh.blogfeed.R;

import java.util.ArrayList;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ViewHolder> {

    public static Context context;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<ProfileViewList> postList;
    private String postURL;

    private FirebaseAuth auth;

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

        postURL = postList.get(position).getThumb_imageUrl();

        final String post_id = postList.get(position).getBlogPostrID();
        holder.setImage(postURL);
        //  Log.e("mtest7", "onBindViewHolder: "+post_id );

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, UserPost.class);
                i.putExtra("blog_post_id", post_id);
                context.startActivity(i);
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
