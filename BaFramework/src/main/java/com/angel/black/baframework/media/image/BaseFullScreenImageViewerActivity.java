package com.angel.black.baframework.media.image;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.intent.IntentConstants;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.image.fragment.GalleryFragment;
import com.angel.black.baframework.media.image.fragment.PreviewsFragment;
import com.angel.black.baframework.media.image.view.PreviewView;
import com.angel.black.baframework.ui.animation.AnimationUtil;
import com.angel.black.baframework.ui.view.imageview.TouchImageView;
import com.angel.black.baframework.ui.view.viewpager.InfinitePagerAdapter;
import com.angel.black.baframework.ui.view.viewpager.InfiniteViewPager;
import com.angel.black.baframework.util.UriUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by KimJeongHun on 2016-07-05.
 */
public class BaseFullScreenImageViewerActivity extends BaseActivity implements PreviewsFragment.PreviewActionListener,
        View.OnClickListener, ViewPager.OnPageChangeListener, GalleryFragment.ImageDisplayer {
    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mDipslayImageOptions;

    private GalleryFragment.ImageDisplayer mImageDisplayer;

    private InfiniteViewPager mPager;
    private InfinitePagerAdapter mInfPagerAdapter;
    private PreviewsFragment mPreviewsFragment;
    private ArrayList<String> mImagePathList;
    private ArrayList<String> mDelImagePathList = new ArrayList<>();

    /** 현재 선택된 페이지 (가상의 증가된 숫자) */
    private int mCurSelectedPage;

    /** 현재 선택된 진짜 페이지 (0~5 사이) */
    private int mCurSelectedRealPage;

    private boolean mIsLockAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_viewer);

        mImagePathList = getIntent().getStringArrayListExtra(IntentConstants.KEY_IMAGE_PATH_LIST);
        final int count = getIntent().getIntExtra(IntentConstants.KEY_IMAGE_COUNT, 1);
        final int initialImgIdx = getIntent().getIntExtra(IntentConstants.KEY_INITIAL_IMAGE_INDEX, 0);

        setTitle(initialImgIdx + 1 + " / " + count);
