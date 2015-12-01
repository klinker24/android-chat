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

package com.uiowa.chat.encryption;

import org.whispersystems.libaxolotl.IdentityKey;
import org.whispersystems.libaxolotl.ecc.ECKeyPair;
import org.whispersystems.libaxolotl.ecc.ECPublicKey;
import org.whispersystems.libaxolotl.state.PreKeyBundle;

public class Distributable {

    ECKeyPair preKeyPair;
    ECKeyPair signedPreKeyPair;
    byte[] signedPreKeySignature;
    PreKeyBundle preKey;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(preKey.getRegistrationId());
        builder.append(",");
        builder.append(preKey.getDeviceId());
        builder.append(",");
        builder.append(preKey.getPreKeyId());
        builder.append(",");
        builder.append(new String(((DjbECPublicKey) preKey.getPreKey()).getPublicKey()));
        builder.append(",");
        builder.append(preKey.getSignedPreKeyId());
        builder.append(",");
        builder.append(new String(((DjbECPublicKey) preKey.getSignedPreKey()).getPublicKey()));
        builder.append(",");
        builder.append(new String(preKey.getSignedPreKeySignature()));
        builder.append(",");
        builder.append(new String(((DjbECPublicKey) preKey.getIdentityKey().getPublicKey()).getPublicKey()));

        return builder.toString();
    }

    public static Distributable parseString(String string) {
        Distributable distributable = new Distributable();

        String[] parts = string.split(",");
        int registrationId = Integer.parseInt(parts[0]);
        int deviceId = Integer.parseInt(parts[1]);
        int preKeyId = Integer.parseInt(parts[2]);
        ECPublicKey preKeyPublic = new DjbECPublicKey(parts[3].getBytes());
        int signedPreKey = Integer.parseInt(parts[4]);
        ECPublicKey signedPreKeyPublic = new DjbECPublicKey(parts[5].getBytes());
        byte[] signedPreKeySignature = parts[6].getBytes();
        IdentityKey identityKey = new IdentityKey(new DjbECPublicKey(parts[7].getBytes()));

        distributable.preKey = new PreKeyBundle(registrationId, deviceId, preKeyId, preKeyPublic,
                signedPreKey, signedPreKeyPublic, signedPreKeySignature, identityKey);

        return distributable;
    }

}
