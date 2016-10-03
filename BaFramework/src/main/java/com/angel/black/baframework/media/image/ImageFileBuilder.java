package com.angel.black.baframework.media.image;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;

import java.io.FileOutputStream;

/**
 * Created by KimJeongHun on 2016-10-03.
 */
public class ImageFileBuilder extends AsyncTask<Bitmap, Void, String> {
    BaseActivity mActivity;
    String mDestFilePath;
    ImageFileBuildListener mListener;

    public ImageFileBuilder(BaseActivity activity, String destFilePath, ImageFileBuildListener imageFileBuildListener) {
        this.mActivity = activity;
        this.mDestFilePath = destFilePath;
        this.mListener = imageFileBuildListener;
    }

    @Override
    protected void onPreExecute() {
        mActivity.showProgress();
    }

    @Override
    protected String doInBackground(Bitmap... params) {
        FileOutputStream outStream;
        Bitmap bitmap = params[0];
        String errMsg;
        try {
            outStream = new FileOutputStream(mDestFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            outStream.close();

            BaLog.d("IMAGE TEST >> bitmap to file build finish!!");

            return "succ";
        } catch (Exception e) {
            e.printStackTrace();
            errMsg = e.getMessage();
        }

        return errMsg;
    }

    @Override
    protected void onPostExecute(String result) {
        if("succ".equals(result)) {
            BaLog.d("image save file success >> " + mDestFilePath);
            mListener.onSuccessImageFileBuild(mDestFilePath);
        } else {
            BaLog.e("image save file faile >> " + result);
            mListener.onFailImageFileBuild(mDestFilePath, result);
        }
        mActivity.hideProgress();
    }
}

interface ImageFileBuildListener {
    void onSuccessImageFileBuild(String filepath);
    void onFailImageFileBuild(String filepath, String errMsg);
}