package com.randeepsingh.blogfeed;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


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
    SharedPref sharedPref;
    private CircleImageView mImage;
    Bitmap cover_Bitmap = null;
    private ImageView mCover;
    private String mProfid = null;
    private String currentUser;
    Uri cover_downloadUrl = null;
    private ProgressBar spinner;
    TextView mNametxt;
    ImageView dots_menu;
    String coverUrl = null;
    String username = null;

    ArrayList<ProfileViewList> profileList = new ArrayList<>();
    ;
    ProfileRecyclerAdapter profileRecyclerAdapter;
    RecyclerView recyclerView;
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
                        switch (menuItem.getItemId()){
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