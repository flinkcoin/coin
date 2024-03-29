/*
 * Copyright © 2021 Flink Foundation (info@flinkcoin.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flinkcoin.crypto;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;

public class PrivateKey extends Key {

    private final EdDSAPrivateKey edDSAPrivateKey;
    private final byte[] privateKey;

    public PrivateKey(EdDSAPrivateKey edDSAPrivateKey, byte[] privateKey) {
        this.edDSAPrivateKey = edDSAPrivateKey;
        this.privateKey = privateKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public EdDSAPrivateKey getEdDSAPrivateKey() {
        return edDSAPrivateKey;
    }

}
