package com.example.randeepsingh.firelogin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPost extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText cmntEditText;
    ImageView btnSend, postImage, btnDelete;
    CircleImageView userProf;
    TextView uName, time;
    String blogPostID;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    ArrayList<Comments> commentList;
    SharedPref sharedPref;
    CommentRecyclerAdapter commentRecyclerAdapter;
    ProgressBar progressBarup;

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
progressBarup=findViewById(R.id.userPost_progress);
progressBarup.setVisibility(View.VISIBLE);
        blogPostID = getIntent().getStringExtra("blog_post_id");
        Log.e("mtest6", "onBindViewHolder: "+blogPostID );
        Log.v("mtest5", "" + blogPostID);
        commentList = new ArrayList<>();
        recyclerView = findViewById(R.id.userPost_recycler);
        cmntEditText = findViewById(R.id.userPost_ed1);
        btnSend = findViewById(R.id.userPost_send);
        postImage = findViewById(R.id.userPost_post);
        btnDelete = findViewById(R.id.userPost_del);
        userProf = findViewById(R.id.userPost_prof);
        uName = findViewById(R.id.userPost_username);
        time = findViewById(R.id.userPost_time);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


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
                            Log.v("bgid", "query called");
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

                    uName.setText(userName);
                    Glide.with(getApplicationContext()).load(thumbUrl).into(userProf);
                    Glide.with(getApplicationContext()).load(postUrl).into(postImage);

progressBarup.setVisibility(View.GONE);
                }
            }
        });


    }
}
