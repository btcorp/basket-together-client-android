package com.angel.black.baframework.logger;

import android.util.Log;

import com.angel.black.baframework.BaApplication;

/**
 * Created by KimJeongHun on 2016-05-22.
 */
public class BaLog {
    public static final String LOG_TAG = BaLog.class.getSimpleName();

    public static final void e(String message) {
        if (BaApplication.debug)
            Log.e(LOG_TAG, buildLogMsg(message));
    }

    public static final void e(String tag, String message) {
        if (BaApplication.debug)
            Log.e(tag, buildLogMsg(message));
    }

    public static final void w(String message) {
        if (BaApplication.debug)
            Log.w(LOG_TAG, buildLogMsg(message));
    }

    public static final void w(String tag, String message) {
        if (BaApplication.debug)
            Log.w(tag, buildLogMsg(message));
    }

    public static void i() {
        if (BaApplication.debug)
            Log.i(LOG_TAG, buildLogMsg(""));
    }

    public static final void i(String message) {
        if (BaApplication.debug)
            Log.i(LOG_TAG, buildLogMsg(message));
    }

    public static final void i(String tag, String message) {
        if (BaApplication.debug)
            Log.i(tag, buildLogMsg(message));
    }

    public static final void d() {
        if (BaApplication.debug)
            Log.d(LOG_TAG, buildLogMsg(""));
    }

    public static final void d(String message) {
        if (BaApplication.debug)
            Log.d(LOG_TAG, buildLogMsg(message));
    }

    public static final void d(String tag, String message) {
        if (BaApplication.debug)
            Log.d(tag, buildLogMsg(message));
    }

    public static final void v(String message) {
        if (BaApplication.debug)
            Log.v(LOG_TAG, buildLogMsg(message));
    }

    public static final void v(String tag, String message) {
        if (BaApplication.debug)
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
//        sb.append(String.format(":at (%s:%d)", ste.getFileName(), ste.getLineNumber()));
        sb.append(message);

        return sb.toString();
    }

}
