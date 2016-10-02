package com.angel.black.baframework.media.image.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.image.ThumbnailDisplayer;
import com.angel.black.baframework.media.image.fragment.PreviewsFragment;
import com.angel.black.baframework.util.ScreenUtil;

import java.lang.ref.WeakReference;

/**
 * Created by KimJeongHun on 2016-07-03.
 */
public class PreviewView extends RelativeLayout implements View.OnClickListener {
    protected BaseActivity mActivity;
    protected ImageView mImgPreview;
    protected ProgressBar mLoadingProgress;
    protected ViewGroup mBtnDelPreview;

    /** 현재 뷰 표시 사각 영역 */
    private Rect mRect;

    public PreviewView(Context context) {
        super(context);
        init(context);
    }

    public PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        mActivity = (BaseActivity) context;
        View view = View.inflate(getContext(), R.layout.preview_view_on_image_pick, this);

        mImgPreview = (ImageView) view.findViewById(R.id.img_preview);
        mLoadingProgress = (ProgressBar) view.findViewById(R.id.loading_progress_bar);

        mBtnDelPreview = (ViewGroup) view.findViewById(R.id.btn_del_preview_layout);
        mBtnDelPreview.setOnClickListener(this);

        mRect = new Rect();
        getGlobalVisibleRect(mRect);
    }

    /**
     * 이 프리뷰 아이템에 대한 정보를 담고있는 tag 오브젝트를 셋팅한다.
     * 그리고 바로 이미지뷰에 이미지를 표시한다.
     * @param tag
     */
    public void setTagAndDisplayImage(PreviewsFragment.PreviewTag tag) {
        if(tag == null) {
            throw new IllegalArgumentException("tag must not null!!");
        }

        setTag(tag);

        new ThumbnailDisplayer(new WeakReference<>(mImgPreview), mLoadingProgress).execute(tag);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_del_preview_layout) {
            if(getTag().isDummpyPreviewData()) {    // 아직 썸네일 생성중인 경우는 삭제 안되게
                return;
            }

            PreviewsFragment fragment = (PreviewsFragment) ((BaseActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.fragment_previews);
            fragment.removePreview(getTag(), true);
        }
    }

    public PreviewsFragment.PreviewTag getTag() {
        return (PreviewsFragment.PreviewTag) super.getTag();
    }

    public void setSelected(boolean select) {
        if(select) {
            ScaleAnimation anim = new ScaleAnimation(1f, 1.1f, 1f, 1.1f,
                    Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
            anim.setDuration(200);
            anim.setFillAfter(true);
            ((View) mImgPreview.getParent()).startAnimation(anim);

        } else {
            ScaleAnimation anim = new ScaleAnimation(1.1f, 1f, 1.1f, 1f,
                    Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
            anim.setDuration(200);
            anim.setFillAfter(true);
            ((View) mImgPreview.getParent()).startAnimation(anim);
        }
    }

    public boolean isOutOfScreen() {
        getHitRect(mRect);

        return ScreenUtil.isOutOfScreenWidth(getContext(), mRect);
    }

    public Rect getRect() {
        return mRect;
    }

    public void setImageBitmap(Bitmap bitmap) {
        BaLog.d("thumbnail setImageBitmap="+ this.toString());
        mImgPreview.setImageBitmap(bitmap);
        mLoadingProgress.setVisibility(View.INVISIBLE);
    }

    public boolean isLoading() {
        return mLoadingProgress.getVisibility() == View.VISIBLE;
    }

    @Override
    public String toString() {
        return "hashCode=" + hashCode() + ", tag=" + getTag();
    }

}
