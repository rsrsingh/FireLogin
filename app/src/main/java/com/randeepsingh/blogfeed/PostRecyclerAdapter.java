package com.randeepsingh.blogfeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    String userPass;
    String userCheck2;

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
        final String postUserID = postList.get(position).getUser_id();
        Log.e("blocking", "onBindViewHolder: post user id at postion: " + position + " " + postUserID);
        pd.setPostid(blogPostID);

        firebaseFirestore.collection("Posts").document(blogPostID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    userCheck2 = task.getResult().getString("User_id");
                    Log.e("blocking", "onComplete: " + userCheck);
                }
            }
        });

        firebaseFirestore.collection("Posts").document(blogPostID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    userCheck = task.getResult().getString("User_id");
                    //Log.e("mtest6", "onComplete: "+userCheck );
                    if (userID.equals(userCheck)) {
                        holder.dotsMenu.setVisibility(View.VISIBLE);

                    } else if (!userID.equals(userCheck)) {
                        holder.reportMenu.setVisibility(View.VISIBLE);
                    }

                    //Log.v("usercheck", "" + userCheck);
                }
            }
        });


        //hiding reported posts
        firebaseFirestore.collection("Posts").document(blogPostID).collection("Report").document(userID).addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    //    Log.e("reported posts ", "reported post " + blogPostID + " by " + userID);
                    try {
                        holder.mView.setVisibility(View.GONE);
                        postList.remove(position);
                    } catch (IndexOutOfBoundsException exception) {
                        //    Log.e("report hide", " post remove exception " + exception.getMessage());
                    }
                } else {
                    holder.mView.setVisibility(View.VISIBLE);
                    //  Log.e("reported posts ", "no post exists");
                }
            }
        });


