package com.angel.black.baskettogether.recruit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.common.view.CommentInputView;
import com.angel.black.baskettogether.core.BaseActivity;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.angel.black.baskettogether.core.network.HttpAPIRequester;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.recruit.fragment.RecruitPostDetailFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecruitPostDetailActivity extends BaseActivity implements CommentInputView.CommentActionListener {
    private long mPostId;

    private CommentInputView mCmntInputView;

    private RecruitPostDetailFragment mPostDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_post_detail);

        mCmntInputView = (CommentInputView) findViewById(R.id.comment_input_view);
        mCmntInputView.setCommentActionListener(this);

        Intent intent = getIntent();
        mPostId = intent.getLongExtra(IntentConst.KEY_EXTRA_POST_ID, 0);

        if(mPostId < 0) {
            showToast("잘못된 접근");
            finish();
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        mPostDetailFragment = (RecruitPostDetailFragment) fm.findFragmentById(R.id.recruit_post_detail_fragment);

        mPostDetailFragment.setPostId(mPostId);
    }


    @Override
    public void onClickRegistComment() {
        try {
            requestRegistComment();
        } catch (JSONException e) {
            e.printStackTrace();
            showOkDialog(R.string.error_regist_comment);
        }
    }

    private void requestRegistComment() throws JSONException {
        String content = mCmntInputView.getInputedComment();
        JSONObject commentData = buildRegistCommentData(content);

        new HttpAPIRequester(this, true, ServerURLInfo.API_RECRUIT_POST_REGIST_COMMENT_PREFIX + mPostId +
                ServerURLInfo.API_RECRUIT_POST_REGIST_COMMENT_POSTFIX, "POST",
                new HttpAPIRequester.OnAPIResponseListener() {
                    @Override
                    public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                        // 댓글등록 성공
                        mPostDetailFragment.requestGetComments();
                    }

                    @Override
                    public void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException {

                    }

                    @Override
                    public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {
                        //TODO 테스트

                    }
                }).execute(commentData);
    }

    private JSONObject buildRegistCommentData(String comment) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("content", comment);

        return json;
    }
}
