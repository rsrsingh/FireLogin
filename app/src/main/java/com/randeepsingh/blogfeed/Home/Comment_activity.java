package com.randeepsingh.blogfeed.Home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.randeepsingh.blogfeed.Adapter.CommentRecyclerAdapter;
import com.randeepsingh.blogfeed.Comments;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.SharedPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Comment_activity extends AppCompatActivity {


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private  String userID;
    private SharedPref sharedPref;

    private  ImageView cmntpost;
    private EditText cmntValue;
    private RecyclerView cRecyclerView;
    private ArrayList<Comments> commentList;
    private CommentRecyclerAdapter commentRecyclerAdapter;
    private String blogPostID;
    private TextView caption;
    private ProgressBar progressBar;
    private String postUser;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);

        if (sharedPref.loadNightModeState() == true) {
            this.setTheme(R.style.DarkTheme);

        } else {
            this.setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_activity);

        // Log.v("bgid", "oncrerate called");
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        commentList = new ArrayList<>();
        userID = auth.getCurrentUser().getUid();
        progressBar = findViewById(R.id.comments_progress);
        progressBar.setVisibility(View.VISIBLE);
        caption = findViewById(R.id.comments_caption);
        caption.setText("");
        cmntpost = findViewById(R.id.comment_send);
        cmntValue = findViewById(R.id.comment_ed1);
        //Log.v("bgid",""+blogPostID);
        blogPostID = getIntent().getStringExtra("blog_post_id");
        postUser = getIntent().getStringExtra("postUserID");


        // Log.v("bgidd",""+blogPostID);
        cRecyclerView = findViewById(R.id.comment_recycler);
        progressBar.setVisibility(View.VISIBLE);

        //Retrieving caption
        firebaseFirestore.collection("Posts").document(blogPostID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    String postCaption = task.getResult().getString("description_value");
                    // Log.v("captions", "" + postCaption);
                    caption.setText(postCaption);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Comment_activity.this, "Some error occured", Toast.LENGTH_SHORT).show();
            }
        });

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
                            progressBar.setVisibility(View.GONE);
                            //  progressBar.setVisibility(View.GONE);

                            //                 Log.v("bgid", "query called");
                        }
                        commentRecyclerAdapter = new CommentRecyclerAdapter(commentList, blogPostID);
                        cRecyclerView.setLayoutManager(new LinearLayoutManager(Comment_activity.this));
                        cRecyclerView.setAdapter(commentRecyclerAdapter);

                    }

                }
                progressBar.setVisibility(View.GONE);
            }
        });


        //Log.v("bgid", "recyclerview set");


        //commments posting
        cmntpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cmntVal = cmntValue.getText().toString();
                
                if (cmntVal.equals("") || cmntVal.equals(null)) {
                    Toast.makeText(Comment_activity.this, "Please write a comment", Toast.LENGTH_SHORT).show();
                }
                else{

                    cmntValue.setText(null);

                    Map<String, Object> cmntMap = new HashMap<>();
                    cmntMap.put("comment_value", cmntVal);
                    cmntMap.put("user_id", userID);
                    cmntMap.put("time_stamp", FieldValue.serverTimestamp());
                    cmntMap.put("post_id", blogPostID);

                    final ProgressDialog progressDialog = new ProgressDialog(Comment_activity.this);
                    progressDialog.setMessage("Posting Comment");
                    progressDialog.show();
                    firebaseFirestore.collection("Posts").document(blogPostID).collection("Comments").add(cmntMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(Comment_activity.this, "Comment Posted", Toast.LENGTH_SHORT).show();
                                commentRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Comment_activity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    });

                    firebaseFirestore.collection("Users").document(postUser).collection("Notifications").add(cmntMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            //              Log.v("mnotif2", "notif collection updated");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Comment_activity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    });



                }
                

            }
        });


    }
}
