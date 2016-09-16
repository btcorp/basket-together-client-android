package com.angel.black.baframework.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by KimJeongHun on 2016-06-19.
 */
public class ScreenUtil {

    public static float convertDpToPixel(Activity activity, int dp) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }
}
