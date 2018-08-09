package com.randeepsingh.blogfeed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> {


    ArrayList<User> searchList;
    public static Context context;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;

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
        return new SearchRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecyclerAdapter.ViewHolder holder, int position) {

        final String fName = searchList.get(position).getFull_name();
      final  String profUrl = searchList.get(position).getThumb_id();
       final String userId=searchList.get(position).UserID;

        Log.e("searchkey", "onBindViewHolder: "+fName+ " profurl: "+profUrl );

        if (!fName.equals(null) && !profUrl.equals(null))
        {
            holder.userName(fName);
            holder.profImage(profUrl);

        }

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, UserProfile.class);
                //               Log.e("mtest8", "onClick: "+userPass );
                i.putExtra("post_user_id", userId);
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        Log.e("searchkey", "getItemCount: "+searchList.size() );
        return searchList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profImage;
        private Button button;
        private TextView userName;
        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

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
