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
import org.whispersystems.libaxolotl.SessionBuilder
import org.whispersystems.libaxolotl.SessionCipher

/**
 * Sender session represents the person who initiated the communication
 */
public class SenderSession extends Session {

    public void initSession(Distributable distributable, AxolotlAddress address) {
        SessionBuilder builder = new SessionBuilder(store, address)
        builder.process(distributable.preKey)
        cipher = new SessionCipher(store, address)
    }

    public void requestPreKey() {
        // do nothing for now, this will be implemented using sms in android
    }

}
