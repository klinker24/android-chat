package com.uiowa.chat.utils.api;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.uiowa.chat.ChatApplication;
import com.uiowa.chat.api_objects.MessagingApi;
import com.uiowa.chat.api_objects.ThreadApi;
import com.uiowa.chat.api_objects.UserApi;
import com.uiowa.chat.data.sql.ThreadDataSource;
import com.uiowa.chat.data.sql.MessageDataSource;
import com.uiowa.chat.encryption.SessionManager;
import com.uiowa.chat.utils.BaseUtils;

/**
 * This is just a helper class to send data to the server.
 *
 * What to learn:
 *      - Remember data calls must be off the UI. Here we send a runnable object into the function.
 *          This runnable is started from an AsyncTask, it doesn't provide any callbacks to the UI,
 *          but it is a clean way to perform background tasks
 */
public class Sender extends BaseUtils {

    /*
        The API objects in the client library I made are all on the UI thread. Android
        doesn't let you perform network operations on UI thread.

        I made a doInBackground(Runnable) method that can handle this for you.
        Example in RegistrationUtils.registerInBackground()
     */

    public static final String SENT_BROADCAST = "com.uiowa.chat.MESSAGE_SENT";
    public static final String FAILED_BROADCAST = "com.klinker.anroid.sai_chat.MESSAGE_FAILED";

    private Context context;

    // the API objects are contained in the client module so that they can be used by both the
    // back end and the app for data calls
    private MessagingApi messaging = new MessagingApi();
    private ThreadApi thread = new ThreadApi();
    private UserApi user = new UserApi();

    public Sender(Context context) {
        this.context = context;
    }

    // used when we are on the NewMessageActivity to create a new thread with a user
    public void sendNewMessage(final Long recipientId, final Long senderId, final String message,
                               final MessageSentListener listener) {
        final Handler handler = new Handler();

        doInBackground(new Runnable() {
            @Override
            public void run() {
                // returns an element with the new thread id
                JsonElement o = messaging.sendNewMessage(recipientId, senderId, message);

                if (o != null) {
                    // we need to also find and save the thread that we just created along with the
                    // message that was send. We can use the client's api objects to make a data call
                    // and find the data

                    final long threadId = o.getAsLong();
                    JsonObject threadObject = thread.findThread(threadId);
                    JsonArray threadMessages = messaging.findMessages(threadId);

                    // save them to the database
                    ThreadDataSource.getInstance(context).createThread(threadObject);
                    MessageDataSource.getInstance(context).createMessages(threadMessages);

                    if (listener != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onMessageSent(threadId);
                            }
                        });
                    }
                }

                logObject(o);
            }
        });
    }

    public void sendThreadedMessage(final String toUsername, final Long threadId,
                                    final Long senderId, final String message) {
        final Handler handler = new Handler();

        doInBackground(new Runnable() {
            @Override
            public void run() {
                ChatApplication application = (ChatApplication) context.getApplicationContext();
                SessionManager manager = application.getSessionManager();
                String encryptedMessage = manager.encrypt(toUsername, message);

                Log.v(TAG, "encoded message to send: " + encryptedMessage);

                // returns a json element of the message we just sent
                try {
                    JsonElement o = messaging.sendThreadedMessage(threadId, senderId, encryptedMessage);
                    MessageDataSource.getInstance(context).createMessage(o.getAsJsonObject(), message);
                    logObject(o);
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Message too long to send! :(", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void updateDeviceId(final Long userId, final String deviceId) {
        doInBackground(new Runnable() {
            @Override
            public void run() {
                JsonObject o = user.updateDeviceId(userId, deviceId);
                logObject(o);
            }
        });
    }

    private void logObject(Object o) {
        if (o != null) {
            log(o.toString());
            sendSuccessBroadcast();
        } else {
            sendFailedBroadcast();
        }
    }

    // this is where we fire off a broadcast to update any foreground UI's
    private void sendSuccessBroadcast() {
        context.sendBroadcast(new Intent(SENT_BROADCAST));
    }

    // we dont handle this broadcast anywhere yet, but could if we needed error handling
    private void sendFailedBroadcast() {
        context.sendBroadcast(new Intent(FAILED_BROADCAST));
    }

    public interface MessageSentListener {
        void onMessageSent(long threadId);
    }
}
