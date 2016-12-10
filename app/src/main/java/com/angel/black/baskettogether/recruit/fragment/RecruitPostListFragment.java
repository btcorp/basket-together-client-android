package com.angel.black.baskettogether.recruit.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.angel.black.baframework.core.base.BaseSwipeRefreshListFragment;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.network.HttpAPIRequester;
import com.angel.black.baframework.ui.view.recyclerview.AbsRecyclerViewHolder;
import com.angel.black.baframework.ui.view.recyclerview.RecyclerViewAdapterData;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.recruit.RecruitPostDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-06-24.
 */
public class RecruitPostListFragment extends BaseSwipeRefreshListFragment
        implements RecyclerViewAdapterData<JSONArray, JSONObject>, View.OnClickListener {

    public static RecruitPostListFragment newInstance() {
        RecruitPostListFragment fragment = new RecruitPostListFragment();
//        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestList();
    }

    @Override
    public RecyclerViewColletionData provideData() {
        return new JSONRecyclerViewCollectionData();
    }

    @Override
    public void populateList(JSONArray dataset) {
        BaLog.d("populatePostList >> response=" + dataset);
        if(mCurPage > 1) {
            mRecyclerViewAdapter.addDataset(dataset);
        } else {
            mRecyclerViewAdapter.setDataset(dataset);
        }

        mCurItemCount += dataset.length();
    }

    @Override
    public AbsRecyclerViewHolder createViewHolder(ViewGroup parent) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.adapter_recruit_post_list, parent, false);
        v.setTag(TAG_LIST_ROW);
        TextView postTitle = (TextView) v.findViewById(R.id.post_title);
        TextView postContent = (TextView) v.findViewById(R.id.post_content);
        TextView postAuthor = (TextView) v.findViewById(R.id.post_author);
        ImageView postAuthorImage = (ImageView) v.findViewById(R.id.post_author_image);

        v.setOnClickListener(this);

        return new ViewHolder(v, postTitle, postContent, postAuthor, postAuthorImage);
    }

    @Override
    public void onBindViewHolder(AbsRecyclerViewHolder holder, int position, JSONObject data) {
        ((ViewHolder) holder).mPostTitle.setText(data.optString("title"));
        ((ViewHolder) holder).mPostContent.setText(data.optString("content"));
        ((ViewHolder) holder).mPostAuthor.setText(data.optString("author_name"));
//        if(mDefaultProfileImageDrawable == null) {
//            mDefaultProfileImageDrawable = RoundedBitmapDrawableFactory.create(getResources(),
//                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_white_24dp));
//            ((RoundedBitmapDrawable) mDefaultProfileImageDrawable).setCornerRadius(ScreenUtil.convertDpToPixel(this, 4));
//        }
        ((ViewHolder) holder).mPostAuthorImage.setImageResource(R.drawable.ic_account_circle_black_24dp);
    }

    @Override
    protected MyRecyclerViewAdapter createListAdapter() {
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this);
        return adapter;
    }

    @Override
    protected void bindHeaderView() {

    }

    @Override
    protected View createHeaderView(ViewGroup parent) {
        return null;
    }

    private JSONArray testResponse(int start) {
        JSONArray jsonArray = new JSONArray();

        for(int i=start; i<start+20; i++) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("id", (long) i);
                jsonObject.put("title", "test Title " + i);
                jsonObject.put("content", "테스트 내용 " + i);
                jsonObject.put("author", "스테판 커리 " + i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    @Override
    public void requestList() {
        BaLog.i("mCurPage=" + mCurPage);

        boolean showLoading = true;
        if(mCurItemCount > 0) showLoading = false;

        new HttpAPIRequester(this, showLoading, String.format(ServerURLInfo.API_RECRUIT_POSTS_GET, mCurPage), "GET",
                new HttpAPIRequester.OnAPIResponseListener() {
                    @Override
                    public void onSuccessResponse(String APIUrl, JSONObject response) throws JSONException {
                        BaLog.i("response=" + response);
                        populateList(response.getJSONArray("post_list"));
                        setPagination(response.getInt("total_count"));
                        setLoadComplete(true);
                    }

                    @Override
                    public void onErrorResponse(String APIUrl, String errCode, String errMessage) {
                        setLoadComplete(false);
                    }

                    @Override
                    public void onError(String APIUrl, int retCode, String message, Throwable cause) {
                        //TODO 테스트 용
                        mRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                populateList(testResponse(mCurItemCount));
                                isCanLoadMore = true;
                            }
                        }, 1000);
                        setLoadComplete(false);
                    }
                }).execute((JSONObject) null);
    }

    private void goDetail(int position, long id) {
        BaLog.d("position=" + position + ", id=" + id);
        Intent intent = new Intent(getActivity(), RecruitPostDetailActivity.class);
        intent.putExtra(IntentConst.KEY_EXTRA_POST_ID, id);
        startActivityForResult(intent, IntentConst.REQUEST_VIEW_RECRUIT_POST_DETAIL);
    }

    @Override
    public void onClick(View v) {
        if(v.getTag().equals(TAG_LIST_ROW)) {
            int position = mRecyclerView.getChildAdapterPosition(v);
            long id = mRecyclerViewAdapter.getItemId(position);

            goDetail(position, id);
        }
    }

    public static class ViewHolder extends AbsRecyclerViewHolder {
        public TextView mPostTitle;
        public TextView mPostContent;
        public TextView mPostAuthor;
        public ImageView mPostAuthorImage;

        public ViewHolder(View rowLayout, TextView postTitle, TextView postContent, TextView postAuthor, ImageView postAuthorImage) {
            super(rowLayout);
            this.mPostTitle = postTitle;
            this.mPostContent = postContent;
            this.mPostAuthor = postAuthor;
            this.mPostAuthorImage = postAuthorImage;
        }
    }
}
