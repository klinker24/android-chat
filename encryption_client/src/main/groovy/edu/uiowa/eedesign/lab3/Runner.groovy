/*
 * Copyright (C) 2015 Jacob Klinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License") 
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
import org.whispersystems.libaxolotl.SessionBuilder
import org.whispersystems.libaxolotl.SessionCipher
import org.whispersystems.libaxolotl.ecc.Curve
import org.whispersystems.libaxolotl.ecc.ECKeyPair
import org.whispersystems.libaxolotl.protocol.CiphertextMessage
import org.whispersystems.libaxolotl.protocol.PreKeyWhisperMessage
import org.whispersystems.libaxolotl.protocol.WhisperMessage
import org.whispersystems.libaxolotl.state.AxolotlStore
import org.whispersystems.libaxolotl.state.PreKeyBundle
import org.whispersystems.libaxolotl.state.PreKeyRecord
import org.whispersystems.libaxolotl.state.SignedPreKeyRecord

import java.lang.reflect.Field

public class Runner {

    private static final AxolotlAddress ALICE_ADDRESS = new AxolotlAddress("alice", 1)
    private static final AxolotlAddress BOB_ADDRESS = new AxolotlAddress("bob", 1)

    public static void main(String[] args) {
        enableSecurity()

        AxolotlStore aliceStore = new TestInMemoryAxolotlStore()
        SessionBuilder aliceSessionBuilder = new SessionBuilder(aliceStore, BOB_ADDRESS)

        final AxolotlStore bobStore = new TestInMemoryAxolotlStore()
        ECKeyPair bobPreKeyPair = Curve.generateKeyPair()
        ECKeyPair bobSignedPreKeyPair = Curve.generateKeyPair()
        byte[] bobSignedPreKeySignature = Curve.calculateSignature(bobStore.getIdentityKeyPair().getPrivateKey(),
                bobSignedPreKeyPair.getPublicKey().serialize())

        PreKeyBundle bobPreKey = new PreKeyBundle(bobStore.getLocalRegistrationId(), 1,
                2, bobPreKeyPair.getPublicKey(),
                3, bobSignedPreKeyPair.getPublicKey(),
                bobSignedPreKeySignature,
                bobStore.getIdentityKeyPair().getPublicKey())

        aliceSessionBuilder.process(bobPreKey)

        String originalMessage = "Testing 1, 2, 3!"
        SessionCipher aliceSessionCipher = new SessionCipher(aliceStore, BOB_ADDRESS)
        CiphertextMessage outgoingMessage = aliceSessionCipher.encrypt(originalMessage.getBytes())

        PreKeyWhisperMessage incomingMessage = new PreKeyWhisperMessage(outgoingMessage.serialize())
        bobStore.storePreKey(2, new PreKeyRecord(bobPreKey.getPreKeyId(), bobPreKeyPair))
        bobStore.storeSignedPreKey(3, new SignedPreKeyRecord(3, System.currentTimeMillis(), bobSignedPreKeyPair, bobSignedPreKeySignature))

        SessionCipher bobSessionCipher = new SessionCipher(bobStore, ALICE_ADDRESS)
        byte[] plaintext = bobSessionCipher.decrypt(incomingMessage)

        println "Original Message: ${originalMessage}"
        println "Decoded Message:  ${new String(plaintext)}"
        println()

        originalMessage = "Hello world!"
        outgoingMessage = bobSessionCipher.encrypt(originalMessage.getBytes())
        plaintext = aliceSessionCipher.decrypt(new WhisperMessage(outgoingMessage.serialize()))

        println "Original Message: ${originalMessage}"
        println "Decoded Message:  ${new String(plaintext)}"
        println()

        originalMessage = "Third message."
        outgoingMessage = aliceSessionCipher.encrypt(originalMessage.getBytes())
        plaintext = bobSessionCipher.decrypt(new WhisperMessage(outgoingMessage.serialize()))

        println "Original Message: ${originalMessage}"
        println "Decoded Message:  ${new String(plaintext)}"
        println()

        originalMessage = "And one last one for shits and giggles."
        outgoingMessage = bobSessionCipher.encrypt(originalMessage.getBytes())
        plaintext = aliceSessionCipher.decrypt(new WhisperMessage(outgoingMessage.serialize()))

        println "Original Message: ${originalMessage}"
        println "Decoded Message:  ${new String(plaintext)}"
    }

    private static void enableSecurity() {
        try {
            Field field = Class.forName("javax.crypto.JceSecurity").
                    getDeclaredField("isRestricted")
            field.setAccessible(true)
            field.set(null, java.lang.Boolean.FALSE)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

}