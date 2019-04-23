package com.randeepsingh.blogfeed.Home.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.randeepsingh.blogfeed.Adapter.ProfileRecyclerAdapter;
import com.randeepsingh.blogfeed.ProfileViewList;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.Settings.SettingsMain;
import com.randeepsingh.blogfeed.SharedPref;
import com.randeepsingh.blogfeed.Welcome.MainActivity;
import com.randeepsingh.blogfeed.postid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class profileFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private Uri mainImageURI;
    private StorageReference coverImgRef;
    private SharedPref sharedPref;
    private CircleImageView mImage;
    private Bitmap cover_Bitmap = null;
    private ImageView mCover;
    private String mProfid = null;
    private String currentUser;
    private Uri cover_downloadUrl = null;
    private ProgressBar spinner;
    private  TextView mNametxt;
    private ImageView dots_menu;
    private String coverUrl = null;
    private String username = null;
    private  TextView followersCount, followingCount;

    private ArrayList<ProfileViewList> profileList = new ArrayList<>();

    private ProfileRecyclerAdapter profileRecyclerAdapter;
    private  RecyclerView recyclerView;
    private String post_id;
    private ProfileViewList profileViewList;

    public profileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPref = new SharedPref(getActivity());

        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.DarkTheme);
        } else if (sharedPref.loadNightModeState() == false) {
            getActivity().setTheme(R.style.AppTheme);
        }

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        // Inflate the layout for this fragment



        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        profileViewList = new ProfileViewList();
        recyclerView = view.findViewById(R.id.profile_recycler);
        dots_menu = view.findViewById(R.id.profile_dots);
        mImage = view.findViewById(R.id.prof_pic);
        mCover = view.findViewById(R.id.prof_mCover);
        spinner = (ProgressBar) view.findViewById(R.id.progressBar1);
        followersCount = view.findViewById(R.id.prof_followersCount);
        followingCount = view.findViewById(R.id.prof_followingCount);
        mNametxt = (TextView) view.findViewById(R.id.prof_name);
        coverImgRef = FirebaseStorage.getInstance().getReference().child("Cover_images");

        spinner.setVisibility(View.VISIBLE);
        postid pd = new postid();
        post_id = pd.getPostid();

        dots_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("profmenu", "onClick: ");
                Context wrapper;
                if (sharedPref.loadNightModeState()) {
                    wrapper = new ContextThemeWrapper(getContext(), R.style.popUpThemeDark);
                } else {
                    wrapper = new ContextThemeWrapper(getContext(), R.style.popUpThemeLight);
                }

                final PopupMenu popupMenu = new PopupMenu(wrapper, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.main_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_logout:
                                Map<String, Object> tokenRemoveMap = new HashMap<>();
                                tokenRemoveMap.put("token_id", "");
                                firebaseFirestore.collection("Users").document(currentUser).update(tokenRemoveMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        getActivity().finish();
                                        mAuth.signOut();
                                        Toast.makeText(getActivity(), "Successfully logged out", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), MainActivity.class));
                                        getActivity().finish();
                                    }
                                });


                                break;
                            case R.id.action_settings:
                                startActivity(new Intent(getActivity(), SettingsMain.class));
                                break;

                        }

                        return false;
                    }
                });
                popupMenu.show();

            }
        });

        //followers count
        firebaseFirestore.collection("Users").document(currentUser).collection("Followers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                try {
                    if (!documentSnapshots.isEmpty()) {
                        int count = documentSnapshots.size();
                        followersCount.setText("" + count);
                    } else {

                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });

        //following count
        firebaseFirestore.collection("Users").document(currentUser).collection("Following").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                try {
                    if (!documentSnapshots.isEmpty()) {
                        int count = documentSnapshots.size();
                        followingCount.setText("" + count);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        firebaseFirestore.collection("Users").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()) {


                    username = task.getResult().getString("full_name");
                    coverUrl = task.getResult().getString("cover_id");
                    mProfid = task.getResult().getString("thumb_id");
                    mNametxt.setText(username);
                    try {
                        Glide.with(profileFragment.this).load(mProfid).into(mImage);
                        Glide.with(profileFragment.this).load(coverUrl).into(mCover);

                        spinner.setVisibility(View.GONE);

                    } catch (Exception e) {
                        //Log.e("mkey", " " + e + "  " + mProfid);
                    }
                } else {
                    mNametxt.setText("");
                }

            }
        });
        profileRecyclerAdapter = new ProfileRecyclerAdapter(profileList);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(profileRecyclerAdapter);

        Query query = firebaseFirestore.collection("Posts").orderBy("Time_stamp", Query.Direction.DESCENDING);


        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        String post_user_id = doc.getDocument().getString("User_id");
                        String post_thumb_url = doc.getDocument().getString("thumb_imageUrl");
                        String blog_post_id = doc.getDocument().getId();
                        //Log.v("profile_test", "cuser " + currentUser + " post_user: " + post_user_id + " thumb_url: " + post_thumb_url);
                        if (post_user_id.equals(currentUser)) {
                            profileList.add(new ProfileViewList(blog_post_id, post_thumb_url));
                            profileViewList.setBlogPostrID(post_id);
                            profileRecyclerAdapter.notifyDataSetChanged();

                        }

                    }
                } else {
                    // Log.v("profilefrag", "some error");
                }

            }
        });


        return view;
    }


    public static Fragment newInstance() {
        profileFragment fragment = new profileFragment();
        return fragment;

    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                Map<String, Object> tokenRemoveMap = new HashMap<>();
                tokenRemoveMap.put("token_id", "");
                firebaseFirestore.collection("Users").document(currentUser).update(tokenRemoveMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getActivity().finish();
                        mAuth.signOut();
                        Toast.makeText(getActivity(), "Successfully logged out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                });


                break;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsMain.class));
                break;
        }


        return true;
    }*/
}
