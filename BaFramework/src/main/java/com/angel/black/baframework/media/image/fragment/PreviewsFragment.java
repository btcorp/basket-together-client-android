package com.angel.black.baframework.media.image.fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.core.base.BaseFragment;
import com.angel.black.baframework.intent.IntentConstants;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.camera.CameraPictureFileBuilder;
import com.angel.black.baframework.media.image.BaseFullScreenImageViewerActivity;
import com.angel.black.baframework.media.image.GalleryBuilder;
import com.angel.black.baframework.media.image.view.PreviewView;
import com.angel.black.baframework.util.BitmapUtil;
import com.angel.black.baframework.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by KimJeongHun on 2016-07-01.
 */
public class PreviewsFragment extends BaseFragment {
    public static final String UNKNOWN_IMAGE_PATH = "Unknown";

    private List<PreviewView> mPreviewViewList;
    private HorizontalScrollView mHScrollView;
    private LinearLayout mContainerPreviews;
    private PreviewActionListener mPreviewActionListener;

    private boolean isAnimating = false;
    private ArrayBlockingQueue<PreviewView> mLastPreviewViewQueue = new ArrayBlockingQueue<>(6);

    public static PreviewsFragment newInstance() {
        PreviewsFragment fragment = new PreviewsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPreviewActionListener = (PreviewActionListener) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_previews, container, false);