//hiding blocked posts
        firebaseFirestore.collection("Users").document(userID).collection("Block").document(postUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {

                    String blockedUserID = documentSnapshot.getId();
                    Log.e("blocking", "onEvent: " + blockedUserID);

                    if (blockedUserID.equals(postUserID)) {
                        try {
                            postList.remove(position);
                            notifyDataSetChanged();

                        } catch (Exception d) {
                            Log.e("blocked", "onEvent: " + d);
                        }
                    } else {

                    }
                }

            }
        });


        final String description_value = postList.get(position).getDescription_value();

        final String thumb_imageUrl = postList.get(position).getThumb_imageUrl();
        String descValue = postList.get(position).getDescription_value();


        final String full_name = postList.get(position).getFull_name();
        final String profile_url = postList.get(position).getThumb_id();

        holder.setProfileImage(profile_url);
        holder.setUserText(full_name);
        holder.setCaption(descValue);


        Date date = postList.get(position).getTime_stamp();


        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat("dd MMMM yyyy");
        final StringBuilder nowMMDDYYYY = new StringBuilder(dateformatMMDDYYYY.format(date));


        holder.setTime(nowMMDDYYYY);
        holder.setPostImage(thumb_imageUrl);


        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                firebaseFirestore.collection("Posts").document(blogPostID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            userPass = task.getResult().getString("User_id");

                            Intent i = new Intent(context, Comment_activity.class);
                            i.putExtra("blog_post_id", blogPostID);
                            i.putExtra("postUserID", userPass);
                            //     Log.e("mtest6", "onClick: "+userPass );
                            context.startActivity(i);

                            progressDialog.dismiss();
                        }
                    }
                });

            }
        });


        sharedPref = new SharedPref(context);


        //report and block
        holder.reportMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.v("menucheck", "report clicked");

                Context wrapper;
                if (sharedPref.loadNightModeState()) {
                    wrapper = new ContextThemeWrapper(context, R.style.popUpThemeDark);
                } else {
                    wrapper = new ContextThemeWrapper(context, R.style.popUpThemeLight);
                }

                final PopupMenu popupMenu = new PopupMenu(wrapper, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.report_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.report_btnReport:
                                userReport(userID, position, blogPostID);
                                break;
                            case R.id.report_btnblock:

                                final AlertDialog alertDialog;
                                alertDialog = new AlertDialog.Builder(context)
                                        .setTitle("Caution!!")
                                        .setMessage("Are you sure you want to block this user?")
                                        .setPositiveButton("Block", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                final ProgressDialog progressDialog = new ProgressDialog(context);
                                                progressDialog.setMessage("Blocking user");
                                                progressDialog.show();
                                                // Log.e("block ", "block clicked");
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("User_id", postUserID);
                                                map.put("thumb_id", profile_url);
                                                map.put("full_name", full_name);
                                                map.put("Time_stamp", FieldValue.serverTimestamp());
                                                firebaseFirestore.collection("Users").document(userID).collection("Block").document(postUserID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            context.startActivity(new Intent(context, AccountMain.class));

                                                            Toast.makeText(context, "Blocked..", Toast.LENGTH_SHORT).show();
                                                            //           Log.v("block ", "user " + userID + " blocked " + userCheck);
                                                        } else {
                                                            //         Log.v("block ", "block error");
                                                        }
                                                    }
                                                });

                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Log.v("block", "No clicked");
                                            }
                                        }).show();

                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFF0000);


                                break;
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        holder.userView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                firebaseFirestore.collection("Posts").document(blogPostID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            userPass = task.getResult().getString("User_id");

                            Intent i = new Intent(context, UserProfile.class);
                            //               Log.e("mtest8", "onClick: "+userPass );
                            i.putExtra("post_user_id", userPass);
                            context.startActivity(i);
                            progressDialog.dismiss();
                        }
                    }
                });


            }
        });


        holder.dotsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.v("blogid", "" + blogPostID);
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
                                    //                   Log.v("blogtest", "deleted");


                                }
                            });

                            firebaseFirestore.collection("Posts").document(blogPostID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //                 Log.v("blogtest", "post deleted");
                                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    postList.remove(position);
                                    notifyDataSetChanged();
                                    progressDialog.dismiss();
                                }
                            });
                          /*  firebaseFirestore.collection("Users").document(userID).collection("Notifications").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                               if (!documentSnapshots.isEmpty()){
                                   for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                                       String postid=doc.getDocument().getString("")
                                   }
                               }
                                }
                            });*/

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //   Log.v("deletion", "failed to delete post from database");
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    private void userBlock(final String userID, final String userCheck) {


    }

    private void userReport(final String userID, final int position, final String blogPostID) {

        final AlertDialog alertDialog;

        if (sharedPref.loadNightModeState()) {
            new AlertDialog.Builder(context, R.style.DarkTheme);
        } else {
            new AlertDialog.Builder(context, R.style.AppTheme);
        }
        alertDialog = new AlertDialog.Builder(context).setTitle("Caution:").setMessage("Are you sure you want to report this post?").setPositiveButton("Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, Object> map = new HashMap<>();
                map.put("Time_stamp", FieldValue.serverTimestamp());
                firebaseFirestore.collection("Posts/" + blogPostID + "/Report").document(userID).set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    try {
                                        postList.remove(position);
                                        postList.remove(position);
                                        notifyDataSetChanged();
                                    } catch (IndexOutOfBoundsException e) {
                                        //           Log.e("Reported", "Exception while removing list " + e.getMessage());
                                    }
                                    //   Log.e("Reported", "Post " + blogPostID + " reported by " + userID);
                                    //     Log.e("report", "Report clicked");
                                    Toast.makeText(context, "Reported..", Toast.LENGTH_SHORT).show();
                                } else {
                                    //  Log.e("report", "error: " + task.getException().getMessage());
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Some error has occured", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Log.e("report", "No clicked");
            }
        }).show();

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
        private TextView userView, dateView;
        private CircleImageView profile;
        private ImageView imageView;
        private ImageView dotsMenu, reportMenu;
        private FirebaseAuth mAuth;
        private ImageView comments;
        private TextView caption;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            dotsMenu = mView.findViewById(R.id.ic_dots);
            comments = mView.findViewById(R.id.home_comment);
            reportMenu = mView.findViewById(R.id.report_dots);
            reportMenu.setVisibility(View.GONE);
            dotsMenu.setVisibility(View.GONE);
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

        public void setCaption(String value) {
            caption = mView.findViewById(R.id.homeRow_caption);
            caption.setText(value);
        }


    }

    public void setFilter(ArrayList<Blog> newPost) {


        postList = new ArrayList<>();


        postList.addAll(newPost);

        //  Log.v("madapter", "setFilter called  ");
        notifyDataSetChanged();

    }


}
