package com.angel.black.baskettogether.recruit.fragment;

import android.view.View;
import android.view.ViewGroup;

import com.angel.black.baskettogether.core.base.BaseListFragment;

/**
 * Created by KimJeongHun on 2016-06-26.
 */
public class RecruitPostDetailCommentListFragment extends BaseListFragment {
    private long mPostId;

    @Override
    protected void requestList() {
        // 댓글목록 조회

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

    public void setPostId(long postId) {
        this.mPostId = postId;
        requestList();
    }
}
