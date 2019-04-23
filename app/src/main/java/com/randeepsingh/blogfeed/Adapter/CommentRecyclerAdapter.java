package com.randeepsingh.blogfeed.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.randeepsingh.blogfeed.Home.Comment_activity;
import com.randeepsingh.blogfeed.Comments;
import com.randeepsingh.blogfeed.Home.UserProfile;
import com.randeepsingh.blogfeed.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder> {
    private ArrayList<Comments> commentList;
    public static Context context;

    private Comment_activity comment_activity = new Comment_activity();
    private String blogPostID;


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String cUserID;


    public CommentRecyclerAdapter(ArrayList<Comments> commentList, String blogPostID) {
        this.commentList = commentList;
        this.blogPostID = blogPostID;
        //    Log.v("bgid", "constructor called");
    }

    @NonNull
    @Override
    public CommentRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false);
        //  Log.v("bgid", "onCreateviewHolder called");
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = parent.getContext();
        auth = FirebaseAuth.getInstance();
        cUserID = auth.getCurrentUser().getUid();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentRecyclerAdapter.ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);
        //Log.v("bgid", "onBind Viewholder called");
        final String message = commentList.get(position).getComment_value();
        final String userID = commentList.get(position).getUser_id();
        // String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        final String commentId = commentList.get(position).CommentID;
        //final String cmntID = commentList.get(position).BlogPostID;


        //profile pic and fullname retrieve
        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //      Log.v("bgid", "User info called");
                if (task.isSuccessful()) {
                    String userData = task.getResult().getString("full_name");
                    String profileUrl = task.getResult().getString("thumb_id");

                    holder.setProfImage(profileUrl);
                    holder.setUsername(userData);

                    //        Log.v("cmnt", "fullname: " + userData + "  Comment value: " + message);

                    //  Log.v("cmntms",""+message);
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.setComment(message);


        //Delete View visibiltiy
        firebaseFirestore.collection("Posts").document(blogPostID).collection("Comments").document(commentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()) {
                    String cmntUserID = task.getResult().getString("user_id");
                    if (cmntUserID.equals(cUserID)) {
                        holder.deleteView.setVisibility(View.VISIBLE);
                    }

                }
            }
        });


        // Comment delete

        holder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Deleting...");
                progressDialog.show();
                firebaseFirestore.collection("Posts").document(blogPostID).collection("Comments").document(commentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        commentList.remove(position);
                        notifyDataSetChanged();

                        Toast.makeText(context, "Comment Deleted", Toast.LENGTH_SHORT).show();

                    }
                });
                progressDialog.dismiss();

            }
        });

        holder.usernameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("post_user_id", userID);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircleImageView profView;
        private TextView usernameView;
        private TextView commentView;
        private ImageView deleteView;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            usernameView = mView.findViewById(R.id.cmntRow_username);
            //Log.v("bgid", "Viewholder called");
            deleteView = mView.findViewById(R.id.cmntRow_delete);
            deleteView.setVisibility(View.GONE);

        }

        public void setProfImage(String profileImage) {
            profView = mView.findViewById(R.id.cmntRow_prof);
            Glide.with(context).load(profileImage).into(profView);

        }

        public void setUsername(String username) {

            usernameView.setText(username);
        }

        public void setComment(String commentValue) {
            commentView = mView.findViewById(R.id.cmntRow_cmnt);
            commentView.setText(commentValue);

        }


    }
}
