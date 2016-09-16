package com.angel.black.baframework.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.angel.black.baframework.logger.BaLog;

/**
 * Created by KimJeongHun on 2016-06-01.
 */
public class DeviceUtil {
    private static final String TAG = DeviceUtil.class.getSimpleName();

    /**
     * 기기의 화면 사이즈를 디버깅한다.
     * @param activity
     */
    public static void writeDebugLogScreenSize(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        BaLog.d(TAG, dm.toString());
    }

    /**
     * 기기의 휴대폰 번호를 가져온다.
     * @param activity
     * @return
     */
    public static String getDevicePhoneNumber(Activity activity) {
        TelephonyManager telManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        return StringUtil.parsePhoneNumberFormat(StringUtil.notNullString(telManager.getLine1Number()));
    }

    /**
     * 기기의 모델명을 가져온다.
     * @return
     */
    public static String getDeviceModelName() {
        return Build.MODEL;
    }
}
