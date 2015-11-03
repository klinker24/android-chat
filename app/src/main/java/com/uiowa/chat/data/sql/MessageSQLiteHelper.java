package com.uiowa.chat.data.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This is the class that creates and opens the database for you.
 *
 * You can also use it for more advanced things like upgrading your databases
 * and telling it what rules to follow.
 */
public class MessageSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_MESSAGE = "message";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_THREAD_ID = "thread_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_SENDER_ID = "sender_id";

    private static final String DATABASE_NAME = "message.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_MESSAGE + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_THREAD_ID + " integer thread number, "
            + COLUMN_TIME + " integer time number, "
            + COLUMN_TEXT + " messageText message, "
            + COLUMN_SENDER_ID + " integer user"
            + "); ";

    public MessageSQLiteHelper(Context context) {
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        onCreate(db);
    }

}