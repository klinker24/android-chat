package com.uiowa.chat.data.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ThreadDataSource {

    // provides access to the database
    public static ThreadDataSource dataSource = null;

    /*

    This is used so that we don't have to open and close the database on different threads or fragments
    every time. This will facilitate it between all of them to avoid Illegal State Exceptions.

     */
    public static ThreadDataSource getInstance(Context context) {

        // if the datasource isn't open or it the object is null
        if (dataSource == null ||
                dataSource.getDatabase() == null ||
                !dataSource.getDatabase().isOpen()) {

            dataSource = new ThreadDataSource(context); // create the database
            dataSource.open(); // open the database
        }

        return dataSource;
    }

    private SQLiteDatabase database;
    private ThreadSQLiteHelper dbHelper;

    public String[] allColumns = {
            ThreadSQLiteHelper.COLUMN_ID,
            ThreadSQLiteHelper.COLUMN_TITLE,
            ThreadSQLiteHelper.COLUMN_USER_1,
            ThreadSQLiteHelper.COLUMN_USER_2
    };

    public ThreadDataSource(Context context) {
        dbHelper = new ThreadSQLiteHelper(context);
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

    public ThreadSQLiteHelper getHelper() {
        return dbHelper;
    }

    public synchronized void createThread(JsonObject thread) {
        ContentValues values = getValues(thread);

        database.insert(
                ThreadSQLiteHelper.TABLE_THREAD,
                null,
                values);
    }

    public synchronized void createThreads(JsonArray threads) {
        if (threads == null) {
            return;
        }

        ContentValues[] values = new ContentValues[threads.size()];
        for (int i = 0; i < values.length; i++) {
            JsonElement e = threads.get(i);
            values[i] = getValues(e.getAsJsonObject());
        }

        insertMultiple(values);
    }

    public ContentValues getValues(JsonObject thread) {
        ContentValues values = new ContentValues();

        JsonArray users = thread.getAsJsonArray("userIds");
        long user1 = users.get(0).getAsLong();
        long user2 = users.get(1).getAsLong();

        values.put(ThreadSQLiteHelper.COLUMN_ID, thread.get("id").getAsLong());
        values.put(ThreadSQLiteHelper.COLUMN_TITLE, ""); // we aren't using this yet
        values.put(ThreadSQLiteHelper.COLUMN_USER_1, user1);
        values.put(ThreadSQLiteHelper.COLUMN_USER_2, user2);

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
                    rowId = database.insert(ThreadSQLiteHelper.TABLE_THREAD, null, initialValues);
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

    public synchronized void deleteThread(long threadId) {
        database.delete(
                ThreadSQLiteHelper.TABLE_THREAD,
                ThreadSQLiteHelper.COLUMN_ID + " = ?",
                new String[] { threadId + "" }
        );

    }

    public synchronized void deleteAllThreads() {
        database.delete(
                ThreadSQLiteHelper.TABLE_THREAD,
                null,
                null
        );
    }

    public synchronized Cursor getCursor() {

        Cursor cursor = database.query(
                ThreadSQLiteHelper.TABLE_THREAD,
                allColumns,
                null, null, null, null, null
        );

        return cursor;
    }
}
