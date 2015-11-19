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

import java.lang.reflect.Field

public class Runner {

    public static void main(String[] args) {
        enableSecurity()

        Client client1 = new Client("Jake")
        Client client2 = new Client("Luke")

        Session session = new Session(client1)
        session.buildSession(client2)
        byte[] encryptedMessage = session.encryptMessage("Hello World!")
        String message = session.decryptMessage(encryptedMessage, client2)
        println message
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