package com.example.randeepsingh.firelogin;


import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class notifFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    String userID;
    ArrayList<Comments> notifList;
    RecyclerView recyclerView;
    NotifRecyclerAdapter notifRecyclerAdapter;

    public notifFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notif, container, false);

        notifList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.notif_recyclerView);

        notifRecyclerAdapter = new NotifRecyclerAdapter(notifList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(notifRecyclerAdapter);

        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPosTID = doc.getDocument().getId();

                            String blogUserID = doc.getDocument().getString("User_id");
                            Log.v("mnotif_test", "" + blogPosTID);


                            if (userID.equals(blogUserID)) {
                                Log.v("mkey3", "" + blogUserID);

                                String cPost_id = doc.getDocument().getId();
                                /*final User user = doc.getDocument().toObject(User.class);*/
                                Log.v("mkey3", "  cpost  " + cPost_id);

                                Query query = firebaseFirestore.collection("Posts").document(cPost_id).collection("Comments").orderBy("time_stamp", Query.Direction.DESCENDING);
                                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        if (!documentSnapshots.isEmpty()) {
                                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                                    final String cmntUser = doc.getDocument().getString("user_id");

                                                    Log.v("userTest", "" + cmntUser);
                                                    String cmntID = doc.getDocument().getId();
                                                    final Comments comments = doc.getDocument().toObject(Comments.class).withID(cmntID);
                                                    notifList.add(comments);

                                                    notifRecyclerAdapter.notifyDataSetChanged();

                                                    //  progressBar.setVisibility(View.GONE);

                                                }


                                            }

                                        }
                                    }
                                });

                                Log.v("notifTest", "user id: " + userID + "   post: " + cPost_id);
                            }

                        }


                    }
                } else {
                    Toast.makeText(getContext(), "No posts..", Toast.LENGTH_LONG).show();
                }

            }
        });


        return view;

    }

    public static Fragment newInstance() {
        notifFragment fragment = new notifFragment();
        return fragment;

    }
}
