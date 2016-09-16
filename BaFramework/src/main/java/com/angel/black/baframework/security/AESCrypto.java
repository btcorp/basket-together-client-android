package com.angel.black.baframework.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypto {
	 
	public static final byte[] KEY = "83e255849ddd6e4c".getBytes();
	
	/**
	 	AES/CBC/NoPadding (128)
		AES/CBC/PKCS5Padding (128)
		AES/ECB/NoPadding (128)
		AES/ECB/PKCS5Padding (128)
		DES/CBC/NoPadding (56)
		DES/CBC/PKCS5Padding (56)
		DES/ECB/NoPadding (56)
		DES/ECB/PKCS5Padding (56)
		DESede/CBC/NoPadding (168)
		DESede/CBC/PKCS5Padding (168)
		DESede/ECB/NoPadding (168)
		DESede/ECB/PKCS5Padding (168)
		RSA/ECB/PKCS1Padding (1024, 2048)
		RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
		RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
	 */
	
	
	/**
	 * Algorithm 	: AES-128bit
	 * Mode 		: ECB(Electric Code Book)
	 * Padding		: PKCS5Padding
	 * 
	 * AES 암호화 모듈
	 * 
	 * @param key (16byte)
	 * @param plain
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt_ecb(byte[] key, byte[] plain) throws Exception {
		
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); 
		
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec); 
		return cipher.doFinal(plain); 
	}
	
	/**
	 * Algorithm 	: AES-128bit
	 * Mode 		: ECB(Electric Code Book)
	 * Padding		: PKCS5Padding
	 * 
	 * AES 복호화 모듈
	 * 
	 * @param key (16byte)
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt_ecb(byte[] key, byte[] encrypted) throws Exception {
		
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); 

		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		
		return cipher.doFinal(encrypted);
		
	}
	
	/**
	 * Algorithm 	: AES-128bit
	 * Mode 		: CBC(Cipher Block Chaining)
	 * Padding		: PKCS5Padding
	 * 
	 * AES 암호화 모듈
	 * 
	 * @param key (16byte)
	 * @param plain
	 * @param iv
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt_cbc(byte[] key, byte[] plain, byte[] iv) throws Exception {
		
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
		
		return cipher.doFinal(plain); 
	}
	
	/**
	 * Algorithm 	: AES-128bit
	 * Mode 		: CBC(Cipher Block Chaining)
	 * Padding		: PKCS5Padding
	 * 
	 * AES 복호화 모듈
	 * 
	 * @param key (16byte)
	 * @param encrypted
	 * @param iv
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt_cbc(byte[] key, byte[] encrypted, byte[] iv) throws Exception {
		
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		
		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
		
		return cipher.doFinal(encrypted);
		
	}
	
	/** 사용 예 */
//	public static void main(String[] args) throws Exception {
//		String planText = "안녕하세요.";
//		byte[] key = "83e255849ddd6e4c".getBytes();
//
//		byte[] encrypted = AESCrypto.encrypt_ecb(key, planText.getBytes());
//		String encoded = BASE64Utils.encodedString(encrypted);
//		System.out.println("cypher text : " + encoded);
//
//
//		byte[] decoded = BASE64Utils.decodedBytes(encoded);
//		byte[] decrypted = AESCrypto.decrypt_ecb(key, decoded);
//		System.out.println("plan text : " + new String(decrypted));
//	}
}
