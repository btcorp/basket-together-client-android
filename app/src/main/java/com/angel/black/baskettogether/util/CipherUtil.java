package com.angel.black.baskettogether.util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Created by KimJeongHun on 2016-05-25.
 */
public class CipherUtil {
    public static String generateKey() {
        try {
            SecureRandom sr = SecureRandom.getInstance("AES");
            sr.generateSeed(12);

            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(30, sr);
            SecretKey key = keyGenerator.generateKey();
            return key.toString();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String encrypt(String message) {
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");

            SecureRandom sr = SecureRandom.getInstance("AES");
            sr.generateSeed(12);

            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(30, sr);
            SecretKey secretKey = keyGenerator.generateKey();

            byte[] output = c.doFinal(message.getBytes());


        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return null;
    }
}
