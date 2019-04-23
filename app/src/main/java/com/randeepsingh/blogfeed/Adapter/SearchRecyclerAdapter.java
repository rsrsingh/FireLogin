package com.randeepsingh.blogfeed.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.randeepsingh.blogfeed.Home.UserProfile;
import com.randeepsingh.blogfeed.R;
import com.randeepsingh.blogfeed.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> {


    private ArrayList<User> searchList;
    public static Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String userID;

    public SearchRecyclerAdapter(ArrayList<User> searchList) {
        this.searchList = searchList;
    }

    @NonNull
    @Override
    public SearchRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = parent.getContext();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        return new SearchRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchRecyclerAdapter.ViewHolder holder, int position) {

        final String fName = searchList.get(position).getFull_name();
        final String profUrl = searchList.get(position).getThumb_id();
        final String posUserId = searchList.get(position).UserID;

        //follow check
        firebaseFirestore.collection("Users").document(posUserId).collection("Followers").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    holder.uFButton.setVisibility(View.VISIBLE);
                } else {
                    holder.fbutton.setVisibility(View.VISIBLE);
                }
            }
        });

        //following feature
        holder.fbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("PLease Wait...");
                progressDialog.show();

                final Map<String, Object> map = new HashMap<>();
                map.put("time_stamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Users").document(userID).collection("Following").document(posUserId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Log.e("followKey", "userID success ");
                        firebaseFirestore.collection("Users").document(posUserId).collection("Followers").document(userID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.e("followKey", "post user success ");
                                holder.fbutton.setVisibility(View.GONE);
                                holder.uFButton.setVisibility(View.VISIBLE);

                            }
                        });

                        Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Some Error Occured", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                });


            }
        });


        //unfollow feature
        holder.uFButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("PLease Wait...");
                progressDialog.show();
                firebaseFirestore.collection("Users").document(userID).collection("Following").document(posUserId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        firebaseFirestore.collection("Users").document(posUserId).collection("Followers").document(userID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Unfollowed successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                holder.uFButton.setVisibility(View.GONE);
                                holder.fbutton.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });


        Log.e("followkey", "onBindViewHolder: user: " + posUserId);

        Log.e("searchkey", "onBindViewHolder: " + fName + " profurl: " + profUrl);

        if (!fName.equals(null) && !profUrl.equals(null)) {
            holder.userName(fName);
            holder.profImage(profUrl);

        }

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, UserProfile.class);
                //               Log.e("mtest8", "onClick: "+userPass );
                i.putExtra("post_user_id", posUserId);
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        Log.e("searchkey", "getItemCount: " + searchList.size());
        return searchList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profImage;
        private Button fbutton, uFButton;
        private TextView userName;
        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            fbutton = mView.findViewById(R.id.searchRow_Fbutton);
            uFButton = mView.findViewById(R.id.searchRow_UFbtn);
            fbutton.setVisibility(View.GONE);
            uFButton.setVisibility(View.GONE);

        }

        public void profImage(String profUrl) {

            profImage = mView.findViewById(R.id.searchRow_prof);
            Glide.with(context).load(profUrl).into(profImage);
        }

        public void userName(String name) {
            userName = mView.findViewById(R.id.searchRow_username);
            userName.setText(name.toString().trim());
        }
    }
}
