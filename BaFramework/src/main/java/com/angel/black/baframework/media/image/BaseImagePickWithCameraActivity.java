package com.angel.black.baframework.media.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angel.black.baframework.R;
import com.angel.black.baframework.intent.IntentConstants;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.camera.CameraPictureFileBuilder;
import com.angel.black.baframework.media.camera.fragment.CameraFragment;
import com.angel.black.baframework.media.image.fragment.GalleryFragment;
import com.angel.black.baframework.media.image.fragment.PreviewsFragment;
import com.angel.black.baframework.media.image.view.ImagePickWithCamTitleView;
import com.angel.black.baframework.media.image.view.PreviewView;
import com.angel.black.baframework.util.BaPackageManager;
import com.angel.black.baframework.util.BuildUtil;

import java.util.ArrayList;

/**
 * Created by KimJeongHun on 2016-09-25.
 */
public class BaseImagePickWithCameraActivity extends BaseImagePickActivity implements PreviewsFragment.PreviewActionListener,
        CameraFragment.CameraActivityCallback {
    protected int mMaxImageCount = 6;
    protected CameraFragment mCameraFragment;
    protected PreviewsFragment mPreviewsFragment;

    /** 가장 마지막에 선택된 갤러리 앨범 id */
    private long mCurrentGalleryAlbumId;
    private TextView mTextViewCameraTitle;
    private View mCamGalleryFmContainer;
    private ImageButton mBtnCamGallery;
    private ImageButton mBtnTakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreviewsFragment = (PreviewsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_previews);

        mCamGalleryFmContainer = findViewById(R.id.container_gallery_camera);
        mBtnCamGallery = (ImageButton) findViewById(R.id.btn_camera_gallery);
        mBtnTakePicture = (ImageButton) findViewById(R.id.btn_take_pic);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_image_pick_with_camera);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_base_image_pick_with_camera, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_gallery_camera);
        ImagePickWithCamTitleView titleView = (ImagePickWithCamTitleView) MenuItemCompat.getActionView(menuItem);
        mSpinnerAlbum = titleView.getSpinnerAlbum();
        mSpinnerAlbum.setVisibility(mMode == Mode.GALLERY ? View.VISIBLE : View.GONE);
        mTextViewCameraTitle = titleView.getTextViewCameraTitle();

        return true;
    }

    @Override
    protected void onModeChanged(Mode mode) {
        BaLog.i("mode=" + mode.ordinal());

        if (mode == Mode.CAMERA) {
            mBtnCamGallery.setImageResource(R.drawable.ic_photo_library_white_24dp);
            mBtnTakePicture.setVisibility(View.VISIBLE);
            if(mSpinnerAlbum != null)
                mSpinnerAlbum.setVisibility(View.INVISIBLE);

            if(mTextViewCameraTitle != null)
                mTextViewCameraTitle.setVisibility(View.VISIBLE);
        } else {
            mBtnCamGallery.setImageResource(R.drawable.ic_camera_alt_white_24dp);
            mBtnTakePicture.setVisibility(View.INVISIBLE);
            if(mSpinnerAlbum != null)
                mSpinnerAlbum.setVisibility(View.VISIBLE);

            if(mTextViewCameraTitle != null)
                mTextViewCameraTitle.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void initGalleryFragment() {
        showGallery();
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_camera_gallery) {

            if (mMode == Mode.GALLERY) {
                showCamera();
            } else {
                showGallery();
            }

        } else if(v.getId() == R.id.btn_take_pic) {
            mCameraFragment.takePicture();
        } else if(v.getId() == R.id.btn_edit) {
            if (mPreviewsFragment.isAnimating())
                return;

            if (mPreviewsFragment.getAttachedPreviewCount() <= 0) {
                showToast(R.string.msg_select_photo);
                return;
            }

            if (mPreviewsFragment.isLoadingPreviewImage()) {
                showToast(R.string.image_loading);
                return;
            }


            Intent intent = new Intent(this, BaseImageEditActivity.class);
            intent.putStringArrayListExtra(IntentConstants.KEY_IMAGE_PATH_LIST, mPreviewsFragment.getPreviewImagePathList());

//            if (mMaxImageCount == 1 || mOneImageChangeIndex >= 0) {
//                // 상품사진 한장 변경인 경우
//                intent.putExtra(BaseIntent.KEY_IMAGE_EDIT_HIDE_ORDERING, true);
//            }

            startActivityForResult(intent, IntentConstants.REQUEST_EDIT_IMAGE);
        }
    }

    private void showCamera() {
        mCurrentGalleryAlbumId = mGalleryFragment.getCurrentGalleryBucketId();
        mCameraFragment = CameraFragment.newInstance(mMaxImageCount - mCurRegisteredItemCount);

        replaceFragment(R.id.container_gallery_camera, mCameraFragment, mCameraFragment.TAG, true, true);
    }

    public void showGallery() {
        if (!isFinishing()) {
            final FragmentManager fm = getSupportFragmentManager();

            if(mGalleryFragment == null) {
                mGalleryFragment = GalleryFragment.newInstance(mCurrentGalleryAlbumId, mMaxImageCount - mCurRegisteredItemCount);
//                mGalleryFragment.setOneImageChange(mMaxImageCount == 1);
                addFragment(R.id.container_gallery_camera, mGalleryFragment, mGalleryFragment.TAG, false);
            } else {

                // 카메라 화면에서 갤러리로 돌아올 때 popBackStack() 시
                // Can not perform this action after onSaveInstanceState 익셉션 발생하는 경우 있어서
                // 아래와 같이 핸들러로 처리함

//                new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
                try {
                    fm.popBackStack();
                    mGalleryFragment.setGalleryBucketIdAndDisplayImages(mCurrentGalleryAlbumId);

                    // 프래그먼트 컨테이너를 프리뷰바 위로 변경
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCamGalleryFmContainer.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                    params.addRule(RelativeLayout.ABOVE, R.id.fragment_previews);
                    mCamGalleryFmContainer.setLayoutParams(params);

                    if (mCameraFragment != null && mCameraFragment.isVisible()) {
                        mCameraFragment.releaseCamera();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
//                            sendEmptyMessageDelayed(0, 500);
                }
//                    }
//                }.sendEmptyMessageDelayed(0, 500);
            }
        }
    }

    @Override
    public void onPickGalleryImage(GalleryBuilder.GalleryBucketItemInfo item) {
        super.onPickGalleryImage(item);
        BaLog.d("item=" + item);

        if(mMaxImageCount == 1) {
            mPreviewsFragment.changePreviewFromGallery(item);
        } else {
            mPreviewsFragment.addPreviewFromGallery(item);
        }
    }

    @Override
    public void onUnPickGalleryImage(GalleryBuilder.GalleryBucketItemInfo item) {
        super.onUnPickGalleryImage(item);
        BaLog.d("item=" + item);

        mPreviewsFragment.removePreview(PreviewsFragment.PreviewData.createPreviewData(item.path, null), false);
    }

    @Override
    public void onAddedPreview(PreviewView previewView) {
        // 롤리팝 이상 일때는 사진 찍자마자 바로 카메라 재개해도 성능문제 없지만
        // 킷캣 이하 일때는 사진 찍자마자 프리뷰도 추가되는 와중에 카메라가 재개되면 매우 버벅거림
        // 따라서 프리뷰 추가가 완료되면 카메라를 재개한다.
        BaLog.d("previewView=" + previewView);
        if(mMode == Mode.CAMERA && !BuildUtil.isGoogleReferencePhoneLollipop()) {
            mCameraFragment.resumeCameraPreview();
        }

        mCurRegisteredItemCount++;
    }

    @Override
    public void onRemovedPreview(int removeIndex, PreviewsFragment.PreviewData previewData, boolean isNeedGalleryDeselect) {
        BaLog.d("removeIndex=" + removeIndex + ", previewData=" + previewData);
        if(isNeedGalleryDeselect) {
            // 삭제한 프리뷰 아이템을 갤러리 그리드 항목에서도 선택해제 처리
            mGalleryFragment.setDeselected(GalleryBuilder.GalleryBucketItemInfo.createInstance(previewData));
        }

        if (mCameraFragment != null && mCameraFragment.isVisible()) {
            mCameraFragment.increaseCanTakeCount();
        }

        mCurRegisteredItemCount--;
    }

    @Override
    public void onClickedPreview(int position, PreviewView previewView) {
        if(mPreviewsFragment.isAnimating())
            return;

        mPreviewsFragment.goFullScreenImagePreviewActivity(previewView);
    }

    @Override
    public void onStartPreviewAnimation() {
        mGalleryFragment.setLockItemClick(true);
    }

    @Override
    public void onEndPreviewAnimation() {
        mGalleryFragment.setLockItemClick(false);
    }

    @Override
    public void onBackPressed() {
        // 프래그먼트 백스택이 쌓였을 때 백버튼 눌렀을 때 (갤러리 프로그먼트 -> 카메라 -> 백버튼 눌러서 돌아올 때)
        int stackCount = getSupportFragmentManager().getBackStackEntryCount();

        if(stackCount >= 1 && mMode == Mode.CAMERA) {
            showGallery();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstants.REQUEST_ZOOM_IMAGE) {
            if (resultCode == RESULT_CANCELED) {
                // 삭제된 이미지 패스 리스트 업데이트
                ArrayList<String> delImagePathList = data.getStringArrayListExtra(IntentConstants.KEY_IMAGE_PATH_LIST);
                BaLog.d("delImagePathList=" + delImagePathList);

                updateSelectedImage(delImagePathList);
            }
        } else if (requestCode == IntentConstants.REQUEST_EDIT_IMAGE) {
            if (resultCode == RESULT_OK) {
                // 편집 완료 후 돌아옴 - 편집완료 된 이미지 경로 리스트 전달 후 종료
                ArrayList<String> pathList = data.getStringArrayListExtra(IntentConstants.KEY_IMAGE_PATH_LIST);
                finishWithReturnData(pathList);

            } else if (resultCode == RESULT_CANCELED) {
                // 편집화면에서 이전 버튼 백버튼으로 돌아왔을 때 - 삭제된 이미지 패스 리스트 업데이트
                ArrayList<String> delImagePathList = data.getStringArrayListExtra(IntentConstants.KEY_IMAGE_PATH_LIST);
                BaLog.d("delImagePathList=" + delImagePathList);

                updateSelectedImage(delImagePathList);
            }
        }
    }


    /**
     * 삭제된 이미지들을 갤러리 화면, 프리뷰 화면에서 제거(업데이트) 한다.
     * @param delImagePathList
     */
    private void updateSelectedImage(ArrayList<String> delImagePathList) {
        mGalleryFragment.updateDeletedImages(delImagePathList);
        mPreviewsFragment.updateDeletedPreviews(delImagePathList);
    }

    @Override
    public void onSuccessTakenPictureNow() {
        BaLog.i();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPreviewsFragment.addPreviewFromCamera();
            }
        });
    }

    @Override
    public void onSuccessTakenPictureAndSaveFile(final CameraPictureFileBuilder.BuildImageResult buildImageResult) {
        BaLog.i();
        showToast(buildImageResult.getFilepath() + " 로 저장되었습니다.");

        final PreviewView previewView = mPreviewsFragment.getLastInsertedPreviewView();
        if(previewView == null) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                BaLog.d("thumbnail build thread start!!");
                Bitmap thumbBitmp = mPreviewsFragment.buildThumbnail(buildImageResult, previewView);
                BaLog.d("thumbnail build fihish!!");
            }
        }).start();

        mGalleryFragment.addSelectedListFromCamera(buildImageResult.getFilepath());

        // 프리마켓 앨범이 새로 생길 수 있으므로 새로 로드
        mGalleryFragment.loadGalleryAlbums();

        // 현재 프리마켓 앨범 선택하고 있었으면
        if(mGalleryFragment.getGalleryBucketName().equals(BaPackageManager.getPublicAppAlbumName(this))) {
            mGalleryFragment.refreshCurrentBucket();
        }

    }

    @Override
    public void onDisplayCameraPreview() {
        BaLog.i();
        setMode(BaseImagePickActivity.Mode.CAMERA);
    }
}
