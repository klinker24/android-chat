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

import org.whispersystems.libaxolotl.SessionBuilder
import org.whispersystems.libaxolotl.SessionCipher
import org.whispersystems.libaxolotl.protocol.CiphertextMessage
import org.whispersystems.libaxolotl.protocol.PreKeyWhisperMessage
import org.whispersystems.libaxolotl.protocol.WhisperMessage
import org.whispersystems.libaxolotl.state.AxolotlStore
import org.whispersystems.libaxolotl.state.PreKeyBundle
import org.whispersystems.libaxolotl.state.SessionRecord

public class Session {

    private Client client
    private SessionCipher cipher

    public Session(Client client) {
        this.client = client
    }

    public void buildSession(Client otherClient) {
        AxolotlStore store = new LabAxolotlStore(client)
        SessionBuilder builder = new SessionBuilder(store, otherClient.address)

        // store the current session so it isn't forgotten
        SessionRecord record = new SessionRecord()
        store.storeSession(otherClient.address, record)

        PreKeyBundle bundle = new PreKeyBundle(
                otherClient.registrationId,
                1,
                otherClient.preKeys.get(0).id,
                otherClient.preKeys.get(0).keyPair.publicKey,
                otherClient.signedPreKey.id,
                otherClient.signedPreKey.keyPair.publicKey,
                otherClient.signedPreKey.signature,
                otherClient.identityKeyPair.publicKey
        )

        builder.process(bundle)

        cipher = new SessionCipher(store, otherClient.address)
    }

    public byte[] encryptMessage(String message) {
        if (cipher == null) {
            throw new RuntimeException("Cipher not built yet, you need to call buildSession first()")
        }

        return cipher.encrypt(message.getBytes()).serialize()
    }

    public String decryptMessage(byte[] message, Client otherClient) {
        PreKeyWhisperMessage cipherMessage = new PreKeyWhisperMessage(message)
        return cipher.decrypt(cipherMessage)
    }

}