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

import org.whispersystems.libaxolotl.AxolotlAddress;

public class SessionManager {

    private Session session;

    public Session getSession() {
        return session;
    }

    public boolean isCreated() {
        return session != null;
    }

    public Distributable initReceiverSession(String senderAddress) {
        session = new ReceiverSession();

        try {
            Distributable distributable = ((ReceiverSession) session).generatePreKey();
            session.initSession(distributable, new AxolotlAddress(senderAddress, 1));
            return distributable;
        } catch (Exception e) {
            throw new RuntimeException("failed to initialize receiver session");
        }
    }

    public void initSenderSession(Distributable distributable, String receiverAddress) {
        session = new SenderSession();

        try {
            session.initSession(distributable, new AxolotlAddress(receiverAddress, 1));
        } catch (Exception e) {
            throw new RuntimeException("failed to initialize receiver session");
        }
    }

    public String encrypt(String message) {
        return new String(session.encryptMessage(message));
    }

    public String decrypt(String message) {
        try {
            return session.decryptMessage(message.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("failed to decrypt message");
        }
    }

}
