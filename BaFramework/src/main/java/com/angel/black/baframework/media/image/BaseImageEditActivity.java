package com.angel.black.baframework.media.image;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.intent.IntentConstants;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.image.fragment.ImageOrderingFragment;
import com.angel.black.baframework.media.image.fragment.PreviewsFragment;
import com.angel.black.baframework.media.image.util.AUILUtil;
import com.angel.black.baframework.media.image.view.PreviewView;
import com.angel.black.baframework.ui.animation.AnimationUtil;
import com.angel.black.baframework.ui.util.ViewUtil;
import com.angel.black.baframework.ui.view.viewpager.InfinitePagerAdapter;
import com.angel.black.baframework.ui.view.viewpager.InfiniteViewPager;
import com.angel.black.baframework.util.BaPackageManager;
import com.angel.black.baframework.util.BitmapUtil;
import com.angel.black.baframework.util.BuildUtil;
import com.angel.black.baframework.util.UriUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class BaseImageEditActivity extends BaseActivity implements
        PreviewsFragment.PreviewActionListener, ViewPager.OnPageChangeListener,
        CropImageView.OnSaveCroppedImageCompleteListener {

    public static final int EDIT_MODE_ROTATE = 0;
    public static final int EDIT_MODE_CROP = 1;
    public static final int EDIT_MODE_FILTER = 2;
    public static final int EDIT_MODE_ORDERING = 3;

    private static final int MAX_IMAGE_SIZE_IF_HIGH_RESOLUTION_IMAGE = 1420;

    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mDipslayImageOptions;

    private int mEditMode;

    private ArrayList<String> mImagePathList;
    private ArrayList<ImageEditInfo> mImageEditInfoList;

    private ViewGroup mOneDepthBottomBar;
    private View mTwoDepthBottomBar;
    //    private View mBtnRotateArea;
//    private View mBtnCropArea;
//    private View mBtnOrderArea;
    private ImageButton mBtnRotate;
    private ImageButton mBtnCrop;
    private ImageButton mBtnOrder;

    private ImageButton mTwoDepthBottomBarBtnClose;
    private ImageButton mTwoDepthBottomBarBtnMain;
    private ImageButton mTwoDepthBottomBarBtnDone;

    private ViewGroup mSubBottomBar;
    private Button mSubBottomBarBtn1;
    private Button mSubBottomBarBtn2;
    private Button mSubBottomBarBtn3;
    private Button mSubBottomBarBtn4;

    private CropImageView mCropImageView;
    private PreviewsFragment mPreviewsFragment;
    private ImageOrderingFragment mImgOrderingFragment;
    private InfiniteViewPager mPager;
    private InfinitePagerAdapter mInfPagerAdapter;
    private View mEmptyViewForNotPagerScroll;

    /**
     * 삭제된 이미지 패쓰를 저장할 리스트
     */
    private ArrayList<String> mDelImagePathList = new ArrayList<>();

    /**
     * 현재 선택된 페이지 (가상의 증가된 숫자)
     */
    private int mCurSelectedPage;

    /**
     * 현재 선택된 진짜 페이지 (0~5 사이)
     */
    private int mCurSelectedRealPage;

    /**
     * 현재 작업대상 비트맵
     */
    private Bitmap mCurPageBitmap;

    /**
     * 최초에 선택될 이미지 위치
     */
    private int mInitialImageNo;

    /**
     * 하단 버튼 애니메이션 중인지 여부
     */
    private boolean mIsAnimatingBtn;

    /**
     * 순서 버튼 숨길지 여부
     */
    private boolean mHideOrderBtn;

    /**
     * 회전 애니메이션 락
     */
    private boolean mLockRotateAnim;
    private MenuItem mMenuItemComplete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        mImageLoader = ImageLoader.getInstance();

        mDipslayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(getResources().getDrawable(R.color.light_gray))
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        mImagePathList = getIntent().getStringArrayListExtra(IntentConstants.KEY_IMAGE_PATH_LIST);
        mInitialImageNo = getIntent().getIntExtra(IntentConstants.KEY_INITIAL_IMAGE_INDEX, 0);
        mHideOrderBtn = getIntent().getBooleanExtra(IntentConstants.KEY_IMAGE_EDIT_HIDE_ORDERING, false);

        mImageEditInfoList = createImageEditInfo(mImagePathList);

        initToolbarWithOnBackPressed();
        setTitle("1 / " + mImagePathList.size());

        mCropImageView = (CropImageView) findViewById(R.id.crop_image_view);
        mCropImageView.setOnSaveCroppedImageCompleteListener(this);

        mPreviewsFragment = (PreviewsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_previews);
        mPreviewsFragment.showPreviewsBar(true);
        mPreviewsFragment.addPreviews(mImagePathList);

        mPager = (InfiniteViewPager) findViewById(R.id.viewpager);
        mPager.addOnPageChangeListener(this);
        mInfPagerAdapter = new InfinitePagerAdapter(new ImagePagerAdapter(this, mImageEditInfoList));
        mPager.setAdapter(mInfPagerAdapter);
        mPager.setCurrentItem(mImagePathList.size() * 100 + mInitialImageNo);        // 무한반복을 위해 * 100

        mEmptyViewForNotPagerScroll = findViewById(R.id.empty_view_for_not_pager_scroll);

        mPreviewsFragment.setSelected(true, mInitialImageNo);

        mSubBottomBar = (ViewGroup) findViewById(R.id.layout_sub_bottombar);
        mSubBottomBarBtn1 = (Button) mSubBottomBar.findViewById(R.id.btn1);
        mSubBottomBarBtn2 = (Button) mSubBottomBar.findViewById(R.id.btn2);
        mSubBottomBarBtn3 = (Button) mSubBottomBar.findViewById(R.id.btn3);
        mSubBottomBarBtn4 = (Button) mSubBottomBar.findViewById(R.id.btn4);

        mOneDepthBottomBar = (ViewGroup) findViewById(R.id.one_depth_bottombar);
//        mBtnRotateArea = mOneDepthBottomBar.findViewById(R.id.btn_rotate_area);
//        mBtnCropArea = mOneDepthBottomBar.findViewById(R.id.btn_crop_area);
//        mBtnOrderArea = mOneDepthBottomBar.findViewById(R.id.btn_order_area);
        mBtnRotate = (ImageButton) mOneDepthBottomBar.findViewById(R.id.btn_rotate);
        mBtnCrop = (ImageButton) mOneDepthBottomBar.findViewById(R.id.btn_crop);
        mBtnOrder = (ImageButton) mOneDepthBottomBar.findViewById(R.id.btn_order);

        mTwoDepthBottomBar = findViewById(R.id.two_depth_bottombar);
        mTwoDepthBottomBarBtnClose = (ImageButton) mTwoDepthBottomBar.findViewById(R.id.btn_close);
        mTwoDepthBottomBarBtnMain = (ImageButton) mTwoDepthBottomBar.findViewById(R.id.btn_center_main);
        mTwoDepthBottomBarBtnDone = (ImageButton) mTwoDepthBottomBar.findViewById(R.id.btn_done);

        if (mHideOrderBtn) {
            mOneDepthBottomBar.removeView(mBtnOrder);
            mOneDepthBottomBar.addView(mBtnOrder, 1);
            mBtnOrder.setVisibility(View.INVISIBLE);
            mOneDepthBottomBar.invalidate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_base_image_edit, menu);
        mMenuItemComplete = menu.findItem(R.id.menu_complete);

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.menu_complete) {
            // 완료 버튼
            if (mPreviewsFragment.isLoadingPreviewImage()) {
                showToast(R.string.image_loading);
                return true;
            }

            Intent returnData = new Intent();
            returnData.putStringArrayListExtra(IntentConstants.KEY_IMAGE_PATH_LIST, getEditedTempImagePathList());
            setResult(RESULT_OK, returnData);
            finish();

            return true;
        }

        return false;
    }

    private ArrayList<ImageEditInfo> createImageEditInfo(ArrayList<String> imagePathList) {
        for(String imagePath : imagePathList) {
            BaLog.e("imagePath=" + imagePath);
        }
        ArrayList<ImageEditInfo> imageEditInfoList = new ArrayList<>();

        int size = imagePathList.size();
        for (int i = 0; i < size; i++) {
            String path = imagePathList.get(i);
            ImageEditInfo imageEditInfo = new ImageEditInfo(i, path);
            imageEditInfoList.add(imageEditInfo);
        }

        return imageEditInfoList;
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_rotate || id == R.id.btn_crop || id == R.id.btn_order) {
            changeTwoDepthBottomBar((ImageButton) v);
            buildSubBottomBar(v.getId());


        } else if (id == R.id.btn_close) {
            startBottomMainBtnMoveOriginalAnimation();

            if (mEditMode == EDIT_MODE_ORDERING) {
                closeOrderingBar();
            } else if (mEditMode == EDIT_MODE_CROP) {
                mCropImageView.clearImage();
                mCropImageView.setVisibility(View.GONE);
            } else if (mEditMode == EDIT_MODE_ROTATE) {
                if (getCurrentImageEditInfo().isChanged()) {
                    // 변경사항 저장하지 않고 닫음 - 뷰 갱신
                    mInfPagerAdapter.notifyDataSetChanged();
                }
            }


        } else if (id == R.id.btn_done) {
            if (mEditMode == EDIT_MODE_ROTATE) {
                final ImageEditInfo editInfo = getCurrentImageEditInfo();

                if (editInfo.isChanged()) {
                    final float rotateDegree = editInfo.rotateDegree - editInfo.originRotateDegree;
                    ImageView editImgView = getCurrentEditImageView();

                    int width = editImgView.getDrawable().getIntrinsicWidth();
                    int height = editImgView.getDrawable().getIntrinsicHeight();
                    BaLog.d("원본이미지 크기 = " + width + " x " + height);

                    startBuildBitmap(width, height, getCurrentBuildImagePath(),
                            new BitmapBuilder.BitmapBuildListener() {
                                @Override
                                public void onBuildBitmap(Bitmap bitmap) {
                                    if (bitmap == null) {
                                        showToast(R.string.error_build_bitmap);
                                        return;
                                    }
                                    // 실제 이미지 회전 및 반전 적용
                                    Bitmap edittedBitmap = BitmapUtil.rotateAndInverseBitmap(bitmap, rotateDegree,
                                            editInfo.leftRightFlipCount, editInfo.upDownFlipCount);

                                    mCurPageBitmap = edittedBitmap;

                                    // 썸네일 회전 및 반전 적용
                                    Bitmap thumbBitmap = BitmapUtil.rotateAndInverseBitmap(mPreviewsFragment.getPreviewImageBitmap(getCurrentImagePosition()),
                                            rotateDegree, editInfo.leftRightFlipCount, editInfo.upDownFlipCount);

                                    int curImageIndex = getCurrentImagePosition();
                                    mPreviewsFragment.setPreviewImageBitmap(curImageIndex, thumbBitmap);

                                    // 회전 및 반전 적용된 비트맵 임시 파일로 저장
                                    String destPath = editInfo.getOrCreateTempImagePath();
                                    saveEditedBitmapToFile(mCurPageBitmap, destPath);
                                }
                            });

                } else {
                    startBottomMainBtnMoveOriginalAnimation();
                }
            } else if (mEditMode == EDIT_MODE_CROP) {
                if (mCropImageView.getVisibility() == View.VISIBLE) {
                    // 크롭 뷰가 활성화 되어있을 때만 크롭 진행
                    ImageEditInfo editInfo = getCurrentImageEditInfo();

                    mCropImageView.startCropWorkerTask(editInfo.cropWidth, editInfo.cropHeight,
                            Uri.parse(UriUtil.convertFilePathToUri(editInfo.getOrCreateTempImagePath())),
                            Bitmap.CompressFormat.JPEG, 90);
                } else {
                    startBottomMainBtnMoveOriginalAnimation();
                }
            } else if (mEditMode == EDIT_MODE_ORDERING) {
                // 순서변경 완료
                if (mImgOrderingFragment.isSelectedAll()) {
                    if (mImgOrderingFragment.isSequenceChanged()) {
                        HashMap<String, Integer> newSeqMap = mImgOrderingFragment.getSequenceMap();
                        arrangeItemSequence(newSeqMap);
                    }

                    closeOrderingBar();
                    startBottomMainBtnMoveOriginalAnimation();
                } else {
                    showToast(R.string.msg_select_seq_all);
                }
            }
        }
    }

    private ArrayList<String> getEditedTempImagePathList() {
        ArrayList<String> tempImagePathList = new ArrayList<>();

        for (ImageEditInfo editInfo : mImageEditInfoList) {
            tempImagePathList.add(editInfo.tempImagePath != null ? editInfo.tempImagePath : editInfo.originImagePath);
        }

        return tempImagePathList;
    }

    private ArrayList<String> getOriginImagePathList() {
        ArrayList<String> originImagePathList = new ArrayList<>();

        for (ImageEditInfo editInfo : mImageEditInfoList) {
            originImagePathList.add(editInfo.originImagePath);
        }

        return originImagePathList;
    }

    private void closeOrderingBar() {
        getSupportFragmentManager().popBackStack();
        setTitleForPager();
    }

    private void setTitleForPager() {
        int realCount = mPager.getAdapter().getCount();
        setTitle((mCurSelectedRealPage + 1) + " / " + realCount);
    }

    /**
     * 아이템 순서 변경 적용
     *
     * @param newSeqMap
     */
    private void arrangeItemSequence(HashMap<String, Integer> newSeqMap) {
        Set<String> seqKeys = newSeqMap.keySet();
        Iterator<String> seqKeyIter = seqKeys.iterator();

        // 삭제전, 뒤 아이템들 순서 하나씩 앞으로 땡기기
        while (seqKeyIter.hasNext()) {
            String path = seqKeyIter.next();

            // 새 순서
            int newSeq = newSeqMap.get(path);
            // 기존 순서
            int prevSeq = mImagePathList.indexOf(path);

            ImageEditInfo prevImageEditInfo = mImageEditInfoList.get(prevSeq);
            prevImageEditInfo.imagePosition = newSeq;
        }

        Collections.sort(mImageEditInfoList, new Comparator<ImageEditInfo>() {
            @Override
            public int compare(ImageEditInfo lhs, ImageEditInfo rhs) {
                if (lhs.imagePosition > rhs.imagePosition) {
                    return 1;
                } else if (lhs.imagePosition < rhs.imagePosition) {
                    return -1;
                }
                return 0;
            }
        });

        mImagePathList.clear();
        mImagePathList.addAll(getOriginImagePathList());

        mPreviewsFragment.replacePreviews(mImagePathList);
        ((ImagePagerAdapter) mPager.getAdapter()).setImageEditInfoList(mImageEditInfoList);
        mInfPagerAdapter.notifyDataSetChanged();
    }

    private void saveEditedBitmapToFile(Bitmap bitmap, String destPath) {
        new ImageFileBuilder(this, destPath, new ImageFileBuildListener() {

            @Override
            public void onSuccessImageFileBuild(String filepath) {
                // 최종 임시 파일 저장
                // 여기서 originPath 도 임시파일로 셋팅해주어야 함
                ImageEditInfo editInfo = getCurrentImageEditInfo();
                String prevOriginImgPath = editInfo.originImagePath;

                refreshImagePathList(prevOriginImgPath, filepath);

                editInfo.tempImagePath = filepath;
                editInfo.originImagePath = filepath;

                final ImageView editImgView = getCurrentEditImageView();
                final ImageView realImgView = getCurrentRealImageView();

                String newImgUri = UriUtil.convertFilePathToUri(filepath);
                deleteImageCache(newImgUri);

                BaLog.d("saveEditedFile >> editImageView=" + editImgView);
                BaLog.d("saveEditedFile >> realImgView=" + realImgView);

                // instantiateItem 호출되면서 편집정보 초기화한다.
                mInfPagerAdapter.notifyDataSetChanged();

                changeOneDepthBottomBar();

                if(mCurPageBitmap != null) {
                    mCurPageBitmap.recycle();
                    mCurPageBitmap = null;
                }
            }

            @Override
            public void onFailImageFileBuild(String filepath, String errMsg) {
                BaLog.e("errMsg=" + errMsg);
            }
        }).execute(bitmap);
    }

    private void deleteImageCache(String imageUri) {
        AUILUtil.deleteImageCache(imageUri);
    }

    /**
     * 이미지 패스 리스트도 편집 완료된 새 파일 경로로 갱신
     *
     * @param oldPath
     * @param newPath
     */
    private void refreshImagePathList(String oldPath, String newPath) {
        mImagePathList.set(mImagePathList.indexOf(oldPath), newPath);
    }

    private void changeTwoDepthBottomBar(ImageButton clickBottomBtnArea) {
        int id = clickBottomBtnArea.getId();
        if (id == R.id.btn_rotate) {
            startBottomBtnMoveCenterAnimation(clickBottomBtnArea);

        } else if (id == R.id.btn_crop) {
            startBottomBtnMoveCenterAnimation(clickBottomBtnArea);

        } else if (id == R.id.btn_order) {
            startBottomBtnMoveCenterAnimation(clickBottomBtnArea);

        }
//        ((View) mBtnFinish.getParent()).setVisibility(View.INVISIBLE);
        mMenuItemComplete.setVisible(false);
    }

    private void changeOneDepthBottomBar() {
        mOneDepthBottomBar.setVisibility(View.VISIBLE);
        mTwoDepthBottomBarBtnMain.setBackgroundResource(android.R.color.transparent);
        mTwoDepthBottomBarBtnMain.setVisibility(View.INVISIBLE);
        mTwoDepthBottomBarBtnClose.setVisibility(View.INVISIBLE);
        mTwoDepthBottomBarBtnDone.setVisibility(View.INVISIBLE);
        mTwoDepthBottomBar.setVisibility(View.INVISIBLE);

        showSubBottomBar(false);
        showOneDepthBottomBar();
//        ((View) mBtnFinish.getParent()).setVisibility(View.VISIBLE);
        mMenuItemComplete.setVisible(true);
    }

    private void showOneDepthBottomBar() {
        ViewUtil.setVisibilityHierancy(mOneDepthBottomBar, View.VISIBLE);
        mBtnOrder.setVisibility(mHideOrderBtn ? View.INVISIBLE : View.VISIBLE);   // 순서버튼 숨겨야 하는 경우 다시 숨김
        mEmptyViewForNotPagerScroll.setVisibility(View.GONE);

        setTitleForPager();
    }

    private View getTargetBackOneDepthButtonArea() {
        final View targetBtnArea;

        if (mEditMode == EDIT_MODE_ROTATE) {
            targetBtnArea = mBtnRotate;
        } else if (mEditMode == EDIT_MODE_CROP) {
            targetBtnArea = mBtnCrop;
        } else {
            targetBtnArea = mBtnOrder;
        }

        return targetBtnArea;
    }

    /**
     * 투 뎁쓰 가운데 버튼(현재 작업을 나타내는 버튼)이
     * 원 뎁쓰 버튼의 원래 자리로 돌아가는 애니메이션
     */
    private void startBottomMainBtnMoveOriginalAnimation() {
        BaLog.d();
        if (mIsAnimatingBtn)
            return;

        final View targetBtnArea = getTargetBackOneDepthButtonArea();
        int offset = !mHideOrderBtn && targetBtnArea == mBtnCrop ? 0 :
                (targetBtnArea == mBtnRotate ? -targetBtnArea.getWidth() : targetBtnArea.getWidth());

        TranslateAnimation movingAnim = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, offset,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);

        movingAnim.setDuration(500);
        movingAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimatingBtn = true;
                mOneDepthBottomBar.setVisibility(View.VISIBLE);
                targetBtnArea.setVisibility(View.INVISIBLE);
