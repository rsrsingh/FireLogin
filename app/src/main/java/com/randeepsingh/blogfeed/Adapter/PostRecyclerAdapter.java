package com.randeepsingh.blogfeed.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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
import com.randeepsingh.blogfeed.Blog;
import com.randeepsingh.blogfeed.Home.AccountMain;
import com.randeepsingh.blogfeed.Home.Comment_activity;
import com.randeepsingh.blogfeed.Home.UserProfile;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.SharedPref;
import com.randeepsingh.blogfeed.postid;

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
    private String userID;
    private String userCheck;
    private String userPass;
    private String userCheck2;
    private String full_name = "", profile_url = "";
    private SharedPref sharedPref;
    public static final String TAG = "PostRecyclerAdapter";


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
        Log.e(TAG, "onBindViewHolder: position; " + position);
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

        final String thumb_imageUrl = postList.get(position).getThumb_imageUrl();
        Date date = postList.get(position).getTime_stamp();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat("dd MMMM yyyy");
        final StringBuilder nowMMDDYYYY = new StringBuilder(dateformatMMDDYYYY.format(date));


        holder.setTime(nowMMDDYYYY);
        holder.setPostImage(thumb_imageUrl);


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

        //this will retrieve user data at for each position
        firebaseFirestore.collection("Users").document(postUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    full_name = task.getResult().getString("full_name");
                    profile_url = task.getResult().getString("thumb_id");

                    Log.e(TAG, "onComplete: usersdata" + full_name + " at position : " + position);
                    holder.setUserText(full_name);
                    holder.setProfileImage(profile_url);
                }
            }
        });


        //hiding reported posts
        hideReportPost(blogPostID, holder, position);

        //hiding blocked posts
        hideBlockPosts(postUserID, position);


        //likes image hide
        likesImageHide(blogPostID, position, userID, holder);

        //like feature
        likeFeature(blogPostID, holder);

        //unlike feature
        unlikeFeature(blogPostID, holder);

        //postDelete feature
        postDelete(blogPostID, position, holder);

        //commenta
        commentsFeature(blogPostID, holder);
        //User profile feature
        userProfile(blogPostID, holder);

        //report and block feature
        reportAndBlockFeature(blogPostID,  holder,  position,  postUserID,  profile_url,  full_name);


            setAnimation(holder.itemView);

        sharedPref = new SharedPref(context);

    }

    private void setAnimation(View itemView) {
        // If the bound view wasn't previously displayed on screen, it's animated
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        itemView.startAnimation(animation);

    }

    private void postDelete(String blogPostID, int position, ViewHolder holder) {
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

    private void userProfile(String blogPostID, ViewHolder holder) {

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


    }

    private void reportAndBlockFeature(String blogPostID, ViewHolder holder, int position, String postUserID, String profile_url, String full_name) {
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


    }

    private void commentsFeature(String blogPostID, ViewHolder holder) {
        holder.commentCard.setOnClickListener(new View.OnClickListener() {
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
    }

    private void unlikeFeature(String blogPostID, ViewHolder holder) {
        holder.likeS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Posts").document(blogPostID).collection("Likes").document(userID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Unliked", Toast.LENGTH_SHORT).show();
                        holder.likeS.setVisibility(View.GONE);
                        holder.likeU.setVisibility(View.VISIBLE);
                        holder.likeSTV.setVisibility(View.GONE);
                        holder.likeUTV.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void likeFeature(String blogPostID, ViewHolder holder) {
        holder.likeU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put("time_stamp", FieldValue.serverTimestamp());
                firebaseFirestore.collection("Posts").document(blogPostID).collection("Likes").document(userID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                        holder.likeU.setVisibility(View.GONE);
                        holder.likeS.setVisibility(View.VISIBLE);
                        holder.likeUTV.setVisibility(View.GONE);
                        holder.likeSTV.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void likesImageHide(String blogPostID, int position, String userID, ViewHolder holder) {
        firebaseFirestore.collection("Posts").document(blogPostID).collection("Likes").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    holder.likeS.setVisibility(View.VISIBLE);
                    holder.likeSTV.setVisibility(View.VISIBLE);
                } else {
                    holder.likeU.setVisibility(View.VISIBLE);
                    holder.likeUTV.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void hideBlockPosts(String postUserID, int position) {
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

    }

    private void hideReportPost(String blogPostID, ViewHolder holder, int position) {
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView userView, dateView, likeSTV, likeUTV;
        private CircleImageView profile;
        private ImageView imageView;
        private ImageView dotsMenu, reportMenu;
        private CardView commentCard, likeCard;
        private ImageView likeS, likeU;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            profile = mView.findViewById(R.id.homeRow_prof);
            userView = mView.findViewById(R.id.homeRow_username);
            dateView = mView.findViewById(R.id.homeRow_time);
            imageView = mView.findViewById(R.id.homeRow_post);
            dotsMenu = mView.findViewById(R.id.ic_dots);
            commentCard = mView.findViewById(R.id.homeRow_commentcard);
            reportMenu = mView.findViewById(R.id.report_dots);
            reportMenu.setVisibility(View.GONE);
            dotsMenu.setVisibility(View.GONE);
            likeS = mView.findViewById(R.id.homeRow_likeS);
            likeSTV = mView.findViewById(R.id.homeRow_likeSTV);
            likeUTV = mView.findViewById(R.id.homeRow_likeUTV);
            likeU = mView.findViewById(R.id.homeRow_likeU);
            likeUTV.setVisibility(View.GONE);
            likeSTV.setVisibility(View.GONE);
            likeS.setVisibility(View.GONE);
            likeU.setVisibility(View.GONE);
        }

        public void setProfileImage(String profileImage) {
            Glide.with(context).load(profileImage).into(profile);
        }

        public void setUserText(String userText) {
            userView.setText(userText);
        }

        public void setTime(StringBuilder time) {
            dateView.setText(time);
        }

        public void setPostImage(String postImage) {
            Glide.with(context).load(postImage).into(imageView);
        }


    }
}
