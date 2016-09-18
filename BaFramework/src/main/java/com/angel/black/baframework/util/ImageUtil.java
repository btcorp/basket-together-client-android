package com.angel.black.baframework.util;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.angel.black.baframework.core.base.BaseActivity;

/**
 * Created by KimJeongHun on 2016-09-18.
 */
public class ImageUtil {
    /**
     * 사진의 URI 경로를 받는 메소드
     */
    public static String getRealImagePath(BaseActivity activity, Uri uri) {
        // uri가 null일경우 null반환
        if( uri == null ) {
            return null;
        }
        // 미디어스토어에서 유저가 선택한 사진의 URI를 받아온다.
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // URI경로를 반환한다.
        return uri.getPath();
    }
}
