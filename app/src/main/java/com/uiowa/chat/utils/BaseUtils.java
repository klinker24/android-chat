package com.uiowa.chat.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.uiowa.chat.activities.GCMRegisterActivity;
import com.uiowa.chat.api_objects.UserApi;

import java.io.IOException;

/**
 * A basic utils calss that we can use for registration, user data and generic functions
 */
public class BaseUtils extends Reporting {

    protected static final String TAG = "PushSyncUtils";
    public static final boolean PUSH_NOTIFICATIONS = true;

    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    protected final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String SENDER_ID = "158973526184";

    public GoogleCloudMessaging getGCM(Context c) {
        return GoogleCloudMessaging.getInstance(c);
    }

    public boolean hasRegistered(Context c) {
        SharedPreferences sp = getGCMPreferences(c);
        return sp.getBoolean("is_logged_in", false);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices(Activity a) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(a);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, a,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    public String getRegistrationId(Context c) {
        final SharedPreferences prefs = getGCMPreferences(c);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(c);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    public SharedPreferences getGCMPreferences(Context a) {
        return a.getSharedPreferences(GCMRegisterActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    protected int getAppVersion(Context c) {
        try {
            PackageInfo packageInfo = c.getPackageManager()
                    .getPackageInfo(c.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    // This creates a background thread by using an AsyncTask.
    protected void doInBackground(final Runnable runnable) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                runnable.run();
                return null;
            }
        }.execute(null, null, null);
    }
}
