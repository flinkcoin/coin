package com.flick.node.managers;

import com.flick.data.proto.common.Common;
import com.google.protobuf.ByteString;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CryptoManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoManager.class);

    private final NodeManager nodeManager;

    @Inject
    public CryptoManager(NodeManager nodeManager) {
        this.nodeManager = nodeManager;

    }

    public ByteString signData(ByteString data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        EdDSAEngine edDSAEngine = new EdDSAEngine(MessageDigest.getInstance(nodeManager.getSpec().getHashAlgorithm()));

        edDSAEngine.initSign(nodeManager.getKeyPair().getPrivateKey().getEdDSAPrivateKey());
        edDSAEngine.update(data.toByteArray());
        return ByteString.copyFrom(edDSAEngine.sign());
    }

    public boolean verifyData(ByteString nodeId, ByteString data, ByteString singature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        Optional<Common.Node> node = nodeManager.getNode(nodeId);

        if (node.isEmpty()) {
            return false;
        }

        EdDSAEngine edDSAEngine = new EdDSAEngine(MessageDigest.getInstance(nodeManager.getSpec().getHashAlgorithm()));

        /*FIX THIS*/
        byte[] toByteArray = node.get().getPublicKey().toByteArray();
        byte[] filteredByteArray = Arrays.copyOfRange(toByteArray, 1, toByteArray.length);
        /*FIX THIS*/

        EdDSAPublicKey puk = new EdDSAPublicKey(new EdDSAPublicKeySpec(filteredByteArray, nodeManager.getSpec()));
        edDSAEngine.initVerify(puk);

        edDSAEngine.update(data.toByteArray());

        return edDSAEngine.verify(singature.toByteArray());
    }

    public boolean verifyBlockData(ByteString publicKey, ByteString data, ByteString singature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        EdDSAEngine edDSAEngine = new EdDSAEngine(MessageDigest.getInstance(nodeManager.getSpec().getHashAlgorithm()));

        /*FIX THIS*/
        byte[] toByteArray = publicKey.toByteArray();
        byte[] filteredByteArray = Arrays.copyOfRange(toByteArray, 1, toByteArray.length);
        /*FIX THIS*/

        EdDSAPublicKey puk = new EdDSAPublicKey(new EdDSAPublicKeySpec(filteredByteArray, nodeManager.getSpec()));
        edDSAEngine.initVerify(puk);

        edDSAEngine.update(data.toByteArray());

        return edDSAEngine.verify(singature.toByteArray());
    }

}