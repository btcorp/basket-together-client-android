package com.angel.black.baframework.util;

import android.util.Base64;


public class BASE64Utils {
	
//	public static byte[] encodedBytes(byte[] src) {
//		if(src == null || src.length <= 0) return null;
//		return Base64.encodeBase64(src);
//	}
//	public static byte[] encodedBytes(String planString) {
//		if(StringUtils.isEmpty(planString)) return null;
//		byte[] planStringBytes = planString.getBytes();
//		return Base64.encodeBase64(planStringBytes);
//	}
	public static String encodedString(byte[] src) {
		if(src == null || src.length <= 0) return "";
		 return new String(Base64.encode(src, Base64.DEFAULT));
	}
	
	public static String encodedString(String planString) {
		if(StringUtil.isEmptyString(planString))
			return "";
		
		byte[] planStringBytes = planString.getBytes();
		byte[] encodedBytes = Base64.encode(planStringBytes, Base64.DEFAULT);
		return new String(encodedBytes);
	}
	
	public static byte[] decodedBytes(String encodedString) {
		byte[] encodedStringBytes = encodedString.getBytes();
		return Base64.decode(encodedStringBytes, Base64.DEFAULT);
	}
	
	public static String decodedString(byte[] src) {
		 return new String(Base64.decode(src, Base64.DEFAULT));
	}
	
	public static String decodedString(String encodedString) {
		if(StringUtil.isEmptyString(encodedString))
			return "";
		
		byte[] encodedStringBytes = encodedString.getBytes();
		byte[] decodedBytes = Base64.decode(encodedStringBytes, Base64.DEFAULT);
		return new String(decodedBytes);
	}
}
