package com.uiowa.chat.data;

import android.content.Context;
import android.database.Cursor;

import com.uiowa.chat.data.sql.MessageSQLiteHelper;

import lombok.Getter;
import lombok.Setter;

/**
 * Data object for the messages
 *
 * Use this object instead of accessing the cursor directly.
 *
 * Available fields:
 *      - messageId
 *      - threadId
 *      - timestamp
 *      - senderId
 *      - text
 *
 *      It also provides a reference to the sender User object so that you don't have to worry
 *      about finding it.
 */
@Getter @Setter
public class Message {

    private Context context;

    private long messageId;
    private long threadId;
    private long timestamp;
    private long senderId;
    private String text;

    private User sender;

    public Message(Context context) {
        this.context = context;
    }

    public void fillFromCursor(Cursor c) {
        if (c == null) {
            return;
        }

        String[] cols = c.getColumnNames();
        for (int i = 0; i < cols.length; i++) {
            if (MessageSQLiteHelper.COLUMN_ID.equals(cols[i])) {
                this.messageId = c.getLong(i);
            } else if (MessageSQLiteHelper.COLUMN_THREAD_ID.equals(cols[i])) {
                this.threadId = c.getLong(i);
            } else if (MessageSQLiteHelper.COLUMN_TIME.equals(cols[i])) {
                this.timestamp = c.getLong(i);
            } else if (MessageSQLiteHelper.COLUMN_TEXT.equals(cols[i])) {
                this.text = c.getString(i);
            } else if (MessageSQLiteHelper.COLUMN_SENDER_ID.equals(cols[i])) {
                this.senderId = c.getLong(i);
            }
        }

        fillSender();
    }


    public void fillSender() {
        sender = new DatabaseHelper(context).findUser(senderId);
    }
}
