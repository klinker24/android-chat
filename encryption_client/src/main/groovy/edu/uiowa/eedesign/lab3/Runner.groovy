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

import java.lang.reflect.Field

public class Runner {

    private static final AxolotlAddress ALICE_ADDRESS = new AxolotlAddress("alice", 1)
    private static final AxolotlAddress BOB_ADDRESS = new AxolotlAddress("bob", 1)

    public static void main(String[] args) {
        enableSecurity()

        SenderSession aliceSession = new SenderSession()
        ReceiverSession bobSession = new ReceiverSession()

        aliceSession.requestPreKey()
        Distributable distributable = bobSession.generatePreKey()

        aliceSession.initSession(distributable, BOB_ADDRESS)
        bobSession.initSession(distributable, ALICE_ADDRESS)

        String originalMessage = "Testing 1, 2, 3!"
        byte[] encryptedMessage = aliceSession.encryptMessage(originalMessage)
        String decryptedMessage = bobSession.decryptMessage(encryptedMessage)

        println "Original Message: ${originalMessage}"
        println "Decoded Message:  ${decryptedMessage}"
        println()

        originalMessage = "Hello world!"
        encryptedMessage = bobSession.encryptMessage(originalMessage)
        decryptedMessage = aliceSession.decryptMessage(encryptedMessage)

        println "Original Message: ${originalMessage}"
        println "Decoded Message:  ${decryptedMessage}"
        println()

        originalMessage = "Third message."
        encryptedMessage = bobSession.encryptMessage(originalMessage)
        decryptedMessage = aliceSession.decryptMessage(encryptedMessage)

        println "Original Message: ${originalMessage}"
        println "Decoded Message:  ${decryptedMessage}"
        println()

        originalMessage = "And one last one for shits and giggles"
        encryptedMessage = aliceSession.encryptMessage(originalMessage)
        decryptedMessage = bobSession.decryptMessage(encryptedMessage)

        println "Original Message: ${originalMessage}"
        println "Decoded Message:  ${decryptedMessage}"
        println()
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