        mHScrollView = (HorizontalScrollView) view.findViewById(R.id.hscroll_view);
        mContainerPreviews = (LinearLayout) view.findViewById(R.id.container_previews);
        mPreviewViewList = Collections.synchronizedList(new ArrayList<PreviewView>());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public ArrayList<String> getPreviewImagePathList() {
        ArrayList<String> pathList = new ArrayList<>();
        for(PreviewView view : mPreviewViewList) {
            pathList.add(view.getTag().previewData.imagePath);
        }

        return pathList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showPreviewsBar(boolean show) {
        if(show) {
            mHScrollView.setVisibility(View.VISIBLE);
            ((View) mHScrollView.getParent()).setVisibility(View.VISIBLE);
        } else {
            mHScrollView.setVisibility(View.GONE);
            ((View) mHScrollView.getParent()).setVisibility(View.GONE);
        }
    }

    private void scrollToEnd(final int direction) {
        mHScrollView.post(new Runnable() {
            @Override
            public void run() {
                mHScrollView.fullScroll(direction);
            }
        });
    }

    public void replacePreviews(ArrayList<String> imagePathList) {
        removeAllPreviews();

        addPreviews(imagePathList);
    }

    private void removeAllPreviews() {
        mPreviewViewList.clear();
        mContainerPreviews.removeAllViews();
    }

    /**
     * 주어진 이미지 패쓰로 프리뷰들을 추가한다.
     * @param imagePathList
     */
    public void addPreviews(ArrayList<String> imagePathList) {
        BaLog.i();
        for(int i=0; i < imagePathList.size(); i++) {
            String path = imagePathList.get(i);
            BaLog.d("path=" + path);
            PreviewView previewView = new PreviewView(getContext());
            previewView.setTagAndDisplayImage(new PreviewTag(i, PreviewData.createPreviewData(path, null)));
            previewView.setOnClickListener(mPreviewClickListener);

            mContainerPreviews.addView(previewView);
            mPreviewViewList.add(previewView);
        }

        scrollToEnd(View.FOCUS_RIGHT);
    }

    public void addPreviewFromGallery(GalleryBuilder.GalleryBucketItemInfo item) {
        BaLog.d("item=" + item);
        isAnimating = true;
        if(mPreviewActionListener != null) {
            mPreviewActionListener.onStartPreviewAnimation();
        }

        addPreviewView(new GalleryPreviewTag(mPreviewViewList.size(), item));
    }

    public void addPreviewFromCamera() {
        isAnimating = true;
        if(mPreviewActionListener != null) {
            mPreviewActionListener.onStartPreviewAnimation();
        }

        PreviewTag previewTag = new PreviewTag(mPreviewViewList.size(), new DummyPreviewData());

        PreviewView previewView = addPreviewView(previewTag);
        mLastPreviewViewQueue.add(previewView);
    }

    private PreviewView addPreviewView(PreviewTag previewTag) {
        BaLog.d("previewTag=" + previewTag);

        if(mPreviewViewList.size() == 0) {
            showPreviewsBar(true);
        }

        PreviewView previewView = createPreviewView(previewTag);

        mContainerPreviews.addView(previewView);
        BaLog.d("previewView added >> " + previewView);
        startAppearAnimation(previewView);

        return previewView;
    }

    private PreviewView createPreviewView(PreviewTag previewTag) {
        PreviewView previewView = new PreviewView(getContext());
        previewView.setTagAndDisplayImage(previewTag);
        previewView.setOnClickListener(mPreviewClickListener);

        return previewView;
    }


    /**
     * 애니메이션 후에 프리뷰 삭제
     * @param imagePath
     */
    public void removePreview(String imagePath) {
        if(isAnimating) {
            return;
        }

        for(PreviewView view : mPreviewViewList) {
            if(view.getTag().previewData.imagePath.equals(imagePath)) {
                removePreview(view.getTag(), true);
                return;
            }
        }
    }

    /**
     * 애니메이션 없이 즉시 프리뷰 삭제
     * @param imagePath
     */
    public void removePreviewImmediately(String imagePath) {
        for(PreviewView view : mPreviewViewList) {
            if(view.getTag().previewData.imagePath.equals(imagePath)) {
                removePreviewReal(view, true);
                return;
            }
        }
    }

    public void removePreview(PreviewData previewData, boolean isNeedGalleryDeselect) {
        if(isAnimating)
            return;

        removePreviewWithAnimation(findPreviewView(previewData), isNeedGalleryDeselect);
    }

    public void removePreview(PreviewTag previewTag, boolean isNeedGalleryDeselect) {
        if(isAnimating)
            return;
        removePreviewWithAnimation(findPreviewView(previewTag), isNeedGalleryDeselect);
    }

    private void removePreviewWithAnimation(PreviewView previewView, boolean isNeedGalleryDeselect) {
        BaLog.d("previewView=" + previewView);

        isAnimating = true;
        if(mPreviewActionListener != null) {
            mPreviewActionListener.onStartPreviewAnimation();
        }
        int removeIndex = previewView.getTag().index;

        startRemoveAnimation(removeIndex, previewView, isNeedGalleryDeselect);
    }

    private void removePreviewReal(final PreviewView view, boolean isNeedGalleryDeselect) {
        PreviewTag previewTag = view.getTag();
        PreviewData previewData = previewTag.previewData;
        int removeIdx = previewTag.index;
        BaLog.d("removeIdx=" + removeIdx);

        rearrangePreviewTags(removeIdx);

        mContainerPreviews.removeView(view);
        mPreviewViewList.remove(view);
        BaLog.d("preview removed >> " + view);

        if(mPreviewViewList.size() == 0) {
            showPreviewsBar(false);
        }

        if(mPreviewActionListener != null) {
            mPreviewActionListener.onRemovedPreview(removeIdx, previewData, isNeedGalleryDeselect);
        }
    }

    /**
     * 삭제하는 프리뷰 뷰 다음의 아이템들의 인덱스를 땡긴다.
     * @param removeIdx
     */
    private void rearrangePreviewTags(int removeIdx) {
        for(int i = removeIdx + 1; i < mPreviewViewList.size(); i++) {
            PreviewView previewView = mPreviewViewList.get(i);
            previewView.getTag().index--;
        }
    }

    public ImageView getPreviewImageView(int position) {
        return (ImageView) mPreviewViewList.get(position).findViewById(R.id.img_preview);
    }

    public void updateDeletedPreviews(ArrayList<String> delImagePathList) {
        for(String imagePath : delImagePathList) {
            removePreviewImmediately(imagePath);
        }
    }

    /**
     * 해당 인덱스의 프리뷰를 주어진 새 패스로 업데이트 한다.
     * @param index
     * @param newImagePath 새로 만들어진 이미지 경로
     */
    public void updatePreview(int index, String newImagePath) {
        PreviewView previewView = mPreviewViewList.get(index);
        previewView.setTagAndDisplayImage(new PreviewTag(index, new PreviewData(newImagePath, null)));
    }

    /**
     * 선택/선택해제 상태로 만든다. (이미지 보더 씌움)
     * @param position
     */
    public void setSelected(boolean select, int position) {
        BaLog.d("position=" + position + ", select=" + select);
        try {
            PreviewView view = mPreviewViewList.get(position);
            view.setSelected(select);

            if(select && view.isOutOfScreen()) {
                if(view.getRect().left <= 0) {
                    scrollToEnd(View.FOCUS_LEFT);
                } else {
                    scrollToEnd(View.FOCUS_RIGHT);
                }
            }

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            // 삭제된 아이템 선택해제 시도시 무시
        }
    }

    public void goFullScreenImagePreviewActivity(PreviewView previewView) {
        BaseActivity activity = (BaseActivity) getActivity();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(getActivity(), BaseFullScreenImageViewerActivity.class);
            intent.putExtra(IntentConstants.KEY_IMAGE_PATH_LIST, getPreviewImagePathList());
            intent.putExtra(IntentConstants.KEY_IMAGE_COUNT, getAttachedPreviewCount());
            intent.putExtra(IntentConstants.KEY_INITIAL_IMAGE_INDEX, findPreviewViewIndex(previewView));

            ImageView previewImageView = (ImageView) previewView.findViewById(R.id.img_preview);
            previewImageView.setTransitionName("zoomImage");

            activity.getWindow().setAllowEnterTransitionOverlap(true);

            activity.startActivityForResult(intent, IntentConstants.REQUEST_ZOOM_IMAGE,
                    ActivityOptions.makeSceneTransitionAnimation(activity, previewImageView, "zoomImage")
                            .toBundle());
        } else {
            Intent intent = new Intent(getActivity(), BaseFullScreenImageViewerActivity.class);
            intent.putExtra(IntentConstants.KEY_IMAGE_PATH_LIST, getPreviewImagePathList());
            intent.putExtra(IntentConstants.KEY_IMAGE_COUNT, getAttachedPreviewCount());
            intent.putExtra(IntentConstants.KEY_INITIAL_IMAGE_INDEX, findPreviewViewIndex(previewView));

            activity.startActivityForResult(intent, IntentConstants.REQUEST_ZOOM_IMAGE);
        }
    }

