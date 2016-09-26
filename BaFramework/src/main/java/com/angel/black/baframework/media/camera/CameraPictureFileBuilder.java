package com.angel.black.baframework.media.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;

import com.angel.black.baframework.logger.BaLog;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by KimJeongHun on 2016-09-26.
 */
public class CameraPictureFileBuilder extends AsyncTask<Byte[], Void, CameraPictureFileBuilder.BuildImageResult> {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;
    private String mDestPath;   // 파일이 저장될 부모 경로. 마지막 /까지 포함
    private boolean mSquareCrop;    // 사진 찍었을 당시 전체 카메라 프리뷰 영역 중 화면에 표시된 영역 제외한 잘라진 영역
    private boolean mIsFrontCamera;
    private Handler mUIHandler;

    public CameraPictureFileBuilder(Context context, String destPath, boolean squareCrop, boolean isFrontCamera, Handler uiHandler) {
        this.mContext = context;
        this.mDestPath = destPath;
        this.mSquareCrop = squareCrop;
        this.mIsFrontCamera = isFrontCamera;
        this.mUIHandler = uiHandler;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected BuildImageResult doInBackground(Byte[]... params) {
        byte[] data = ArrayUtils.toPrimitive(params[0]);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);

        Bitmap finalBitmap = null;
        int outputWidth, outputHeight;

        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

        Matrix m = new Matrix();
        if (opts.outHeight < opts.outWidth) {   // 가로가 더 크면 회전되있는걸로 판단 > 회전한다.
            if(mSquareCrop) {
                outputHeight = bmp.getHeight();
                outputWidth = bmp.getHeight();
//                outputWidth = (int) (bmp.getWidth() * (1.0f - mCropRatio));
            } else {
                outputWidth = bmp.getWidth();
                outputHeight = bmp.getHeight();
            }

            m.setRotate(90, (float) outputWidth, outputHeight);
        } else {
            if(mSquareCrop) {
                outputWidth = bmp.getWidth();
                outputHeight = bmp.getWidth();

            } else {
                outputWidth = bmp.getWidth();
                outputHeight = bmp.getHeight();
            }

            m.setRotate(0, (float) outputWidth, outputHeight);
        }

        BaLog.d("origin bitmap size=" + bmp.getWidth() + " x " + bmp.getHeight());
        BaLog.d("crop bitmap size=" + outputWidth + " x " + outputHeight);

        finalBitmap = Bitmap.createBitmap(bmp, 0, 0, outputWidth, outputHeight, m, false);
        if(finalBitmap != bmp) {
            bmp.recycle();
        }

        BaLog.d("final bitmap size=" + finalBitmap.getWidth() + " x " + finalBitmap.getHeight());

        FileOutputStream outStream = null;
        String filename = System.currentTimeMillis() + ".jpg";
        filename = mDestPath + filename;

        File outputPath = new File(mDestPath);

        if(!outputPath.exists()) {
            outputPath.mkdir();
        }

        try {
            outStream = new FileOutputStream(filename);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            outStream.close();

            BaLog.d(TAG, "IMAGE TEST >> camera taken picture save success!!");

        } catch (FileNotFoundException e)  {
            // 결과의 filename 에 "FileNotFoundException" 을 셋팅한다.
            filename = e.getClass().getSimpleName();
            e.printStackTrace();
        } catch (IOException e)  {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new BuildImageResult(data, finalBitmap, filename);
    }

    private float getRatio(Point cropSize) {
        BaLog.i("cropSize=" + cropSize.toString());
        float ratio;
        if(cropSize.y > cropSize.x) {
            ratio = cropSize.y / (float) cropSize.x;
        } else {
            ratio = cropSize.x / (float) cropSize.y;
        }
        BaLog.d("ratio=" + ratio);
        return ratio;
    }

    @Override
    protected void onPostExecute(BuildImageResult result) {
        if(mUIHandler != null) {
            mUIHandler.sendMessage(mUIHandler.obtainMessage(0, result));
        }
    }

    public class BuildImageResult {
        private byte[] mData;
        private Bitmap mBitmap;

        /**
         * 저장된 파일명 (** 에러가 난 경우에는 익셉션 클래스 명)
         */
        private String mFilepath;

        public BuildImageResult(byte[] data, Bitmap bitmap, String filepath) {
            this.mData = data;
            this.mBitmap = bitmap;
            this.mFilepath = filepath;
        }

        public byte[] getData() {
            return mData;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public String getFilepath() {
            return mFilepath;
        }

        public String getFilename() {
            String filename = mFilepath.substring(mFilepath.lastIndexOf("/") + 1, mFilepath.indexOf(".jpg"));
            BaLog.d("originImagePath=" + mFilepath + ", filename=" + filename);
            return filename;
        }

        @Override
        public String toString() {
            return "bitmap.size=" + mBitmap != null ? mBitmap.getWidth() + "x" + mBitmap.getHeight() : "0x0"
                    + ", mFilePath=" + mFilepath;
        }
    }

    public static class CameraPictureItemInfo {
        public String mFilepath;
        public Bitmap mThumbnail;

        public CameraPictureItemInfo() {

        }
    }
}