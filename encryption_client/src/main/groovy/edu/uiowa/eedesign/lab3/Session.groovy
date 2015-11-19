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
import org.whispersystems.libaxolotl.ecc.ECKeyPair
import org.whispersystems.libaxolotl.protocol.PreKeyWhisperMessage
import org.whispersystems.libaxolotl.state.PreKeyBundle
import org.whispersystems.libaxolotl.state.PreKeyRecord
import org.whispersystems.libaxolotl.state.SignedPreKeyRecord

import java.security.interfaces.ECKey

public class Session {

    private Client alice
    private Client bob
    private SessionCipher cipher
    private Distributable distributable

    public Session(Client alice, Client bob) {
        this.alice = alice
        this.bob = bob
    }

    public void buildSession(Distributable distributable) {
        SessionBuilder builder = new SessionBuilder(alice.store, bob.address)
        builder.process(distributable.preKey)

        this.cipher = new SessionCipher(alice.store, bob.address)
        this.distributable = distributable
    }

    public byte[] encryptMessage(String message) {
        return cipher.encrypt(message.getBytes()).serialize()
    }

    public String decryptMessage(byte[] message) {
        PreKeyWhisperMessage incomingMessage = new PreKeyWhisperMessage(message)

        alice.store.storePreKey(2, new PreKeyRecord(distributable.preKey.getPreKeyId(),
                distributable.preKeyPair))
        alice.store.storeSignedPreKey(3, new SignedPreKeyRecord(3,
                System.currentTimeMillis(), distributable.signedPreKeyPair,
                distributable.signedPreKeyPairSignature))

        byte[] plaintext = cipher.decrypt(incomingMessage)
        return new String(plaintext)
    }

}