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

import org.whispersystems.libaxolotl.ecc.Curve
import org.whispersystems.libaxolotl.ecc.ECKeyPair
import org.whispersystems.libaxolotl.state.PreKeyBundle

public class Distributable {

    ECKeyPair preKeyPair
    ECKeyPair signedPreKeyPair
    byte[] signedPreKeyPairSignature
    PreKeyBundle preKey

    private Distributable() {

    }

    public static Distributable getInstance(Client client) {
        Distributable distributable = new Distributable()

        distributable.preKeyPair = Curve.generateKeyPair()
        distributable.signedPreKeyPair = Curve.generateKeyPair()
        distributable.signedPreKeyPairSignature = Curve.calculateSignature(
                client.store.getIdentityKeyPair().getPrivateKey(),
                distributable.signedPreKeyPair.getPublicKey().serialize())
        distributable.preKey = generatePreKey(client, distributable.preKeyPair,
                distributable.signedPreKeyPair, distributable.signedPreKeyPairSignature)

        return distributable
    }

    private static PreKeyBundle generatePreKey(Client client, ECKeyPair preKeyPair,
                                               ECKeyPair signedPreKeyPair,
                                               byte[] signedPreKeyPairSignature) {
        return new PreKeyBundle(client.store.getLocalRegistrationId(), 1,
                2, preKeyPair.getPublicKey(),
                3, signedPreKeyPair.getPublicKey(),
                signedPreKeyPairSignature,
                client.store.getIdentityKeyPair().getPublicKey())
    }

}