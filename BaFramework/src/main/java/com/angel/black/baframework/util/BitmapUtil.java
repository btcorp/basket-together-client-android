/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.angel.black.baframework.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.camera.CameraPictureFileBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class BitmapUtil {
    /**
     * 주어진 파일의 크기로 비트맵 샘플사이즈를 구한다.
     */
    public static int getBitmapSampleSize(String filename) {

        File file = new File(filename);
        long len = file.length();

        Log.i("KJH", "IMAGE TEST >> getBitmapSampleSize(filename=" + filename + ") >> length=" + len);
        if (len <= 0)
            return -1;

        if (len > 0 && len <= 2049536)
            return 1;
        else if (len > 2049536 && len <= 4194304)
            return 2;
        else
            return 4;
    }

    /**
     * 주어진 바이트 데이터의 크기로 비트맵 샘플사이즈를 구한다.
     */
    public static int getBitmapSampleSize(byte[] data) {
        long len = data.length;

        Log.i("KJH", "IMAGE TEST >> getBitmapSampleSize(byte data.length=" + len + ")");
        if (len <= 0)
            return -1;

        if (len > 0 && len <= 1024768)
            return 1;
        else if (len > 1024768 && len <= 4194304)
            return 2;
        else
            return 4;
    }

    public static int getBitmapThumbnailOptionForGallery(String filename, BitmapFactory.Options opts) {

        File file = new File(filename);
        long len = file.length();

        if (len <= 0)
            return -1;

        opts.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filename, opts);

        int width = opts.outWidth;
        if(width > 0 && width <= 480) {
            return 1;
        } else if(width > 480 && width <= 960) {
            return 2;
        } else if(width > 960 && width <= 1920) {
            return 4;
        } else if(width > 1920 && width <= 3840) {
            return 8;
        } else {
            return 16;
        }
    }

    public static int getBitmapThumbnailOptionSampleSize(int imageWidth, int baseSize) {
        if(imageWidth > 0 && imageWidth <= baseSize) {
            return 1;
        } else if(imageWidth > baseSize && imageWidth <= baseSize * 2) {
            return 2;
        } else if(imageWidth > baseSize * 2 && imageWidth <= baseSize * 4) {
            return 4;
        } else if(imageWidth > baseSize * 4 && imageWidth <= baseSize * 8) {
            return 8;
        } else {
            return 16;
        }
    }

    public static String getDataSizeString(long byteLen) {
        String oriSize = "(" + byteLen + ")";
        long kb = byteLen / 1024;

        if(kb < 1000) {
            return kb + " Kbytes" + oriSize;
        } else if(kb >= 1000) {
            double mb = (double) byteLen / 1048576;

            return "" + (Math.round(mb*100d) / 100d) + " MBytes" + oriSize;
        } else {
            return "unknown size";
        }

    }

    public static Bitmap getBitmapThumbnail(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        options.inJustDecodeBounds = false;
        options.inSampleSize = getBitmapThumbnailOptionSampleSize(options.outWidth, 240);

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 비트맵을 회전한다.
     * @param origin
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap origin, int degree) {
        if(origin == null) {
            return null;
        }

        BaLog.d("arg degree=" + degree);
        degree = degree % 360;
        BaLog.d("real degree=" + degree);

        Matrix m = new Matrix();
        m.postRotate(degree);

        Bitmap rotatedBitmap = Bitmap.createBitmap(origin, 0, 0, origin.getWidth(), origin.getHeight(), m, true);
        return rotatedBitmap;
    }

    /**
     * 비트맵을 반전한다.
     * @param origin
     * @param inverseCode 1 : 좌우반전, 2 : 상하반전
     * @return
     */
    public static Bitmap inverseBitmap(Bitmap origin, int inverseCode) {
        Matrix m = new Matrix();

        if (!(inverseCode == 1 || inverseCode == 2))
            throw new IllegalArgumentException("inverseCode must be 1 or 2");

        if (inverseCode == 1) {
            m.setScale(-1, 1);  // 좌우반전
        } else {
            m.setScale(1, -1);  // 상하반전
        }

        Bitmap inverseBitmap = Bitmap.createBitmap(origin, 0, 0, origin.getWidth(), origin.getHeight(), m, false);

//        origin.recycle();
        return inverseBitmap;
    }

    public static Bitmap rotateAndInverseBitmap(Bitmap origin, float degree, int leftRightFlip, int upDownFlip) {
        BaLog.d("arg degree=" + degree);
        degree = degree % 360;
        BaLog.d("real degree=" + degree);

        Matrix m = new Matrix();
        m.postRotate(degree);

        if(leftRightFlip % 2 > 0 && upDownFlip % 2 > 0) {
            m.postScale(-1, -1);
        } else if(leftRightFlip % 2 > 0) {
            m.postScale(-1, 1);
        } else if(upDownFlip % 2 > 0) {
            m.postScale(1, -1);
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(origin, 0, 0, origin.getWidth(), origin.getHeight(), m, true);

//        origin.recycle();
        return rotatedBitmap;
    }

    public static byte[] getBlobData(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageInByte = stream.toByteArray();

        BaLog.d("bitmap byte data size=" + imageInByte.length);

        return imageInByte;
    }

//    public static Bitmap rotate(Bitmap b, int degrees) {
//        if (degrees != 0 && b != null) {
//            Matrix m = new Matrix();
//
//            m.setRotate(degrees);
//            try {
//                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
//                if (b != b2) {
//                    b.recycle();
//                    b = b2;
//                }
//            } catch (OutOfMemoryError ex) {
//                throw ex;
//            }
//        }
//        return b;
//    }

    public static int getPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    /**
     * 카메라 찍은 사진으로부터 썸네일을 만든다. (별도의 작업스레드에서 실행되어야함)
     */
    public static Bitmap buildThumbnail(CameraPictureFileBuilder.BuildImageResult buildImageResult, int width, int height) {
        BaLog.i();
        Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(buildImageResult.getBitmap(), width, height);

        return thumbBitmap;
    }
}
