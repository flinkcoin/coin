package com.flick.node.configuration.bootstrap;

import com.flick.crypto.HashHelper;
import com.flick.crypto.KeyGenerator;
import com.flick.crypto.KeyPair;
import com.flick.data.proto.common.Common;
import com.flick.data.proto.common.Common.Block;
import com.flick.data.proto.common.Common.Block.Body;
import com.flick.data.proto.common.Common.Block.Hash;
import com.flick.data.proto.common.Common.Block.Signatures;
import com.flick.helper.helpers.Base32Helper;
import com.flick.helper.helpers.RandomHelper;
import com.flick.helper.helpers.UUIDHelper;
import com.google.protobuf.ByteString;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BootstrapTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapTest.class);

    @Test
    public void testGenesisBlock() throws Exception {

        byte[] seed = new byte[64];
        RandomHelper.get().nextBytes(seed);

        LOGGER.info("Seed: {}", Base32Helper.encode(seed));

        KeyPair keyPair = KeyGenerator.getKeyPairFromSeed(seed);

        Body.Builder bodyBuilder = Body.newBuilder();
        ByteString accountId = ByteString.copyFrom(UUIDHelper.asBytes());
        bodyBuilder.setAccountId(accountId);
        bodyBuilder.setBalance(Bootstrap.MAX_SUPPLY);
        bodyBuilder.setAmount(0);
        bodyBuilder.setDelegatedNodeId(ByteString.copyFrom(Base32Helper.decode("3JEFYDDBOZGXNIL3Z2B3XSD3QI")));
        bodyBuilder.setVersion(1);
        bodyBuilder.setBlockType(Block.BlockType.CREATE);
        bodyBuilder.setPublicKeys(Block.PublicKeys.newBuilder()
                .addPublicKey(ByteString.copyFrom(keyPair.getPublicKey().getPublicKey())));

        Body body = bodyBuilder.build();

        Common.FullBlock.Builder fullBlockBuilder = Common.FullBlock.newBuilder()
                .setBlock(Block.newBuilder()
                        .setBody(body)
                        .setSignatues(Signatures.newBuilder()
                                .addSignature(ByteString.copyFrom(signData(keyPair, body.toByteArray())))
                        )
                        .setBlockHash(Hash.newBuilder().setHash(ByteString.copyFrom(HashHelper.sha512(body.toByteArray()))))
                );

        Common.FullBlock fullBlock = fullBlockBuilder.build();

        LOGGER.info("AccountId: {}", Base32Helper.encode(accountId.toByteArray()));
        LOGGER.info("Block: {}{}", fullBlock.toString(), Base32Helper.encode(fullBlock.toByteArray()));
        LOGGER.info("BlockHash: {}", Base32Helper.encode(fullBlock.getBlock().getBlockHash().getHash().toByteArray()));
    }

    public byte[] signData(KeyPair keyPair, byte[] data) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
        EdDSANamedCurveSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        EdDSAEngine edDSAEngine = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));

        edDSAEngine.initSign(keyPair.getPrivateKey().getEdDSAPrivateKey());
        edDSAEngine.update(data);
        return edDSAEngine.sign();
    }

}