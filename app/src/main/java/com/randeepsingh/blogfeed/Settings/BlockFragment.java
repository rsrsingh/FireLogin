package com.randeepsingh.blogfeed.Settings;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.randeepsingh.blogfeed.Adapter.BlockListAdapter;
import com.randeepsingh.blogfeed.Blog;
import com.randeepsingh.blogfeed.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlockFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Blog> blockList;
    private BlockListAdapter blockListAdapter;
    private FirebaseAuth auth;
    private  String userID;

    public BlockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block, container, false);

        blockList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.block_recycler);
        blockListAdapter = new BlockListAdapter(blockList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(blockListAdapter);
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();


        firebaseFirestore.collection("Users").document(userID).collection("Block").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            final Blog blog = doc.getDocument().toObject(Blog.class);
                            blockList.add(blog);
                            blockListAdapter.notifyDataSetChanged();
                        }

                    }
                }

            }
        });

        return view;
    }

}
