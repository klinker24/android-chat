package com.uiowa.chat.services;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.uiowa.chat.ChatApplication;
import com.uiowa.chat.R;
import com.uiowa.chat.activities.ConversationListActivity;
import com.uiowa.chat.activities.MessageListActivity;
import com.uiowa.chat.api_objects.ThreadApi;
import com.uiowa.chat.api_objects.UserApi;
import com.uiowa.chat.data.Thread;
import com.uiowa.chat.data.DatabaseHelper;
import com.uiowa.chat.data.Message;
import com.uiowa.chat.data.User;
import com.uiowa.chat.data.sql.ThreadDataSource;
import com.uiowa.chat.data.sql.MessageDataSource;
import com.uiowa.chat.data.sql.UserDataSource;
import com.uiowa.chat.encryption.SessionManager;
import com.uiowa.chat.fragments.MessageListFragment;
import com.uiowa.chat.receivers.PushNotificationReceiver;
import com.uiowa.chat.receivers.SmsBroadcastReceiver;
import com.uiowa.chat.utils.RegistrationUtils;
import com.uiowa.chat.utils.api.Sender;

import java.net.URLDecoder;

/**
 * This service will handle the push notifications for new messages
 *
 * It is an intent service because the are always run on a background thread. Intent services
 * do not provide a callback to the UI though, which is why we do other data tasks with an AysncTask.
 *
 * What to learn:
 *      - Notifications:
 *          - creating them
 *          - using different styles
 *          - displaying them
 *          - cancelling them can be seen in the ConversationListActivity.onResume() method
*       - Using the Database methods to save an object
 */
public class PushNotificationService extends IntentService {

    private static final String TAG = "PushNotificationService";
    private static final int ADMIN_MESSAGE_THREAD_ID = -1;

    public static final int ADMIN_NOTIFICATION_ID = 1;
    public static final int MESSAGE_NOTIFICATION_ID = 2;

    public PushNotificationService() {
        super("PushNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Bundle extras = intent.getExtras();

            ChatApplication application = (ChatApplication) getApplicationContext();
            SessionManager manager = application.getSessionManager();

            Log.v(TAG, "original message: " + extras.getString("message"));
            Log.v(TAG, "decoded message: " + extras.getString("message").replace(" ", "+"));

            // each push notification contains the message details
            Long threadId = Long.parseLong(extras.getString("thread_id"));
            Long time = Long.parseLong(extras.getString("time"));
            Long messageId = Long.parseLong(extras.getString("message_id"));
            Long senderId = Long.parseLong(extras.getString("sender_id"));

            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            User sender = databaseHelper.findUser(senderId);
            String message = manager.decrypt(sender.getUsername(),
                    extras.getString("message").replace(" ", "+"));

            if (message.equals("Establishing connection...")) {
                Sender s = new Sender(this);
                s.sendThreadedMessage(
                        sender.getUsername(),
                        threadId,
                        new RegistrationUtils().getMyUserId(this),
                        "Connection established."
                );
            }

            if (!handledAdminMessage(threadId, message)) {

                // make a new message object from those details
                Message object = new Message(this);
                object.setText(message);
                object.setMessageId(messageId);
                object.setThreadId(threadId);
                object.setTimestamp(time);
                object.setSenderId(senderId);
                object.fillSender();

                if (object.getSender() == null) {
                    UserApi userApi = new UserApi();
                    JsonArray userList = userApi.findAllUsers();
                    if(userList != null) {
                        UserDataSource dataSource = UserDataSource.getInstance(this);
                        dataSource.createUsers(userList);
                    }

                    object.fillSender();
                }

                // save the message to our database
                MessageDataSource source = MessageDataSource.getInstance(this);
                source.createMessage(object);

                // if the thread doesn't currently exist, lets fetch the information for it
                // from our server.
                DatabaseHelper helper = new DatabaseHelper(this);
                Thread convo = helper.findConversation(threadId);

                if (convo == null) {
                    ThreadApi threadApi = new ThreadApi();
                    JsonObject thread = threadApi.findThread(threadId);

                    ThreadDataSource.getInstance(this).createThread(thread);
                }

                // send the notification using the message object
                makeMessageNotification(object);
                refreshFragments();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // finish the service and tell the system that it is allowed to sleep again
        PushNotificationReceiver.completeWakefulIntent(intent);
    }

    // I made a function that lets me send an administrative message to every device, to test the notifications
    // or whatever else needs done. This just checks whether the given message is from us
    public boolean handledAdminMessage(long threadId, String message) {
        if (threadId == ADMIN_MESSAGE_THREAD_ID) {
            makeAdminNotification(message);
            return true;
        } else {
            return false;
        }
    }

    // send the notification from the administrator, don't save anything to the database
    private void makeAdminNotification(String message) {

        // All notifications need at least three things:
        //      - Small icon
        //      - Content title
        //      - Content Text
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


        builder.setSmallIcon(R.drawable.sb_icon);
        builder.setContentTitle("Encrypto");
        builder.setContentText(message);

        // creating a "Big Style" from our text allows the notification to be expanded on 4.1+
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        Intent resultIntent = new Intent(this, ConversationListActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // intents are used for instant action. Since a notification could be clicked any time, we
        // create a PendingIntent and place it on our notification builder
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(resultPendingIntent);

        builder.setDefaults(Notification.DEFAULT_ALL);

        sendNotification(builder, ADMIN_NOTIFICATION_ID);
    }

    private void makeMessageNotification(Message message) {

        // use the builder to create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.sb_icon);
        builder.setContentTitle(message.getSender().getRealName());
        builder.setContentText(message.getText());
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getText()));

        Intent resultIntent = new Intent(this, MessageListActivity.class);
        resultIntent.putExtra(MessageListFragment.EXTRA_CONVO_NAME, message.getSender().getRealName());
        resultIntent.putExtra(MessageListFragment.EXTRA_THREAD_ID, message.getThreadId());
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // pending intents are used so specify the action we want for clicks on the notification
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        RemoteInput remoteInput = new RemoteInput.Builder(ReplyService.EXTRA_VOICE_REPLY)
                .setLabel("Reply")
                .build();

        Intent replyIntent = new Intent(this, ReplyService.class);
        replyIntent.putExtra(ReplyService.EXTRA_THREAD_ID, message.getThreadId());
        replyIntent.putExtra(ReplyService.EXTRA_USERNAME, message.getSender().getUsername());
        PendingIntent replyPendingIntent =
                PendingIntent.getService(this, 0, replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the reply action and add the remote input
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_reply,
                        "Reply", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        builder.extend(new NotificationCompat.WearableExtender().addAction(action));

        sendNotification(builder, MESSAGE_NOTIFICATION_ID);
    }

    private void sendNotification(NotificationCompat.Builder builder, int id) {
        // the notification manager service sends the notification to the system
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(id, builder.build());
    }

    // send a broadcast to refresh the currently open fragment (if there is one, remember that
    // the fragments only listen for the broadcast when they are in the foreground)
    private void refreshFragments() {
        sendBroadcast(new Intent(Sender.SENT_BROADCAST));
    }
}
