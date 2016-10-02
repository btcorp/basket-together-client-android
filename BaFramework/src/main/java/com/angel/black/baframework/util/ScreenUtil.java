package com.angel.black.baframework.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.angel.black.baframework.logger.BaLog;

/**
 * Created by KimJeongHun on 2016-06-19.
 */
public class ScreenUtil {

    public static int convertDpToPixel(Activity activity, int dp) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 주어진 사각영역이 화면 스크린 밖으로 빠져나갔는지 검사한다.
     * @param context
     * @param rect
     * @return
     */
    public static boolean isOutOfScreenWidth(Context context, Rect rect) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        BaLog.d("device widthPixels=" + dm.widthPixels + ", heightPixels=" + dm.heightPixels + ", rect=" + rect);
        if(rect.right > dm.widthPixels || rect.left <= 0)
            return true;

        return false;
    }

    /**
     * 주어진 뷰의 영역이 화면 스크린 밖으로 빠져나갔는지 검사한다.
     * @param context
     * @param view
     * @return
     */
    public static boolean isOutOfScreenWidth(Context context, View view) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        Rect rect = new Rect();
        view.getHitRect(rect);

        BaLog.d("device widthPixels=" + dm.widthPixels + ", heightPixels=" + dm.heightPixels + ", rect=" + rect);
        if(rect.right > dm.widthPixels || rect.left < 0)
            return true;

        return false;
    }
}
