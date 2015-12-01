/*
 * Copyright (C) 2015 Jacob Klinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uiowa.chat.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.uiowa.chat.ChatApplication;
import com.uiowa.chat.activities.NewMessageActivity;
import com.uiowa.chat.data.DatabaseHelper;
import com.uiowa.chat.data.User;
import com.uiowa.chat.encryption.Distributable;
import com.uiowa.chat.encryption.SessionManager;
import com.uiowa.chat.utils.RegistrationUtils;

import java.util.ArrayList;

/**
 * Handles receiving sms and sending back the prekey information when needed
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";
    private static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            if (sms == null) {
                return;
            }

            String message = "";
            String address = "";

            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                message += smsMessage.getMessageBody();
                address = smsMessage.getOriginatingAddress();
            }

            RegistrationUtils utils = new RegistrationUtils();
            DatabaseHelper helper = new DatabaseHelper(context);
            final long currentUserId = utils.getMyUserId(context);
            final User currentUser = helper.findUser(currentUserId);

            if (message.startsWith("Hey from encrypted chat, want to start a conversation with ")) {
                Log.v(TAG, "got request to start new conversation, so starting it by sending them " +
                        "a prekey bundle");

                String[] messageParts = message.split(" ");
                String user = messageParts[messageParts.length - 1].replace("?", "");

                ChatApplication application = (ChatApplication) context.getApplicationContext();
                SessionManager manager = application.getSessionManager();
                Distributable distributable = manager.initReceiverSession(user);

                Log.v(TAG, "sent prekey: " + distributable.toString());

                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> dividedMessage = smsManager.divideMessage(
                        "PreKey: " + distributable.toString() + " " + currentUser.getUsername()
                );

                smsManager.sendMultipartTextMessage(address, null, dividedMessage, null, null);
                Log.v(TAG, "initialized receiving session, ready to receive");
            } else if (message.startsWith("PreKey: ")) {
                Log.v(TAG, "received prekey message: " + message);
                message = message.substring(8);
                Log.v(TAG, "trimmed off first part of message: " + message);

                String[] messageParts = message.split(" ");
                String user = messageParts[messageParts.length - 1];

                Log.v(TAG, "found message from " + user);
                message = message.replace(" " + user, "");
                Log.v(TAG, "removed username from distributable: " + message);

                ChatApplication application = (ChatApplication) context.getApplicationContext();
                SessionManager manager = application.getSessionManager();
                manager.initSenderSession(Distributable.parseString(message), user);
                Log.v(TAG, "initialized sending session, ready to send messages");
            }
        }
    }

}
