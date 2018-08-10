package com.randeepsingh.blogfeed;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    SharedPref sharedPref;
    RecyclerView recyclerView;
    SearchRecyclerAdapter searchRecyclerAdapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String UserId;
    ArrayList<User> searchList;
    TextInputEditText textInputEditText;

    public SearchFragment() {
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


        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchList = new ArrayList<>();
        textInputEditText = view.findViewById(R.id.searchFrag_ed1);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.searchFrag_recycler);
        searchRecyclerAdapter = new SearchRecyclerAdapter(searchList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(searchRecyclerAdapter);

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (i2 > 0) {
                    String name = StringUtils.capitalize(charSequence.toString());
                    Log.e("searchkey", "onTextChanged: " + name);
                    CollectionReference ref = firebaseFirestore.collection("Users");
                    //this is the workaround for "LIKE" query of SQL

                    Query query = ref.orderBy("full_name").startAt(charSequence.toString()).endAt(charSequence.toString() + "\uf8ff");
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            try {
                                if (task.isSuccessful()) {
                                    //to remove items from the list every time before loop
                                    searchList.removeAll(searchList);

                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        String user_id = documentSnapshot.getId();
                                        User user = documentSnapshot.toObject(User.class).withID(user_id);
                                        searchList.add(user);
                                        String user_name = documentSnapshot.getString("full_name");
                                        Log.e("searchkey", "Name " + user_name + " user id " + user_id);
                                        searchRecyclerAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Log.e("Search", "No data");
                                    searchList.removeAll(searchList);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }

    public static Fragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;

    }

}
