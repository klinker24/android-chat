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
import org.whispersystems.libaxolotl.ecc.Curve
import org.whispersystems.libaxolotl.ecc.ECKeyPair
import org.whispersystems.libaxolotl.protocol.PreKeyWhisperMessage
import org.whispersystems.libaxolotl.state.PreKeyBundle
import org.whispersystems.libaxolotl.state.PreKeyRecord
import org.whispersystems.libaxolotl.state.SignedPreKeyRecord

/**
 * Receiver session represents the person who received the request to communicate
 */
public class ReceiverSession extends Session {

    boolean processedFirstMessage = false

    public Distributable generatePreKey() {
        ECKeyPair preKeyPair = Curve.generateKeyPair()
        ECKeyPair signedPreKeyPair = Curve.generateKeyPair()
        byte[] signedPreKeySignature = Curve.calculateSignature(store.getIdentityKeyPair()
                .getPrivateKey(), signedPreKeyPair.getPublicKey().serialize())

        PreKeyBundle preKey = new PreKeyBundle(store.getLocalRegistrationId(), 1,
                2, preKeyPair.getPublicKey(),
                3, signedPreKeyPair.getPublicKey(),
                signedPreKeySignature,
                store.getIdentityKeyPair().getPublicKey())

        Distributable distributable = new Distributable()
        distributable.preKeyPair = preKeyPair
        distributable.signedPreKeyPair = signedPreKeyPair
        distributable.signedPreKeySignature = signedPreKeySignature
        distributable.preKey = preKey

        return distributable
    }
    
    public void initSession(Distributable distributable, AxolotlAddress address) {
        store.storePreKey(2, new PreKeyRecord(distributable.preKey.getPreKeyId(),
                distributable.preKeyPair))
        store.storeSignedPreKey(3, new SignedPreKeyRecord(3, System.currentTimeMillis(),
                distributable.signedPreKeyPair, distributable.signedPreKeySignature))

        cipher = new SessionCipher(store, address)
    }

    @Override
    public String decryptMessage(byte[] encryptedMessage) {
        if (!processedFirstMessage) {
            PreKeyWhisperMessage incomingMessage = new PreKeyWhisperMessage(encryptedMessage)
            byte[] plaintext = cipher.decrypt(incomingMessage)
            processedFirstMessage = true

            return new String(plaintext)
        } else {
            return super.decryptMessage(encryptedMessage)
        }
    }

}
