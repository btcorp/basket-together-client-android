package com.angel.black.baframework.network;

import android.os.AsyncTask;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;

public class ImageUploaderTask {
	private BaseActivity mActivity;
	private ImageUploadListener mImageUploadListener;
	private GeoPictureUploader mPictureuploader;

	public ImageUploaderTask(BaseActivity activity, GeoPictureUploader pictureUploader) {
		this.mActivity = activity;
		this.mPictureuploader = pictureUploader;
	}

	public void setImageUploadListener(ImageUploadListener listener) {
		mImageUploadListener = listener;
	}

	public void uploadImage() {
		new ImageFileUploader().execute();
	}
	
	public class ImageFileUploader extends AsyncTask<String, Void, GeoPictureUploader.ReturnCode> {
		
		protected void onPreExecute() {
			mActivity.showProgress();
		}

		@Override
		protected GeoPictureUploader.ReturnCode doInBackground(String... params) {
			GeoPictureUploader.ReturnCode rc = GeoPictureUploader.ReturnCode.unknown;

			rc = mPictureuploader.uploadPicture();

			return rc;
		}
		
		protected void onPostExecute(GeoPictureUploader.ReturnCode result) {
			mActivity.hideProgress();

			BaLog.e(":::::::::: onPostExecute result : " + result);
			mImageUploadListener.onUploadComplete(result);
		}
	}

	public interface ImageUploadListener {
		void onUploadComplete(GeoPictureUploader.ReturnCode result);
	}
}
