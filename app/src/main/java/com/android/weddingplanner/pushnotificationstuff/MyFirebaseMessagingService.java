package com.android.weddingplanner.pushnotificationstuff;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.weddingplanner.R;
import com.android.weddingplanner.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String NOTIFICATION_TYPE_REQUEST = "1";
    private static final String NOTIFICATION_TYPE_WORK_ORDER = "2";


    private NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;


        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG, "onMessageReceived: " + data);


        // Check if message contains a data payload.
        if (data.size() > 0) {
            Log.e(TAG, "Data Payload: " + data.toString());
            try {
                JSONObject json = new JSONObject(data.toString());
                handleMessageData(json);

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }


    }

    private void handleMessageData(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("payload");
            Log.d(TAG, "handleMessageData: " + data.toString());
            String NOTIFICATION_TYPE_ID;
            String messageTitle = "New Message";
            String messageContent = "New Chat Message Received";
            if (data.has("NOTIFICATION_TYPE_ID")) {
                NOTIFICATION_TYPE_ID = data.getString("NOTIFICATION_TYPE_ID");
                Log.d(TAG, "NotificationTypeId: " + NOTIFICATION_TYPE_ID);
            }

            if (data.has("title")) {
                messageTitle = data.getString("title");
                Log.d(TAG, "messageTitle: " + messageTitle);
            }

            if (data.has("message")) {
                messageContent = data.getString("message");
                Log.d(TAG, "messageContent: " + messageContent);
            }

            displayNotification(messageContent, messageTitle);

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    protected void displayNotification(String message, String title) {

        Log.i("Start", "notification");

        // Invoking the default notification service //
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);
        mBuilder.setAutoCancel(true);

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);

        // Creates an explicit intent for an Activity in your app //

//        Intent resultIntent = new Intent(this, MessagesListActivity.class);
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(MainActivity.class);
//
//        // Adds the Intent that starts the Activity to the top of the stack //
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);


        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent resultPendingIntent = PendingIntent.getActivities(
                this, 0,
                new Intent[]{backIntent}, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(resultPendingIntent);


        //  mBuilder.setOngoing(true);
        Notification note = mBuilder.build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // notificationID allows you to update the notification later on. //
        mNotificationManager.notify(1, mBuilder.build());

    }

}
