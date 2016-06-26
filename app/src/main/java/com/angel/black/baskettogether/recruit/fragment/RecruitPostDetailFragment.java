package com.angel.black.baskettogether.recruit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.angel.black.baskettogether.core.base.BaseListFragment;

/**
 * Created by KimJeongHun on 2016-06-26.
 */
public class RecruitPostDetailFragment extends BaseListFragment {
    private long mPostId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void requestList() {

    }

    @Override
    protected MyRecyclerViewAdapter createListAdapter() {
        return null;
    }

    @Override
    protected void bindHeaderView() {

    }

    @Override
    protected View createHeaderView(ViewGroup parent) {
        return null;
    }

    /**
     * 가져올 포스트 id를 지정 후 API 요청한다.
     * @param postId
     */
    public void setPostId(long postId) {
        this.mPostId = postId;
    }

}
