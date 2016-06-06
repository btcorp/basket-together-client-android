package com.angel.black.baskettogether.util;

/**
 * Created by KimJeongHun on 2016-05-23.
 */
public class StringUtil {
    public static String notNullString(String str) {
        if(str == null || str.equals("null") || str.trim().equals("")) {
            str = "";
        }
        return str;
    }

    public static boolean isEmptyString(String str) {
        if(str == null || str.equals("null") || str.equals("")) {
            return true;
        }
        return false;
    }

    public static boolean isEmptyInputString(String str) {
        if(str == null || str.trim().equals("")) {
            return true;
        }
        return false;
    }
}
