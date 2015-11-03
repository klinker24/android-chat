package com.uiowa.chat.receivers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.uiowa.chat.services.PushNotificationService;

/**
 * This broadcast receiver is fired when the system receives a push notification
 * from Google Cloud Messaging.
 *
 * It is wakeful so that we can start the service to save the data without worrying
 * about the device going back to sleep before it has completed.
 *
 * We need to pass the information off to a service, because broadcast receivers are done
 * on the UI thread, whereas IntentServices are performed on a background thread.
 */
public class PushNotificationReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(),
                PushNotificationService.class.getName());

        // Start the service, keeping the device awake while it is launching.
        // Send the information from the push notification to the service
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}