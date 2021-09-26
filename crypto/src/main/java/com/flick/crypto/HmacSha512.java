package com.flick.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HmacSha512 {

    private static final String HMAC_SHA512 = "HmacSHA512";

    public static byte[] hmac512(byte[] key, byte[] seed) {
        try {
            Mac sha512_HMAC = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec keySpec = new SecretKeySpec(seed, HMAC_SHA512);
            sha512_HMAC.init(keySpec);
            return sha512_HMAC.doFinal(key);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Unable to perform hmac512.", e);
        }
    }
}