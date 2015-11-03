package com.uiowa.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonObject;
import com.klinker.android.sai_chat.api_objects.UserApi;

import java.io.IOException;

/**
 * Implementation of the BaseUtils specifically for managing the Google App Engine and
 * Google Cloud Messaging registration
 */
public class RegistrationUtils extends BaseUtils {

    public static final String REGISTRATION_COMPLETE =
            "com.klinker.android.sai.REGISTRATION_COMPLETE";

    public void registerInBackground(Context c, String realName, String username) {
        storeUserInfo(c, realName, username);
        registerInBackground(c);
    }

    public void registerInBackground(final Context c) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                log("registering in background");
                GoogleCloudMessaging gcm = getGCM(c);

                try {
                    String regid = gcm.register(SENDER_ID);

                    UserApi user = new UserApi();
                    JsonObject userObject = user.createUser(getUsername(c), getRealName(c), regid);

                    saveMyUserId(c, userObject.get("id").getAsLong());

                    // Persist the regID - no need to register again.
                    storeRegistrationId(c, regid);
                } catch (IOException ex) {
                    logError(ex);
                    notifyError(c, "Error: " + ex.getMessage());
                }

                c.sendBroadcast(new Intent(REGISTRATION_COMPLETE));
            }
        });
    }

    public String getUsername(Context c) {
        final SharedPreferences prefs = getGCMPreferences(c);
        return prefs.getString("username", "");
    }

    public String getRealName(Context c) {
        final SharedPreferences prefs = getGCMPreferences(c);
        return prefs.getString("real_name", "");
    }

    public long getMyUserId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getLong("my_id", 0l);
    }

    public void saveMyUserId(Context context, long id) {
        final SharedPreferences prefs = getGCMPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("my_id", id);
        editor.commit();
    }

    public void storeUserInfo(Context c, String realName, String username) {
        final SharedPreferences prefs = getGCMPreferences(c);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("real_name", realName);
        editor.putString("username", username);
        editor.commit();
    }

    public void storeRegistrationId(Context c, String regId) {
        final SharedPreferences prefs = getGCMPreferences(c);
        int appVersion = getAppVersion(c);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.putBoolean("is_logged_in", true);
        editor.commit();
    }

    public void removeRegistrationId(Context c) {
        final SharedPreferences prefs = getGCMPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.remove(PROPERTY_APP_VERSION);
        editor.commit();
    }
}
