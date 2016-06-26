package com.angel.black.baskettogether.recruit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.base.BaseFragment;
import com.angel.black.baskettogether.core.network.HttpAPIRequester;
import com.angel.black.baskettogether.core.network.ServerURLInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-06-26.
 */
public class RecruitPostDetailHeaderFragment extends BaseFragment {
    private long mPostId;

    private TextView mTitle;
    private TextView mContent;
    private TextView mAuthor;
    private ImageView mAuthorImage;
    private Button mBtnAttenderCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recruit_post_detail, container, false);

        mTitle = (TextView) view.findViewById(R.id.post_title);
        mContent = (TextView) view.findViewById(R.id.post_content);
        mAuthor = (TextView) view.findViewById(R.id.post_author);
        mAuthorImage = (ImageView) view.findViewById(R.id.post_author_image);
        mBtnAttenderCount = (Button) view.findViewById(R.id.btn_attender_count);

        return view;
    }

    /**
     * 가져올 포스트 id를 지정 후 API 요청한다.
     * @param postId
     */
    public void setPostId(long postId) {
        this.mPostId = postId;
        requestGetPostDetail(postId);
    }

    private void requestGetPostDetail(long postId) {
        new HttpAPIRequester(this, true, ServerURLInfo.API_GET_RECRUIT_POST_DETAIL + postId + "/", "GET", new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                setData(response);
            }

            @Override
            public void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException {

            }

            @Override
            public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {
                //TODO 테스트
                try {
                    setData(testResponse());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute((JSONObject)null);
    }

    private JSONObject testResponse() {
        JSONObject object = new JSONObject();

        try {
            object.put("title", "농구합시다 3:3");
            object.put("content", "양주시 백석체육공원에서 농구합니다. 오세요.");
            object.put("author", "김정훈");
            object.put("recruit_count", "6");
            object.put("attend_count", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    private void setData(JSONObject response) throws JSONException {
        mTitle.setText(response.optString("title"));
        mContent.setText(response.optString("content"));
        mAuthor.setText(response.optString("author"));
        mBtnAttenderCount.setText(getString(R.string.attender_count) +
                response.optInt("attend_count") + "/" + response.optString("recruit_count"));
    }
}
