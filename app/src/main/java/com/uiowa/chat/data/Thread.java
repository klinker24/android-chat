package com.uiowa.chat.data;

import android.content.Context;
import android.database.Cursor;

import com.uiowa.chat.data.sql.ThreadSQLiteHelper;

import lombok.Getter;
import lombok.Setter;

/**
 * Data object for the threads
 *
 * Use this object instead of accessing the cursor directly.
 *
 * Available fields:
 *      - threadId
 *      - user1Id
 *      - user2Id
 *
 *      It also provides a reference to the User objects that are part of the conversation and the latest
 *      message in that thread.
 */
@Getter @Setter
public class Thread {

    private Context context;

    private long threadId;
    private long user1Id;
    private long user2Id;

    private User user1;
    private User user2;

    private Message latestMessage;

    public Thread(Context c) {
        this.context = c;
    }

    public void fillFromCursor(Cursor c) {
        if (c == null) {
            return;
        }

        String[] cols = c.getColumnNames();
        for (int i = 0; i < cols.length; i++) {
            if (ThreadSQLiteHelper.COLUMN_ID.equals(cols[i])) {
                this.threadId = c.getLong(i);
            } else if (ThreadSQLiteHelper.COLUMN_USER_1.equals(cols[i])) {
                this.user1Id = c.getLong(i);
            } else if (ThreadSQLiteHelper.COLUMN_USER_2.equals(cols[i])) {
                this.user2Id = c.getLong(i);
            }
        }

        getUsers();
        getMessage();
    }

    public void getUsers() {
        DatabaseHelper helper = new DatabaseHelper(context);
        user1 = helper.findUser(user1Id);
        user2 = helper.findUser(user2Id);
    }

    public void getMessage() {
        latestMessage = new DatabaseHelper(context).findLatestMessage(threadId);
    }
}
