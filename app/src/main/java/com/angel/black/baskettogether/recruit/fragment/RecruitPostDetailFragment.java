package com.angel.black.baskettogether.recruit.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.angel.black.baframework.core.base.BaseListFragment;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.ui.view.recyclerview.AbsRecyclerViewHolder;
import com.angel.black.baframework.ui.view.recyclerview.RecyclerViewAdapterData;
import com.angel.black.baframework.util.CalendarUtil;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.api.APICallSuccessNotifier;
import com.angel.black.baskettogether.api.RecruitAPI;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.angel.black.baskettogether.recruit.RecruitPostDetailActivity;
import com.angel.black.baskettogether.recruit.googlemap.LocationInfo;
import com.angel.black.baskettogether.recruit.googlemap.RecruitPostLocationMapActivity;
import com.angel.black.baskettogether.user.UserHelper;

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
    private boolean mIsAttendingThis;   // 내가 이 모집글에 참가중인지 여부

    private boolean mIsMyPost;          // 이 글이 내가 쓴글인지 여부

    private TextView mTitle;
    private TextView mContent;
    private TextView mAuthor;
    private ImageView mAuthorImage;
    private Button mBtnAttenderCount;
    private Button mBtnReqAttend;
    private TextView mRegDate;
    private TextView mMeetingDate;
    private TextView mMeetingPlaceAddr1;
    private TextView mMeetingPlaceAddr2;
    private Button mBtnViewLocation;
    private TextView mCommentEmptyView;

    private RecruitPostDetailAttendeeFragment mAttendeeFragment;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                // 이렇게 해야 헤더 뷰의 높이가 조정됨
                ViewGroup.LayoutParams params = view.getLayoutParams();
                outRect.top = params.height;
            }
        }, 0);
    }

    @Override
    public void requestList() {
        RecruitAPI.getRecruitPostDetail(getBaseActivity(), String.valueOf(mPostId), new APICallSuccessNotifier() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    setData(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    showOkDialog("모집글 데이터를 가져오는 중 문제가 발생했습니다.");
                }
            }
        });
    }

    @Override
    protected MyRecyclerViewAdapter createListAdapter() {
        return new MyRecyclerViewAdapterWithHeader(this);
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
        mMeetingDate = (TextView) view.findViewById(R.id.recruit_datetime);
        mMeetingPlaceAddr1 = (TextView) view.findViewById(R.id.recruit_place_addr1);
        mMeetingPlaceAddr2 = (TextView) view.findViewById(R.id.recruit_place_addr2);
        mBtnViewLocation = (Button) view.findViewById(R.id.btn_location_map);
        mBtnViewLocation.setOnClickListener(this);
        mCommentEmptyView = (TextView) view.findViewById(R.id.comment_empty);

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

    /**
     * 이 모집글에 내가 참가신청을 했는지 여부 반환
     * @param attendList
     * @return
     */
    private boolean isAttendingThis(JSONArray attendList) {
        try {
            for (int i = 0; i < attendList.length(); i++) {
                JSONObject attendInfo = attendList.getJSONObject(i);

                if(UserHelper.userUid == attendInfo.getLong("user_id")) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void setData(JSONObject response) throws JSONException {
        mIsMyPost = response.optLong("author_id") == UserHelper.userUid;

        if(mIsMyPost) {
            ((RecruitPostDetailActivity) getBaseActivity()).showOptionsMenuItems();
        }

        mTitle.setText(response.optString("title"));
        mContent.setText(response.optString("content"));
        mAuthor.setText(response.optString("author_name"));
        mBtnAttenderCount.setText(getString(R.string.attender_count) + " " +
                response.optInt("attend_count") + "/" + response.optString("recruit_count"));

        JSONArray attendList = response.getJSONArray("attend_list");

        // 참가 신청, 참가 신청 취소 버튼 셋팅
        if(!mIsMyPost) {
            mBtnReqAttend.setVisibility(View.VISIBLE);
            mIsAttendingThis = isAttendingThis(attendList);

            if (mIsAttendingThis) {
                mBtnReqAttend.setText(R.string.request_attend_cancel);
            } else {
                mBtnReqAttend.setText(R.string.request_attend);
            }
        } else {
            // 내 글이면 참가신청 버튼 숨김
            mIsAttendingThis = true;
        }

        createAttendeeFragment(attendList);

        mRegDate.setText(CalendarUtil.getDateString(response.optLong("registered_date")));
        mMeetingDate.setText(CalendarUtil.getDateString(response.optLong("meeting_date")));
        String addr1 = response.optString("adress1");
        String addr2 = response.optString("adress2");
        mMeetingPlaceAddr1.setText(addr1);
        mMeetingPlaceAddr2.setText(addr2);

        mBtnViewLocation.setTag(new LocationInfo(response.optDouble("lat"), response.optDouble("lng"), addr1 + " " + addr2));

        populateList(response.getJSONArray("comments"));
    }

    private void createAttendeeFragment(JSONArray attendList) {
        mAttendeeFragment = RecruitPostDetailAttendeeFragment.newInstance();

        for(int i=0; i < attendList.length(); i++) {
            try {
                JSONObject jsonObject = attendList.getJSONObject(i);

                long id = jsonObject.getLong("user_id");
                String name = jsonObject.getString("user_name");

                mAttendeeFragment.addAttendee(id, name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public AbsRecyclerViewHolder createViewHolder(ViewGroup parent) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.adapter_recruit_post_detail_comment_list, parent, false);
        v.setTag(TAG_LIST_ROW);
        TextView commentContent = (TextView) v.findViewById(R.id.comment_content);
        TextView commentAuthor = (TextView) v.findViewById(R.id.comment_author);
        ImageView commentAuthorImage = (ImageView) v.findViewById(R.id.comment_author_image);
        TextView commentRegDate = (TextView) v.findViewById(R.id.comment_reg_date);

        v.setOnClickListener(this);

        return new ViewHolder(v, commentContent, commentAuthor, commentAuthorImage, commentRegDate);
    }

    @Override
    public void onBindViewHolder(AbsRecyclerViewHolder holder, int position, JSONObject data) {
        ((ViewHolder) holder).mCommentContent.setText(data.optString("content"));
        ((ViewHolder) holder).mCommentAuthor.setText(data.optString("author_name"));
        ((ViewHolder) holder).mCommentAuthorImage.setImageResource(R.drawable.ic_account_circle_black_24dp);
        ((ViewHolder) holder).mCommentRegDate.setText(CalendarUtil.getDateString(data.optLong("registered_date")));
    }

    @Override
    public RecyclerViewColletionData provideData() {
        return new JSONRecyclerViewCollectionData();
    }

    @Override
    public void populateList(JSONArray dataset) {
        BaLog.d("dataset=" + dataset);

        if(mCurPage > 1) {
            mRecyclerViewAdapter.addDataset(dataset);
        } else {
            mRecyclerViewAdapter.setDataset(dataset);
            showCommentsEmptyView(dataset.length() == 0);
        }

        mCurItemCount += dataset.length();
    }

    private void showCommentsEmptyView(boolean show) {
        if(show) {
            mCommentEmptyView.setVisibility(View.VISIBLE);
        } else {
            mCommentEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_attender_count:
                if(mAttendeeFragment != null && !mAttendeeFragment.isVisible()) {
                    showAttendee(true);
                } else {
                    showAttendee(false);
                }

                break;
            case R.id.btn_request_attend:
                if(mIsAttendingThis) {
                    // 참가 신청 취소
                    requestAttendCancelToRecruit();
                } else {
                    // 참가 신청
                    requestAttendToRecruit();
                }

                break;
            case R.id.btn_location_map:
                if(mBtnViewLocation.getTag() != null) {
                    LocationInfo locationInfo = (LocationInfo) mBtnViewLocation.getTag();

                    Intent intent = new Intent(getContext(), RecruitPostLocationMapActivity.class);

                    intent.putExtra(IntentConst.KEY_EXTRA_MAP_LATITUDE, locationInfo.latitude);
                    intent.putExtra(IntentConst.KEY_EXTRA_MAP_LONGITUDE, locationInfo.longitude);
                    intent.putExtra(IntentConst.KEY_EXTRA_MAP_MODE, RecruitPostLocationMapActivity.MapMode.VIEW.toString());
                    intent.putExtra(IntentConst.KEY_EXTRA_MAP_ADDRESS, locationInfo.address);

                    startActivity(intent);
                }

                break;
        }
    }

    /**
     * 참가신청 취소
     */
    private void requestAttendCancelToRecruit() {
        RecruitAPI.cancelAttendToRecruit(getBaseActivity(), mPostId, new APICallSuccessNotifier() {
            @Override
            public void onSuccess(JSONObject response) {
                requestList();

                showToast(R.string.succ_recruit_req_attend_canceled);
            }
        });
    }

    /**
     * 참가신청
     */
    private void requestAttendToRecruit() {
        RecruitAPI.requestAttendToRecruit(getBaseActivity(), mPostId, new APICallSuccessNotifier() {
            @Override
            public void onSuccess(JSONObject response) {
                //TODO 참가신청 성공 후 모집글 데이터 재요청
                requestList();

                showToast(R.string.succ_recruit_req_attend);
            }
        });
    }

    private void showAttendee(boolean show) {
        if(mAttendeeFragment == null)
            return;

        if (show) {
            addChildFragment(R.id.container_attendee_fragment, mAttendeeFragment,
                    R.anim.slide_top_in, R.anim.slide_bottom_out);
        } else {
            removeChildFragment(mAttendeeFragment, R.anim.slide_top_out, R.anim.slide_bottom_in);
        }
    }

    /**
     * 댓글 조회 API 요청
     */
    public void requestGetComments() {
        RecruitAPI.getRecruitPostComments(getBaseActivity(), mPostId, new APICallSuccessNotifier() {
            @Override
            public void onSuccess(JSONObject response) {
                showToast("댓글 조회 SUccess");

                try {
                    JSONArray jsonArray = response.getJSONArray("json_array");
                    mRecyclerViewAdapter.setDataset(jsonArray);
                    showCommentsEmptyView(jsonArray.length() == 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("댓글 조회 후 데이터 교체 실패");
                }
            }
        });
    }

    public static class ViewHolder extends AbsRecyclerViewHolder {
        public TextView mCommentContent;
        public TextView mCommentAuthor;
        public ImageView mCommentAuthorImage;
        public TextView mCommentRegDate;

        public ViewHolder(View rowLayout, TextView commentContent, TextView commentAuthor, ImageView commentAuthorImage, TextView commentRegDate) {
            super(rowLayout);
            this.mCommentContent = commentContent;
            this.mCommentAuthor = commentAuthor;
            this.mCommentAuthorImage = commentAuthorImage;
            this.mCommentRegDate = commentRegDate;
        }
    }
}
