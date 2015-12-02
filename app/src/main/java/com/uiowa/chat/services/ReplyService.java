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

package com.uiowa.chat.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import com.uiowa.chat.utils.RegistrationUtils;
import com.uiowa.chat.utils.api.Sender;

public class ReplyService extends IntentService {

    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    public static final String EXTRA_THREAD_ID = "extra_thread_id";
    public static final String EXTRA_USERNAME = "extra_username";

    public ReplyService() {
        this("ReplyService");
    }

    public ReplyService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        Sender s = new Sender(this);
        s.sendThreadedMessage(
                intent.getStringExtra(EXTRA_USERNAME),
                intent.getLongExtra(EXTRA_THREAD_ID, -1),
                new RegistrationUtils().getMyUserId(this),
                remoteInput.getCharSequence(EXTRA_VOICE_REPLY).toString()
        );

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.cancel(PushNotificationService.MESSAGE_NOTIFICATION_ID);
    }

}
