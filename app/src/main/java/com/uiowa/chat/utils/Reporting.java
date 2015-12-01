package com.uiowa.chat.utils;


import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Class to use for logging errors or other messages
 *
 * There is also a notification that can be sent.
 */
public class Reporting {
    private static String TAG = "UIowaChatLogging";

    public static void log(String message) {
        Log.v(TAG, message);
    }

    public static void logError(Exception e) {
        e.printStackTrace();
    }

    public static void notifyError(Context c, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c)
                .setContentTitle("GCM from SAI Chat")
                .setContentText(message);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(c);

        notificationManager.notify(1, mBuilder.build());
    }
}
