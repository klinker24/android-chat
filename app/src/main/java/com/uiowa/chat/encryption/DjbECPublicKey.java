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

import org.whispersystems.libaxolotl.ecc.Curve;
import org.whispersystems.libaxolotl.ecc.ECPublicKey;
import org.whispersystems.libaxolotl.util.ByteUtil;

import java.math.BigInteger;
import java.util.Arrays;

public class DjbECPublicKey implements ECPublicKey {

    private final byte[] publicKey;

    DjbECPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public byte[] serialize() {
        byte[] type = {Curve.DJB_TYPE};
        return ByteUtil.combine(type, publicKey);
    }

    @Override
    public int getType() {
        return Curve.DJB_TYPE;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)                      return false;
        if (!(other instanceof DjbECPublicKey)) return false;

        DjbECPublicKey that = (DjbECPublicKey)other;
        return Arrays.equals(this.publicKey, that.publicKey);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(publicKey);
    }

    @Override
    public int compareTo(ECPublicKey another) {
        return new BigInteger(publicKey).compareTo(new BigInteger(((DjbECPublicKey)another).publicKey));
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}
