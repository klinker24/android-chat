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
import org.whispersystems.libaxolotl.IdentityKey
import org.whispersystems.libaxolotl.IdentityKeyPair
import org.whispersystems.libaxolotl.InvalidKeyIdException
import org.whispersystems.libaxolotl.state.AxolotlStore
import org.whispersystems.libaxolotl.state.PreKeyRecord
import org.whispersystems.libaxolotl.state.SessionRecord
import org.whispersystems.libaxolotl.state.SignedPreKeyRecord

public class LabAxolotlStore implements AxolotlStore {

    private IdentityKeyPair keyPair
    private int registrationId
    private HashMap<String, IdentityKey> identities
    private HashMap<Integer, PreKeyRecord> preKeys
    private HashMap<Integer, SignedPreKeyRecord> signedPreKeyRecords
    private HashMap<AxolotlAddress, SessionRecord> sessions

    public LabAxolotlStore(Client client) {
        this.keyPair = client.identityKeyPair
        this.registrationId = client.registrationId
        this.identities = new HashMap<String, IdentityKey>()

        this.preKeys = new HashMap<Integer, PreKeyRecord>()
        for (PreKeyRecord record : client.preKeys) {
            this.preKeys.put(record.id, record)
        }

        this.preKeys.put(client.lastResortKey.id, client.lastResortKey)

        this.signedPreKeyRecords = new HashMap<Integer, SignedPreKeyRecord>()
        this.signedPreKeyRecords.put(client.signedPreKey.id, client.signedPreKey)

        this.sessions = new HashMap<AxolotlAddress, SessionRecord>()
    }

    @Override
    public IdentityKeyPair getIdentityKeyPair() {
        return keyPair
    }

    @Override
    public int getLocalRegistrationId() {
        return registrationId
    }

    @Override
    public void saveIdentity(String name, IdentityKey identityKey) {
        identities.put(name, identityKey)
    }

    @Override
    public boolean isTrustedIdentity(String name, IdentityKey identityKey) {
        IdentityKey key = identities.get(name)
        return key == null || key.fingerprint.equals(identityKey.fingerprint)
    }

    @Override
    public PreKeyRecord loadPreKey(int preKeyId) throws InvalidKeyIdException {
        if (preKeys.containsKey(preKeyId)) {
            return preKeys.get(preKeyId)
        } else {
            throw new InvalidKeyIdException("Invalid key id ${preKeyId}")
        }
    }

    @Override
    public void storePreKey(int preKeyId, PreKeyRecord record) {
        preKeys.put(preKeyId, record)
    }

    @Override
    public boolean containsPreKey(int preKeyId) {
        return preKeys.containsKey(preKeyId)
    }

    @Override
    public void removePreKey(int preKeyId) {
        preKeys.remove(preKeyId)
    }

    @Override
    public SessionRecord loadSession(AxolotlAddress address) {
        return sessions.get(address)
    }

    @Override
    public List<Integer> getSubDeviceSessions(String name) {
        return []
    }

    @Override
    public void storeSession(AxolotlAddress address, SessionRecord record) {
        sessions.put(address, record)
    }

    @Override
    public boolean containsSession(AxolotlAddress address) {
        return sessions.containsKey(address)
    }

    @Override
    public void deleteSession(AxolotlAddress address) {
        sessions.remove(address)
    }

    @Override
    public void deleteAllSessions(String name) {
        sessions = new HashMap<AxolotlAddress, SessionRecord>()
    }

    @Override
    public SignedPreKeyRecord loadSignedPreKey(int signedPreKeyId) throws InvalidKeyIdException {
        if (signedPreKeyRecords.containsKey(signedPreKeyId)) {
            return signedPreKeyRecords.get(signedPreKeyId)
        } else {
            throw new InvalidKeyIdException("Invalid signed pre key id ${signedPreKeyId}")
        }
    }

    @Override
    public List<SignedPreKeyRecord> loadSignedPreKeys() {
        def keys = []
        signedPreKeyRecords.entrySet().each { entry ->
            keys << entry.getValue()
        }
        return keys
    }

    @Override
    public void storeSignedPreKey(int signedPreKeyId, SignedPreKeyRecord record) {
        signedPreKeyRecords.put(signedPreKeyId, record)
    }

    @Override
    public boolean containsSignedPreKey(int signedPreKeyId) {
        return signedPreKeyRecords.containsKey(signedPreKeyId)
    }

    @Override
    public void removeSignedPreKey(int signedPreKeyId) {
        signedPreKeyRecords.remove(signedPreKeyId)
    }

}