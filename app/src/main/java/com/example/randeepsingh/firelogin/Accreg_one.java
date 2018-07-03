package com.example.randeepsingh.firelogin;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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
public class Accreg_one extends Fragment {

    private CircleImageView circleImageView;
    private StorageReference mStorageRef;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    String mUserid;
    Bitmap thumb_Bitmap=null;
    Uri mainImageUri=null;
    private StorageReference thumbImgRef;
    Uri thumb_downloadUrl=null;
    private Button btnNext;
    Bundle bundle;
    FirebaseFirestore mfirebaseFirestore;

    public Accreg_one() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accreg_one, container, false);

        circleImageView = view.findViewById(R.id.Accreg1_profile);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mUserid = mAuth.getCurrentUser().getUid();
        thumbImgRef=FirebaseStorage.getInstance().getReference().child("Thumb_images");
        btnNext=view.findViewById(R.id.AccReg1_btn);
        mfirebaseFirestore=FirebaseFirestore.getInstance();
        bundle=new Bundle();

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mainImageUri == null) {
                    Toast.makeText(getActivity(), "Please upload a display picture", Toast.LENGTH_SHORT).show();
                }
                if(thumb_downloadUrl==null){
                    Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
                }
                else if(thumb_downloadUrl != null) {
                    Accreg_two accreg_two = new Accreg_two();
                    accreg_two.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.Accreg_frame, accreg_two).commit();
                }
            }
        });


        return view;
    }

    private void ImagePicker() {

        Log.e("hi", "imagpicker");

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

            mainImageUri=result.getUri();
            Log.v("mkeyreg","mianuri= "+mainImageUri);
          //  circleImageView.setImageURI(mainImageUri);

                File thumb_filePathUri=new File(mainImageUri.getPath());
                try{
                    thumb_Bitmap=new Compressor(getActivity()).setMaxWidth(200).setMaxHeight(200).setQuality(50).compressToBitmap(thumb_filePathUri);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_Bitmap.compress(Bitmap.CompressFormat.JPEG, 50,byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                final StorageReference thumb_filePath=thumbImgRef.child(mUserid +".jpg");

                final ProgressDialog mprogressDialog=new ProgressDialog(getActivity());
                mprogressDialog.setMessage("Please wait");
                mprogressDialog.show();

                thumb_filePath.putBytes(thumb_byte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        thumb_downloadUrl=taskSnapshot.getDownloadUrl();
                        Log.v("mkey","thumb download url: "+thumb_downloadUrl);

                        circleImageView.setImageURI(mainImageUri);
                        bundle.putString("thumb_url",thumb_downloadUrl.toString());


                        mprogressDialog.dismiss();
                    }
                });

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }

    private void updateDetails() {



    }


}
