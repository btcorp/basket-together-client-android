package com.angel.black.baskettogether.recruit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.BaseActivity;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.angel.black.baskettogether.recruit.fragment.RecruitPostDetailCommentListFragment;
import com.angel.black.baskettogether.recruit.fragment.RecruitPostDetailFragment;

public class RecruitPostDetailActivity extends BaseActivity {
    private long mPostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_post_detail);

        Intent intent = getIntent();
        mPostId = intent.getLongExtra(IntentConst.KEY_EXTRA_POST_ID, 0);

        if(mPostId < 0) {
            showToast("잘못된 접근");
            finish();
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        RecruitPostDetailFragment fragment1 = (RecruitPostDetailFragment) fm.findFragmentById(R.id.recruit_post_detail_fragment);
        RecruitPostDetailCommentListFragment fragment2 = (RecruitPostDetailCommentListFragment) fm.findFragmentById(R.id.recruit_post_detail_comment_list_fragment);

        fragment1.setPostId(mPostId);
        fragment2.setPostId(mPostId);
    }
}
