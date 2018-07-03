package com.example.randeepsingh.firelogin;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


/**
 * A simple {@link Fragment} subclass.
 */
public class Accreg_two extends Fragment {


    String thumb_url;
    ImageView imageView;
    String mUserid;
    Bitmap cover_Bitmap = null;
    Uri coverImageUri = null;
    private StorageReference coverImgRef;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private StorageReference mStorageRef;
    Uri cover_downloadUrl=null;
    Button btnNext;
    Bundle bundle2;
    Bundle bundle;
    CircleImageView circleImageView;


    public Accreg_two() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_accreg_two, container, false);
        bundle = this.getArguments();
        thumb_url = bundle.get("thumb_url").toString();

        circleImageView = view.findViewById(R.id.Accreg2_profile);
        progressBar=view.findViewById(R.id.accreg2_progress);


        progressBar.setVisibility(view.VISIBLE);
        try {

            Glide.with(getActivity()).load(thumb_url).into(circleImageView);

        } catch (Exception e) {

        }



        imageView = view.findViewById(R.id.Accreg2_cover);
        mAuth = FirebaseAuth.getInstance();
        mUserid = mAuth.getCurrentUser().getUid();
        coverImgRef = FirebaseStorage.getInstance().getReference().child("Cover_images");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        btnNext = view.findViewById(R.id.AccReg2_btn);
        bundle2 = new Bundle();
        btnNext.setVisibility(view.GONE);


        //wait fot 2 sec
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // yourMethod();
                imageView.setImageResource(R.drawable.add_cover);


                btnNext.setVisibility(view.VISIBLE);
progressBar.setVisibility(view.GONE);

            }
        }, 2300);
//end wait



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
                if(cover_downloadUrl==null){
                    Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
                }
                else if(cover_downloadUrl != null){
                    Accreg_three accreg_three = new Accreg_three();
                    accreg_three.setArguments(bundle2);
                    getFragmentManager().beginTransaction().replace(R.id.Accreg_frame, accreg_three).commit();
                }
            }
        });



        return view;
    }

    private void ImagePicker() {

        Log.e("hi", "imagpicker");

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
                Log.v("mkeyreg", "mianuri= " + coverImageUri);
                //  circleImageView.setImageURI(mainImageUri);

                File thumb_filePathUri = new File(coverImageUri.getPath());
                try {
                    cover_Bitmap = new Compressor(getActivity()).setMaxWidth(200).setMaxHeight(200).setQuality(50).compressToBitmap(thumb_filePathUri);
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
                        cover_downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.v("Accreg_two", "cover download url: " + cover_downloadUrl);

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
