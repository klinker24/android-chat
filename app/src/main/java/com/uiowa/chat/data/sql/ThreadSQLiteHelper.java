package com.uiowa.chat.data.sql;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ThreadSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_THREAD = "thread";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_USER_1 = "user_1";
    public static final String COLUMN_USER_2 = "user_2";

    private static final String DATABASE_NAME = "thread.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_THREAD + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_TITLE + " messageText thread title, "
            + COLUMN_USER_1 + " integer user one, "
            + COLUMN_USER_2 + " integer user two"
            + "); ";

    public ThreadSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MessageSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THREAD);
        onCreate(db);
    }

}