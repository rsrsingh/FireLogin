package com.randeepsingh.blogfeed.Register;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
public class Accreg_two extends Fragment {


    private String thumb_url;
    private ImageView imageView;
    private String mUserid;
    private Bitmap cover_Bitmap = null;
    private Uri coverImageUri = null;
    private Fragment fragment = null;
    private StorageReference coverImgRef;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private StorageReference mStorageRef;
    private Uri cover_downloadUrl = null;
    private Button btnNext, btnUpdate;
    private Bundle bundle2;
    private Bundle bundle;
    private CircleImageView circleImageView;
    private SharedPref sharedPref;
    private FirebaseFirestore firebaseFirestore;

    public Accreg_two() {
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

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_accreg_two, container, false);


        firebaseFirestore = FirebaseFirestore.getInstance();
        imageView = view.findViewById(R.id.Accreg2_cover);
        imageView.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        mUserid = mAuth.getCurrentUser().getUid();
        coverImgRef = FirebaseStorage.getInstance().getReference().child("Cover_images");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        btnNext = view.findViewById(R.id.AccReg2_btn);
        btnUpdate = view.findViewById(R.id.AccReg2_update);
        bundle2 = new Bundle();
        btnNext.setVisibility(view.GONE);
        btnUpdate.setVisibility(View.GONE);
        circleImageView = view.findViewById(R.id.Accreg2_profile);
        circleImageView.setVisibility(View.GONE);
        bundle = this.getArguments();
        progressBar = view.findViewById(R.id.accreg2_progress);


        progressBar.setVisibility(view.VISIBLE);

        firebaseFirestore.collection("Users").document(mUserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    String profID = task.getResult().getString("thumb_id");
                    String coverID = task.getResult().getString("cover_id");
                    Glide.with(getActivity()).load(profID).into(circleImageView);
                    Glide.with(getActivity()).load(coverID).into(imageView);
                    imageView.setVisibility(View.VISIBLE);
                    circleImageView.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.GONE);


                    btnUpdate.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.add_cover);
                    thumb_url = bundle.get("thumb_url").toString();


                    try {

                        Glide.with(getActivity()).load(thumb_url).into(circleImageView);
                        circleImageView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);


                    } catch (Exception e) {

                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Some error has occured", Toast.LENGTH_SHORT).show();

            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (coverImageUri == null) {
                    Toast.makeText(getActivity(), "Please upload a cover picture", Toast.LENGTH_SHORT).show();
                }
                if (cover_downloadUrl == null) {
                    Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
                } else if (cover_downloadUrl != null) {
                    Accreg_three accreg_three = new Accreg_three();
                    accreg_three.setArguments(bundle2);
                    getFragmentManager().beginTransaction().replace(R.id.Accreg_frame, accreg_three).commit();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (coverImageUri == null) {
                    Toast.makeText(getActivity(), "Please upload a display picture", Toast.LENGTH_SHORT).show();
                }
                if (cover_downloadUrl == null) {
                    Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
                } else if (cover_downloadUrl != null) {

                    Map update = new HashMap();
                    update.put("cover_id", cover_downloadUrl.toString());
                    firebaseFirestore.collection("Users").document(mUserid).update(update).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(getActivity(), "Updated Succcessfully", Toast.LENGTH_SHORT).show();
                            fragment = SettingsFragment.newInstance();
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.settingsMain_frame, fragment);
                            ft.commit();

                        }
                    });
                }


            }
        });


        return view;
    }

    private void ImagePicker() {

        //  Log.e("hi", "imagpicker");

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(16, 9)
                .start(getContext(), this);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {

                coverImageUri = result.getUri();
                //        Log.v("mkeyreg", "mianuri= " + coverImageUri);
                //  circleImageView.setImageURI(mainImageUri);

                File thumb_filePathUri = new File(coverImageUri.getPath());
                try {
                    cover_Bitmap = new Compressor(getActivity()).setMaxWidth(640).setMaxHeight(480).setQuality(60).compressToBitmap(thumb_filePathUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                cover_Bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                final StorageReference thumb_filePath = coverImgRef.child(mUserid + ".jpg");

                final ProgressDialog mprogressDialog = new ProgressDialog(getActivity());
                mprogressDialog.setMessage("Please wait");
                mprogressDialog.show();

                thumb_filePath.putBytes(thumb_byte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      //  cover_downloadUrl = taskSnapshot.getDownloadUrl();//////////////enable this
                        //              Log.v("Accreg_two", "cover download url: " + cover_downloadUrl);

                        imageView.setImageURI(coverImageUri);
                        bundle2.putString("cover_url", cover_downloadUrl.toString());
                        bundle2.putString("thumb_url", thumb_url);

                        mprogressDialog.dismiss();
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }


}
