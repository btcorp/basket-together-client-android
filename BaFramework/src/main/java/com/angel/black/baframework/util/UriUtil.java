package com.angel.black.baframework.util;

import android.support.annotation.DrawableRes;

/**
 * Created by KimJeongHun on 2016-09-23.
 */
public class UriUtil {
    public static String drawable2uri(@DrawableRes int drawable) {
        return "drawable://" + drawable;
    }
    /**assets://image.png*/
    public static String assets2uri(String assets_image) {
        return "assets://" + assets_image;
    }
}
