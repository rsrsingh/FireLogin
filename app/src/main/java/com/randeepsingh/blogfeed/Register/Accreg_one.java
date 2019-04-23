package com.randeepsingh.blogfeed.Register;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.Settings.SettingsFragment;
import com.randeepsingh.blogfeed.SharedPref;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


/**
 * A simple {@link Fragment} subclass.
 */
public class Accreg_one extends Fragment {

    private CircleImageView circleImageView;
    private StorageReference mStorageRef;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private String mUserid;
    private Fragment fragment = null;
    private Bitmap thumb_Bitmap = null;
    private Uri mainImageUri = null;
    private StorageReference thumbImgRef;
    private Uri thumb_downloadUrl = null;
    private Button btnNext, btnUpdate;
    private Bundle bundle;
    private FirebaseFirestore mfirebaseFirestore;
    private SharedPref sharedPref;
    private ProgressBar progressBar;

    public Accreg_one() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedPref = new SharedPref(getActivity());

        if (sharedPref.loadNightModeState() == true) {
            getActivity().setTheme(R.style.DarkTheme);
        } else if (sharedPref.loadNightModeState() == false) {
            getActivity().setTheme(R.style.AppTheme);
        }


        final View view = inflater.inflate(R.layout.fragment_accreg_one, container, false);
        sharedPref = new SharedPref(getActivity());
        circleImageView = view.findViewById(R.id.Accreg1_profile);
        circleImageView.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mUserid = mAuth.getCurrentUser().getUid();
        thumbImgRef = FirebaseStorage.getInstance().getReference().child("Thumb_images");
        btnNext = view.findViewById(R.id.AccReg1_btn);
        btnUpdate = view.findViewById(R.id.AccReg1_update);
        btnUpdate.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.AccReg1_progress);
        progressBar.setVisibility(View.VISIBLE);
        mfirebaseFirestore = FirebaseFirestore.getInstance();
        bundle = new Bundle();


        firebaseFirestore.collection("Users").document(mUserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    btnUpdate.setVisibility(View.VISIBLE);
                    String thumbID = task.getResult().getString("thumb_id");
                    Glide.with(getActivity()).load(thumbID).into(circleImageView);
                    progressBar.setVisibility(View.GONE);
                    circleImageView.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    circleImageView.setVisibility(View.VISIBLE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Some error has occured", Toast.LENGTH_SHORT).show();
            }
        });


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainImageUri == null) {
                    Toast.makeText(getActivity(), "Please upload a display picture", Toast.LENGTH_SHORT).show();
                }
                if (thumb_downloadUrl == null) {
                    Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
                } else if (thumb_downloadUrl != null) {
                    final Map update = new HashMap();
                    update.put("thumb_id", thumb_downloadUrl.toString());
                    firebaseFirestore.collection("Users").document(mUserid).update(update).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            //   Log.v("updateTest", "on complete for users");
                            //   Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                            fragment = SettingsFragment.newInstance();
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.settingsMain_frame, fragment);
                            ft.commit();
                            firebaseFirestore.collection("Posts").addSnapshotListener((Activity) getContext(), new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                    // Log.v("updateTest", "snapshot for Posts");
                                    if (!documentSnapshots.isEmpty()) {
                                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                            final String post_id = doc.getDocument().getId();
                                            // Log.v("updateTest", "post id: " + post_id);
                                            firebaseFirestore.collection("Posts").document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.getResult().exists()) {
                                                        // Log.v("updateTest", "on complete inside posts");
                                                        String user_ID = task.getResult().getString("User_id");
                                                        if (mUserid.equals(user_ID)) {
                                                            firebaseFirestore.collection("Posts").document(post_id).update(update).addOnCompleteListener(new OnCompleteListener() {
                                                                @Override
                                                                public void onComplete(@NonNull Task task) {
                                                                    if (task.isSuccessful()) {
                                                                        //               Log.v("updateTest", "on complete for update posts");
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), "Some error has occured", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Some error has occured", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mainImageUri == null) {
                    Toast.makeText(getActivity(), "Please upload a display picture", Toast.LENGTH_SHORT).show();
                }
                if (thumb_downloadUrl == null) {
                    Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
                } else if (thumb_downloadUrl != null) {
                    Accreg_two accreg_two = new Accreg_two();
                    accreg_two.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.Accreg_frame, accreg_two).commit();
                }
            }
        });


        return view;
    }

    private void ImagePicker() {

        //   Log.e("hi", "imagpicker");

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getContext(), this);

    }

    public static Accreg_one newInstance() {
        Accreg_one fragment = new Accreg_one();
        return fragment;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {

                mainImageUri = result.getUri();
                //         Log.v("mkeyreg", "mianuri= " + mainImageUri);
                //  circleImageView.setImageURI(mainImageUri);

                File thumb_filePathUri = new File(mainImageUri.getPath());
                try {
                    thumb_Bitmap = new Compressor(getActivity()).setMaxWidth(200).setMaxHeight(200).setQuality(50).compressToBitmap(thumb_filePathUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_Bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                final StorageReference thumb_filePath = thumbImgRef.child(mUserid + ".jpg");

                final ProgressDialog mprogressDialog = new ProgressDialog(getActivity());
                mprogressDialog.setMessage("Please wait");
                mprogressDialog.show();

                thumb_filePath.putBytes(thumb_byte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                       // thumb_downloadUrl = taskSnapshot.getDownloadUrl(); ///////////////////////////////////////////////enable this
                        //               Log.v("mkey", "thumb download url: " + thumb_downloadUrl);

                        circleImageView.setImageURI(mainImageUri);
                        bundle.putString("thumb_url", thumb_downloadUrl.toString());


                        mprogressDialog.dismiss();
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }

    private void updateDetails() {


    }


}
