package com.uiowa.chat.data.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.uiowa.chat.data.User;

public class UserDataSource {
    // provides access to the database
    public static UserDataSource dataSource = null;

    /*

    This is used so that we don't have to open and close the database on different threads or fragments
    every time. This will facilitate it between all of them to avoid Illegal State Exceptions.

     */
    public static UserDataSource getInstance(Context context) {

        // if the datasource isn't open or it the object is null
        if (dataSource == null ||
                dataSource.getDatabase() == null ||
                !dataSource.getDatabase().isOpen()) {

            dataSource = new UserDataSource(context); // create the database
            dataSource.open(); // open the database
        }

        return dataSource;
    }

    private SQLiteDatabase database;
    private UserSQLiteHelper dbHelper;
    public String[] allColumns = {
            UserSQLiteHelper.COLUMN_ID,
            UserSQLiteHelper.COLUMN_REAL_NAME,
            UserSQLiteHelper.COLUMN_USERNAME,
    };

    public UserDataSource(Context context) {
        dbHelper = new UserSQLiteHelper(context);
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

    public UserSQLiteHelper getHelper() {
        return dbHelper;
    }

    public synchronized void createUser(JsonObject user) {
        ContentValues values = getValues(user);

        database.insert(
                UserSQLiteHelper.TABLE_USER,
                null,
                values);
    }

    public synchronized void createUsers(JsonArray users) {
        if (users == null) {
            return;
        }

        ContentValues[] values = new ContentValues[users.size()];
        for (int i = 0; i < values.length; i++) {
            JsonElement e = users.get(i);
            values[i] = getValues(e.getAsJsonObject());
        }

        insertMultiple(values);
    }

    public ContentValues getValues(JsonObject user) {
        ContentValues values = new ContentValues();

        values.put(UserSQLiteHelper.COLUMN_ID, user.get("id").getAsLong());
        values.put(UserSQLiteHelper.COLUMN_USERNAME, user.get("username").getAsString());
        values.put(UserSQLiteHelper.COLUMN_REAL_NAME, user.get("realName").getAsString());
        values.put(UserSQLiteHelper.COLUMN_DEV_ID, user.get("deviceId").getAsString());

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
                    rowId = database.insert(UserSQLiteHelper.TABLE_USER, null, initialValues);
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

    public synchronized void deleteUser(long userId) {
        database.delete(
                UserSQLiteHelper.TABLE_USER,
                UserSQLiteHelper.COLUMN_ID + " = ?",
                new String[] { userId + "" }
        );

    }

    public synchronized void deleteAllUsers() {
        database.delete(
                UserSQLiteHelper.TABLE_USER,
                null,
                null
        );
    }

    public synchronized Cursor getAllUsersCursor() {

        Cursor cursor = database.query(
                UserSQLiteHelper.TABLE_USER,
                allColumns,
                null, null, null, null, null
        );

        return cursor;
    }

    public synchronized Cursor getUserCursor(long userId) {
        return database.query(
                UserSQLiteHelper.TABLE_USER,
                allColumns,
                UserSQLiteHelper.COLUMN_ID + " = ?", new String[] { userId + "" },
                null, null, null
        );
    }

    public synchronized Cursor getUserCursor(String username) {
        return database.query(
                UserSQLiteHelper.TABLE_USER,
                allColumns,
                UserSQLiteHelper.COLUMN_USERNAME + " = ?", new String[] { username + "" },
                null, null, null
        );
    }

}
