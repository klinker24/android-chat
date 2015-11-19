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
import org.whispersystems.libaxolotl.ecc.ECKeyPair
import org.whispersystems.libaxolotl.state.PreKeyBundle
import org.whispersystems.libaxolotl.state.impl.InMemoryAxolotlStore

public class Client {

    InMemoryAxolotlStore store
    AxolotlAddress address

    public Client(String name) {
        this.store = new TestInMemoryAxolotlStore()
        this.address = new AxolotlAddress(name, 1)
    }

}