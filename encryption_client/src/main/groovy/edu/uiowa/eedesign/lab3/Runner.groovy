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

    public static void main(String[] args) {
        enableSecurity()

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))

        print 'User 1 name: '
        AxolotlAddress user1 = new AxolotlAddress(reader.readLine(), 1)
        print 'User 2 name: '
        AxolotlAddress user2 = new AxolotlAddress(reader.readLine(), 1)

        SenderSession sender = new SenderSession()
        ReceiverSession receiver = new ReceiverSession()

        sender.requestPreKey()
        Distributable distributable = receiver.generatePreKey()

        sender.initSession(distributable, user2)
        receiver.initSession(distributable, user1)

        String message = null
        boolean sendUser1 = true

        while (true) {
            print "Enter message for ${sendUser1 ? user2.name : user1.name}: "
            message = reader.readLine()

            if (message.equals("exit")) {
                break
            }

            if (sendUser1) {
                message = receiver.decryptMessage(sender.encryptMessage(message))
            } else {
                message = sender.decryptMessage(receiver.encryptMessage(message))
            }

            println "${sendUser1 ? user2.name : user1.name} received: ${message}\n"
            sendUser1 = !sendUser1
        }

        reader.close()
    }

    private static void enableSecurity() {
        try {
            Field field = Class.forName("javax.crypto.JceSecurity").
                    getDeclaredField("isRestricted")
            field.setAccessible(true)
            field.set(null, Boolean.FALSE)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

}