package com.angel.black.baframework.util;

import android.net.Uri;
import android.support.annotation.DrawableRes;

import java.io.File;

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

    public static String filePath2Uri(String filePath) {
        return "file://" + filePath;
    }

    public static String convertFilePathToUri(String path) {
        return Uri.decode(Uri.fromFile(new File(path)).toString());
    }
}
