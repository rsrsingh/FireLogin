package com.randeepsingh.firelogin;


import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class homeFragment extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private ArrayList<Blog> postList;
    SharedPref sharedPref;


    private ProgressBar progressBar;
    FirebaseFirestore firebaseFirestore;
    private PostRecyclerAdapter postRecyclerAdapter;
    private FirebaseAuth auth;
    private Toolbar toolbar;


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


        toolbar = view.findViewById(R.id.home_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        setHasOptionsMenu(true);


        postList = new ArrayList<>();


        recyclerView = view.findViewById(R.id.home_view);
        postRecyclerAdapter = new PostRecyclerAdapter(postList);
        progressBar = view.findViewById(R.id.home_progress);
        progressBar.setVisibility(view.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postRecyclerAdapter);
        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
        }

        Query query=firebaseFirestore.collection("Posts").orderBy("Time_stamp", Query.Direction.DESCENDING);
      query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {

                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        String blogPostID = documentChange.getDocument().getId();
                        final Blog blog = documentChange.getDocument().toObject(Blog.class).withID(blogPostID);

                        postList.add(blog);
                        progressBar.setVisibility(GONE);
                        postRecyclerAdapter.notifyDataSetChanged();


                    }

                }

            }
        });


        // inflate the layout using the cloned inflater, not default inflater
        return view;
    }

    public static Fragment newInstance() {
        homeFragment fragment = new homeFragment();
        return fragment;
    }



/*
    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "refreshed", Toast.LENGTH_SHORT).show();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postRecyclerAdapter);
        swipeLayout.setRefreshing(false);

        //   postRecyclerAdapter.notifyDataSetChanged();

    }*/


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);


    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();

        ArrayList<Blog> newPost = new ArrayList<>();


        for (Blog blog : postList) {
            String fname = blog.getFull_name().toLowerCase();
           // Log.v("mhome", "onquery text changed called  " + fname);
            if (fname.contains(newText)) {
                newPost.add(blog);


            }


        }


        postRecyclerAdapter.setFilter(newPost);

        return true;
    }
}