    private View.OnClickListener mPreviewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View previewView) {
            if(((PreviewView) previewView).isLoading()) {
                return;
            }

            mPreviewActionListener.onClickedPreview(findPreviewViewIndex((PreviewView) previewView), (PreviewView) previewView);
        }
    };

    private void startAppearAnimation(final PreviewView appearView) {
        BaLog.d();
        Animation appearAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_in);
        appearAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                BaLog.d();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                BaLog.d();
                mPreviewViewList.add(appearView);
                scrollToEnd(View.FOCUS_RIGHT);

                isAnimating = false;
                if(mPreviewActionListener != null) {
                    mPreviewActionListener.onEndPreviewAnimation();
                    mPreviewActionListener.onAddedPreview(appearView);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        appearView.startAnimation(appearAnim);
    }

    private void startRemoveAnimation(final int removeIndex, final PreviewView removeView, final boolean isNeedGalleryDeselect) {
        BaLog.d("removeIndex=" + removeIndex);

        Animation removeAnim = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        final boolean removeReal = removeIndex == mPreviewViewList.size() - 1;

        removeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(removeReal) {
                    endRemoveAnimation(removeView, isNeedGalleryDeselect);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        removeView.startAnimation(removeAnim);

        int index = removeIndex + 1;

        int previewSize = mPreviewViewList.size();

        for(int i=index, cnt=0; i < previewSize; i++, cnt++) {
            Animation pullAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_out);
            pullAnim.setFillEnabled(true);

            View v = mPreviewViewList.get(i);

            if(i == previewSize - 1) {
                pullAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        endRemoveAnimation(removeView, isNeedGalleryDeselect);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
            v.startAnimation(pullAnim);
        }
    }

    private void endRemoveAnimation(PreviewView removeView, boolean isNeedGalleryDeselect) {
        removePreviewReal(removeView, isNeedGalleryDeselect);
        isAnimating = false;
        if (mPreviewActionListener != null) {
            mPreviewActionListener.onEndPreviewAnimation();
        }
    }

    private PreviewView findPreviewView(PreviewData previewData) {
        for(PreviewView previewView : mPreviewViewList) {
            if(previewView.getTag().equals(previewData)) {
                BaLog.d("found!! previewView=" + previewView);
                return previewView;
            }
        }

        BaLog.e("not found! return null");
        return null;
    }

    private PreviewView findPreviewView(PreviewTag previewTag) {
        for(PreviewView previewView : mPreviewViewList) {
            if(previewView.getTag() == previewTag) {
                BaLog.d("found!! previewView=" + previewView);
                return previewView;
            }
        }
        BaLog.e("not found! return null");
        return null;
    }

    /**
     * 주어진 프리뷰 뷰가 속해있는 PreviewViewList 인덱스를 찾아서 반환
     * @param previewView
     * @return
     */
    private int findPreviewViewIndex(PreviewView previewView) {
        int size = mPreviewViewList.size();

        for(int i=0; i < size; i++) {
            PreviewView view = mPreviewViewList.get(i);

            if(view == previewView) {
                return i;
            }
        }

        return -1;
    }


    public int getAttachedPreviewCount() {
        return mPreviewViewList.size();
    }

    /**
     * 카메라 찍은 사진으로부터 썸네일을 만든다. (별도의 작업스레드에서 실행)
     * @param buildImageResult
     * @param previewView
     * @return
     */
    public Bitmap buildThumbnail(CameraPictureFileBuilder.BuildImageResult buildImageResult, PreviewView previewView) {
        BaLog.i();

        Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(buildImageResult.getBitmap(), 160, 160);

        PreviewTag previewTag = previewView.getTag();
        previewTag.previewData = new PreviewData(buildImageResult.getFilepath(), thumbBitmap);

        Message retMsg = mTempPreviewHandler.obtainMessage(0, previewView);
        mTempPreviewHandler.sendMessage(retMsg);

        return thumbBitmap;
    }

    public Bitmap getPreviewImageBitmap(int position) {
        return mPreviewViewList.get(position).getTag().getBitmap();
    }

    public void setPreviewImageBitmap(int position, Bitmap thumbBitmap) {
        BaLog.d("thumbBitmap width=" + thumbBitmap.getWidth() + ", height=" + thumbBitmap.getHeight());
        PreviewView previewView = mPreviewViewList.get(position);
        previewView.getTag().previewData.bitmap = thumbBitmap;
        previewView.setImageBitmap(thumbBitmap);
    }

    /**
     * 아직 프리뷰 뷰가 로딩중인지 조사한다.
     * @return
     */
    public boolean isLoadingPreviewImage() {
//        if(mTempPreviewJobThread != null) {
//            return mTempPreviewJobThread.mHandler.hasMessages(MSG_ADD_TEMP_EMPTY_PREVIEW_FROM_CAMERA)
//                    || mTempPreviewJobThread.mHandler.hasMessages(MSG_LOADED_THUMBNAIL_FOR_TEMP_PREVIEW)
//                    || isExistNotCompletedLoadingPreviews();
//        }


        return isExistNotCompletedLoadingPreviews();
    }

    /**
     * 아직 로딩 완료되지 않은 프리뷰 뷰가 있는지 검사한다.
     * @return
     */
    private boolean isExistNotCompletedLoadingPreviews() {
        ArrayList<String> imagePathList = getPreviewImagePathList();

        for (String imagePath : imagePathList) {
            if(StringUtil.isEmptyString(imagePath) || UNKNOWN_IMAGE_PATH.equals(imagePath)) {
                return true;
            }
        }

        return false;
    }

    public PreviewView getLastInsertedPreviewView() {
        PreviewView temp = mLastPreviewViewQueue.poll();
        return temp;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    /**
     * 이미지 한장 선택 모드로 들어와서 한장을 바꿀때 프리뷰도 바꿈
     * @param item
     */
    public void changePreviewFromGallery(GalleryBuilder.GalleryBucketItemInfo item) {
        if(mPreviewViewList != null && mPreviewViewList.size() == 1) {
            removePreviewReal(mPreviewViewList.get(0), true);
        }
        addPreviewFromGallery(item);
    }

    public interface PreviewActionListener {
        void onAddedPreview(PreviewView previewView);

        /**
         * 프리뷰 바에서 프리뷰 오브젝트 삭제될 때 콜백
         * @param removeIndex
         * @param previewData                  삭제된 프리뷰 아이템
         * @param isNeedGalleryDeselect 갤러리 화면에서 선택해제 되어야 하는지 여부. false 인 경우 이미 해제한 상태
         */
        void onRemovedPreview(int removeIndex, PreviewData previewData, boolean isNeedGalleryDeselect);

        /**
         * 프리뷰 바의 프리뷰 오브젝트를 클릭했을 때 콜백
         * @param position
         * @param previewView
         */
        void onClickedPreview(int position, PreviewView previewView);

        void onStartPreviewAnimation();

        void onEndPreviewAnimation();
    }

    public class CameraPreviewTag extends PreviewTag {
        public CameraPreviewTag(int index, CameraPictureFileBuilder.CameraPictureItemInfo item) {
            super(index, item != null ? new PreviewData(item.mFilepath, item.mThumbnail) : null);
        }

        @Override
        public String toString() {
            return "index=" + index + ", previewData=" + previewData;
        }
    }

    /**
     * 갤러리로부터 셋팅되는 프리뷰 아이템
     */
    public class GalleryPreviewTag extends PreviewTag {

        public GalleryPreviewTag(int index, GalleryBuilder.GalleryBucketItemInfo item) {
            super(index, new PreviewData(item.path, null));
        }

        @Override
        public String toString() {
            return "index=" + index + ", previewData=" + previewData;
        }
    }

    /**
     * RegistProductImagePreviewView 에 셋팅할 Tag 오브젝트 (프리뷰 아이템에 대한 정보를 담음)
     */
    public class PreviewTag {
        public int index;
        public PreviewData previewData;     // null 일 수 있음. 항상 검사해야함

        public PreviewTag(int index, PreviewData previewData) {
            this.index = index;
            this.previewData = previewData;
        }

        public Bitmap getBitmap() {
            BaLog.d();
            if(previewData.bitmap != null) {
                return previewData.bitmap;
            } else {
                File f = new File(previewData.imagePath);
                BaLog.d("f=" + f.toString() + ", f.exist()=" + f.exists());
                Bitmap bitmap = BitmapUtil.getBitmapThumbnail(previewData.imagePath);

                Bitmap rotateBitmap = BitmapUtil.rotateBitmap(bitmap, BitmapUtil.getPhotoOrientation(previewData.imagePath));

                return rotateBitmap != null ? rotateBitmap : bitmap;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof PreviewData) {
                PreviewData other = (PreviewData) o;

                return this.previewData.imagePath.equals(other.imagePath);
            }

            return false;
        }

        public boolean isDummpyPreviewData() {
            return previewData instanceof DummyPreviewData;
        }

        @Override
        public String toString() {
            return "index=" + index + ", previewData=" + previewData;
        }
    }

    public static class PreviewData {
        public String imagePath;
        public Bitmap bitmap;

        public PreviewData(String filepath, Bitmap bitmap) {
            this.imagePath = filepath;
            this.bitmap = bitmap;
        }

        public static PreviewData createPreviewData(String filepath, Bitmap bitmap) {
            if(filepath == null || filepath.isEmpty()) {
                return new DummyPreviewData();
            } else {
                return new PreviewData(filepath, bitmap);
            }
        }

        @Override
        public String toString() {
            return "imagePath=" + imagePath + ", bitmap=" + bitmap + (bitmap != null ? " size=" + bitmap.getWidth() + "x" + bitmap.getHeight() : "");
        }
    }

    public static class DummyPreviewData extends PreviewData {
        public DummyPreviewData() {
            super(UNKNOWN_IMAGE_PATH, null);
        }
    }

    private Handler mTempPreviewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                PreviewView previewView = (PreviewView) msg.obj;
                previewView.setImageBitmap(previewView.getTag().getBitmap());
            }
        }
    };
}
