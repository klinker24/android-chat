package com.uiowa.chat.utils.api;

import android.content.Context;

import com.google.gson.JsonElement;
import com.uiowa.chat.api_objects.MessagingApi;
import com.uiowa.chat.api_objects.ThreadApi;
import com.uiowa.chat.api_objects.UserApi;
import com.uiowa.chat.utils.BaseUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * This is just a helper class to delete data from the server.
 *
 * I haven't used this class anywhere yet, but its possible implementations are pretty easy.
 *
 * What to learn:
 *      - Remember data calls must be off the UI. Here we send a runnable object into the function.
 *          This runnable is started from an AsyncTask, it doesn't provide any callbacks to the UI,
 *          but it is a clean way to perform background tasks
 */
public class Deleter extends BaseUtils {

    /*
        The API objects in the client library I made are all on the UI thread. Android
        doesn't let you perform network operations on UI thread.

        I made a doInBackground(Runnable) method that can handle this for you.
        Example implementation in Sender class
     */

    private Context context;

    // the API objects are contained in the client module so that they can be used by both the
    // back end and the app for data calls
    private MessagingApi messaging = new MessagingApi();
    private ThreadApi thread = new ThreadApi();
    private UserApi user = new UserApi();

    public Deleter(Context context) {
        this.context = context;
    }

    public void deleteAllThreads() {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                JsonElement o = thread.deleteAllThreads();
                logObject(o);
            }
        });
    }

    public void deleteUserThreads(final Long userId) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                JsonElement o = thread.deleteUserThreads(userId);
                logObject(o);
            }
        });
    }

    public void deleteThread(final Long threadId) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                JsonElement o = thread.deleteThread(threadId);
                logObject(o);
            }
        });
    }

    public void deleteMessages(final List<Long> messageIds) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                JsonElement o = messaging.deleteMessages(messageIds);
                logObject(o);
            }
        });
    }

    public void deleteAllMessages() {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                JsonElement o = messaging.deleteAllMessages();
                logObject(o);
            }
        });
    }

    public void deleteAllUsers() {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                JsonElement o = user.deleteAllUsers();
                logObject(o);
            }
        });
    }

    public void deleteUserById(final Long userId) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                JsonElement o = user.deleteUserById(userId);
                logObject(o);
            }
        });
    }

    public void deleteUserByName(final String username) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                JsonElement o = user.deleteUserByName(username);
                logObject(o);
            }
        });
    }

    private void logObject(Object o) {
        if (o != null) {
            log(o.toString());
        }
    }
}
