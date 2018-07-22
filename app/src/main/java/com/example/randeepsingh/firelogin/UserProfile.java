package com.example.randeepsingh.firelogin;

import android.hardware.camera2.params.BlackLevelPattern;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    ImageView coverImage;
    CircleImageView profImage;
    RecyclerView recyclerView;
    Toolbar toolbar;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    ArrayList<ProfileViewList> postList = new ArrayList<>();
    TextView uName;
    SharedPref sharedPref;
    String postUserID;
    ProfileRecyclerAdapter profileRecyclerAdapter;
    String username, thumbURL, coverURL;

    String post_id;

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
        Log.e("mtest6", "onCreate: " + postUserID);
        coverImage = findViewById(R.id.userProf_mCover);
        profImage = findViewById(R.id.userProf_pic);
        recyclerView = findViewById(R.id.userProf_recycler);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        uName = findViewById(R.id.userProf_name);

        postid pd = new postid();
        post_id = pd.getPostid();

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


    }
}
