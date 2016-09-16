package com.angel.black.baframework.util;

import android.os.Build;

import com.angel.black.baframework.logger.BaLog;

/**
 * Created by KimJeongHun on 2016-05-24.
 */
public class BuildUtil {
    public static boolean isLowDevice() {
        String model = Build.MODEL;
        BaLog.i("model=" + model);
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            BaLog.i("this device is low device!");
            return true;
        }

        BaLog.i("this device is not low device!");
        return false;
    }

    public static boolean isGoogleReferencePhoneMarshmallow() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String model = Build.MODEL;
            BaLog.i("model=" + model);

            if(model.contains("Nexus")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGoogleReferencePhoneLollipop() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String model = Build.MODEL;
            BaLog.i("model=" + model);

            if(model.contains("Nexus")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAboveMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isAboveLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isAboveIcecreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean isModelSamsungGalaxy() {
        BaLog.v("Build.MANUFACTURER=" + Build.MANUFACTURER);
        return Build.MANUFACTURER.contains("samsung") || Build.MANUFACTURER.contains("SAMSUNG");
    }

    public static boolean isAboveHoneyComb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
}
