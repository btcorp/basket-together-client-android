package com.angel.black.baskettogether.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.R;

/**
 * Created by KimJeongHun on 2016-09-01.
 */
public class CommentInputView extends LinearLayout implements View.OnClickListener {
    private EditText mEditComment;
    private Button mBtnRegistComment;
    private CommentActionListener mCommentActionListener;

    public void setCommentActionListener(CommentActionListener commentActionListener) {
        this.mCommentActionListener = commentActionListener;
    }

    public CommentInputView(Context context) {
        this(context, null, 0);
    }

    public CommentInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.comment_input_bar, this);

        mEditComment = (EditText) findViewById(R.id.detail_edit_comment);
        mBtnRegistComment = (Button) findViewById(R.id.btn_comment_regist);
        mBtnRegistComment.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_comment_regist) {
            if(StringUtil.isEmptyInputString(getInputedComment())) {
                return;
            }

            if(mCommentActionListener != null) {
                mCommentActionListener.onClickRegistComment();
            }
        }
    }

    public String getInputedComment() {
        return mEditComment.getText().toString().trim();
    }

    public void setCommentText(String comment) {
        mEditComment.setText(comment);
    }

    public interface CommentActionListener {
        void onClickRegistComment();
    }
}
