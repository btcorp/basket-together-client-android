package com.angel.black.baskettogether.recruit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.ui.dialog.DialogClickListener;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.api.APICallSuccessNotifier;
import com.angel.black.baskettogether.api.RecruitAPI;
import com.angel.black.baskettogether.common.view.CommentInputView;
import com.angel.black.baskettogether.core.base.BtBaseActivity;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.angel.black.baskettogether.recruit.fragment.RecruitPostDetailFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class RecruitPostDetailActivity extends BtBaseActivity implements CommentInputView.CommentActionListener {
    private long mPostId;

    private Menu mOptionsMenu;                  // 툴바 수정, 삭제 메뉴
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recruit_post_detail, menu);
        mOptionsMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.menu_edit) {
            BaLog.d("글 수정 버튼 클릭!");

        } else if(item.getItemId() == R.id.menu_delete) {
            BaLog.d("글 삭제 버튼 클릭!");
            showAlertDialog(R.string.confirm_delete_this_recruit_post, new DialogClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BaLog.d("글 삭제 OK 버튼 클릭");

                    RecruitAPI.deleteRecruitPost(RecruitPostDetailActivity.this, mPostId, new APICallSuccessNotifier() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            setResult(IntentConst.RESULT_DELETED);
                            finish();
                        }
                    });
                }
            });
        }
        return false;
    }

    public void showOptionsMenuItems() {
        mOptionsMenu.getItem(0).setVisible(true);
        mOptionsMenu.getItem(1).setVisible(true);
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

        RecruitAPI.registCommentToRecruit(this, mPostId, content, new APICallSuccessNotifier() {
            @Override
            public void onSuccess(JSONObject response) {
                // 댓글등록 성공
                hideCurrentFocusKeyboard();
                mCmntInputView.setCommentText("");
                showToast("댓글 등록 성공");

                mPostDetailFragment.requestGetComments();
            }
        });
    }
}
