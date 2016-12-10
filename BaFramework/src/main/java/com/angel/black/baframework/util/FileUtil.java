package com.angel.black.baframework.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KimJeongHun on 2016-07-05.
 */
public class FileUtil {
    public static String getRealFilePathFromUri(Context context, Uri uri){
        String path = "";
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null );

        if(cursor != null) {
            cursor.moveToNext();
            path = cursor.getString(cursor.getColumnIndex("_data"));
            cursor.close();
        }

        return path;
    }

    public static String convertUri(String path) {
        return Uri.decode(Uri.fromFile(new File(path)).toString());
    }

    public static void saveImageFile(BaseActivity activity, Bitmap bitmap, String tempImagePath, ImageFileBuildListener imageFileBuildListener) {
        new ImageFileSaver(activity, tempImagePath, imageFileBuildListener).execute(bitmap);
    }

    /**
     * 임의로 테스트 파일 하나를 만들었다가 지워본다.
     * @param context
     * @return 파일 접근 성공, 실패
     */
    public static boolean testFileAccess(Context context) {
        String tempPath = BaPackageManager.getTempImagePath(context);
        File testFile = new File(tempPath + "test.dat");

        boolean success = false;
        try {
            if(testFile.createNewFile()) {
                if (testFile.delete()) {
                    success = true;
                }
            }
        } catch (IOException e) {
            BaLog.v("test file access failed >> " + e.getMessage());
        }

        return success;
    }

    public static void deleteTempImageFiles(Context context) {
        File dir = new File(BaPackageManager.getTempImagePath(context));

        File[] files = dir.listFiles();

        if (files == null)
            return;

        for (File f : files) {
            try {
                f.delete();
            } catch (IllegalArgumentException ie) {
                ie.printStackTrace();
            }
        }
    }

    public static String createTempImageFile(String tempDirPath) {
        File newFile = new File(tempDirPath, System.currentTimeMillis() + ".jpg");

        try {
            newFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return newFile.toString();
    }

    static class ImageFileSaver extends AsyncTask<Bitmap, Void, String> {
        BaseActivity mActivity;
        String mDestFilePath;
        ImageFileBuildListener mListener;

        ImageFileSaver(BaseActivity activity, String destFilePath, ImageFileBuildListener imageFileBuildListener) {
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

    public interface ImageFileBuildListener {
        void onSuccessImageFileBuild(String filepath);
        void onFailImageFileBuild(String filepath, String errMsg);
    }

    /**
     * 웹에서 한개의 파일을 다운로드 하여 로컬 파일에 저장할 때 사용하는 다운로더
     */
    public static class WebFileDownloader extends AsyncTask<String, Void, Boolean> {
        private FileDownloadListener mFileDownloadListener;
        private BaseActivity mActivity;

        public WebFileDownloader(BaseActivity activity, FileDownloadListener listener) {
            this.mActivity = activity;
            this.mFileDownloadListener = listener;
        }

        @Override
        protected void onPreExecute() {

        }

        /**
         *
         * @param params 0 : 다운로드 파일 url, 1 : 저장할 로컬 파일 경로
         * @return
         */
        @Override
        protected Boolean doInBackground(String... params) {
            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            Boolean success = false;

            try {
                URL url = new URL(params[0]);

                inputStream = url.openStream();
                outputStream = new FileOutputStream(params[1]);

                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                success = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {}
                }
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {}
                }
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result.booleanValue()) {
                BaLog.d("save file success!!");
                mFileDownloadListener.onSuccessFileDownload();
            } else {
                BaLog.e("save file failed!!");
                mFileDownloadListener.onFailFileDownload();
            }
        }
    }

    /**
     * 웹에서 여러개의 파일을 다운로드 하여 로컬 파일에 저장할 때 사용하는 다운로더
     */
    public static class WebFileListDownloader extends AsyncTask<List<String>, Void, Boolean> {
        private FileListDownloadListener mFileListDownloadListener;
        private BaseActivity mActivity;
        private List<String> mSavedFileList;    // 저장된 로컬 파일 리스트

        public WebFileListDownloader(BaseActivity activity, FileListDownloadListener listener) {
            this.mActivity = activity;
            this.mFileListDownloadListener = listener;
            this.mSavedFileList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {

        }

        /**
         * @param params 다운로드할 웹 파일 url 리스트
         * @return
         */
        @Override
        protected Boolean doInBackground(List<String>... params) {
            Boolean success = true;

            for(String urlStr : params[0]) {
                InputStream inputStream = null;
                FileOutputStream outputStream = null;

                try {
                    URL url = new URL(urlStr);

                    String filepath = Environment.getExternalStorageDirectory() + "/Android/data/" + mActivity.getPackageName() + "/temp/";

                    File outputPath = new File(filepath);

                    if(!outputPath.exists()) {
                        outputPath.mkdir();
                    }

                    String filename = filepath  + "temp_" + System.currentTimeMillis() + ".jpg";

                    inputStream = url.openStream();
                    outputStream = new FileOutputStream(filename);

                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;
                    while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    mSavedFileList.add(filename);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    success = false;
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    success = false;
                    break;
                } finally {
                    if(outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {}
                    }
                    if(inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {}
                    }
                }
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result.booleanValue()) {
                BaLog.d("save file success!!");
                mFileListDownloadListener.onSuccessFileListDownload(mSavedFileList);
            } else {
                BaLog.e("save file failed!!");
                mFileListDownloadListener.onFailFileListDownload();
            }
        }
    }

    public interface FileDownloadListener {
        void onSuccessFileDownload();
        void onFailFileDownload();
    }

    public interface FileListDownloadListener {
        void onSuccessFileListDownload(List<String> savedFileList);
        void onFailFileListDownload();
    }
}
