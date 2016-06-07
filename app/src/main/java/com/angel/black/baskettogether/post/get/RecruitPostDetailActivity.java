package com.angel.black.baskettogether.post.get;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.BaseActivity;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.angel.black.baskettogether.core.network.HttpAPIRequester;
import com.angel.black.baskettogether.core.network.ServerURLInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecruitPostDetailActivity extends BaseActivity {
    private long mPostId;

    private ListViewCompat mList;
    private TextView mTitle;
    private TextView mContent;
    private TextView mAuthor;
    private ImageView mAuthorImage;
    private Button mBtnAttenderCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_post_detail);
        initToolbar();

        mList = (ListViewCompat) findViewById(R.id.comment_list);
        mTitle = (TextView) findViewById(R.id.post_title);
        mContent = (TextView) findViewById(R.id.post_content);
        mAuthor = (TextView) findViewById(R.id.post_author);
        mAuthorImage = (ImageView) findViewById(R.id.post_author_image);
        mBtnAttenderCount = (Button) findViewById(R.id.btn_attender_count);

        Intent intent = getIntent();
        mPostId = intent.getLongExtra(IntentConst.KEY_EXTRA_POST_ID, 0);

        if(mPostId <= 0) {
            showToast("잘못된 접근");
            finish();
            return;
        }

        requestGetPostDetail();
    }

    private void requestGetPostDetail() {
        new HttpAPIRequester(this, ServerURLInfo.API_GET_RECRUIT_POST_DETAIL + mPostId + "/", "GET", new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                setData(response);
            }

            @Override
            public void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException {

            }

            @Override
            public void onErrorResponse(String APIUrl, String message, Throwable cause) {

            }
        }).execute((JSONObject)null);
    }

    private void setData(JSONObject response) throws JSONException {
        mTitle.setText(response.optString("title"));
        mContent.setText(response.optString("content"));
        mAuthor.setText(response.optString("author"));
        mBtnAttenderCount.setText(response.optString("recruit_count"));
    }

}
