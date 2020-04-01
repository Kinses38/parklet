package com.kinses38.parklet.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kinses38.parklet.R;
import com.kinses38.parklet.view.ui.activities.LandingActivity;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class ParkLetFirebaseMessagingService extends FirebaseMessagingService {


    private final String TAG = this.getClass().getSimpleName();
    private final DatabaseReference DB = FirebaseDatabase.getInstance().getReference("users/");
    private static AtomicInteger notificationID = new AtomicInteger(0);

    @Override
    public void onNewToken(@NotNull String token) {
        Log.d(TAG, "Token refreshed: " + token);
        updateUsersToken(token);
    }

    //In the case of uninstall/updates/changing device
    private void updateUsersToken(String token) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DB.child(uid).child("fcmToken").setValue(token).addOnSuccessListener(aVoid ->
                    Log.i(TAG, "User: " + uid + " token updated"));
        }
    }

    private int getUniqueReqID() {
        return notificationID.getAndIncrement();
    }

    /**
     * Based on Firebase quick start tutorial.
     * https://github.com/firebase/quickstart-android/blob/8d2ad7100bcc9c32e6b2aa7e10f27795078c4e03/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/java/MyFirebaseMessagingService.java#L58-L101
     * Required to handle foreground messages and create a channel to receive both foreground and
     * background notifications if running Oreo or higher.
     *
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Bring user to landing screen in case they are not signed in.
        Intent intent = new Intent(this, LandingActivity.class);
        //Do not replace activity if it is already in the foreground
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        String parkletChannel = "parklet_channel";
        boolean unimportant = true;
        String title = remoteMessage.getNotification().getTitle();
        if (title.equals("Booking Cancellation")) {
            unimportant = false;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //If important, disable auto cancel and non-swipeable.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, parkletChannel)
                .setSmallIcon(R.drawable.ic_stat_parklet_fcm_logo)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(unimportant)
                .setContentIntent(pendingIntent).setOngoing(!unimportant);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //If higher than Oreo (8.0) require custom channel for foreground messages.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel;
            if (unimportant) {
                channel = setDefaultImportance(parkletChannel);
            } else {
                channel = setHighImportance(parkletChannel);
            }
            manager.createNotificationChannel(channel);
        }
        manager.notify(getUniqueReqID(), builder.build());
    }

    /**
     * Set high importance for cancellations. This may be overridden by the device settings however.
     *
     * @param parkletChannel
     * @return
     */
    private NotificationChannel setHighImportance(String parkletChannel) {
        NotificationChannel channel = new NotificationChannel(parkletChannel, "Parklet",
                NotificationManager.IMPORTANCE_HIGH);
        return channel;
    }

    /**
     * Normal priority for all other notifications recieved by ParkLet
     *
     * @param parkletChannel
     * @return
     */
    private NotificationChannel setDefaultImportance(String parkletChannel) {
        NotificationChannel channel = new NotificationChannel(parkletChannel, "Parklet",
                NotificationManager.IMPORTANCE_DEFAULT);
        return channel;
    }
}


