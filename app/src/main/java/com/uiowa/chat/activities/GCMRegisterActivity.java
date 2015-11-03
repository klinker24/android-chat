package com.uiowa.chat.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.uiowa.chat.R;
import com.uiowa.chat.utils.BaseUtils;
import com.uiowa.chat.utils.RegistrationUtils;

/**
 * This class is used to register your device with our Google App Engine server and prepare it for
 * push notifications using Google Cloud Messaging.
 *
 * We would look at this class too much, but the important function is the registerCompleteReceiver()
 * which will be used in the login activity
 */
public abstract class GCMRegisterActivity extends AbstractToolbarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // sends our registration details to the server when it needs to refresh
        BaseUtils baseUtils = new BaseUtils();
        RegistrationUtils registrationUtils = new RegistrationUtils();
        if (baseUtils.checkPlayServices(this) && baseUtils.hasRegistered(this)) {
            String regid = baseUtils.getRegistrationId(this);

            if (regid.isEmpty()) {
                registrationUtils.registerInBackground(this);
            }
        }
    }

    /**
     * Call this method before you send the registration data to GAE.
     *
     * It provides a broadcast receiver to pick up when the registration
     * is complete so that you can return to the conversation page
     */
    public void registerCompleteReceiver() {

        // the intent filter is used to 'listen' for a broadcast
        IntentFilter filter = new IntentFilter();

        // we add the registration complete action because the RegistrationUtils will send out this
        // action after the server gets our info
        filter.addAction(RegistrationUtils.REGISTRATION_COMPLETE);

        // register a new receiver for the above action
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(
                        GCMRegisterActivity.this,
                        R.string.registration_complete,
                        Toast.LENGTH_SHORT
                ).show();

                // always unregister the receivers when they aren't in use
                // this avoids memory leaks
                context.unregisterReceiver(this);

                // return you to the conversation list page
                if (GCMRegisterActivity.this instanceof LoginActivity) {
                    ((Activity)context).setResult(RESULT_OK);
                    finish();
                }

            }
        }, filter);
    }
}