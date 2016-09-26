package com.angel.black.baframework.media.camera.view;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.media.camera.CameraPictureFileBuilder;
import com.angel.black.baframework.util.BuildUtil;

/**
 * Created by KimJeongHun on 2016-06-09.
 */
public abstract class CameraViewCompat implements View.OnClickListener {
    protected final String TAG = this.getClass().getSimpleName();

    public static final int FLASH_MODE_OFF = 0;
    public static final int FLASH_MODE_AUTO = 1;
    public static final int FLASH_MODE_ALWAYS_ON = 2;

    protected BaseActivity mActivity;
    protected Fragment mFragment;
    protected ViewGroup mParentCameraPreview;
    protected ViewGroup mRootView;
    protected CameraActionCallback mCameraActionCallback;

    protected ImageButton mBtnSwitchCamera;
    protected ImageButton mBtnSwitchFlash;

    /** 현재 사진 찍기를 시도하는 중인지 여부 */
    protected boolean isNowTakingPic;

    protected CameraViewCompat(Fragment fragment, ViewGroup root) {
        mFragment = fragment;
        mActivity = (BaseActivity) fragment.getActivity();
        mRootView = root;
        View v = fragment.getActivity().getLayoutInflater().inflate(R.layout.camera_view, mRootView);
        mParentCameraPreview = (ViewGroup) v.findViewById(R.id.layout_camera_view);
        mBtnSwitchCamera = (ImageButton) mParentCameraPreview.findViewById(R.id.btn_camera_switch);
        mBtnSwitchFlash = (ImageButton) mParentCameraPreview.findViewById(R.id.btn_flash_switch);
        mBtnSwitchCamera.setOnClickListener(this);
        mBtnSwitchFlash.setOnClickListener(this);

        setCameraActionCallback((CameraActionCallback) mFragment);
    }

    public static CameraViewCompat createInstance(Fragment fragment, ViewGroup root) {
        if (BuildUtil.isAboveLollipop()) {
            return new CameraViewLollipop(fragment, root);
        } else {
//            return new CameraViewKitkat(fragment, root);
            return null;
        }
    }

    public void setCameraActionCallback(CameraActionCallback cameraCallback) {
        this.mCameraActionCallback = cameraCallback;
    }

    public abstract boolean isCreated();

    public abstract void takePicture() throws Exception;

    public abstract void pauseCamera();

    public abstract void resumeCamera();

    public abstract boolean isAliveCamera();

    public abstract boolean isShowingPreview();

    public abstract void startPreview();

    public abstract void stopPreview();

    public abstract View getCameraPreview();

    public abstract void openCameraAfterViewCreated();

    public abstract int getFlashMode();

    public abstract boolean isFrontCamera();

    public abstract void switchCamera(boolean front);

    public abstract void releaseCamera();

    protected void toggleFlash(int flashMode) {
        setFlashIcon(flashMode);
    }

    /**
     * 정지된 카메라 프리뷰를 재개한다.
     * (셋팅 변환 없이)
     */
    public abstract void resumeCameraPreview();

    private void setFlashIcon(int flashMode) {
        if (flashMode == FLASH_MODE_OFF) {
            mBtnSwitchFlash.setImageResource(R.drawable.ic_flash_on_white_24dp);
        } else if (flashMode == FLASH_MODE_AUTO){
            mBtnSwitchFlash.setImageResource(R.drawable.ic_flash_auto_white_24dp);
        } else {
            mBtnSwitchFlash.setImageResource(R.drawable.ic_flash_off_white_24dp);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_camera_switch) {
            switchCamera(!isFrontCamera());
        } else if(v.getId() == R.id.btn_flash_switch) {
            toggleFlash((getFlashMode() + 1) % 3);
        }
    }

//    public void setCameraSizeToSquare() {
//        if(BuildUtil.isAboveHoneyComb()) {
//            mParentCameraPreview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                @Override
//                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                    BaLog.d("left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom);
//
//                    if(mBlackEmptyView == null) {
//                        return;
//                    }
//
//                    final int width = right - left;
//
//                    new Handler() {
//                        @Override
//                        public void handleMessage(Message msg) {
//                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mBlackEmptyView.getLayoutParams();
//                            params.topMargin = width;
//                            mBlackEmptyView.setLayoutParams(params);
//                            mParentCameraPreview.updateViewLayout(mBlackEmptyView, params);
//                        }
//                    }.sendEmptyMessageDelayed(0, 100);
//
//                    if(BuildUtil.isAboveHoneyComb()) {
//                        mParentCameraPreview.removeOnLayoutChangeListener(this);
//                    }
//                }
//            });
//
//            mBlackEmptyView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                @Override
//                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                    BaLog.d("emptyView left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom);
//
//                }
//            });
//        }
//    }

    public interface CameraActionCallback {
        /** 사진이 막 찍혔을 때 콜백 (파일 만들기 시작). 사진찍힌 영역을 반환 */
        void onSuccessTakenPicture();
        /** 사진 찍는 중 실패했을 때 콜백 */
        void onFailTakenPicture();
        /** 찍은 사진의 파일 저장이 완료 되었을 때 콜백 */
        void onSuccessSavePictureImageToFile(CameraPictureFileBuilder.BuildImageResult buildImageResult);
        /** 찍은 사진의 파일 저장이 실패 했을 때 콜백 */
        void onFailSavePictureImageToFile(String fileName);
        /** 카메라 프리뷰가 재시작될 때 콜백 */
    }

    public interface CameraOpenCallback {
        void onStartCameraOpen();

        void onFailCameraOpen();

        void onSuccessCameraOpen();
    }
}
