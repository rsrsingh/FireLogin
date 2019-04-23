package com.randeepsingh.blogfeed.Home.Fragments;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.randeepsingh.blogfeed.Adapter.SearchRecyclerAdapter;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.SharedPref;
import com.randeepsingh.blogfeed.User;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {



   // private AdView adView;
    private SharedPref sharedPref;
    private RecyclerView recyclerView;
    private SearchRecyclerAdapter searchRecyclerAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String UserId;
    private ArrayList<User> searchList;
    private TextInputEditText textInputEditText;

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

      /*  MobileAds.initialize(getActivity(), "ca-app-pub-5059411314324031/1095479460");
        adView = view.findViewById(R.id.search_bannerAds);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
*/

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
