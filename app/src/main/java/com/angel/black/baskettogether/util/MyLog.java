package com.angel.black.baskettogether.util;

import android.util.Log;

import com.angel.black.baskettogether.core.MyApplication;

/**
 * Created by KimJeongHun on 2016-05-22.
 */
public class MyLog {
    public static final String LOG_TAG = MyLog.class.getSimpleName();

    /** Log Level Error **/
    public static final void e(String message) {
        if (MyApplication.debug)
            Log.e(LOG_TAG, buildLogMsg(message));
    }

    /** Log Level Error **/
    public static final void e(String tag, String message) {
        if (MyApplication.debug)
            Log.e(tag, buildLogMsg(message));
    }

    /** Log Level Warning **/
    public static final void w(String message) {
        if (MyApplication.debug)
            Log.w(LOG_TAG, buildLogMsg(message));
    }

    /** Log Level Warning **/
    public static final void w(String tag, String message) {
        if (MyApplication.debug)
            Log.w(tag, buildLogMsg(message));
    }

    /** Log Level Information **/
    public static void i() {
        if (MyApplication.debug)
            Log.i(LOG_TAG, buildLogMsg(""));
    }

    /** Log Level Information **/
    public static final void i(String message) {
        if (MyApplication.debug)
            Log.i(LOG_TAG, buildLogMsg(message));
    }

    /** Log Level Information **/
    public static final void i(String tag, String message) {
        if (MyApplication.debug)
            Log.i(tag, buildLogMsg(message));
    }

    public static final void d() {
        if (MyApplication.debug)
            Log.d(LOG_TAG, buildLogMsg(""));
    }

    /** Log Level Debug **/
    public static final void d(String message) {
        if (MyApplication.debug)
            Log.d(LOG_TAG, buildLogMsg(message));
    }

    /** Log Level Debug **/
    public static final void d(String tag, String message) {
        if (MyApplication.debug)
            Log.d(tag, buildLogMsg(message));
    }

    /** Log Level Verbose **/
    public static final void v(String message) {
        if (MyApplication.debug)
            Log.v(LOG_TAG, buildLogMsg(message));
    }

    /** Log Level Verbose **/
    public static final void v(String tag, String message) {
        if (MyApplication.debug)
            Log.v(tag, buildLogMsg(message));
    }
    
    public static String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("] ");
        sb.append(message);

        return sb.toString();
    }

}
