package com.example.randeepsingh.firelogin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Comment;

import java.io.ObjectInput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    private ArrayList<Blog> postList;

    public static Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    String userID;
    String userCheck;

    private SharedPref sharedPref;


    //String username;

    //public String postID = "";
    private postid pd = new postid();
    //private String postUserID = null;

    public PostRecyclerAdapter(ArrayList<Blog> postList) {
        this.postList = postList;
    }


    @NonNull
    @Override
    public PostRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = parent.getContext();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final PostRecyclerAdapter.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final String blogPostID = postList.get(position).BlogPostID;


        pd.setPostid(blogPostID);


        final String description_value = postList.get(position).getDescription_value();

        final String thumb_imageUrl = postList.get(position).getThumb_imageUrl();


        String full_name = postList.get(position).getFull_name();
        final String profile_url = postList.get(position).getThumb_id();

        holder.setProfileImage(profile_url);
        holder.setUserText(full_name);


        Date date = postList.get(position).getTime_stamp();


        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat("dd MMMM yyyy");
        final StringBuilder nowMMDDYYYY = new StringBuilder(dateformatMMDDYYYY.format(date));


        holder.setTime(nowMMDDYYYY);
        holder.setPostImage(thumb_imageUrl);
        holder.setDescText(description_value);


        firebaseFirestore.collection("Posts").document(blogPostID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    userCheck = task.getResult().getString("User_id");
                    Log.v("usercheck", "" + userCheck);
                }
            }
        });


        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Comment_activity.class);
                i.putExtra("blog_post_id", blogPostID);
                context.startActivity(i);

            }
        });


        sharedPref = new SharedPref(context);

        holder.dotsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context wrapper;
                if (sharedPref.loadNightModeState()) {
                    wrapper = new ContextThemeWrapper(context, R.style.popUpThemeDark);
                } else {
                    wrapper = new ContextThemeWrapper(context, R.style.popUpThemeLight);
                }
                PopupMenu popupMenu = new PopupMenu(wrapper, view);
                MenuInflater inflater = popupMenu.getMenuInflater();

                if (userID.equals(userCheck)) {
                    inflater.inflate(R.menu.delete_menu, popupMenu.getMenu());
                } else {
                    inflater.inflate(R.menu.report_menu,popupMenu.getMenu());
                }


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {

                            case R.id.delete:
                                Log.v("blogid", "" + blogPostID);
                                final ProgressDialog progressDialog = new ProgressDialog(context);
                                progressDialog.setMessage("Deleting...");
                                progressDialog.show();
                                firebaseFirestore.collection("Posts").document(blogPostID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            String url = task.getResult().getString("thumb_imageUrl");
                                            final FirebaseStorage postsImageRef = FirebaseStorage.getInstance();
                                            StorageReference photoRef = postsImageRef.getReferenceFromUrl(url);
                                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.v("blogtest", "deleted");


                                                }
                                            });

                                            firebaseFirestore.collection("Posts").document(blogPostID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.v("blogtest", "post deleted");
                                                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                    postList.remove(position);
                                                    notifyDataSetChanged();
                                                    progressDialog.dismiss();
                                                }
                                            });

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.v("deletion", "failed to delete post from database");

                                        Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }


                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {

        return postList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView descView, userView, dateView;
        private CircleImageView profile;
        private ImageView imageView;
        private ImageView dotsMenu;
        private FirebaseAuth mAuth;
        private ImageView comments;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            dotsMenu = mView.findViewById(R.id.ic_dots);
            comments = mView.findViewById(R.id.home_comment);

        }

        public void setProfileImage(String profileImage) {

            profile = mView.findViewById(R.id.homeRow_prof);
            Glide.with(context).load(profileImage).into(profile);

        }

        public void setUserText(String userText) {
            userView = mView.findViewById(R.id.homeRow_username);
            userView.setText(userText);
        }

        public void setTime(StringBuilder time) {
            dateView = mView.findViewById(R.id.homeRow_time);
            dateView.setText(time);
        }

        public void setPostImage(String postImage) {
            imageView = mView.findViewById(R.id.homeRow_post);
            Glide.with(context).load(postImage).into(imageView);
        }

        public void setDescText(String descText) {
            descView = mView.findViewById(R.id.homeRow_caption);
            descView.setText(descText);
        }
    }

    public void setFilter(ArrayList<Blog> newPost) {


        postList = new ArrayList<>();


        postList.addAll(newPost);

        Log.v("madapter", "setFilter called  ");
        notifyDataSetChanged();

    }


}