//                ((View) mBtnFinish.getParent()).setVisibility(View.VISIBLE);
                mMenuItemComplete.setVisible(true);
                startBottomOutsideBtnMoveOriginalAnimation(targetBtnArea);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimatingBtn = false;
                mTwoDepthBottomBarBtnMain.setVisibility(View.INVISIBLE);
                mTwoDepthBottomBarBtnClose.setVisibility(View.INVISIBLE);
                mTwoDepthBottomBarBtnDone.setVisibility(View.INVISIBLE);
                mTwoDepthBottomBar.setVisibility(View.INVISIBLE);
                showOneDepthBottomBar();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation slideLeftOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        Animation slideRightOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
        slideLeftOutAnim.setDuration(500);
        slideRightOutAnim.setDuration(500);

        mTwoDepthBottomBarBtnMain.startAnimation(movingAnim);
        mTwoDepthBottomBarBtnClose.startAnimation(slideLeftOutAnim);
        mTwoDepthBottomBarBtnDone.startAnimation(slideRightOutAnim);
    }

    /**
     * 투 뎁쓰 가운데 버튼(현재 작업을 나타내는 버튼) 외의
     * 숨어있는 나머지 두 버튼(원 뎁쓰 버튼)의 원래 자리로 돌아가는 애니메이션
     *
     * @param targetBtnArea
     */
    private void startBottomOutsideBtnMoveOriginalAnimation(View targetBtnArea) {
        BaLog.d();

        View btn1Area, btn2Area = null;
        TranslateAnimation movingAnim1, movingAnim2 = null;

        if (targetBtnArea == mBtnRotate) {
            btn1Area = mBtnCrop;


            movingAnim1 = new TranslateAnimation(
                    Animation.ABSOLUTE, btn1Area.getWidth() * 2,
                    Animation.ABSOLUTE, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0);

            if (!mHideOrderBtn) {
                btn2Area = mBtnOrder;

                movingAnim2 = new TranslateAnimation(
                        Animation.ABSOLUTE, btn1Area.getWidth(),
                        Animation.ABSOLUTE, 0,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0);
            }
        } else if (targetBtnArea == mBtnCrop) {
            btn1Area = mBtnRotate;


            movingAnim1 = new TranslateAnimation(
                    Animation.ABSOLUTE, -btn1Area.getWidth(),
                    Animation.ABSOLUTE, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0);

            if (!mHideOrderBtn) {
                btn2Area = mBtnOrder;
                movingAnim2 = new TranslateAnimation(
                        Animation.ABSOLUTE, btn2Area.getWidth(),
                        Animation.ABSOLUTE, 0,
                        Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0);
            }
        } else {
            btn1Area = mBtnRotate;
            btn2Area = mBtnCrop;

            movingAnim1 = new TranslateAnimation(
                    Animation.ABSOLUTE, -btn1Area.getWidth(),
                    Animation.ABSOLUTE, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0);

            movingAnim2 = new TranslateAnimation(
                    Animation.ABSOLUTE, -btn2Area.getWidth() * 2,
                    Animation.ABSOLUTE, 0,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0);
        }

        movingAnim1.setFillBefore(true);
        movingAnim1.setDuration(500);
        movingAnim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimatingBtn = true;
                showSubBottomBar(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimatingBtn = false;
                showOneDepthBottomBar();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (movingAnim2 != null) {
            movingAnim2.setFillBefore(true);
            movingAnim2.setDuration(500);
        }

        if (btn1Area != null)
            btn1Area.startAnimation(movingAnim1);
        if (btn2Area != null)
            btn2Area.startAnimation(movingAnim2);
    }

    /**
     * 작업대상 원뎁쓰버튼을 투뎁쓰버튼 가운데에 나타내는 애니메이션
     *
     * @param clickBottomBtnArea
     */
    private void startBottomBtnMoveCenterAnimation(final ImageButton clickBottomBtnArea) {
        BaLog.d();
        if (mIsAnimatingBtn)
            return;

        TranslateAnimation anim = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, mTwoDepthBottomBarBtnMain.getLeft() - clickBottomBtnArea.getLeft(),
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);

        anim.setDuration(500);
        anim.setFillBefore(false);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimatingBtn = true;
                mBtnRotate.setVisibility(clickBottomBtnArea != mBtnRotate ? View.INVISIBLE : View.VISIBLE);
                mBtnCrop.setVisibility(clickBottomBtnArea != mBtnCrop ? View.INVISIBLE : View.VISIBLE);
                if (!mHideOrderBtn) {
                    mBtnOrder.setVisibility(clickBottomBtnArea != mBtnOrder ? View.INVISIBLE : View.VISIBLE);
                }
                mTwoDepthBottomBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimatingBtn = false;
                mTwoDepthBottomBarBtnMain.setVisibility(View.VISIBLE);

                ImageButton childBtn = clickBottomBtnArea;
                mTwoDepthBottomBarBtnMain.setImageDrawable(childBtn.getDrawable().mutate());
//                mTwoDepthBottomBarBtnMain.setText(((Button) childBtn).getText());
                mTwoDepthBottomBarBtnMain.setVisibility(View.VISIBLE);
                mTwoDepthBottomBarBtnClose.setVisibility(View.VISIBLE);
                mTwoDepthBottomBarBtnDone.setVisibility(View.VISIBLE);

                mOneDepthBottomBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation slideLeftInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        Animation slideRightInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideLeftInAnim.setDuration(500);
        slideRightInAnim.setDuration(500);

        mTwoDepthBottomBarBtnClose.startAnimation(slideLeftInAnim);
        mTwoDepthBottomBarBtnDone.startAnimation(slideRightInAnim);

        clickBottomBtnArea.startAnimation(anim);
    }

    private void buildSubBottomBar(int viewId) {
        if (viewId == R.id.btn_rotate) {
            buildSubBottomBarRotate();
            showSubBottomBar(true);
            mEditMode = EDIT_MODE_ROTATE;

            // 타이틀 - rotate
            setTitle(R.string.rotate);

        } else if (viewId == R.id.btn_crop) {
            buildSubBottomBarCrop();
            showSubBottomBar(true);
            mEditMode = EDIT_MODE_CROP;

            // 타이틀 - 순서변경
            setTitle(R.string.crop);

        } else if (viewId == R.id.btn_order) {
            showSubBottomBar(false);
            mImgOrderingFragment = ImageOrderingFragment.newInstance(mImagePathList);
            addFragment(R.id.container_ordering_fragment, mImgOrderingFragment, ImageOrderingFragment.TAG, true);
            mEditMode = EDIT_MODE_ORDERING;

            // 타이틀 - 순서변경
            setTitle(R.string.order_change);


        }
    }

    private void showSubBottomBar(boolean show) {
        if (show) {
            mSubBottomBar.setVisibility(View.VISIBLE);
            mSubBottomBar.startLayoutAnimation();
        } else {
            if (mSubBottomBar.getVisibility() == View.VISIBLE) {
                Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
                anim.setDuration(300);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mSubBottomBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mSubBottomBar.startAnimation(anim);
            }
        }
    }

    private void buildSubBottomBarRotate() {
        mSubBottomBarBtn1.setText("");
        mSubBottomBarBtn2.setText("");
        mSubBottomBarBtn3.setText("");
        mSubBottomBarBtn4.setText("");
        mSubBottomBarBtn1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_edit_right_rotate, 0, 0);
        mSubBottomBarBtn2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_edit_left_rotate, 0, 0);
        mSubBottomBarBtn3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_edit_horizontal_mirror, 0, 0);
        mSubBottomBarBtn4.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_edit_vertical_mirror, 0, 0);
        mSubBottomBarBtn1.setOnClickListener(mRotateBtnClick);
        mSubBottomBarBtn2.setOnClickListener(mRotateBtnClick);
        mSubBottomBarBtn3.setOnClickListener(mRotateBtnClick);
        mSubBottomBarBtn4.setOnClickListener(mRotateBtnClick);

        mEmptyViewForNotPagerScroll.setVisibility(View.VISIBLE);
    }

    private void buildSubBottomBarCrop() {
        mSubBottomBarBtn1.setText(R.string.original);
        mSubBottomBarBtn2.setText("1:1");
        mSubBottomBarBtn3.setText("3:4");
        mSubBottomBarBtn4.setText("4:3");
        mSubBottomBarBtn1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_edit_crop_org, 0, 0);
        mSubBottomBarBtn2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_edit_crop_11, 0, 0);
        mSubBottomBarBtn3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_edit_crop_34, 0, 0);
        mSubBottomBarBtn4.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_edit_crop_43, 0, 0);
        mSubBottomBarBtn1.setOnClickListener(mCropBtnClick);
        mSubBottomBarBtn2.setOnClickListener(mCropBtnClick);
        mSubBottomBarBtn3.setOnClickListener(mCropBtnClick);
        mSubBottomBarBtn4.setOnClickListener(mCropBtnClick);

        mEmptyViewForNotPagerScroll.setVisibility(View.VISIBLE);
    }

    /**
     * 현재 작업 대상 이미지 위치 반환
     *
     * @return
     */
    private int getCurrentImagePosition() {
        return mPager.getCurrentItemReal();
    }

    /**
     * 현재 작업 대상 이미지 뷰 반환
     *
     * @return
     */
    private ImageView getCurrentEditImageView() {
        return (ImageView) mPager.findViewWithTag(mImageEditInfoList.get(getCurrentImagePosition()));
    }

    /**
     * 현재 작업이 완료된 리얼 이미지 뷰 반환
     *
     * @return
     */
    private ImageView getCurrentRealImageView() {
        return (ImageView) mPager.findViewWithTag("realImg" + getCurrentImageEditInfo().imagePosition);
    }

    private ImageEditInfo getCurrentImageEditInfo() {
        BaLog.d("currentImageEditInfo=" + mImageEditInfoList.get(getCurrentImagePosition()));
        return mImageEditInfoList.get(getCurrentImagePosition());
    }

    private void startImageRotateAnimation(int direction) {
        if (mLockRotateAnim) {
            return;
        }

        final ImageEditInfo editInfo = getCurrentImageEditInfo();
        final float fromDegree = editInfo.prevRotateDegree;

        float offsetDegree = (direction == View.FOCUS_LEFT) ?
                ((editInfo.leftRightFlipCount + editInfo.upDownFlipCount) % 2 == 0 ? 90 : -90) :
                ((editInfo.leftRightFlipCount + editInfo.upDownFlipCount) % 2 == 0 ? -90 : 90);

        float toDegree = fromDegree + offsetDegree;

        final ImageView editImgView = getCurrentEditImageView();
        final ImageView realImgView = getCurrentRealImageView();

        AnimationUtil.startRotateAnimation(editImgView, fromDegree, toDegree, new AnimationUtil.ViewRotateListener() {

            @Override
            public void onStartRotateImage(View imgView) {
                mLockRotateAnim = true;
                editImgView.setVisibility(View.VISIBLE);
                realImgView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onEndRotateImage(View imgView, int toDegree) {
                mLockRotateAnim = false;
                editInfo.rotateDegree = toDegree;
                editInfo.prevRotateDegree = editInfo.rotateDegree;
            }
        });
    }


    /**
     * 반전 애니메이션을 시작한다.
     *
     * @param inverseDirection 1 : 좌우반전, 2 : 상하반전
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void startImageFlipAnimation(final int inverseDirection) {
        if (!(inverseDirection == 1 || inverseDirection == 2))
            throw new IllegalArgumentException("direction must be 1 or 2");

        if (mLockRotateAnim) {
            return;
        }

        final ImageView editImgView = getCurrentEditImageView();
        final ImageView realImgView = getCurrentRealImageView();
        final ImageEditInfo editInfo = getCurrentImageEditInfo();

        if (inverseDirection == 1) {
            AnimationUtil.startFlipXAnimation(editImgView, editInfo.leftRightFlipCount % 2 != 0, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mLockRotateAnim = true;
                    editImgView.setVisibility(View.VISIBLE);
                    realImgView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    editInfo.leftRightFlipCount++;
                    mLockRotateAnim = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        } else {
            AnimationUtil.startFlipYAnimation(editImgView, editInfo.upDownFlipCount % 2 != 0, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mLockRotateAnim = true;
                    editImgView.setVisibility(View.VISIBLE);
                    realImgView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    editInfo.upDownFlipCount++;
                    mLockRotateAnim = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
    }

    private View.OnClickListener mRotateBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn1) {// 왼쪽 회전:
                startImageRotateAnimation(View.FOCUS_LEFT);

            } else if (id == R.id.btn2) {
                startImageRotateAnimation(View.FOCUS_RIGHT);

            } else if (id == R.id.btn3) {
                if (BuildUtil.isAboveIcecreamSandwich()) {
                    startImageFlipAnimation(1);
                } else {
                    //TODO 저사양폰 플립 애니 - 그냥 미지원 표시
                    showToast(R.string.not_support_feature_on_device);
                }


            } else if (id == R.id.btn4) {
                if (BuildUtil.isAboveIcecreamSandwich()) {
                    startImageFlipAnimation(2);
                } else {
                    showToast(R.string.not_support_feature_on_device);
                }

            }
        }
    };


    private View.OnClickListener mCropBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int[] size = getBitmapSize(getCurrentImageEditInfo().originImagePath);

            int id = v.getId();
            if (id == R.id.btn1) {
                goCrop(size[0], size[1], size[0], size[1]);

            } else if (id == R.id.btn2) {
                goCrop(size[0], size[1], 1, 1);

            } else if (id == R.id.btn3) {
                goCrop(size[0], size[1], 3, 4);

            } else if (id == R.id.btn4) {
                goCrop(size[0], size[1], 4, 3);

            }
        }
    };

    private void goCrop(int bmWidth, int bmHeight, int widthRatio, int heightRatio) {
        ImageEditInfo editInfo = getCurrentImageEditInfo();

        int outputX = bmWidth;
        int outputY = bmHeight;

        if(bmWidth > MAX_IMAGE_SIZE_IF_HIGH_RESOLUTION_IMAGE) {
            outputX = MAX_IMAGE_SIZE_IF_HIGH_RESOLUTION_IMAGE;

            float ratio = heightRatio / (float) widthRatio;
            outputY = (int) (outputX * ratio);
        }

        editInfo.cropWidth = outputX;
        editInfo.cropHeight = outputY;

        mCropImageView.setVisibility(View.VISIBLE);
        mCropImageView.setAspectRatio(widthRatio, heightRatio);
        mCropImageView.setImageUriAsync(Uri.parse(UriUtil.convertFilePathToUri(editInfo.originImagePath)));
        mCropImageView.setRotatedDegrees(BitmapUtil.getPhotoOrientation(editInfo.originImagePath));
    }

    @Override
    public void onAddedPreview(PreviewView previewView) {

    }

    @Override
    public void onRemovedPreview(int removeIndex, PreviewsFragment.PreviewData previewData, boolean isNeedGalleryDeselect) {
        BaLog.d("mCurSelectedPage=" + mCurSelectedPage + ", removeIndex=" + removeIndex);
        mDelImagePathList.add(previewData.imagePath);
        mImagePathList.remove(removeIndex);

        if(mImagePathList.size() == 0) {
            onBackPressed();
            return;
        }

        // 삭제 전 이미지 에딧 정보의 이미지 인덱스를 조정한다.
        for(int i = removeIndex + 1; i < mImageEditInfoList.size(); i++) {
            ImageEditInfo editInfo = mImageEditInfoList.get(i);
            editInfo.imagePosition--;
        }
        mImageEditInfoList.remove(removeIndex);

        int curPageRealIdx = mCurSelectedRealPage;
        BaLog.d("curPageRealIdx=" + curPageRealIdx + ", removeIndex=" + removeIndex);

        int willSelectPage;     // 새롭게 선택할 페이지

        if(removeIndex == curPageRealIdx) {
            // 현재 페이지에서 현재 아이템 삭제했을 때
            if (removeIndex == 0) {
                // 처음에서 지웠을 때
                willSelectPage = mCurSelectedPage;
            } else if (removeIndex == mPager.getAdapter().getCount() - 1) {
                // 마지막에서 지웠을 때
                willSelectPage = mCurSelectedPage - 1;
            } else {
                // 그 외
                willSelectPage = mCurSelectedPage;
            }

        } else if (removeIndex < curPageRealIdx) {
            // 현재 페이지가 아닌 다른 페이지 아이템 삭제했을 때
            willSelectPage = mCurSelectedPage - 1;
        } else {
            willSelectPage = mCurSelectedPage;
        }

        int realCount = mPager.getAdapter().getCount();
        int willSelectRealPage = willSelectPage % realCount;

        ((ImagePagerAdapter) mPager.getAdapter()).setImageEditInfoList(mImageEditInfoList);
        mInfPagerAdapter.notifyDataSetChanged();

        willSelectPage = getWillSelectPage(willSelectRealPage, willSelectPage);
        mPager.setCurrentItem(willSelectPage);

        if(willSelectPage == mCurSelectedPage) {    // 새 포지션이 원래 포지션과 같을 경우 onPageSelected 호출되지 않으므로 수동 호출함
            onPageSelected(willSelectPage);
        }
    }

    private int getWillSelectPage(int willSelectRealPage, int willSelectPage) {
        int realCount = mPager.getAdapter().getCount();
        int curSelectRealPage = willSelectPage % realCount;

        int diff = willSelectRealPage - curSelectRealPage;

        while(willSelectRealPage != curSelectRealPage) {
            if(diff > 0) {
                curSelectRealPage++;
                willSelectPage++;
            } else {
                curSelectRealPage--;
                willSelectPage--;
            }
        }

        BaLog.d("willSelectPage=" + willSelectPage);

        return willSelectPage;
    }

    @Override
    public void onClickedPreview(int position, PreviewView previewView) {
        BaLog.d("position=" + position);
        int pageOffset = mPager.getCurrentItemReal() - position;
        mPager.setCurrentItem(mCurSelectedPage - pageOffset, true);
    }

    @Override
    public void onStartPreviewAnimation() {

    }

    @Override
    public void onEndPreviewAnimation() {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        BaLog.d("prev position=" + mCurSelectedPage + ", new position=" + position);

        int realCount = mPager.getAdapter().getCount();
        int realPosition = position % realCount;

        mPreviewsFragment.setSelected(false, mCurSelectedPage % realCount);
        mPreviewsFragment.setSelected(true, realPosition);

        mCurSelectedPage = position;
        mCurSelectedRealPage = realPosition;

        setTitle((realPosition+1) + " / " + realCount);

        BaLog.d("mCurSelectedPage=" + mCurSelectedPage);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        BaLog.d("state=" + state);
        if(state == ViewPager.SCROLL_STATE_IDLE) {

        }
    }

    private void startBuildBitmap(int destWidth, int destHeight, String path, final BitmapBuilder.BitmapBuildListener listener) {
        new BitmapBuilder(this, true,
                destWidth,
                destHeight,
                listener)
                .execute(path);
    }

    private int[] getBitmapSize(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        return new int[] {options.outWidth, options.outHeight};
    }

    /**
     * 현재 페이지의 작업대상 이미지 패스명을 반환한다.
     * 임시저장 패쓰가 있을 경우(편집 적용된 것) 그것을 반환
     * 임시저장 패쓰가 없을 경우(편집 적용 전) 원본을 반환
     *
     * @return
     */
    private String getCurrentBuildImagePath() {
        ImageEditInfo editInfo = getCurrentImageEditInfo();
        return editInfo.tempImagePath == null ? editInfo.originImagePath : editInfo.tempImagePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveCroppedImageComplete(CropImageView view, Uri uri, Exception error) {
        BaLog.i();
        if(error == null) { // 크롭 성공
            mCropImageView.setVisibility(View.GONE);

            refreshImageCache(uri.toString(), getCurrentRealImageView());
            refreshImageCache(uri.toString(), getCurrentEditImageView());

            ImageEditInfo editInfo = getCurrentImageEditInfo();
            String prevOriginImgPath = editInfo.originImagePath;
            refreshImagePathList(prevOriginImgPath, editInfo.tempImagePath);

            mPreviewsFragment.updatePreview(editInfo.imagePosition, editInfo.tempImagePath);
            editInfo.setOriginImagePathToTempPath();

            changeOneDepthBottomBar();

        } else {            // 크롭 실패
            showToast(R.string.fail_crop);
            BaLog.e(error.getMessage());
            error.printStackTrace();
        }
    }

    private void refreshImageCache(String uri, ImageView imageView) {
        mImageLoader.displayImage(uri, imageView, mDipslayImageOptions,
                new AUILUtil.ImageCacheRefreshLoadingListener(mDipslayImageOptions));
    }

    @Override
    public void onBackPressed() {
        int stackCount = getSupportFragmentManager().getBackStackEntryCount();

        if(stackCount > 0) {
            changeOneDepthBottomBar();

            if(mEditMode == EDIT_MODE_ORDERING) {
                // 순서바꾸기 프래그먼트 띄워져 있을 때 - 닫음
                closeOrderingBar();
                return;
            }
        }

        // 이전 버튼 or 백버튼 눌렀을 때 - 현재 가지고 있는 원본 이미지경로 리스트 전달(삭제된게 있을 수 있으므로)
        Intent retIntent = new Intent();
        retIntent.putExtra(IntentConstants.KEY_IMAGE_PATH_LIST, mDelImagePathList);
        setResult(RESULT_CANCELED, retIntent);
        finish();

        super.onBackPressed();
    }

    public class ImagePagerAdapter extends PagerAdapter {
        private Context context;
        private ArrayList<ImageEditInfo> imageEditInfoList;

        public ImagePagerAdapter(Context c, ArrayList<ImageEditInfo> imageEditInfoList) {
            super();
            this.context = c;
            this.imageEditInfoList = imageEditInfoList;
        }

        @Override
        public Object instantiateItem(final ViewGroup pager, final int position) {
            ImageEditInfo imageEditInfo = imageEditInfoList.get(position);
            BaLog.d("position=" + position + ", editInfo=" + imageEditInfo);
            final String uri = UriUtil.convertFilePathToUri(imageEditInfo.tempImagePath == null ? imageEditInfo.originImagePath : imageEditInfo.tempImagePath);

            final ImageView editImgView = new ImageView(context);
            editImgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            editImgView.setTag(imageEditInfo);

            imageEditInfo.initDegreeInfo();
            imageEditInfo.initFlipInfo();

            ImageView realImgView = new ImageView(context);
            realImgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            realImgView.setTag("realImg" + imageEditInfo.imagePosition);

            FrameLayout frame = new FrameLayout(context);
            frame.addView(editImgView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            frame.addView(realImgView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            pager.addView(frame, 0);

            mImageLoader.displayImage(uri, editImgView, mDipslayImageOptions);
            mImageLoader.displayImage(uri, realImgView, mDipslayImageOptions);

            editImgView.setVisibility(View.INVISIBLE);

            return frame;
        }

        @Override
        public int getCount() {
            return imageEditInfoList.size();
        }

        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
        }

        public void setImageEditInfoList(ArrayList<ImageEditInfo> imageEditInfoList) {
            this.imageEditInfoList = imageEditInfoList;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;   // 이렇게 해주어야 setCurrentItem 할때 뷰가 갱신됨
        }
    }

    class ImageEditInfo {
        /** SD카드에 있는 실제 이미지 경로 */
        public String realOriginImagePath;
        /** 현재상태의 원본 이미지 경로. 편집을 적용하면 임시패쓰를 원본으로 다시 설정함 */
        public String originImagePath;
        /** 이미지 회전 및 크롭, 필터를 적용한 비트맵을 저장할 임시 파일 패스 */
        public String tempImagePath;

        public int imagePosition;
        public float originRotateDegree = 0;
        public float prevRotateDegree = 0;
        public float rotateDegree = 0;
        public int leftRightFlipCount;
        public int upDownFlipCount;
        public int cropWidth;
        public int cropHeight;

        public ImageEditInfo(int imagePosition, String imagePath) {
            this.imagePosition = imagePosition;
            this.realOriginImagePath = this.originImagePath = imagePath;
        }

        /**
         * 이미지 뷰가 재생성될 경우 초기화 해야함.
         * 왜냐하면 이미지뷰의 애니메이션의 fillAfter 정보가 날라가기 때문
         */
        public void initDegreeInfo() {
            this.originRotateDegree = 0;
            this.prevRotateDegree = 0;
            this.rotateDegree = 0;
        }

        public void initFlipInfo() {
            this.leftRightFlipCount = 0;
            this.upDownFlipCount = 0;
        }
        /**
         * 임시 파일 경로를 반환한다. 없을 때만 새로 만들어서 리턴
         * @return
         */
        public String getOrCreateTempImagePath() {
            if(tempImagePath == null) {
                String outputPathStr = BaPackageManager.getTempImagePath(BaseImageEditActivity.this.getApplicationContext());
                File outputPath = new File(outputPathStr);

                if(!outputPath.exists()) {
                    outputPath.mkdir();
                }

                tempImagePath = outputPathStr + System.currentTimeMillis() + imagePosition + ".jpg";
            }

            return tempImagePath;
        }

        @Override
        public String toString() {
            return "imagePosition=" + imagePosition +
                    ", originRotateDegree=" + originRotateDegree +
                    ", prevRotateDegree=" + prevRotateDegree +
                    ", rotateDegree=" + rotateDegree +
                    "\n, originImagePath=" + originImagePath +
                    ", tempImagePath=" + tempImagePath;
        }

        /**
         * 현재 원본 이미지 패쓰를 편집한 임시 이미지 패쓰로 설정한다.
         */
        public void setOriginImagePathToTempPath() {
            this.originImagePath = tempImagePath;
        }

        public boolean isChanged() {
            return originRotateDegree != 0 || rotateDegree != 0 || prevRotateDegree != 0 || leftRightFlipCount != 0 || upDownFlipCount != 0;
        }
    }
}