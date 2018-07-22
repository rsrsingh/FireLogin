package com.example.randeepsingh.firelogin;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody= remoteMessage.getNotification().getBody();
        String click_action=remoteMessage.getNotification().getClickAction();

        String blogPostID=remoteMessage.getData().get("blog_post_id");

        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this,getString(R.string.default_notification_channel_id)).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(messageTitle).setContentText(messageBody);



        Intent resultIntent =new Intent(click_action);
        resultIntent.putExtra("blog_post_id",blogPostID);

        PendingIntent resultPendingIntent =PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);




    }

}
