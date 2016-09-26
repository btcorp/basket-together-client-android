package com.angel.black.baframework.media.camera.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.core.base.BaseFragment;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.camera.CameraPictureFileBuilder;
import com.angel.black.baframework.media.camera.view.CameraViewCompat;
import com.angel.black.baframework.security.PermissionConstants;

import java.io.FileNotFoundException;

/**
 * Created by KimJeongHun on 2016-07-01.
 */
public class CameraFragment extends BaseFragment implements CameraViewCompat.CameraActionCallback,
        CameraViewCompat.CameraOpenCallback {

    /** 사진을 더 촬영할 수 있는 수 */
    private static final String ARG_CAN_TAKE_COUNT = "canTakeCount";

    private ViewGroup mLayoutCameraView;
    private CameraViewCompat mCameraView;
    private Button mTakePictureButton;

    public void setTakePictureButton(Button btnTakePicture) {
        mTakePictureButton = btnTakePicture;
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mCameraView.takePicture();
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(R.string.error_camera);
                }
            }
        });
    }

    private CameraActivityCallback cameraActivityCallback;

    private boolean isBuildingFile;
    private boolean mLockCameraTake;        // 카메라 촬영 락
    private int mCanTakeCount;

    public static CameraFragment newInstance(int canTakeCount) {
        CameraFragment instance = new CameraFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CAN_TAKE_COUNT, canTakeCount);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkPermission(Manifest.permission.CAMERA)) {
            requestPermission(Manifest.permission.CAMERA, R.string.request_camera_permission,
                    PermissionConstants.REQUEST_CAMERA_PERMISSION, true);
            return;
        } else if(!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.request_write_storage_permission,
                    PermissionConstants.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION, true);
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BaLog.i();
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        mLayoutCameraView = (ViewGroup) view.findViewById(R.id.layout_root);

        showProgress();

        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        BaLog.i();
        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 프래그먼트 전환 애니메이션이 끝난 후 카메라 오픈
                BaLog.i(TAG, "fragment anim end");
                createCameraView();
//                mCameraView.setCameraSizeToSquare();
            }
        });

        return anim;
    }

    private void createCameraView() {
        if(mCameraView == null) {
            mCameraView = CameraViewCompat.createInstance(CameraFragment.this, mLayoutCameraView);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BaLog.i();
        cameraActivityCallback = (CameraActivityCallback) getActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BaLog.i();

        createCameraView();

//        ((RegistProductImagesActivity) getActivity()).setMode(RegistProductImagesActivity.MODE_CAMERA);

        Bundle args = getArguments();
        if(args != null) {
            mCanTakeCount = getArguments().getInt(ARG_CAN_TAKE_COUNT);
        } else {
            mCanTakeCount = 1;
        }

        cameraActivityCallback.onDisplayCameraPreview();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BaLog.i();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        BaLog.i("pemissions=" + permissions.length + ", grantResults=" + grantResults.length);
        if (requestCode == PermissionConstants.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 퍼미션 거부
                getActivity().finish();
            } else {
                if(checkPermission(Manifest.permission.CAMERA) && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    openCamera();
                }
            }
        }
    }

    public void openCamera() {
        mCameraView.openCameraAfterViewCreated();
    }

    @Override
    public void onSuccessTakenPicture() {
        BaLog.i();
        isBuildingFile = true;
        mCanTakeCount--;
        cameraActivityCallback.onSuccessTakenPictureNow();
        mLockCameraTake = false;
    }

    @Override
    public void onFailTakenPicture() {
        BaLog.i();
        Toast.makeText(getActivity(), R.string.error_camera, Toast.LENGTH_SHORT).show();
        mLockCameraTake = false;
    }

    @Override
    public void onSuccessSavePictureImageToFile(CameraPictureFileBuilder.BuildImageResult buildImageResult) {
        BaLog.i();
        isBuildingFile = false;

        cameraActivityCallback.onSuccessTakenPictureAndSaveFile(buildImageResult);
    }

    /**
     * 카메라로 사진 찍고 파일로 저장중 에러발생했을 때 받는 콜백
     *
     * @param fileName 익셉션 명이 전달됨
     */
    @Override
    public void onFailSavePictureImageToFile(String fileName) {
        if(FileNotFoundException.class.getSimpleName().equals(fileName)) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Toast.makeText(getActivity(), "사진을 저장하는데 문제가 발생했습니다.\n저장공간 권한 허용 후에는 앱을 재시작하셔야 합니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "사진을 저장하는데 문제가 발생했습니다.\n앱을 재시작 후 다시 시도해보세요.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFailCameraOpen() {
        BaLog.i();
        hideProgress();
//        ((RegistProductImagesActivity) getActivity()).showGallery();
    }

    @Override
    public void onSuccessCameraOpen() {
        BaLog.i();

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        });

        // 카메라 오픈이 성공했는데 프리뷰가 시작되고 있지 않다면, 프리뷰 시작
        if(!mCameraView.isShowingPreview()) {
            mCameraView.startPreview();
        }
    }

    @Override
    public void onStartCameraOpen() {
        BaLog.i();
        showProgress();
    }

    public void takePicture() {
        if(mCanTakeCount <= 0) {
            ((BaseActivity) getActivity()).showToast(R.string.no_more_can_take_camera);
            return;
        }

        BaLog.v("mLockCameraTake=" + mLockCameraTake);

        try {
            mCameraView.takePicture();
        } catch (Exception e) {
            ((BaseActivity) getActivity()).showToast(R.string.error_camera);
            mLockCameraTake = false;
        }
    }

    public void increaseCanTakeCount() {
        this.mCanTakeCount++;
    }

    public void resumeCameraPreview() {
        BaLog.i();
        if(mCameraView != null) {
            mCameraView.resumeCameraPreview();
        }
    }

    public void releaseCamera() {
        BaLog.i();
        if(mCameraView != null) {
            mCameraView.releaseCamera();
        }
    }

    public interface CameraActivityCallback {
        /**
         * 방금 막 사진을 찍었을 때 콜백. 파일저장 과는 무관. 사진 찍은 영역을 반환
         */
        void onSuccessTakenPictureNow();

        /**
         * 사진을 찍고 파일 저장까지 완료했을 때 콜백
         * @param buildImageResult
         */
        void onSuccessTakenPictureAndSaveFile(CameraPictureFileBuilder.BuildImageResult buildImageResult);

        /**
         * 프리뷰화면이 보여질 때 콜백
         */
        void onDisplayCameraPreview();
    }
}
