//package com.angel.black.baframework.media.camera.view;
//
//import android.annotation.TargetApi;
//import android.hardware.Camera;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.Fragment;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.angel.black.baframework.R;
//import com.angel.black.baframework.core.base.BaseActivity;
//import com.angel.black.baframework.logger.BaLog;
//
//
///**
// * Created by KimJeongHun on 2016-06-09.
// */
//@TargetApi(Build.VERSION_CODES.KITKAT)
//public class CameraViewKitkat extends CameraViewCompat {
//    private CameraPreviewKitkat mCameraPreview;
//
//    protected CameraViewKitkat(Fragment fragment, ViewGroup root) {
//        super(fragment, root);
//        initCamerPreview((BaseActivity) fragment.getActivity());
//    }
//
//    private void initCamerPreview(BaseActivity activity) {
//        mCameraPreview = new CameraPreviewKitkat(activity);
//        mParentCameraPreview.addView(mCameraPreview, 0);
//        mCameraPreview.setCameraOpenCallback((CameraOpenCallback) mFragment);
//
//        BaLog.d("mParentCameraPreview.addView(mCameraPreview, 0)");
//    }
//
//    @Override
//    public boolean isCreated() {
//        return mCameraPreview.isShowingPreview();
//    }
//
//    @Override
//    public void takePicture() throws Exception {
//        final Camera camera = mCameraPreview.getCurrentCamera();
//
//        synchronized (camera) {
//            BaLog.d("isNowTakingPic=" + isNowTakingPic);
//
//            camera.autoFocus(new Camera.AutoFocusCallback() {
//                @Override
//                public void onAutoFocus(boolean success, Camera camera) {
//                    BaLog.d(TAG, "onAutoFocus: success=" + success);
//
//                    try {
//                        if (success) {
//                            camera.takePicture(shutterCallback, null, jpegCallback);
//                        } else {
//                            // 포커스실패
//                            if (isFrontCamera()) {
//                                // 전면카메라 일때는 그냥 촬영
//                                camera.takePicture(shutterCallback, null, jpegCallback);
//                            } else {
//                                mCameraActionCallback.onFailTakenPicture();
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        camera.cancelAutoFocus();
//                        isNowTakingPic = false;
//                    }
//                }
//            });
//
//        }
//    }
//
//    @Override
//    public void pauseCamera() {
//        mCameraPreview.stopPreview();
//        mCameraPreview.getCurrentCamera().setPreviewCallback(null);	// 화면이 onPause 될때마다 등록되있던 프리뷰 콜백을 제거해줘야함
//    }
//
//    @Override
//    public void resumeCamera() {
//        mCameraPreview.resumeCamera();
//    }
//
//    @Override
//    public boolean isAliveCamera() {
//        return mCameraPreview.isAliveCamera();
//    }
//
//    @Override
//    public boolean isShowingPreview() {
//        return mCameraPreview.isShowingPreview();
//    }
//
//    @Override
//    public void startPreview() {
//        resumeCamera();
//    }
//
//    @Override
//    public void stopPreview() {
//        mCameraPreview.stopPreview();
//    }
//
//    @Override
//    public View getCameraPreview() {
//        return mCameraPreview;
//    }
//
//    @Override
//    public void openCameraAfterViewCreated() {
//        mCameraPreview.openCameraAfterViewCreated();
//    }
//
//    @Override
//    public int getFlashMode() {
//        return mCameraPreview.getFlashMode();
//    }
//
//    @Override
//    public void toggleFlash(int flashMode) {
//        BaLog.d("flashMode=" + flashMode);
//
//        if(isFrontCamera()) {
//            // 전면카메라에서 플래쉬 체인지 했을 때 익셉션 캐치
//            super.toggleFlash(FLASH_MODE_OFF);
//            mCameraPreview.setFlashMode(FLASH_MODE_OFF);
//
//            mActivity.showToast(R.string.not_support_flash_on_front_camera);
//
//        } else {
//            int prevFlashMode = mCameraPreview.getFlashMode();
//            super.toggleFlash(flashMode);
//
//            try {
//                mCameraPreview.setFlash(flashMode);
//
//                if (isAliveCamera()) {
//                    mCameraPreview.resumeCamera();
//                }
//            } catch (RuntimeException e) {
//                // 전면카메라에서 플래쉬 체인지 했을 때 익셉션 캐치
//                super.toggleFlash(prevFlashMode);
//                mCameraPreview.setFlashMode(prevFlashMode);
//            }
//        }
//    }
//
//    @Override
//    public void resumeCameraPreview() {
//        mCameraPreview.resumeCameraPreview();
//    }
//
//    @Override
//    public boolean isFrontCamera() {
//        return mCameraPreview.isFrontCamera();
//    }
//
//    @Override
//    public void switchCamera(boolean front) {
////        mCameraPreview.switchCamera(front);
//
//        if(front) {
//            this.toggleFlash(FLASH_MODE_OFF);
//        }
//    }
//
//    @Override
//    public void releaseCamera() {
//        // 카메라 자원 해제
//        mCameraPreview.releaseCameraAndPreview();
//    }
//
//    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
//        public void onShutter() {
//            BaLog.d(TAG, "onShutter'd");
//        }
//    };
//
//    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
//        public void onPictureTaken(byte[] data, Camera camera) {
//            BaLog.i("data.length=" + data.length);
//
//            isNowTakingPic = false;
//
//            if(mCameraActionCallback != null) {
//                mCameraActionCallback.onSuccessTakenPicture();
//            }
//
//            CameraPictureFileBuilder task = new CameraPictureFileBuilder(mActivity,
//                    MyPackageManager.getPublicAppAlbumPath(mActivity), true,
//                    isFrontCamera(), new Handler(mActivity.getMainLooper()) {
//                @Override
//                public void handleMessage(Message msg) {
//                    if(msg.what == 0) {
//                        CameraPictureFileBuilder.BuildImageResult result = (CameraPictureFileBuilder.BuildImageResult) msg.obj;
//
//                        if(mCameraActionCallback != null) {
//                            mCameraActionCallback.onSuccessSavePictureImageToFile(result);
//                        }
//                    }
//                }
//            });
//            task.execute(ArrayUtils.toObject(data));
//
//            // 바로 다시 카메라 재개하지 않고, 프리뷰 추가가 완료되면
//            // 액티비티의 onAddedPreview 에서 재개함.(성능때문에)
//        }
//    };
//}
