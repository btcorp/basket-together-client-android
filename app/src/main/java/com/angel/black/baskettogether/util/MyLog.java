package com.angel.black.baskettogether.util;

import android.util.Log;

import com.angel.black.baskettogether.core.MyApplication;

/**
 * Created by KimJeongHun on 2016-05-22.
 */
public class MyLog {
    public static final String TAG = MyLog.class.getSimpleName();
    public static void d(String message) {
        if(MyApplication.debug) {
            Log.d(TAG, message);
        }
    }

    public static void e(String message) {
        if(MyApplication.debug) {
            Log.e(TAG, message);
        }
    }
}
