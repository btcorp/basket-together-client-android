package com.angel.black.baskettogether.recruit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.base.BaseListFragment;
import com.angel.black.baskettogether.core.network.HttpAPIRequester;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.core.view.recyclerview.AbsRecyclerViewHolder;
import com.angel.black.baskettogether.core.view.recyclerview.RecyclerViewAdapterData;
import com.angel.black.baskettogether.util.CalendarUtil;
import com.angel.black.baskettogether.util.MyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-06-26.
 */
public class RecruitPostDetailFragment extends BaseListFragment implements
        RecyclerViewAdapterData<JSONArray, JSONObject>,
        View.OnClickListener {
    private long mPostId;

    private TextView mTitle;
    private TextView mContent;
    private TextView mAuthor;
    private ImageView mAuthorImage;
    private Button mBtnAttenderCount;
    private Button mBtnReqAttend;
    private TextView mRegDate;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void requestList() {
        new HttpAPIRequester(this, true, ServerURLInfo.API_GET_RECRUIT_POST_DETAIL + mPostId + "/", "GET", new HttpAPIRequester.OnAPIResponseListener() {
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

    @Override
    protected MyRecyclerViewAdapter createListAdapter() {
        return new MyRecyclerViewAdapter(this);
    }

    @Override
    protected void bindHeaderView() {

    }

    @Override
    protected View createHeaderView(ViewGroup parent) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_recruit_post_detail, parent, false);

        mTitle = (TextView) view.findViewById(R.id.post_title);
        mContent = (TextView) view.findViewById(R.id.post_content);
        mAuthor = (TextView) view.findViewById(R.id.post_author);
        mAuthorImage = (ImageView) view.findViewById(R.id.post_author_image);
        mBtnAttenderCount = (Button) view.findViewById(R.id.btn_attender_count);
        mBtnAttenderCount.setOnClickListener(this);
        mBtnReqAttend = (Button) view.findViewById(R.id.btn_request_attend);
        mBtnReqAttend.setOnClickListener(this);
        mRegDate = (TextView) view.findViewById(R.id.post_reg_date);

        return view;
    }

    /**
     * 가져올 포스트 id를 지정 후 API 요청한다.
     * @param postId
     */
    public void setPostId(long postId) {
        this.mPostId = postId;
        requestList();
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
        mBtnAttenderCount.setText(getString(R.string.attender_count) + " " +
                response.optInt("attend_count") + "/" + response.optString("recruit_count"));
        mRegDate.setText(CalendarUtil.getDateString(response.optString("registered_date")));

        populateList(response.getJSONArray("comments"));

    }

    @Override
    public AbsRecyclerViewHolder createViewHolder(ViewGroup parent) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.adapter_recruit_post_detail_comment_list, parent, false);
        v.setTag(TAG_LIST_ROW);
        TextView commentContent = (TextView) v.findViewById(R.id.comment_content);
        TextView commentAuthor = (TextView) v.findViewById(R.id.comment_author);
        ImageView commentAuthorImage = (ImageView) v.findViewById(R.id.comment_author_image);

        v.setOnClickListener(this);

        return new ViewHolder(v, commentContent, commentAuthor, commentAuthorImage);
    }

    @Override
    public void onBindViewHolder(AbsRecyclerViewHolder holder, int position, JSONObject data) {
        ((ViewHolder) holder).mCommentContent.setText(data.optString("content"));
        ((ViewHolder) holder).mCommentAuthor.setText(data.optString("author_name"));
        ((ViewHolder) holder).mCommentAuthorImage.setImageResource(R.drawable.ic_account_circle_black_24dp);
    }

    @Override
    public RecyclerViewColletionData provideData() {
        return new JSONRecyclerViewCollectionData();
    }

    @Override
    public void populateList(JSONArray dataset) {
        MyLog.d("dataset=" + dataset);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_attender_count:
                showAttendee(true);

                break;
            case R.id.btn_request_attend:
                // 참가 신청
                requestAttendToRecruit();

                break;
        }
    }

    /**
     * 참가신청
     */
    private void requestAttendToRecruit() {
        new HttpAPIRequester(this, true, ServerURLInfo.API_RECRUIT_ATTEND + mPostId + "/", "POST",
                new HttpAPIRequester.OnAPIResponseListener() {
                    @Override
                    public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                        //TODO 참가신청 성공 후 포스트 데이터 재요청
                        requestList();
                    }

                    @Override
                    public void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException {

                    }

                    @Override
                    public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {
                        //TODO 테스트

                    }
                }).execute((JSONObject)null);
    }

    private void showAttendee(boolean show) {
        if (show) {
            FragmentManager fm = getChildFragmentManager();

//            FragmentTransaction ft = fm.beginTransaction();
//            ft.add()



        } else {

        }
    }

    /**
     * 댓글 조회 API 요청
     */
    public void requestGetComments() {

    }

    public static class ViewHolder extends AbsRecyclerViewHolder {
        public TextView mCommentContent;
        public TextView mCommentAuthor;
        public ImageView mCommentAuthorImage;

        public ViewHolder(View rowLayout, TextView commentContent, TextView commentAuthor, ImageView commentAuthorImage) {
            super(rowLayout);
            this.mCommentContent = commentContent;
            this.mCommentAuthor = commentAuthor;
            this.mCommentAuthorImage = commentAuthorImage;
        }
    }
}
