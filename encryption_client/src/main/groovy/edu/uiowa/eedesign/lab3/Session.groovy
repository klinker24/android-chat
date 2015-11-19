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

package edu.uiowa.eedesign.lab3

import org.whispersystems.libaxolotl.AxolotlAddress
import org.whispersystems.libaxolotl.SessionCipher
import org.whispersystems.libaxolotl.protocol.WhisperMessage
import org.whispersystems.libaxolotl.state.AxolotlStore

public abstract class Session {

    protected AxolotlStore store
    protected SessionCipher cipher

    public Session() {
        store = new TestInMemoryAxolotlStore()
    }

    public abstract void initSession(Distributable distributable, AxolotlAddress address)

    public byte[] encryptMessage(String plaintextMessage) {
        return cipher.encrypt(plaintextMessage.getBytes()).serialize()
    }

    public String decryptMessage(byte[] encryptedMessage) {
        return new String(cipher.decrypt(new WhisperMessage(encryptedMessage)))
    }

}
