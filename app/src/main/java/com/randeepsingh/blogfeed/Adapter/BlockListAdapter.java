package com.randeepsingh.blogfeed.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.randeepsingh.blogfeed.Blog;
import com.randeepsingh.blogfeed.Home.AccountMain;
import com.randeepsingh.blogfeed.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.ViewHolder> {


    public static Context context;
    private  FirebaseFirestore firebaseFirestore;
    private  FirebaseAuth auth;
    private String userID;

    private  ProgressDialog progressDialog;

    public BlockListAdapter(ArrayList<Blog> blockList) {
        this.blockList = blockList;
    }

    ArrayList<Blog> blockList;

    @NonNull
    @Override
    public BlockListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.block_row, parent, false);
        context = parent.getContext();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockListAdapter.ViewHolder holder, final int position) {


        final String profURL = blockList.get(position).getThumb_id();
        final String fName = blockList.get(position).getFull_name();
        final String blockedUserID = blockList.get(position).getUser_id();
        holder.setProfileImage(profURL);
        holder.setName(fName);

        holder.btnUnblock.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Unblocking user...");
                progressDialog.show();
                firebaseFirestore.collection("Users").document(userID).collection("Block").document(blockedUserID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "User unblocked successfully. Now you can see their posts", Toast.LENGTH_SHORT).show();
                            blockList.remove(position);
                            notifyDataSetChanged();
                            progressDialog.dismiss();
                            context.startActivity(new Intent(context, AccountMain.class));
                        }
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return blockList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircleImageView circleImageView;
        private TextView username;
        Button btnUnblock;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            btnUnblock = mView.findViewById(R.id.blockRow_btn);
        }

        public void setProfileImage(String url) {
            circleImageView = mView.findViewById(R.id.blockRow_profile);
            Glide.with(context).load(url).into(circleImageView);

        }

        public void setName(String name) {
            username = mView.findViewById(R.id.blockRow_username);
            username.setText(name);
        }
    }
}
