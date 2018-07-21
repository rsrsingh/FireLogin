package com.example.randeepsingh.firelogin;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody= remoteMessage.getNotification().getBody();

        Log.v("mnotif","body: "+messageBody+" title: "+messageTitle);

    }

}
