package com.uiowa.chat.data;


import android.content.Context;
import android.database.Cursor;

import com.uiowa.chat.data.sql.ThreadDataSource;
import com.uiowa.chat.data.sql.MessageDataSource;
import com.uiowa.chat.data.sql.UserDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple helper class to manage the data sources and find the data objects from the cursors that they return
 *
 * Android uses SQLite for database management. We won't talk much about it today, but take a look at the
 * MessageDataSource and the MessageSQLiteHelper to check out how to set up and query the databases.
 */
public class DatabaseHelper {

    /**
     * TODO:
     *
     *      All these are very similar methods. Fill in the findAllConversations() method.
     */
    private Context context;

    private MessageDataSource messageData;
    private ThreadDataSource threadData;
    private UserDataSource userData;

    public DatabaseHelper(Context context) {
        this.context = context;

        this.messageData = MessageDataSource.getInstance(context);
        this.threadData = ThreadDataSource.getInstance(context);
        this.userData = UserDataSource.getInstance(context);
    }

    /**
     * MESSAGES DATA SOURCE
     */
    public List<Message> findThreadMessages(Long threadId) {
        List<Message> messages = new ArrayList<>();

        Cursor cursor = messageData.getThreadCursor(threadId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Message message = new Message(context);
                message.fillFromCursor(cursor);
                messages.add(message);
            } while (cursor.moveToNext());
        }

        return messages;
    }

    public Message findLatestMessage(Long threadId) {
        Cursor cursor = messageData.getThreadCursor(threadId);

        if (cursor.moveToLast()) {
            Message message = new Message(context);
            message.fillFromCursor(cursor);
            return message;
        } else {
            return null;
        }
    }

    /**
     * CONVERSATION DATA SOURCE
     */
    public List<Thread> findAllConversations() {

        // TODO #1
        // hint: look at the other methods, which are almost the same as this.
        // ThreadDataSource.getCursor() will return the cursor you need.
        // then just iterate through and fill an array to return rather than returning null.

        Cursor cursor = threadData.getCursor();

        final List<Thread> conversations = new ArrayList<Thread>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Thread conversation = new Thread(context);
                conversation.fillFromCursor(cursor);

                if (conversation.getLatestMessage() != null) {
                    conversations.add(conversation);
                }
            } while (cursor.moveToNext());
        }

        return conversations;
    }

    public Thread findConversation(long threadId) {
        List<Thread> conversations = findAllConversations();

        for (Thread c : conversations) {
            if (c.getThreadId() == threadId) {
                return c;
            }
        }

        return null;
    }

    /**
     * USER DATA SOURCE
     */
    public List<User> findAllUsers() {
        List<User> users =  new ArrayList<>();
        Cursor cursor = userData.getAllUsersCursor();

        if (cursor.moveToFirst()) {
            do {
                User u = new User();
                u.fillFromCursor(cursor);

                users.add(u);
            } while (cursor.moveToNext());
        }

        return users;
    }

    public User findUser(Long userId) {
        Cursor cursor = userData.getUserCursor(userId);
        return findUser(cursor);
    }

    public User findUser(String username) {
        Cursor cursor = userData.getUserCursor(username);
        return findUser(cursor);
    }

    private User findUser(Cursor cursor) {
        if (cursor.moveToFirst()) {
            User user = new User();
            user.fillFromCursor(cursor);
            return user;
        } else {
            return null;
        }
    }
}
