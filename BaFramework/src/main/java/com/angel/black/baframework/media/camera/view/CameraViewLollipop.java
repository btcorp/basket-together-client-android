package com.angel.black.baframework.media.camera.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;

/**
 * Created by KimJeongHun on 2016-06-09.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraViewLollipop extends CameraViewCompat {
    private CameraPreviewLollipop mCameraPreview;

    protected CameraViewLollipop(Fragment fragment, ViewGroup root) {
        super(fragment, root);
        initCameraPreview((BaseActivity) fragment.getActivity());
    }

    private void initCameraPreview(BaseActivity activity) {
        mCameraPreview = new CameraPreviewLollipop(activity);
        mCameraPreview.setCameraActionCallback((CameraActionCallback) mFragment);
        mCameraPreview.setCameraOpenCallback((CameraOpenCallback) mFragment);


        mParentCameraPreview.addView(mCameraPreview, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public boolean isCreated() {
        return mCameraPreview.isAttachedToWindow();
    }

    @Override
    public void takePicture() throws Exception {
        if(isNowTakingPic) return;

        isNowTakingPic = true;

        try {
            if (isCreated()) {
                mCameraPreview.takePicture();
                isNowTakingPic = false;

            }
        } catch (Exception e) {
            mCameraActionCallback.onFailTakenPicture();
            isNowTakingPic = false;
        }
    }

    @Override
    public void pauseCamera() {
        BaLog.d();
        if(isAliveCamera()) {   // 카메라가 살아있을 경우 (화면만 꺼진 경우)
            stopPreview();
        }
    }

    @Override
    public void resumeCamera() {
        BaLog.d();
        if(isCreated() && isAliveCamera() && !mCameraPreview.isTakenPicture()) {
            // 초기화되었고, 카메라가 아직 살아있고, 사진찍은 상태가 아닌경우 (카메라 도중 화면이 꺼지고, 다시 켜진 경우), 다시찍기 한경우
            mCameraPreview.resumeCamera();
        }
    }

    @Override
    public boolean isAliveCamera() {
        return mCameraPreview.isAliveCamera();
    }

    @Override
    public boolean isShowingPreview() {
        return false;
    }

    @Override
    public void startPreview() {

    }

    @Override
    public void stopPreview() {
        mCameraPreview.stopPreview();
    }

    @Override
    public View getCameraPreview() {
        return mCameraPreview;
    }

    @Override
    public void openCameraAfterViewCreated() {
        mCameraPreview.openCameraAfterViewCreated();
    }

    @Override
    public int getFlashMode() {
        return mCameraPreview.getFlashMode();
    }

    @Override
    public void toggleFlash(int flashMode) {
        if(isFrontCamera()) {
            // 전면카메라에서 플래쉬 체인지 했을 때 익셉션 캐치
            super.toggleFlash(FLASH_MODE_OFF);
            mCameraPreview.setFlashMode(FLASH_MODE_OFF);

            mActivity.showToast(R.string.not_support_flash_on_front_camera);
        } else {
            super.toggleFlash(flashMode);
            BaLog.d();
            mCameraPreview.setFlashMode(flashMode);

            if (isAliveCamera()) {
                mCameraPreview.refreshCameraPreviewSession();
            }
        }
    }

    @Override
    public void resumeCameraPreview() {

    }

    @Override
    public void setDestFilePath(String destFilePath) {
        mCameraPreview.setDestFilePath(destFilePath);
    }

    @Override
    public boolean isFrontCamera() {
        return mCameraPreview.isFrontCamera();
    }

    @Override
    public void switchCamera(boolean front) {
        if(front) {
            super.toggleFlash(FLASH_MODE_OFF);
            mCameraPreview.setFlashMode(FLASH_MODE_OFF);
        }
        mCameraPreview.switchCamera(front);
    }

    @Override
    public void releaseCamera() {
        mCameraPreview.closeCamera();
    }
}
