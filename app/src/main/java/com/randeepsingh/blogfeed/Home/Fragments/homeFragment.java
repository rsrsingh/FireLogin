package com.randeepsingh.blogfeed.Home.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.randeepsingh.blogfeed.Adapter.PostRecyclerAdapter;
import com.randeepsingh.blogfeed.Blog;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.SharedPref;

import java.util.ArrayList;

import javax.annotation.Nullable;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class homeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Blog> postList;
    private SharedPref sharedPref;
    private boolean CHECK_FUNCTION = false;
    private String userID;
    private FirebaseFirestore firebaseFirestore;
    private PostRecyclerAdapter postRecyclerAdapter;
    private FirebaseAuth auth;
    private String followingUserId;

    // private Boolean isfirstPageLoad = true;

    private DocumentSnapshot lastVisible;


    public homeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPref = new SharedPref(getActivity());

        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.DarkTheme);
        } else if (sharedPref.loadNightModeState() == false) {
            getActivity().setTheme(R.style.AppTheme);
        }


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.e("mprocess", "onCreateView: ");

        CHECK_FUNCTION = true;

        postList = new ArrayList<>();


        recyclerView = view.findViewById(R.id.home_view);
        postRecyclerAdapter = new PostRecyclerAdapter(postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postRecyclerAdapter);
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();


        if (auth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
        }

        addItems();

        return view;
    }

    private void addItems() {

        firebaseFirestore.collection("Users").document(userID).collection("Following").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (final DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        followingUserId = documentChange.getDocument().getId();
                        Log.e("followingkey", "onEvent: " + followingUserId);

                        Query firstQuery = firebaseFirestore.collection("Posts")
                                .orderBy("Time_stamp", Query.Direction.DESCENDING)
                                .whereEqualTo("User_id", followingUserId);

                        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                try {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                                String blogPostID = doc.getDocument().getId();
                                                Log.e("followingkey", "onEvent blogpostid: " + blogPostID);
                                                final Blog blog = doc.getDocument().toObject(Blog.class).withID(blogPostID);
                                                postList.add(blog);
                                                postRecyclerAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });


                    }
                }
            }
        });

    }

    public static Fragment newInstance() {
        homeFragment fragment = new homeFragment();
        return fragment;
    }
}



