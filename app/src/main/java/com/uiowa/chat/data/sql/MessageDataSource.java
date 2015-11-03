package com.uiowa.chat.data.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.uiowa.chat.data.Message;

/**
 * Class to faciliate accessing the SQL database for our messages
 */
public class MessageDataSource {

    // provides access to the database
    public static MessageDataSource dataSource = null;

    /*

    This is used so that we don't have to open and close the database on different threads or fragments
    every time. This will facilitate it between all of them to avoid Illegal State Exceptions.

     */
    public static MessageDataSource getInstance(Context context) {

        // if the datasource isn't open or it the object is null
        if (dataSource == null ||
                dataSource.getDatabase() == null ||
                !dataSource.getDatabase().isOpen()) {

            dataSource = new MessageDataSource(context); // create the database
            dataSource.open(); // open the database
        }

        return dataSource;
    }

    private Context context;
    private SQLiteDatabase database;
    private MessageSQLiteHelper dbHelper;

    public String[] allColumns = {
            MessageSQLiteHelper.COLUMN_ID,
            MessageSQLiteHelper.COLUMN_THREAD_ID,
            MessageSQLiteHelper.COLUMN_TIME,
            MessageSQLiteHelper.COLUMN_TEXT,
            MessageSQLiteHelper.COLUMN_SENDER_ID
    };

    public MessageDataSource(Context context) {
        this.context = context;
        this.dbHelper = new MessageSQLiteHelper(context);
    }

    public void open() throws SQLException {
        try {
            database = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            close();
        }
    }

    public void close() {
        try {
            dbHelper.close();
        } catch (Exception e) {

        }
        database = null;
        dataSource = null;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public MessageSQLiteHelper getHelper() {
        return dbHelper;
    }

    public synchronized void createMessage(Message message) {

        // we save data to our SQL database using the ContentValues class
        ContentValues values = new ContentValues();

        // just put the column and the data
        values.put(MessageSQLiteHelper.COLUMN_ID, message.getMessageId());
        values.put(MessageSQLiteHelper.COLUMN_THREAD_ID, message.getThreadId());
        values.put(MessageSQLiteHelper.COLUMN_TIME, message.getTimestamp());
        values.put(MessageSQLiteHelper.COLUMN_TEXT, message.getText());
        values.put(MessageSQLiteHelper.COLUMN_SENDER_ID, message.getSenderId());

        // Android's databases will combine your queries for you
        database.insert(
                MessageSQLiteHelper.TABLE_MESSAGE,
                null,
                values);
    }

    public synchronized void createMessage(JsonObject message) {
        ContentValues values = getValues(message);

        database.insert(
                MessageSQLiteHelper.TABLE_MESSAGE,
                null,
                values);
    }

    // Here we are inserting an array. So we find an array of the ContentValues
    // then we can start a transaction on the database to make adding large amounts of data
    // MUCH more effient than doing it individually
    public synchronized void createMessages(JsonArray messages) {
        if (messages == null) {
            return;
        }

        ContentValues[] values = new ContentValues[messages.size()];
        for (int i = 0; i < values.length; i++) {
            JsonElement e = messages.get(i);
            values[i] = getValues(e.getAsJsonObject());
        }

        insertMultiple(values);
    }

    public ContentValues getValues(JsonObject message) {
        ContentValues values = new ContentValues();

        values.put(MessageSQLiteHelper.COLUMN_ID, message.get("id").getAsLong());
        values.put(MessageSQLiteHelper.COLUMN_THREAD_ID, message.get("threadId").getAsLong());
        values.put(MessageSQLiteHelper.COLUMN_TIME, message.get("time").getAsLong());
        values.put(MessageSQLiteHelper.COLUMN_TEXT, message.get("text").getAsString());
        values.put(MessageSQLiteHelper.COLUMN_SENDER_ID, message.get("senderId").getAsLong());

        return values;
    }

    public synchronized int insertMultiple(ContentValues[] allValues) {
        int rowsAdded = 0;
        long rowId;

        if (database == null || !database.isOpen()) {
            open();
        }

        try {
            database.beginTransaction();

            for (int i = 0; i < allValues.length; i++) {
                ContentValues initialValues = allValues[i];

                if (initialValues != null) {
                    rowId = database.insert(MessageSQLiteHelper.TABLE_MESSAGE, null, initialValues);
                    if (rowId > 0) {
                        rowsAdded++;
                    }
                }
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        return rowsAdded;
    }

    public synchronized void deleteMessage(long messageId) {
        database.delete(
                MessageSQLiteHelper.TABLE_MESSAGE,
                MessageSQLiteHelper.COLUMN_ID + " = ?",
                new String[] { messageId + "" }
        );

    }

    public synchronized void deleteAllMessages() {
        database.delete(
                MessageSQLiteHelper.TABLE_MESSAGE,
                null, null
        );
    }

    public synchronized Cursor getThreadCursor(long threadId) {
        return database.query(
                MessageSQLiteHelper.TABLE_MESSAGE,
                allColumns,
                MessageSQLiteHelper.COLUMN_THREAD_ID + " = ?",
                new String[] { threadId + "" },
                null,
                null,
                MessageSQLiteHelper.COLUMN_TIME + " ASC"
        );
    }
}
