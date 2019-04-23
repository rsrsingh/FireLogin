package com.randeepsingh.blogfeed.Home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.randeepsingh.blogfeed.Adapter.ProfileRecyclerAdapter;
import com.randeepsingh.blogfeed.ProfileViewList;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.SharedPref;
import com.randeepsingh.blogfeed.postid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private ImageView coverImage;
    private CircleImageView profImage;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private ArrayList<ProfileViewList> postList = new ArrayList<>();
    private TextView uName;
    private String userID;
    private SharedPref sharedPref;
    private String postUserID;
    private TextView followingCount, followerCount;
    private ProfileRecyclerAdapter profileRecyclerAdapter;
    private String username, thumbURL, coverURL;
    private ImageView followAdd, followMinus;

    private String post_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);

        if (sharedPref.loadNightModeState() == true) {
            this.setTheme(R.style.DarkTheme);

        } else {
            this.setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        postUserID = getIntent().getStringExtra("post_user_id");
        // Log.e("mtest6", "onCreate: " + postUserID);
        coverImage = findViewById(R.id.userProf_mCover);
        profImage = findViewById(R.id.userProf_pic);
        recyclerView = findViewById(R.id.userProf_recycler);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        uName = findViewById(R.id.userProf_name);

        followAdd = findViewById(R.id.userPorf_followAddBtn);
        followMinus = findViewById(R.id.userPorf_followMinusBtn);
        followAdd.setVisibility(View.GONE);
        followMinus.setVisibility(View.GONE);

        followingCount = findViewById(R.id.User_prof_followingCount);
        followerCount = findViewById(R.id.User_prof_followersCount);
        userID = auth.getCurrentUser().getUid();
        postid pd = new postid();
        post_id = pd.getPostid();

        //follow check
        firebaseFirestore.collection("Users").document(postUserID).collection("Followers").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    followMinus.setVisibility(View.VISIBLE);
                } else {
                    followAdd.setVisibility(View.VISIBLE);
                }
            }
        });


        //getting user info
        firebaseFirestore.collection("Users").document(postUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    username = task.getResult().getString("full_name");
                    thumbURL = task.getResult().getString("thumb_id");
                    coverURL = task.getResult().getString("cover_id");
                    uName.setText(username);
                    try {
                        Glide.with(getApplicationContext()).load(thumbURL).into(profImage);
                        Glide.with(getApplicationContext()).load(coverURL).into(coverImage);
                    } catch (Exception e) {

                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Some error has occured", Toast.LENGTH_SHORT).show();
            }
        });

        profileRecyclerAdapter = new ProfileRecyclerAdapter(postList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(profileRecyclerAdapter);


        //getting user posts
        Query query = firebaseFirestore.collection("Posts").orderBy("Time_stamp", Query.Direction.DESCENDING);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        String userID = doc.getDocument().getString("User_id");
                        String post_thumb_url = doc.getDocument().getString("thumb_imageUrl");
                        String blog_post_id = doc.getDocument().getId();
                        if (postUserID.equals(userID)) {
                            postList.add(new ProfileViewList(blog_post_id, post_thumb_url));

                            profileRecyclerAdapter.notifyDataSetChanged();


                        }
                    }
                }

            }
        });

        //following feature
        followAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final ProgressDialog progressDialog = new ProgressDialog(UserProfile.this);
                progressDialog.setMessage("PLease Wait...");
                progressDialog.show();

                final Map<String, Object> map = new HashMap<>();
                map.put("time_stamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Users").document(userID).collection("Following").document(postUserID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Log.e("followKey", "userID success ");
                        firebaseFirestore.collection("Users").document(postUserID).collection("Followers").document(userID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.e("followKey", "post user success ");
                                followAdd.setVisibility(View.GONE);
                                followMinus.setVisibility(View.VISIBLE);

                            }
                        });

                        Toast.makeText(UserProfile.this, "Successful", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfile.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                });


            }
        });

        //unfollow feature
        followMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(UserProfile.this);
                progressDialog.setMessage("PLease Wait...");
                progressDialog.show();
                firebaseFirestore.collection("Users").document(userID).collection("Following").document(postUserID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        firebaseFirestore.collection("Users").document(postUserID).collection("Followers").document(userID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(UserProfile.this, "Unfollowed successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                followMinus.setVisibility(View.GONE);
                                followAdd.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserProfile.this, "Some error occured", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfile.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });


        //followers count
        firebaseFirestore.collection("Users/" + postUserID + "/Followers").addSnapshotListener(UserProfile.this, new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                try {
                    if (!documentSnapshots.isEmpty()) {
                        int count = documentSnapshots.size();
                        Log.e("followKey", "onEvent: count follower: " + count);
                        followerCount.setText("" + count);
                    } else if (documentSnapshots.isEmpty()) {
                        // followerCount.setText("0");
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        //following count
        firebaseFirestore.collection("Users/" + postUserID + "/Following").addSnapshotListener(UserProfile.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                try {
                    if (!documentSnapshots.isEmpty()) {
                        int count2 = documentSnapshots.size();
                        Log.e("followKey", "onEvent: count following: " + count2);

                        followingCount.setText("" + count2);
                    } else if (documentSnapshots.isEmpty()) {
                        //   followingCount.setText("0");
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });

    }
}
