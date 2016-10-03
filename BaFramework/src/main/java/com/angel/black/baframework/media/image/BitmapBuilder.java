package com.angel.black.baframework.media.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.util.BitmapUtil;

/**
 * Created by KimJeongHun on 2016-07-07.
 */
public class BitmapBuilder extends AsyncTask<String, Void, Bitmap> {
    private BaseActivity mActivity;
    private boolean mShowProgress;
    private int mDestWidth;
    private int mDestHeight;
    private BitmapBuildListener mListener;

    /**
     * 요구 크기를 전달.
     * @param width
     * @param height
     */
    public BitmapBuilder(BaseActivity activity, boolean showProgress, int width, int height, BitmapBuildListener listener) {
        this.mActivity = activity;
        this.mShowProgress = showProgress;
        this.mDestWidth = width;
        this.mDestHeight = height;
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        if(mShowProgress) {
            mActivity.showProgress();
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imagePath = params[0];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, options);
        BaLog.d("original bitmap size = " + options.outWidth + " x " + options.outHeight);

        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize(options, mDestWidth, mDestHeight);
        BaLog.d("inSampleSize=" + options.inSampleSize);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

        if(bitmap != null) {
            BaLog.d("sampling bitmap size = " + bitmap.getWidth() + " x " + bitmap.getHeight());

            // 회전정보 보고 똑바로 세움
            Bitmap rotatedBitmap = BitmapUtil.rotateBitmap(bitmap, BitmapUtil.getPhotoOrientation(imagePath));

            Bitmap scaledBitmap;

            if(rotatedBitmap.getWidth() > mDestWidth) {
                float ratio = mDestHeight / (float) mDestWidth;
                scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, mDestWidth, (int) (mDestWidth * ratio), false);
                BaLog.d("scaled bitmap size = " + scaledBitmap.getWidth() + " x " + scaledBitmap.getHeight());

                if(rotatedBitmap != null && rotatedBitmap != scaledBitmap) {
                    rotatedBitmap.recycle();
                }
                return scaledBitmap;
            } else {
                return rotatedBitmap;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(mShowProgress) {
            mActivity.hideProgress();
        }
        mListener.onBuildBitmap(bitmap);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public interface BitmapBuildListener {
        void onBuildBitmap(Bitmap bitmap);
    }
}
