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
import org.whispersystems.libaxolotl.ecc.Curve
import org.whispersystems.libaxolotl.ecc.ECKeyPair
import org.whispersystems.libaxolotl.state.PreKeyBundle

import java.lang.reflect.Field

public class Runner {

    private static Session aliceSession
    private static Session bobSession

    public static void main(String[] args) {
        enableSecurity()

        final Client alice = new Client("Alice")
        final Client bob = new Client("Bob")

        Distributable distributable = Distributable.getInstance(bob)

        aliceSession = new Session(alice, bob)
        bobSession = new Session(bob, alice)

        aliceSession.buildSession(distributable)
        bobSession.buildSession(distributable)

        sendAliceMessage()
        //println()
        //sendBobMessage()
    }

    private static void sendAliceMessage() {
        String originalMessage = "Testing 1, 2, 3..."

        byte[] encryptedMessage = aliceSession.encryptMessage(originalMessage)
        String decryptedMessage = bobSession.decryptMessage(encryptedMessage)

        println "Alice sends: ${originalMessage}"
        println "Bob receives:  ${decryptedMessage}"
    }

    private static void sendBobMessage() {
        String originalMessage = "Hello World!"

        byte[] encryptedMessage = bobSession.encryptMessage(originalMessage)
        String decryptedMessage = aliceSession.decryptMessage(encryptedMessage)

        println "Bob sends: ${originalMessage}"
        println "Alice receives: ${decryptedMessage}"
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