package com.randeepsingh.blogfeed.Home.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.randeepsingh.blogfeed.Adapter.NotifRecyclerAdapter;
import com.randeepsingh.blogfeed.Comments;
import com.randeepsingh.blogfeed.Home.AccountMain;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.SharedPref;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class notifFragment extends Fragment {

  // private AdView adView;
    private SharedPref sharedPref;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private String userID;
    private ArrayList<Comments> notifList;
    private RecyclerView recyclerView;
    private NotifRecyclerAdapter notifRecyclerAdapter;
    private ProgressBar progressBar;
    private Button btnClear;

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


      /*  MobileAds.initialize(getActivity(), "ca-app-pub-5059411314324031/5707326671");
        adView = view.findViewById(R.id.notif_bannerAds);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
*/
        notifList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.notif_recyclerView);
        btnClear = view.findViewById(R.id.notif_clear);
        notifRecyclerAdapter = new NotifRecyclerAdapter(notifList);

        progressBar = view.findViewById(R.id.notif_progress);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(notifRecyclerAdapter);

        Query query = firebaseFirestore.collection("Users").document(userID).collection("Notifications").orderBy("time_stamp", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        Comments notifications = doc.getDocument().toObject(Comments.class);
                        notifList.add(notifications);
                        progressBar.setVisibility(View.GONE);
                        notifRecyclerAdapter.notifyDataSetChanged();
                    }
                } else if (documentSnapshots.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Users").document(userID).collection("Notifications").addSnapshotListener((Activity) getContext(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (!documentSnapshots.isEmpty()) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                String id = doc.getDocument().getId();
                                Log.e("allId ", "onEvent: " + id);
                                firebaseFirestore.collection("Users").document(userID).collection("Notifications").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(getActivity(), "Cleared", Toast.LENGTH_SHORT).show();
                                            //        notifRecyclerAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        notifRecyclerAdapter.notifyDataSetChanged();
                                        Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }

                    }
                });
                getActivity().startActivity(new Intent(getContext(), AccountMain.class));
                getActivity().finish();
                Toast.makeText(getActivity(), "Cleared", Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }

    public static Fragment newInstance() {
        notifFragment fragment = new notifFragment();
        return fragment;

    }
}
