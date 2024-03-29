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

import org.flinkcoin.node.configuration.Config;
import com.google.protobuf.ByteString;
import java.util.List;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class BootstrapBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapBase.class);

    protected ByteString getGenesisAccountId() {
        return Config.get().network().getGenesisAccountId();
    }

    protected ByteString getGenesisBlockHash() {
        return Config.get().network().getGenesisBlockHash();
    }

    protected ByteString getGenesisBlock() {
        return Config.get().network().getGenesisBlock();
    }

    protected List<Nodes> getNodes() {
        return Config.get().network() == Network.PROD ? Nodes.getProdNodes() : Nodes.getTestNodes();
    }

}
