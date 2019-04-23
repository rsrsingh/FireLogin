package com.randeepsingh.blogfeed.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.randeepsingh.blogfeed.Comments;
import com.randeepsingh.blogfeed.Home.UserPost;
import com.randeepsingh.blogfeed.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotifRecyclerAdapter extends RecyclerView.Adapter<NotifRecyclerAdapter.ViewHolder> {

    public static Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String userID;
    private ArrayList<Comments> notifList;

    public NotifRecyclerAdapter(ArrayList<Comments> notifList) {
        this.notifList = notifList;

    }

    @NonNull
    @Override
    public NotifRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_row, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = parent.getContext();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final NotifRecyclerAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String postID = notifList.get(position).getPost_id();
        //  Log.e("postID", "onBindViewHolder: "+ postID );
//final String commentID=comntList.get(position).CommentID;
//imageURL=comntList.get(position).

        String descValue = notifList.get(position).getComment_value();
        String from_user = notifList.get(position).getUser_id();

        Date date = notifList.get(position).getTime_stamp();


        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat("dd MMMM yyyy hh:mm");
        final StringBuilder nowMMDDYYYY = new StringBuilder(dateformatMMDDYYYY.format(date));

        //Log.v("mdate", "" + nowMMDDYYYY.toString());

        firebaseFirestore.collection("Users").document(from_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    String thumbUrl = task.getResult().getString("thumb_id");
                    String uName = task.getResult().getString("full_name");
                    holder.setImage(thumbUrl);
                    holder.setUsername(uName);


                }
            }
        });

        holder.setDesc(descValue);
        holder.setTime(nowMMDDYYYY);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, UserPost.class);
                i.putExtra("blog_post_id", postID);
                context.startActivity(i);
            }
        });
        //Date date = comntList.get(position).getTime_stamp();


      /*  @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat("dd MMMM yyyy");
        final StringBuilder nowMMDDYYYY = new StringBuilder(dateformatMMDDYYYY.format(date));

        Log.v("mdate",""+nowMMDDYYYY.toString());*/


        //   holder.setTime(nowMMDDYYYY);

    }

    @Override
    public int getItemCount() {
        return notifList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView username, mDescription, timeStamp;
        CircleImageView circleImageView;
        CardView cardView;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            cardView = mView.findViewById(R.id.notif_card);

        }


        public void setImage(String url) {
            circleImageView = mView.findViewById(R.id.notif_image);
            Glide.with(context).load(url).into(circleImageView);
        }

        public void setDesc(String desc) {
            mDescription = mView.findViewById(R.id.notif_desc);
            mDescription.setText(desc);
        }

        public void setTime(StringBuilder tStamp) {
            timeStamp = mView.findViewById(R.id.notif_time);
            timeStamp.setText(tStamp.toString());
        }

        public void setUsername(String uName) {
            username = mView.findViewById(R.id.notif_username);
            username.setText(uName + " commented on your post:");
        }
    }

}
