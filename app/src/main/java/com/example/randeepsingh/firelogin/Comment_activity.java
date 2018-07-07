package com.example.randeepsingh.firelogin;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Comment_activity extends AppCompatActivity {


    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    String userID;


    ImageView cmntpost;
    EditText cmntValue;
    RecyclerView cRecyclerView;
    ArrayList<Comments> commentList;
    CommentRecyclerAdapter commentRecyclerAdapter;
    String blogPostID;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_activity);

        Log.v("bgid", "oncrerate called");
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        commentList = new ArrayList<>();
        userID = auth.getCurrentUser().getUid();
        progressBar = findViewById(R.id.comment_progress);
        progressBar.setVisibility(View.VISIBLE);
        cmntpost = findViewById(R.id.comment_send);
        cmntValue = findViewById(R.id.comment_ed1);
        // Log.v("bgid",""+blogPostID);
        blogPostID = getIntent().getStringExtra("blog_post_id");

        // Log.v("bgidd",""+blogPostID);
        cRecyclerView = findViewById(R.id.comment_recycler);


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

                            Log.v("bgid", "query called");
                        }

                    }

                }
            }
        });
        commentRecyclerAdapter = new CommentRecyclerAdapter(commentList, blogPostID);
        cRecyclerView.setLayoutManager(new LinearLayoutManager(Comment_activity.this));
        cRecyclerView.setAdapter(commentRecyclerAdapter);


        Log.v("bgid", "recyclerview set");


        //commments posting
        cmntpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cmntVal = cmntValue.getText().toString();
                cmntValue.setText(null);

                Map<String, Object> cmntMap = new HashMap<>();
                cmntMap.put("comment_value", cmntVal);
                cmntMap.put("user_id", userID);
                cmntMap.put("time_stamp", FieldValue.serverTimestamp());

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
                });

            }
        });


    }
}
