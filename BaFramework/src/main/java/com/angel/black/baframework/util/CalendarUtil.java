package com.angel.black.baframework.util;

import java.util.Calendar;

/**
 * Created by KimJeongHun on 2016-05-31.
 */
public class CalendarUtil {
    public static String getDateString(int year, int month, int day) {
        return year + "년 " + (month + 1) + "월 " + day + "일";
    }

    public static String getDateString(Calendar c) {
        return c.get(Calendar.YEAR) + "년 " + (c.get(Calendar.MONTH) + 1) + "월 " + c.get(Calendar.DAY_OF_MONTH) + "일";
    }

    public static String getDateString(long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);

        return c.get(Calendar.YEAR) + "년 " + (c.get(Calendar.MONTH) + 1) + "월 " + c.get(Calendar.DAY_OF_MONTH) + "일";
    }

    public static String getDateStringWithDash(String str) {
//        return new SimpleDateFormat("yyyy년 mm월 dd일").format(str);
        return "2016-09-17 16:04:05";       // 임시 하드코딩
    }

    public static boolean isEalierThanToday(int year, int month, int day) {
        final Calendar c = Calendar.getInstance();

        int todayYear = c.get(Calendar.YEAR);
        int todayMonth = c.get(Calendar.MONTH);
        int todayDay = c.get(Calendar.DAY_OF_MONTH);

        if(year < todayYear) {
            return true;
        } else if(year == todayYear) {
            if (month < todayMonth) {
                return true;
            } else if (month == todayMonth) {
                if (day < todayDay) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getTimeString(int hourOfDay, int minute) {
        int hourInt = hourOfDay % 12;
        hourInt = hourInt == 0 ? 12 : hourInt;

        String hourStr = "" + hourInt;

        String minStr = "" + minute;
        if(minute < 10) {
            minStr = "0" + minStr;
        }

        return (hourOfDay >= 12 && hourOfDay < 24 ? "오후 " : "오전 ") + hourStr + "시 " + minStr + "분";
    }

    public static String getDateTimeString(long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);

        return c.get(Calendar.YEAR) + "년 " + (c.get(Calendar.MONTH) + 1) + "월 " + c.get(Calendar.DAY_OF_MONTH) + "일"
                + c.get(Calendar.HOUR_OF_DAY) + "시 " + c.get(Calendar.MINUTE) + "분";
    }
}
