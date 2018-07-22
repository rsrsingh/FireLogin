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
import android.widget.ProgressBar;
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

    SharedPref sharedPref;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    String userID;
    ArrayList<Comments> notifList;
    RecyclerView recyclerView;
    NotifRecyclerAdapter notifRecyclerAdapter;
    ProgressBar progressBar;

    public notifFragment() {
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

        View view = inflater.inflate(R.layout.fragment_notif, container, false);

        notifList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.notif_recyclerView);

        notifRecyclerAdapter = new NotifRecyclerAdapter(notifList);

        progressBar=view.findViewById(R.id.notif_progress);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(notifRecyclerAdapter);

        Query query =firebaseFirestore.collection("Users").document(userID).collection("Notifications").orderBy("time_stamp", Query.Direction.DESCENDING);
query.addSnapshotListener(new EventListener<QuerySnapshot>() {
    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

        if (!documentSnapshots.isEmpty()){
            for(DocumentChange doc: documentSnapshots.getDocumentChanges()){
                Comments notifications=doc.getDocument().toObject(Comments.class);
                notifList.add(notifications);
                progressBar.setVisibility(View.GONE);
                notifRecyclerAdapter.notifyDataSetChanged();
            }
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
