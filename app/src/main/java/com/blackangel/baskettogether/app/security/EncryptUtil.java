package com.blackangel.baskettogether.app.security;

import android.util.Base64;

import com.blackangel.baframework.logger.MyLog;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

/**
 * Created by kimjeonghun on 2017. 12. 3..
 */

public class EncryptUtil {

    public static PublicKey makeRSAPublicKey(String modulus, String exponent) throws Exception {
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                new BigInteger(modulus, 16), new BigInteger(exponent, 16));

        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(publicKeySpec);
    }

    public static PrivateKey makeRSAPrivateKey(String modulus, String exponent) throws Exception {
        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(
                new BigInteger(modulus, 16), new BigInteger(exponent, 16));

        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(privateKeySpec);
    }

    /**
     * 공개키를 이용한 RSA 암호화
     * @param publicKey
     * @param plainText
     */
    public static String encryptRSA(PublicKey publicKey, String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        return new String(Base64.encode(encryptedBytes, Base64.NO_WRAP), "UTF-8");
//        return new String(encryptedBytes, "UTF-8");
    }

    /** 개인키를 이용한 RSA 복호화
     *  @param privateKey session에 저장된 PrivateKey
     *  @param encryptedText 암호화된 문자열
     */
    public static String decryptRSA(PrivateKey privateKey, String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodeBase64 = Base64.decode(encryptedText, Base64.NO_WRAP);
        String decodeStr = new String(decodeBase64, "UTF-8");

        MyLog.w("rsa decode str = " + decodeStr);

        byte[] decryptedBytes = cipher.doFinal();


        return new String(decryptedBytes, "UTF-8");
    }

    // 16진수 문자열을 byte 배열로 변환
    private static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return new byte[] {};
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        }
        return bytes;
    }
}