//        , 0, R.drawable.top_btn_close, R.string.del, 0, this, this);

        mImageLoader = ImageLoader.getInstance();

        mDipslayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(getResources().getDrawable(R.color.light_gray))
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        mImageDisplayer = this;

        mPager = (InfiniteViewPager) findViewById(R.id.viewpager);
        mPager.addOnPageChangeListener(this);

        mPreviewsFragment = (PreviewsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_previews);
        mPreviewsFragment.showPreviewsBar(true);
        mPreviewsFragment.addPreviews(mImagePathList);

        ZoomImagePagerAdapter adapter = new ZoomImagePagerAdapter(this, mImagePathList);
        mInfPagerAdapter = new InfinitePagerAdapter(adapter);
        mPager.setAdapter(mInfPagerAdapter);
        mPager.setCurrentItem(count * 100 + initialImgIdx);		// 최초 사진의 위치로 페이저어댑터 위치시킴
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private ArrayList<String> convertUriList(ArrayList<String> pathList) {
        ArrayList<String> uriList = new ArrayList<>();
        for(String path : pathList) {
            uriList.add(UriUtil.convertFilePathToUri(path));
        }
        return uriList;
    }

    @Override
    public void onClick(View view) {
        BaLog.i("view.id=" + view.getId() + ", view.getTag=" + view.getTag());
//        if (view.getId() == R.id.btn_title_left_area) {
//            // 닫기 버튼
//            onBackPressed();
//        } else if (view.getId() == R.id.btn_title_right_area) {
//            // 삭제 버튼
//            startRemovePagerItemAnimation();
//        } else
        if (((String)view.getTag()).contains("imgView")) {
            // 뷰페이저 영역 클릭

            if(mIsLockAnimation)
                return;

            mIsLockAnimation = true;

            if (mPreviewsFragment.getView().getVisibility() == View.VISIBLE) {
                // 숨김 애니메이션
                AnimationUtil.startViewDisAppearSlideTopBottomAnim(this, mToolbar.getRootView(), mPreviewsFragment.getView());
            } else {
                // 보임 애니메이션
                AnimationUtil.startViewAppearSlideTopBottomAnim(this, mToolbar.getRootView(), mPreviewsFragment.getView());
            }

            // 0.5초 락걸고 해제
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    mIsLockAnimation = false;
                }
            }.sendEmptyMessageDelayed(0, 500);
        }
    }

    private ImageView getCurrentPageItemView() {
        return (ImageView) mPager.findViewWithTag("imgView" + mCurSelectedRealPage);
    }

    private void startRemovePagerItemAnimation() {
        View view = getCurrentPageItemView();

        AnimationUtil.startViewDisAppearScaleToZeroAnim(this, view, new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mPreviewsFragment.removePreview(mImagePathList.get(getCurrentItemRealIndex()));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private int getCurrentItemRealIndex() {
        return mCurSelectedPage % mPager.getAdapter().getCount();
    }

    @Override
    public void onAddedPreview(PreviewView previewView) {

    }

    @Override
    public void onRemovedPreview(int removeIndex, PreviewsFragment.PreviewData previewData, boolean isNeedGalleryDeselect) {
        mDelImagePathList.add(previewData.imagePath);
        mImagePathList.remove(previewData.imagePath);

        if(mImagePathList.size() == 0) {
            onBackPressed();
            return;
        }

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

        ((ZoomImagePagerAdapter) mPager.getAdapter()).setUriList(mImagePathList);
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

    private int getItemRealIndex(int itemRealIndex) {
        return mPager.getCurrentItem();
    }

    @Override
    public void onClickedPreview(int position, PreviewView previewView) {
        BaLog.d("position=" + position);
        int pageOffset = getCurrentItemRealIndex() - position;
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
        BaLog.d("arg position=" + position);
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
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent(); // 삭제된 이미지 패스 리스트 반환
        returnIntent.putStringArrayListExtra(IntentConstants.KEY_IMAGE_PATH_LIST, mDelImagePathList);

        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void displayImage(String uri, ImageView imgView, Object... extras) {
        mImageLoader.displayImage(uri, imgView, mDipslayImageOptions);
    }

    public class ZoomImagePagerAdapter extends PagerAdapter {
        private Context context;
        private ArrayList<String> uriList;

        public ZoomImagePagerAdapter(Context c, ArrayList<String> imagePathList) {
            super();
            this.context = c;
            this.uriList = convertUriList(imagePathList);
        }

        @Override
        public Object instantiateItem(final ViewGroup pager, final int position) {
            BaLog.w("position=" + position);
            final String uri = uriList.get(position);

            final TouchImageView imgView = new TouchImageView(context);
            imgView.setMaxZoom(5.0f);
            imgView.setTag("imgView" + position);
            imgView.setOnClickListener(BaseFullScreenImageViewerActivity.this);

            ((ViewPager)pager).addView(imgView, 0);
            BaLog.d("zoom touchimgView width=" + imgView.getWidth() + ", height=" + imgView.getHeight());

            imgView.post(new Runnable() {
                @Override
                public void run() {
//                    ImageAware imageAware = new ImageViewAware(imgView, false);
//                    BaLog.d("zoom touchimgView post width=" + imageAware.getWidth() + ", height=" + imageAware.getHeight());
//                    mImageLoader.displayImage(uri, imageAware, mOptions);

                    mImageDisplayer.displayImage(uri, imgView);
                }
            });

            return imgView;
        }

        @Override
        public int getCount() {
            return uriList.size();
        }

        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
        }

        public void setUriList(ArrayList<String> imagePathList) {
            this.uriList = convertUriList(imagePathList);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;   // 이렇게 해주어야 setCurrentItem 할때 뷰가 갱신됨
        }
    }
}
