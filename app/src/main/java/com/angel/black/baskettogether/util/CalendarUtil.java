package com.angel.black.baskettogether.util;

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
}
