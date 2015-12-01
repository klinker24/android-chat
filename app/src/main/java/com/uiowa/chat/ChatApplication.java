package com.uiowa.chat;

import android.app.Application;

import com.uiowa.chat.encryption.SessionManager;

import java.lang.reflect.Field;

public class ChatApplication extends Application {

    private SessionManager sessionManager;

    /**
     * In this class, we could do things like cache management or some data loading.
     * This app doesn't need any of that of course, but we created it and put a
     * reference to it in the AndroidManifest.xml
     */
    @Override
    public void onCreate() {
        super.onCreate();
        enableSecurity();

        sessionManager = new SessionManager();
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * By default, java does not allow for strong security schemes due to export laws in other
     * countries. This gets around that. Might not be necessary on Android, but we'll put it here
     * anyways just in case.
     */
    private static void enableSecurity() {
        try {
            Field field = Class.forName("javax.crypto.JceSecurity").
                    getDeclaredField("isRestricted");
            field.setAccessible(true);
            field.set(null, Boolean.FALSE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
