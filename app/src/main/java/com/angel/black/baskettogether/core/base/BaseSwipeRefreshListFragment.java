package com.angel.black.baskettogether.core.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.util.MyLog;

/**
 * Created by KimJeongHun on 2016-06-25.
 */
public abstract class BaseSwipeRefreshListFragment extends BaseListFragment implements SwipeRefreshLayout.OnRefreshListener {
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup parentView = (ViewGroup) inflater.inflate(R.layout.fragment_base_swipe_refresh_list, container, false);
        View childView = super.onCreateView(inflater, container, savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) parentView.findViewById(R.id.layout_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        parentView.addView(childView);

        return parentView;
    }

    @Override
    public void onRefresh() {
        MyLog.i("before mCurPage=" + mCurPage);
        mCurPage = 1;
        mTotalItemCount = 0;
        requestList();
    }

    protected void refreshComplete(boolean success) {
        if(mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);

            if(success) {
                isCanLoadMore = true;
            }
        }
    }
}
