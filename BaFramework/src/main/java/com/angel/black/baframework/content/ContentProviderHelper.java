package com.angel.black.baframework.content;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.camera.CameraPictureFileBuilder;
import com.angel.black.baframework.util.BitmapUtil;

/**
 * Created by KimJeongHun on 2016-09-30.
 */
public class ContentProviderHelper {
    /**
     * 카메라로 찍은 사진 데이터를 컨텐트 프로바이더에 저장한다.(썸네일 데이터도 같이)
     *
     * @param buildImageResult
     * @param thumbBitmap
     */
    public static void addContentProvider(Context context, CameraPictureFileBuilder.BuildImageResult buildImageResult, Bitmap thumbBitmap) {
        BaLog.d("buildImageResult=" + buildImageResult + ", thumbBitmap=" +thumbBitmap);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, buildImageResult.getFilename());
        values.put(MediaStore.Images.Media.DISPLAY_NAME, buildImageResult.getFilename());
        values.put(MediaStore.Images.Media.DESCRIPTION, "");
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.ORIENTATION, 0);
        values.put(MediaStore.Images.Media.DATA, buildImageResult.getFilepath());

        Uri insertedUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            ContentValues thumbValues = new ContentValues();
            thumbValues.put(MediaStore.Images.Thumbnails.THUMB_DATA, BitmapUtil.getBlobData(thumbBitmap));
            thumbValues.put(MediaStore.Images.Thumbnails.IMAGE_ID, insertedUri.getPath());
            thumbValues.put(MediaStore.Images.Thumbnails.WIDTH, thumbBitmap.getWidth());
            thumbValues.put(MediaStore.Images.Thumbnails.HEIGHT, thumbBitmap.getHeight());

            context.getContentResolver().insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, thumbValues);

            BaLog.d(values, thumbValues, thumbBitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
            BaLog.e("썸네일 컨텐트 프로바이더에 저장 중 에러!!");
        }
    }

}
