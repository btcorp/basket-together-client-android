package com.angel.black.baframework.ui.view.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.angel.black.baframework.logger.BaLog;

/**
 * 무한 스크롤 뷰페이저. 어댑터로 InfinitePagerAdapter 사용해야함 
 * @author KimJeongHun
 *
 */
public class InfiniteViewPager extends ViewPager {
	private final String TAG = InfiniteViewPager.class.getSimpleName();
	private boolean enabled = true;
	
    public InfiniteViewPager(Context context) {
        super(context);
    }

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        // offset first element so that we can scroll to the left
        setCurrentItem(0);
    }

    @Override
    public void setCurrentItem(int item) {
        // offset the current item to ensure there is space to scroll
        BaLog.d("position=" + item);
        setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        BaLog.d("position=" + item + ", smoothScroll=" + smoothScroll);
        if (getAdapter().getCount() == 0) {
            super.setCurrentItem(item, smoothScroll);
            return;
        }
//        item = getOffsetAmount() + (item % getAdapter().getCount());
        item = getOffsetAmount() + item;
        BaLog.d("offset added position=" + item);
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public int getCurrentItem() {
        if (getAdapter().getCount() == 0) {
            return super.getCurrentItem();
        }
        int position = super.getCurrentItem();
        if (getAdapter() instanceof InfinitePagerAdapter) {
            InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
            // Return the actual item position in the data backing InfinitePagerAdapter
            return (position % infAdapter.getRealCount());
        } else {
            return super.getCurrentItem();
        }
    }

    public int getCurrentItemReal() {
        return getCurrentItem() % getAdapter().getCount();
    }

    private int getOffsetAmount() {
        if (getAdapter().getCount() == 0) {
            return 0;
        }
        if (getAdapter() instanceof InfinitePagerAdapter) {
            InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
            // allow for 100 back cycles from the beginning
            // should be enough to create an illusion of infinity
            // warning: scrolling to very high values (1,000,000+) results in
            // strange drawing behaviour
            return infAdapter.getRealCount() * 100;
        } else {
            return 0;
        }
//        return Math.max(1, getAdapter().getCount()) * 100;
    }

//    @Override
//    public PagerAdapter getAdapter() {
//        return super.getAdapter();
//    }

    @Override
    public PagerAdapter getAdapter() {
    	InfinitePagerAdapter adapter = ((InfinitePagerAdapter)super.getAdapter());
    	
    	return adapter.getRealAdapter();
    }
    
    // 뷰페이저내의 이미지 뷰 확대했을 때 스크롤 이슈 관련
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if(enabled)
            return super.onInterceptTouchEvent(arg0);

        return false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
