package com.example.randeepsingh.firelogin;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.net.Uri;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;


/**
 * A simple {@link Fragment} subclass.
 */
public class addFragment extends Fragment {

    private ImageView mImageview;
    private TextInputEditText mCaption;
    private Button mBtnPublish;
    private StorageReference coverImgRef;
    private Uri mainImageUri = null;
    Uri cover_downloadUrl;
    Bitmap post_Bitmap = null;
    String fullName;
    String thumbID;
  //  private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private String mUserid;

    private String postDesc;

    private FirebaseFirestore mFirebaseFirestore;
    private final String randomName = UUID.randomUUID().toString();
    ;

    public addFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        mImageview = view.findViewById(R.id.add_image);
        mCaption = (TextInputEditText) view.findViewById(R.id.add_txt);
        mBtnPublish = view.findViewById(R.id.add_btnPublish);
       // mStorage = FirebaseStorage.getInstance().getReference();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        coverImgRef = FirebaseStorage.getInstance().getReference().child("Posts");


        mImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mUserid = mAuth.getCurrentUser().getUid();

        mFirebaseFirestore.collection("Users").document(mUserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    fullName = task.getResult().getString("full_name");
                    thumbID = task.getResult().getString("thumb_id");

                }
            }
        });


        mBtnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPosting();
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


    private void startPosting() {
        postDesc = mCaption.getText().toString().trim();

        String postLink; //postDec, mUderid
        postLink = cover_downloadUrl.toString();

        Map<String, Object> mdata = new HashMap<>();
        mdata.put("description_value", postDesc);
        mdata.put("full_name", fullName);
        mdata.put("thumb_id", thumbID);
        mdata.put("thumb_imageUrl", postLink);
        mdata.put("User_id", mUserid);
        mdata.put("Time_stamp", FieldValue.serverTimestamp());

        mFirebaseFirestore.collection("Posts").document(randomName).set(mdata).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public static Fragment newInstance() {
        addFragment addfragment = new addFragment();
        return addfragment;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("mkey2", "onactivity result called");

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                mainImageUri = result.getUri();
                Log.v("mkey2", "add image uri: " + mainImageUri.toString());
                mImageview.setImageURI(mainImageUri);


                File thumb_filePathUri = new File(mainImageUri.getPath());
                try {
                    post_Bitmap = new Compressor(getActivity()).setMaxWidth(640).setMaxHeight(480).setQuality(30).compressToBitmap(thumb_filePathUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                post_Bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

                final StorageReference thumb_filePath = coverImgRef.child(mUserid).child(randomName + ".jpg");

                final ProgressDialog mprogressDialog = new ProgressDialog(getActivity());
                mprogressDialog.setMessage("Please wait");
                mprogressDialog.show();

                thumb_filePath.putBytes(thumb_byte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        cover_downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.v("Accreg_two", "cover download url: " + cover_downloadUrl);

                        mImageview.setImageURI(mainImageUri);

                        mprogressDialog.dismiss();
                    }
                });


            }
        }

    }
}
