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
package org.flinkcoin.node.configuration.bootstrap;

import org.flinkcoin.helper.helpers.Base32Helper;
import com.google.protobuf.ByteString;

public enum Network {

    PROD("RA2RBX6FTNGDLLQJ4NPQSFBFLQ", "WPNLM4B7TGBCN3MNV7UMG6LQ6B2MM7ELNY2JMXT7FVS4UVNXWV4SLC5UIZ3SH6AKSCO7SSU7VDE7QPDKJB6NI4RUSWNWOAIV6G2OQJQ", "BLPQCCSVBAASUEEIGUIN7RM3JQ224CPDL4ERIJK4GIINUSC4BRQXMTLWUF545A53ZB5YEOEAQCIJJDUKTPZXYYRDCIQQASGIOD6TMHWEVO5WFG2ZK4LQVEZZUHFXKQ5WRVAGAAZO77RO77Z7CJBAUQFT3K3HAP4ZQITO3DNP5DBXS4HQOTDHZC3OGSLF47ZNMXFFLN5VPESYXNCGO4R7QCUQTX4UVH5IZH4DY2SIPTKHENEVTNTQCFPRWTUCMGSCBJAGFLE5MJUWVLYCUIH3LCAWGYR3UDIWRKTB5KEBSH3EWQPMHCGNQPHPCPCYCRERCAOCDDPO7LFYAJNJ5PRF3C25LSJE4AQ7NQ6QRTPAB4"),
    TEST("RA2RBX6FTNGDLLQJ4NPQSFBFLQ", "WPNLM4B7TGBCN3MNV7UMG6LQ6B2MM7ELNY2JMXT7FVS4UVNXWV4SLC5UIZ3SH6AKSCO7SSU7VDE7QPDKJB6NI4RUSWNWOAIV6G2OQJQ", "BLPQCCSVBAASUEEIGUIN7RM3JQ224CPDL4ERIJK4GIINUSC4BRQXMTLWUF545A53ZB5YEOEAQCIJJDUKTPZXYYRDCIQQASGIOD6TMHWEVO5WFG2ZK4LQVEZZUHFXKQ5WRVAGAAZO77RO77Z7CJBAUQFT3K3HAP4ZQITO3DNP5DBXS4HQOTDHZC3OGSLF47ZNMXFFLN5VPESYXNCGO4R7QCUQTX4UVH5IZH4DY2SIPTKHENEVTNTQCFPRWTUCMGSCBJAGFLE5MJUWVLYCUIH3LCAWGYR3UDIWRKTB5KEBSH3EWQPMHCGNQPHPCPCYCRERCAOCDDPO7LFYAJNJ5PRF3C25LSJE4AQ7NQ6QRTPAB4");

    private final String genesisAccountId;
    private final String genesisBlockHash;
    private final String genesisBlock;

    private Network(String genesisAccountId, String genesisBlockHash, String genesisBlock) {
        this.genesisAccountId = genesisAccountId;
        this.genesisBlockHash = genesisBlockHash;
        this.genesisBlock = genesisBlock;
    }

    public ByteString getGenesisAccountId() {
        byte[] bytes = Base32Helper.decode(genesisAccountId);
        return ByteString.copyFrom(bytes);
    }

    public ByteString getGenesisBlockHash() {
        byte[] bytes = Base32Helper.decode(genesisBlockHash);
        return ByteString.copyFrom(bytes);
    }

    public ByteString getGenesisBlock() {
        byte[] bytes = Base32Helper.decode(genesisBlock);
        return ByteString.copyFrom(bytes);
    }

}
