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

import android.util.Base64;
import org.whispersystems.libaxolotl.IdentityKey;
import org.whispersystems.libaxolotl.ecc.DjbECPublicKey;
import org.whispersystems.libaxolotl.ecc.ECKeyPair;
import org.whispersystems.libaxolotl.ecc.ECPublicKey;
import org.whispersystems.libaxolotl.state.PreKeyBundle;

import java.lang.reflect.Constructor;

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
        builder.append(Base64.encodeToString(((DjbECPublicKey) preKey.getPreKey()).getPublicKey(), 0));
        builder.append(",");
        builder.append(preKey.getSignedPreKeyId());
        builder.append(",");
        builder.append(Base64.encodeToString(((DjbECPublicKey) preKey.getSignedPreKey()).getPublicKey(), 0));
        builder.append(",");
        builder.append(Base64.encodeToString(preKey.getSignedPreKeySignature(), 0));
        builder.append(",");
        builder.append(Base64.encodeToString(((DjbECPublicKey) preKey.getIdentityKey().getPublicKey()).getPublicKey(), 0));

        return builder.toString();
    }

    public static Distributable parseString(String string) {
        Distributable distributable = new Distributable();

        String[] parts = string.split(",");
        int registrationId = Integer.parseInt(parts[0]);
        int deviceId = Integer.parseInt(parts[1]);
        int preKeyId = Integer.parseInt(parts[2]);
        ECPublicKey preKeyPublic = createPublicKey(Base64.decode(parts[3], 0));
        int signedPreKey = Integer.parseInt(parts[4]);
        ECPublicKey signedPreKeyPublic = createPublicKey(Base64.decode(parts[5], 0));
        byte[] signedPreKeySignature = Base64.decode(parts[6], 0);
        IdentityKey identityKey = new IdentityKey(createPublicKey(Base64.decode(parts[7], 0)));

        distributable.preKey = new PreKeyBundle(registrationId, deviceId, preKeyId, preKeyPublic,
                signedPreKey, signedPreKeyPublic, signedPreKeySignature, identityKey);

        return distributable;
    }

    private static DjbECPublicKey createPublicKey(byte[] bytes) {
        try {
            Constructor constructor = DjbECPublicKey.class.getDeclaredConstructor(byte[].class);
            constructor.setAccessible(true);
            return (DjbECPublicKey) constructor.newInstance(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't construct object", e);
        }
    }

}
