package com.uiowa.chat.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.uiowa.chat.utils.BaseUtils;
import com.uiowa.chat.utils.RegistrationUtils;

/**
 * This broadcast receiver is different than the ones we register in an activity.
 * It is registered in the AndroidManifest as a system broadcast and will fire whenever
 * the system sends the required broadcast.
 *
 * For this one, it is right after the boot-up.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        // Google Cloud Messaging is very weird with your device ids.
        // It will usually recreate them and assign you a different one when
        // ever you restart your device, or update the app.

        // This handles the restart occurrences

        final RegistrationUtils utils = new RegistrationUtils();
        String regid = utils.getRegistrationId(context);

        if (regid.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    utils.registerInBackground(context);
                }
            }).start();
        }
    }
}