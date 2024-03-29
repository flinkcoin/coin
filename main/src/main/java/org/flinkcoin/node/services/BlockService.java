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
package org.flinkcoin.node.services;

import org.flinkcoin.data.proto.common.Common.Block;
import org.flinkcoin.data.proto.communication.Message.BlockConfirmPub;
import org.flinkcoin.helper.Pair;
import org.flinkcoin.helper.helpers.UUIDHelper;
import org.flinkcoin.node.caches.NodeCache;
import org.flinkcoin.node.communication.CommonProcessor;
import org.flinkcoin.node.configuration.ProcessorBase;
import org.flinkcoin.node.handlers.ValidationHandler;
import org.flinkcoin.node.managers.CryptoManager;
import org.flinkcoin.node.managers.NodeManager;
import org.flinkcoin.node.voting.BlockVoting;
import org.flinkcoin.node.voting.stock.BlockStock;
import com.google.inject.Singleton;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import io.reactivex.rxjava3.core.BackpressureOverflowStrategy;
import io.reactivex.rxjava3.processors.PublishProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class BlockService extends ProcessorBase<Pair<ByteString, Block>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockService.class);

    private final Provider<CommonProcessor> commonHandler;
    private final NodeManager nodeManager;
    private final CryptoManager cryptoManager;
    private final BlockVoting blockVoting;
    private final BlockStock blockStock;
    private final ValidationHandler blockHandler;

    @Inject
    public BlockService(Provider<CommonProcessor> commonHandler, NodeCache nodeCache, NodeManager nodeManager, CryptoManager cryptoManager, BlockVoting blockVoting,
            BlockStock blockStock, ValidationHandler blockHandler) {
        super(PublishProcessor.create());
        this.blockVoting = blockVoting;
        this.nodeManager = nodeManager;
        this.cryptoManager = cryptoManager;
        this.commonHandler = commonHandler;
        this.blockStock = blockStock;
        this.blockHandler = blockHandler;
        this.publishProcessor
                .onBackpressureBuffer(1000, () -> {
                }, BackpressureOverflowStrategy.DROP_LATEST)
                .observeOn(Schedulers.single())
                .subscribe(this);
    }

    public void newBlock(Pair<ByteString, Block> pair) {
        publishProcessor.onNext(pair);
    }

    @Override
    public void process(Pair<ByteString, Block> pair) {

        ByteString nodeId = pair.getFirst();
        Block block = pair.getSecond();

        if (!blockHandler.validateBlock(block)) {
            return;
        }

        try {
            blockStock.putBlock(block.getBlockHash().getHash(), block);

            BlockConfirmPub.Builder blockExistConfirmPub = BlockConfirmPub.newBuilder();

            BlockConfirmPub.Body.Builder bodyBuilder = BlockConfirmPub.Body.newBuilder();

            bodyBuilder.setBlockHash(block.getBlockHash().getHash());
            bodyBuilder.setMsgId(ByteString.copyFrom(UUIDHelper.asBytes()));
            bodyBuilder.setNodeId(nodeManager.getNodeId());

            BlockConfirmPub.Body body = bodyBuilder.build();

            blockExistConfirmPub.setBody(body);
            blockExistConfirmPub.setSignature(cryptoManager.signData(body.toByteString()));
            commonHandler.get().flood(Any.pack(blockExistConfirmPub.build()));

            blockVoting.newBlockVote(Pair.of(nodeId, block.getBlockHash().getHash()));

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            LOGGER.error("Error!", ex);
        }
    }

}
