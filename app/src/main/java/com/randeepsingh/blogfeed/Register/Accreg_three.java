package com.randeepsingh.blogfeed.Register;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.randeepsingh.blogfeed.Home.AccountMain;
import com.randeepsingh.blogfeed.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Accreg_three extends Fragment {


    private ImageView imageView;
    private CircleImageView circleImageView;
    private Bundle bundle;
    private String thumb_url;
    private String cover_url;
    private Button btnNext;
    private String userId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirebaseFirestore;
    private EditText username;
    private ProgressBar progressBar;


    public Accreg_three() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_accreg_three, container, false);
        bundle = this.getArguments();
        thumb_url = bundle.get("thumb_url").toString();
        cover_url = bundle.get("cover_url").toString();

//        Log.v("Accreg_3","thumb:  "+thumb_url);
        //      Log.v("Accreg_3","cover:  "+cover_url);

        progressBar = view.findViewById(R.id.accreg3_progress);
        imageView = view.findViewById(R.id.Accreg3_cover);
        circleImageView = view.findViewById(R.id.Accreg3_profile);
        btnNext = view.findViewById(R.id.AccReg3_btn);
        username = view.findViewById(R.id.Accreg3_fname);

        mfirebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        username.setVisibility(view.GONE);
        progressBar.setVisibility(view.VISIBLE);


        try {

            Glide.with(getActivity()).load(cover_url).into(imageView);
        } catch (Exception e) {

        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Glide.with(getActivity()).load(thumb_url).into(circleImageView);

                } catch (Exception e) {
                }
                username.setVisibility(view.VISIBLE);

                progressBar.setVisibility(view.GONE);

            }
        }, 1500);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDetails();


            }
        });


        return view;
    }

    private void updateDetails() {
        String fullName;
        fullName = username.getText().toString();


        Map<String, Object> user = new HashMap<>();

        user.put("full_name", fullName);
        user.put("thumb_id", thumb_url);
        user.put("cover_id", cover_url);
        //Log.v("mkeythumb",""+thumb_downloadUrl.toString());
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("updating...");
        progressDialog.show();
        mfirebaseFirestore.collection("Users").document(userId).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                String tokenID = FirebaseInstanceId.getInstance().getToken();
                String user_id = mAuth.getCurrentUser().getUid();
                //                    Log.v("tokenID"," "+tokenID+"  "+user_id);
                Map<String, Object> map = new HashMap<>();
                map.put("token_id", tokenID);
                mfirebaseFirestore.collection("Users").document(user_id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        progressDialog.dismiss();
                        startActivity(new Intent(getActivity(), AccountMain.class));
                        Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

                Toast.makeText(getActivity(), "Failed to update", Toast.LENGTH_SHORT).show();
            }
        });

        final Map<String, Object> map2 = new HashMap<>();
        map2.put("time_stamp", FieldValue.serverTimestamp());
        mfirebaseFirestore.collection("Users").document(userId).collection("Following").document(userId).set(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        mfirebaseFirestore.collection("Users").document(userId).collection("Following").document("wm2fqzaT11XhmdHNdnwIcByXVkm1").set(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public static Fragment newInstance() {
        Accreg_three fragment = new Accreg_three();
        return fragment;

    }
}
