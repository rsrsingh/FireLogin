package com.randeepsingh.blogfeed.Home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.randeepsingh.blogfeed.Home.Fragments.homeFragment;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.SharedPref;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class AddPost extends AppCompatActivity {


   // private AdView adView;
    private ImageView mImageview;
    private TextInputEditText mCaption;
    private Button mBtnPublish;
    private StorageReference coverImgRef;
    private Uri mainImageUri = null;
    private Uri cover_downloadUrl;
    private homeFragment homeFrag = null;
    private Bitmap post_Bitmap = null;
    private String fullName;
    private String thumbID;
    private SharedPref sharedPref;
    private byte[] thumb_byte;
    private String postLink;
    private String token_id;
    //  private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private StorageReference thumb_filePath;
    private String mUserid;

    private String postDesc;

    private FirebaseFirestore mFirebaseFirestore;
    private final String randomName = UUID.randomUUID().toString();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            this.setTheme(R.style.DarkTheme);
        } else if (sharedPref.loadNightModeState() == false) {
            this.setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        ImagePicker();

       /* MobileAds.initialize(this, "ca-app-pub-5059411314324031/8151842409");
        adView = findViewById(R.id.add_bannerAds);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);*/

        homeFrag = new homeFragment();
        mImageview = findViewById(R.id.add_image);
        mCaption = (TextInputEditText) findViewById(R.id.add_txt);
        mBtnPublish = findViewById(R.id.add_btnPublish);
        // mStorage = FirebaseStorage.getInstance().getReference();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        coverImgRef = FirebaseStorage.getInstance().getReference().child("Posts");


  /*      mImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker();
            }
        });*/
        mAuth = FirebaseAuth.getInstance();
        mUserid = mAuth.getCurrentUser().getUid();


        token_id = FirebaseInstanceId.getInstance().getToken();

        mFirebaseFirestore.collection("Users").document(mUserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    fullName = task.getResult().getString("full_name");
                    thumbID = task.getResult().getString("thumb_id");

                } else if (!task.isSuccessful()) {

                    Toast.makeText(AddPost.this, "Some error has occured", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPost.this, "Some error has occured", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mainImageUri == null) {
                    Toast.makeText(AddPost.this, "Please upload a photo", Toast.LENGTH_SHORT).show();
                } else {

                    final ProgressDialog mprogressDialog = new ProgressDialog(AddPost.this);
                    mprogressDialog.setMessage("Please wait");
                    mprogressDialog.show();


                    thumb_filePath = coverImgRef.child(mUserid).child(randomName + ".jpg");


                    thumb_filePath.putBytes(thumb_byte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                          //  cover_downloadUrl = taskSnapshot.getDownloadUrl();/////////////////////////////////enable this
                            postLink = cover_downloadUrl.toString();
                            postDesc = mCaption.getText().toString().trim();


                            Map<String, Object> mdata = new HashMap<>();
                            mdata.put("description_value", postDesc);
                            mdata.put("full_name", fullName);
                            mdata.put("thumb_id", thumbID);
                            mdata.put("thumb_imageUrl", postLink);
                            mdata.put("User_id", mUserid);
                            mdata.put("Time_stamp", FieldValue.serverTimestamp());
                            mdata.put("token_id", token_id);
//                            Log.v("plink", "" + postLink);


                            mFirebaseFirestore.collection("Posts").document(randomName).set(mdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(AddPost.this, "Successfully Published", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AddPost.this, AccountMain.class));
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddPost.this, "Some error occureed", Toast.LENGTH_SHORT).show();
                                }
                            });

                            //                          Log.v("Accreg_two", "cover download url: " + cover_downloadUrl);
                            mprogressDialog.dismiss();
                        }
                    });

                }


            }
        });





    }

    private void ImagePicker() {

        //    Log.e("hi", "imagpicker");

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(16, 9)
                .start( AddPost.this);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  Log.v("mkey2", "onactivity result called");

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                //        Log.v("mkey2", "add image uri: " + mainImageUri.toString());
                mImageview.setImageURI(mainImageUri);


                File thumb_filePathUri = new File(mainImageUri.getPath());
                try {
                    post_Bitmap = new Compressor(this).setMaxWidth(640).setMaxHeight(480).setQuality(50).compressToBitmap(thumb_filePathUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                post_Bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                thumb_byte = byteArrayOutputStream.toByteArray();

            }
        }

    }

}

