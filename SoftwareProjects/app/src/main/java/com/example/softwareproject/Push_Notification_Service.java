package com.example.softwareproject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Push_Notification_Service extends FirebaseMessagingService {

    // This creates the notification received from the other users that the user is following
    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle(); // The title of the message
        String text = remoteMessage.getNotification().getBody(); // The post is shown on the message
        String CHANNEL_ID = "MESSAGE";
        NotificationChannel channel = new NotificationChannel( /* This makes the notification to
                                                                  appear first on the phone*/
                CHANNEL_ID,
                "Message Notification",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID) // This builds the notification
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notification) // Puts a notification icon on the the notification
                .setAutoCancel(true); // Make this notification automatically dismissed when the user touches it
        NotificationManagerCompat.from(this).notify(1, notification.build());
        super.onMessageReceived(remoteMessage);
    }
}
