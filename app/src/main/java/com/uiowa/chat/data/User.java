package com.uiowa.chat.data;

import android.database.Cursor;

import com.uiowa.chat.data.sql.UserSQLiteHelper;

import lombok.Getter;
import lombok.Setter;


/**
 * Data object for the users
 *
 * Use this object instead of accessing the cursor directly.
 *
 * Available fields:
 *      - userId
 *      - deviceId (used by Google Cloud Messaging to send you push notifications)
 *      - realName
 *      - username
 *      - messageText
 */
@Getter @Setter
public class User {

    private long userId;
    private String deviceId;
    private String realName;
    private String username;

    public void fillFromCursor(Cursor c) {
        if (c == null) {
            return;
        }

        String[] cols = c.getColumnNames();
        for (int i = 0; i < cols.length; i++) {
            if (UserSQLiteHelper.COLUMN_ID.equals(cols[i])) {
                this.userId = c.getLong(i);
            } else if (UserSQLiteHelper.COLUMN_USERNAME.equals(cols[i])) {
                this.username = c.getString(i);
            } else if (UserSQLiteHelper.COLUMN_REAL_NAME.equals(cols[i])) {
                this.realName = c.getString(i);
            } else if (UserSQLiteHelper.COLUMN_DEV_ID.equals(cols[i])) {
                this.deviceId = c.getString(i);
            }
        }
    }
}
