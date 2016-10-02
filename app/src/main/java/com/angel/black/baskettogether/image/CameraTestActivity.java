package com.angel.black.baskettogether.image;

import android.os.Bundle;
import android.widget.Button;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.camera.CameraPictureFileBuilder;
import com.angel.black.baframework.media.camera.fragment.CameraFragment;
import com.angel.black.baskettogether.R;

/**
 * Created by KimJeongHun on 2016-09-26.
 */
public class CameraTestActivity extends BaseActivity implements CameraFragment.CameraActivityCallback {
    private CameraFragment mCameraFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraFragment = (CameraFragment) getSupportFragmentManager().findFragmentById(R.id.camera_fragment);
        mCameraFragment.setTakePictureButton((Button) findViewById(R.id.btn_take_pic));
    }

    @Override
    public void onSuccessTakenPictureNow() {
        BaLog.i();
    }

    @Override
    public void onSuccessTakenPictureAndSaveFile(CameraPictureFileBuilder.BuildImageResult buildImageResult) {
        BaLog.i("buildImageResult=" + buildImageResult);
        showToast(buildImageResult.getFilepath() + " 로 저장되었습니다.");
    }

    @Override
    public void onDisplayCameraPreview() {
        BaLog.i();
    }
}
