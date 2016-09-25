//package com.angel.black.baframework.media.camera.view;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.hardware.Camera;
//import android.hardware.Camera.Size;
//import android.os.AsyncTask;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentActivity;
//import android.view.Surface;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.angel.black.baframework.R;
//import com.angel.black.baframework.logger.BaLog;
//import com.angel.black.baframework.ui.dialog.PermissionConfirmationDialog;
//import com.angel.black.baframework.util.StringUtil;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//public class CameraPreviewKitkat extends SurfaceView implements SurfaceHolder.Callback {
//	public static final int REQUEST_PERMISSIONS = 1;
//
//	private Context mContext;
//	private SurfaceHolder mHolder;
//	private Camera mCamera;
//	private List<Size> mSupportedPreviewSizes;
//	private Size mPreviewSize;
//	private CameraViewCompat.CameraOpenCallback mCameraOpenCallback;
//
//	/** 프리뷰 화면이 디스플레이 되고 있는지 여부 */
//	private boolean mIsShowingPreview = false;
//	private boolean mIsFrontCamera = false;
//	private int mFlashMode = CameraViewCompat.FLASH_MODE_OFF;
//
//	public void setCameraOpenCallback(CameraViewCompat.CameraOpenCallback cameraOpenCallback) {
//		this.mCameraOpenCallback = cameraOpenCallback;
//	}
//
//	public boolean isFrontCamera() {
//		return mIsFrontCamera;
//	}
//
//	public void setFlashMode(int flashMode) {
//		this.mFlashMode = flashMode;
//	}
//
//	public boolean isShowingPreview() {
//		return mIsShowingPreview;
//	}
//
//	public CameraPreviewKitkat(Context context) {
//		super(context);
//		mContext = context;
//
//		mHolder = getHolder();
//		mHolder.addCallback(this);
//		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//	}
//
//	public void surfaceCreated(SurfaceHolder holder) {
//		BaLog.i();
//
//		Activity activity = (Activity) getContext();
//		if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//			requestCameraPermission();
//			return;
//		} else if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//			requestWriteStoragePermission(true);
//			return;
//		}
//
//		if(mCamera == null) {
//			openCamera();
//		}
//	}
//
//	private void openCamera() {
//		releaseCameraAndPreview();
//		new CameraOpener(mCameraOpenCallback).execute();
//	}
//
//	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//		BaLog.e("surfaceChanged!! w=" + w + ", h=" + h + ", mIsShowingPreview=" + mIsShowingPreview);
//		if (mHolder.getSurface() == null || mCamera == null) {
//			return;
//		}
//
////		int screenHeight = ScreenUtil.getScreenHeight(getContext());
//		startPreview(w, h);
//	}
//
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		BaLog.i();
//		stopPreviewAndFreeCamera();
//	}
//
//	public void setCamera(Camera camera) {
//		if (mCamera == camera) { return; }
//
//		stopPreviewAndFreeCamera();
//
//		mCamera = camera;
//
//		if (mCamera != null) {
//			List<Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
//			mSupportedPreviewSizes = localSizes;
//			requestLayout();
//
//			try {
//				mCamera.setPreviewDisplay(mHolder);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			mCamera.startPreview();
//		}
//	}
//
//	/**
//	 * SurfaceView 가 다 초기화 되고 다시 카메라를 시작해야될 때 호출
//	 */
//	public void openCameraAfterViewCreated() {
//		if(mCamera == null) {
//			openCamera();
//		}
//	}
//
//	private void startPreview(int viewWidth, int viewHeight) {
//		BaLog.i("viewWidth=" + viewWidth + ", viewHeight=" + viewHeight);
//		try {
//			mCamera.stopPreview();
//		} catch (Exception e) {
//		}
//
//		setCameraDisplayOrientation((Activity) getContext(), getBackCameraId(), mCamera);
//
//		Camera.Parameters parameters = mCamera.getParameters();
//		mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
//		BaLog.d("debug available preview sizes!!");
//		debugSupportedSizeList(mSupportedPreviewSizes);
//
//		mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, viewWidth, viewHeight);
//		BaLog.d("getOptimalPreviewSize= " + mPreviewSize.width + " x " + mPreviewSize.height +
//				", ratio=" + mPreviewSize.width / (float) mPreviewSize.height);
//
//		Size pictureSize = determineBestPictureSize(parameters.getSupportedPictureSizes());
//
//		parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
////		BaLog.d("real previewSize=" + parameters.getPreviewSize().width + "x" + parameters.getPreviewSize().height);
//		parameters.setPictureSize(pictureSize.width, pictureSize.height);
//		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//		setFlashParameter(parameters, mFlashMode);
//
//		ViewGroup.LayoutParams params = getLayoutParams();
//		params.width = viewWidth;
//		params.height = (int) (viewWidth * (Math.max(mPreviewSize.width, mPreviewSize.height)
//				/ (float) Math.min(mPreviewSize.width, mPreviewSize.height)));
//		setLayoutParams(params);
//
//		try {
//			mCamera.setParameters(parameters);
//			mCamera.setPreviewDisplay(mHolder);
//			mCamera.startPreview();
//			mIsShowingPreview = true;
//		} catch (Exception e){
//			BaLog.e("Error starting camera preview: " + e.getMessage());
//		}
//	}
//
//	public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
//		Camera.CameraInfo info = new Camera.CameraInfo();
//		Camera.getCameraInfo(cameraId, info);
//		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//		int degrees = 0;
//		switch (rotation) {
//			case Surface.ROTATION_0: degrees = 0; break;
//			case Surface.ROTATION_90: degrees = 90; break;
//			case Surface.ROTATION_180: degrees = 180; break;
//			case Surface.ROTATION_270: degrees = 270; break;
//		}
//
//		int result;
//		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//			result = (info.orientation + degrees) % 360;
//			result = (360 - result) % 360;  // compensate the mirror
//		} else {  // back-facing
//			result = (info.orientation - degrees + 360) % 360;
//		}
//		camera.setDisplayOrientation(result);
//	}
//
//	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
//		final double ASPECT_TOLERANCE = 0.2;
//		double targetRatio = (double) h / w;
//
//		if (sizes == null)
//			return null;
//
//		Size optimalSize = null;
//		double minDiff = Double.MAX_VALUE;
//
//		int targetHeight = h;
//
//		for (Size size : sizes) {
//			// 작은 사이즈를 width 로 큰 사이즈를 height 으로 받아옴 ( 카메라에 따라 width x height 값이 바뀔수 있기 때문)
//			int width = Math.min(size.width, size.height);
//			int height = Math.max(size.width, size.height);
//
//			double ratio = (double) height / width;
//			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
//				continue;
//
//			if (Math.abs(height - targetHeight) < minDiff) {
//				optimalSize = size;
//				minDiff = Math.abs(height - targetHeight);
//			}
//		}
//
//		if (optimalSize == null) {
//			minDiff = Double.MAX_VALUE;
//			for (Size size : sizes) {
//				// 큰 사이즈를 height 으로 받아옴 ( 카메라에 따라 width x height 값이 바뀔수 있기 때문)
//				int height = Math.max(size.width, size.height);
//
//				if (Math.abs(height - targetHeight) < minDiff) {
//					optimalSize = size;
//					minDiff = Math.abs(height - targetHeight);
//				}
//			}
//		}
//
//		return optimalSize;
//	}
//
//	public void requestCameraPermission() {
//		FragmentActivity activity = (FragmentActivity) getContext();
//		if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
//			PermissionConfirmationDialog.newInstance(getResources().getString(R.string.request_camera_permission),
//					Manifest.permission.CAMERA, REQUEST_PERMISSIONS, false)
//					.show(activity.getSupportFragmentManager(), PermissionConfirmationDialog.TAG);
//		} else {
//			ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, REQUEST_PERMISSIONS);
//		}
//	}
//
//	public void requestWriteStoragePermission(boolean showRequestReason) {
//		FragmentActivity activity = (FragmentActivity) getContext();
//		if (showRequestReason && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//			PermissionConfirmationDialog.newInstance(getResources().getString(R.string.request_save_storage_permission),
//					Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSIONS, false)
//					.show(activity.getSupportFragmentManager(), PermissionConfirmationDialog.TAG);
//		} else {
//			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
//		}
//	}
//
//	private void debugSupportedSizeList(List<Size> sizes) {
//		for (Size size : sizes) {
//			BaLog.d("available size = " + size.width + " x " + size.height + ", ratio=" + size.width / (float) size.height);
//		}
//	}
//
//	protected Size determineBestPictureSize(List<Size> sizes) {
//		final double ASPECT_TOLERANCE = 0.2;
//		Size bestSize = null;
//		long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//		long availableMemory = Runtime.getRuntime().maxMemory() - used;
//
//		ArrayList<Size> contenderSizeList = new ArrayList<>();
//		for (Size currentSize : sizes) {
//			int newArea = currentSize.width * currentSize.height;
//			long neededMemory = newArea * 4 * 4; // newArea * 4 Bytes/pixel * 4 needed copies of the bitmap (for safety :) )
//
//			float previewRatio = Math.max(mPreviewSize.width, mPreviewSize.height) / (float) Math.min(mPreviewSize.width, mPreviewSize.height);
//			float curSizeRatio = Math.max(currentSize.width, currentSize.height) / (float) Math.min(currentSize.width, currentSize.height);
//			boolean isDesiredRatio = Math.abs(previewRatio - curSizeRatio) <= ASPECT_TOLERANCE;
//
//			boolean isSafe = neededMemory < availableMemory;
//
//			if (isDesiredRatio && isSafe) {
//				contenderSizeList.add(currentSize);
//			}
//		}
//
//		Collections.sort(contenderSizeList, new CompareSizesByRatio());
//		BaLog.d("preview ratio near size debug!!");
//		debugSupportedSizeList(contenderSizeList);
//
//		if(contenderSizeList.size() > 0) {
//			bestSize = contenderSizeList.get(0);
//		}
//
//		if(bestSize == null) {
//			bestSize = sizes.get(0);
//		}
//
//		BaLog.d("bestSize=" + bestSize.width + " x " + bestSize.height);
//		return bestSize;
//	}
//
//	public void setFlash(int flashMode) {
//		if(mCamera != null) {
//			Camera.Parameters parameters = mCamera.getParameters();
//
//			setFlashParameter(parameters, flashMode);
//			this.mFlashMode = flashMode;
//			mCamera.setParameters(parameters);
//		}
//	}
//
//	private void setFlashParameter(Camera.Parameters parameters, int flashMode) {
//		if(parameters.getSupportedFlashModes() != null) {
//			if (flashMode == CameraViewCompat.FLASH_MODE_ALWAYS_ON) {
//				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
//			} else if (flashMode == CameraViewCompat.FLASH_MODE_AUTO) {
//				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//			} else {
//				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//			}
//		}
//	}
//
//	private int getBackCameraId() {
//		int cameraCount = 0;
//
//		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//		cameraCount = Camera.getNumberOfCameras();
//		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
//			Camera.getCameraInfo(camIdx, cameraInfo);
//			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//				return camIdx;
//			}
//		}
//
//		return 0;
//	}
//
//	public int getFlashMode() {
//		String flashMode = mCamera.getParameters().getFlashMode();
//
//		flashMode = StringUtil.notNullString(flashMode);
//
//		if(flashMode.equals(Camera.Parameters.FLASH_MODE_AUTO)) {
//			return CameraViewCompat.FLASH_MODE_AUTO;
//		} else if(flashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
//			return CameraViewCompat.FLASH_MODE_ALWAYS_ON;
//		} else {
//			return CameraViewCompat.FLASH_MODE_OFF;
//		}
//	}
//
//	public void resumeCamera() {
//		BaLog.d();
////		int screenHeight = ScreenUtil.getScreenHeight(getContext());
//		startPreview(getWidth(), getHeight());
//	}
//
//	public Camera getCurrentCamera() {
//		return mCamera;
//	}
//
//	public void stopPreview() {
//		Camera camera = getCurrentCamera();
//		camera.stopPreview();
//		mIsShowingPreview = false;
//	}
//
//	public boolean isAliveCamera() {
//		return mCamera != null;
//	}
//
//	public void releaseCameraAndPreview() {
//		setCamera(null);
//		if (mCamera != null) {
//			mCamera.release();
//			mCamera = null;
//		}
//	}
//
//	private void stopPreviewAndFreeCamera() {
//		if (mCamera != null) {
//			mCamera.stopPreview();
//			mCamera.release();
//			mCamera = null;
//			mIsShowingPreview = false;
//		}
//	}
//
//	public void resumeCameraPreview() {
//		if (mCamera != null) {
//			mCamera.stopPreview();
//			mCamera.startPreview();
//		}
//	}
//
//	/**
//	 * 프리뷰 비율과 제일 차이가 작은 사이즈 순으로 정렬
//	 */
//	class CompareSizesByRatio implements Comparator<Size> {
//
//		@Override
//		public int compare(Size lhs, Size rhs) {
//			float previewRatio = Math.max(mPreviewSize.width, mPreviewSize.height) / (float) Math.min(mPreviewSize.width, mPreviewSize.height);
//			float leftSizeRatio = Math.max(lhs.width, lhs.height) / (float) Math.min(lhs.width, lhs.height);
//			float rightSizeRatio = Math.max(rhs.width, rhs.height) / (float) Math.min(rhs.width, rhs.height);
//
//			float leftRatioDiff = Math.abs(previewRatio - leftSizeRatio);
//			float rightRatioDiff = Math.abs(previewRatio - rightSizeRatio);
//
//			if(leftRatioDiff < rightRatioDiff)
//				return -1;
//			else if(leftRatioDiff > rightRatioDiff)
//				return 1;
//			else
//				return 0;
//		}
//	}
//
//	class CameraOpener extends AsyncTask {
//		private CameraViewCompat.CameraOpenCallback mCameraOpenCallback;
//
//		public CameraOpener(CameraViewCompat.CameraOpenCallback cameraOpenCallback) {
//			this.mCameraOpenCallback = cameraOpenCallback;
//		}
//
//		@Override
//		protected void onPreExecute() {
//			if(mCameraOpenCallback != null) {
//				mCameraOpenCallback.onStartCameraOpen();
//			}
//		}
//
//		@Override
//		protected Object doInBackground(Object[] params) {
//			String result = null;
//
//			try {
//				mCamera = Camera.open();
//				result = "succ";
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			return result;
//		}
//
//		@Override
//		protected void onPostExecute(Object result) {
//			if("succ".equals(result)) {
//				BaLog.i("camera open success!");
//				if(mCameraOpenCallback != null) {
//					mCameraOpenCallback.onSuccessCameraOpen();
//				}
//			} else {
//				if(mCameraOpenCallback != null) {
//					mCameraOpenCallback.onFailCameraOpen();
//					Toast.makeText(mContext, "카메라를 실행할 수 없습니다.", Toast.LENGTH_LONG).show();
//				}
//			}
//		}
//	}
//}
