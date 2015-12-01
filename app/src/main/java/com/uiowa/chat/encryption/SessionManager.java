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

package com.uiowa.chat.encryption;

import android.util.Base64;

import org.whispersystems.libaxolotl.AxolotlAddress;

import java.util.HashMap;

public class SessionManager {

    private HashMap<String, Session> sessions;

    public SessionManager() {
        sessions = new HashMap<String, Session>();
    }

    public boolean isCreated(String address) {
        return sessions.containsKey(address);
    }

    public Distributable initReceiverSession(String senderAddress) {
        Session session = new ReceiverSession();

        try {
            Distributable distributable = ((ReceiverSession) session).generatePreKey();
            session.initSession(distributable, new AxolotlAddress(senderAddress, 1));

            sessions.put(senderAddress, session);

            return distributable;
        } catch (Exception e) {
            throw new RuntimeException("failed to initialize receiver session", e);
        }
    }

    public void initSenderSession(Distributable distributable, String receiverAddress) {
        Session session = new SenderSession();

        try {
            session.initSession(distributable, new AxolotlAddress(receiverAddress, 1));
            sessions.put(receiverAddress, session);
        } catch (Exception e) {
            throw new RuntimeException("failed to initialize receiver session", e);
        }
    }

    public String encrypt(String address, String message) {
        return Base64.encodeToString(sessions.get(address).encryptMessage(message), 0);
    }

    public String decrypt(String address, String message) {
        try {
            return sessions.get(address).decryptMessage(Base64.decode(message, 0));
        } catch (Exception e) {
            throw new RuntimeException("failed to decrypt message", e);
        }
    }

}
