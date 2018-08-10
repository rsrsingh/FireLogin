package com.randeepsingh.blogfeed;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPost extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText cmntEditText;
    ImageView btnSend, postImage, btnDelete;
    CircleImageView userProf;
    ProgressDialog progressDialog;
    TextView uName, time;
    String blogPostID;
    String userID;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    ArrayList<Comments> commentList;
    SharedPref sharedPref;
    CommentRecyclerAdapter commentRecyclerAdapter;
    ProgressBar progressBarup;
    String postUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            this.setTheme(R.style.DarkTheme);

        } else {
            this.setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);
        progressBarup = findViewById(R.id.userPost_progress);
        progressBarup.setVisibility(View.VISIBLE);
        blogPostID = getIntent().getStringExtra("blog_post_id");
        //  Log.e("mtest6", "onBindViewHolder: " + blogPostID);
        //Log.v("mtest5", "" + blogPostID);
        commentList = new ArrayList<>();
        recyclerView = findViewById(R.id.userPost_recycler);
        cmntEditText = findViewById(R.id.userPost_ed1);
        btnSend = findViewById(R.id.userPost_send);
        postImage = findViewById(R.id.userPost_post);
        btnDelete = findViewById(R.id.userPost_del);
        btnDelete.setVisibility(View.GONE);
        userProf = findViewById(R.id.userPost_prof);
        uName = findViewById(R.id.userPost_username);
        time = findViewById(R.id.userPost_time);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);

        commentRecyclerAdapter = new CommentRecyclerAdapter(commentList, blogPostID);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserPost.this));
        recyclerView.setAdapter(commentRecyclerAdapter);

        //commments Retreving
        Query query = firebaseFirestore.collection("Posts").document(blogPostID).collection("Comments").orderBy("time_stamp", Query.Direction.ASCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String cmntID = doc.getDocument().getId();
                            Comments comments = doc.getDocument().toObject(Comments.class).withID(cmntID);
                            commentList.add(comments);
                            //  progressBar.setVisibility(View.GONE);
                            commentRecyclerAdapter.notifyDataSetChanged();
                            //      Log.v("bgid", "query called");
                        }


                    }

                }

            }
        });


        firebaseFirestore.collection("Posts").document(blogPostID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    String userName = task.getResult().getString("full_name");
                    String thumbUrl = task.getResult().getString("thumb_id");
                    String postUrl = task.getResult().getString("thumb_imageUrl");
                    postUserID = task.getResult().getString("User_id");
                    if (postUserID.equals(userID)) {
                        btnDelete.setVisibility(View.VISIBLE);
                    }
                    Date date = task.getResult().getDate("Time_stamp");
                    //Log.e("mtest9", "onComplete: " + date.toString());

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat("dd MMMM yyyy");
                    final StringBuilder nowMMDDYYYY = new StringBuilder(dateformatMMDDYYYY.format(date));
                    time.setText(nowMMDDYYYY.toString());

                    uName.setText(userName);
                    Glide.with(getApplicationContext()).load(thumbUrl).into(userProf);
                    Glide.with(getApplicationContext()).load(postUrl).into(postImage);

                    progressBarup.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserPost.this, "Some error has occured", Toast.LENGTH_SHORT).show();
            }
        });

        //commments posting
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cmntVal = cmntEditText.getText().toString();
                cmntEditText.setText(null);

                Map<String, Object> cmntMap = new HashMap<>();
                cmntMap.put("comment_value", cmntVal);
                cmntMap.put("user_id", userID);
                cmntMap.put("time_stamp", FieldValue.serverTimestamp());
                cmntMap.put("post_id", blogPostID);

                final ProgressDialog progressDialog = new ProgressDialog(UserPost.this);
                progressDialog.setMessage("Posting Comment");
                progressDialog.show();
                firebaseFirestore.collection("Posts").document(blogPostID).collection("Comments").add(cmntMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(UserPost.this, "Comment Posted", Toast.LENGTH_SHORT).show();
                            commentRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                });

                firebaseFirestore.collection("Users").document(postUserID).collection("Notifications").add(cmntMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        //              Log.v("mnotif2", "notif collection updated");
                    }
                });


            }
        });

        uName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                intent.putExtra("post_user_id", postUserID);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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
                                    //           Log.v("blogtest", "deleted");


                                }
                            });

                            firebaseFirestore.collection("Posts").document(blogPostID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //             Log.v("blogtest", "post deleted");
                                    Toast.makeText(UserPost.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UserPost.this, AccountMain.class));
                                    finish();
                                    progressDialog.dismiss();
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //   Log.v("deletion", "failed to delete post from database");

                        Toast.makeText(UserPost.this, "Failed to delete post", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
