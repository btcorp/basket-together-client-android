package com.angel.black.baframework.util;

import android.os.Build;
import android.telephony.PhoneNumberUtils;

import com.angel.black.baframework.logger.BaLog;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.regex.Pattern;

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
        if(str.equals("") || str == null) {
            return true;
        }
        return false;
    }

    /**
     * -, +82 문자가 들어간 휴대폰번호를 순수 숫자로만 이루어진 폰번호로 바꾼다.
     * @param phone
     * @return
     */
    public static String parsePhoneNumberFormat(String phone) {
        if (phone == null || phone.equals(""))
            return "";
        else {
            if (phone.indexOf("-") > 0)
                phone = phone.replaceAll("-", "");

            if (phone.indexOf("\\+82") != 0)
                phone = phone.replaceAll("\\+82", "0");

            return phone;
        }
    }

    /**
     * 스트링 어레이의 내용을 한줄 문자열로 가져온다.
     */
    public static String debugStringArray(String[] arr) {
        return Arrays.toString(arr);
    }


    public static String getMoney(String money) {
        if (money == null || money.equals(""))
            return "";
        else {
            if (money.indexOf(",") > 0)
                money = money.replaceAll(",", "");
        }

        long value = 0;

        try {
            value = Long.parseLong(money);
        } catch (Exception e) {
            value = 0;
        }

        DecimalFormat format = new DecimalFormat("###,###,###,###");
        return format.format(value);
    }

    /**
     * 문자열이 - 포함된 휴대폰번호 인지 검사
     */
    public static boolean isCellPhoneWithDash(String str) {
        if (str == null) {
            return false;
        }

        Pattern p = Pattern.compile("(01[016789])([\\d.-]+)");
        return p.matcher(str).matches();
    }


    public static String convertPhoneNumWithDash(String str) {
        String phoneNum;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            phoneNum = PhoneNumberUtils.formatNumber(str, "KR");
        } else {
            phoneNum = PhoneNumberUtils.formatNumber(str);
        }

        BaLog.d("phoneNum=" + phoneNum);
        return phoneNum;
    }

    public static class RegularExpressions {
        int LENGTH_resRegNo = 13;
        /** 메일주소 인것 */
        String regularExpressionEmail = "^([\\w-\\.]+)@((?:[\\w]+\\.)+[a-zA-Z]{2,4})$";
        /** 전화번호 인것 */
        String regularExpressionPhoneNo = "^(0(?:505|70|10|11|16|17|18|19))(\\d{3}|\\d{4})(\\d{4})$";
        /** 파일네임에 적합하지 않은것 */
        String regularExpressionVFilename = "\\\\/|[&{}?=/\\\\: <>*|\\]\\[\\\"\\']";
        /** 자연수가 아닌것 */
        String regularExpressionNotdecimal = "\\D";
        /** Password로 사용가능한 문자열 */
        String regularExpressionPassword = "(((?=.*[a-z])|(?=.*[A-Z])).{8,20})";
        /** 엔터가 포함되어 있지 않은 ""로 둘러 쌓여있는 문자열 */
        String regularExpressionWrodNotCR = "(\\\"[^\\\"\\r]+\\\")";
    }
}
