package com.example.randeepsingh.firelogin;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


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
    String coverUrl = null;
    String username = null;
    Toolbar toolbar;


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

        toolbar = view.findViewById(R.id.prof_toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        setHasOptionsMenu(true);


        // Inflate the layout for this fragment


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mImage = view.findViewById(R.id.prof_pic);


        mCover = view.findViewById(R.id.prof_mCover);
        spinner = (ProgressBar) view.findViewById(R.id.progressBar1);
        mNametxt = (TextView) view.findViewById(R.id.prof_name);
        coverImgRef = FirebaseStorage.getInstance().getReference().child("Cover_images");

        spinner.setVisibility(View.VISIBLE);


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
                        Log.e("mkey", " " + e + "  " + mProfid);
                    }
                } else {
                    mNametxt.setText("");
                }

            }
        });


        return view;
    }


    public static Fragment newInstance() {
        profileFragment fragment = new profileFragment();
        return fragment;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                getActivity().finish();
                mAuth.signOut();
                Toast.makeText(getActivity(), "Successfully logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();

                break;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), AccountReg.class));
                break;
            case R.id.action_themes:
                startActivity(new Intent(getActivity(), ThemeActivity.class));
                break;
        }


        return true;
    }
}
