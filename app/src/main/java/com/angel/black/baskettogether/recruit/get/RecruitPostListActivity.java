package com.angel.black.baskettogether.recruit.get;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.BaseListActivity;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.angel.black.baskettogether.core.network.HttpAPIRequester;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.core.view.recyclerview.AbsRecyclerViewHolder;
import com.angel.black.baskettogether.core.view.recyclerview.RecyclerViewAdapterData;
import com.angel.black.baskettogether.recruit.RecruitPostRegistActivity;
import com.angel.black.baskettogether.util.MyLog;
import com.angel.black.baskettogether.util.ScreenUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-06-06.
 */
public class RecruitPostListActivity extends BaseListActivity implements RecyclerViewAdapterData,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG_FAB = "fab";
    private static final String TAG_LIST_ROW = "listRow";

    private ImageLoader mImageLoader;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNaviView;

    private Drawable mDefaultProfileImageDrawable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.base_drawer_layout, null);
        FrameLayout layoutContent = (FrameLayout) mDrawerLayout.findViewById(R.id.content_frame);

        ((ViewGroup) mRootLayout.getParent()).removeView(mRootLayout);
        layoutContent.addView(mRootLayout);
        setContentView(mDrawerLayout);

        initToolbar(R.drawable.ic_menu_white_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        mNaviView = (NavigationView) mDrawerLayout.findViewById(R.id.navigation);
        mNaviView.inflateHeaderView(R.layout.recruit_post_list_drawer_header);
        mNaviView.inflateMenu(R.menu.recruit_post_list_drawer_items);

        mNaviView.setNavigationItemSelectedListener(this);
        mNaviView.setItemBackgroundResource(R.drawable.base_list_item_selector);

        addFloatingActionButton();
        requestList();
    }

    private void addFloatingActionButton() {
        FloatingActionButton fab = new FloatingActionButton(this);
        fab.setImageResource(R.drawable.ic_add_white_24dp);
        fab.setRippleColor(getResources().getColor(R.color.colorPrimaryDark));
        fab.setOnClickListener(this);
        fab.setTag(TAG_FAB);
        int size = (int) ScreenUtil.convertDpToPixel(this, 48);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size, Gravity.RIGHT|Gravity.BOTTOM);

        params.bottomMargin = size/3;
        params.rightMargin = size/3;

        mContentsLayout.addView(fab, params);
    }

    @Override
    public void onClick(View v) {
        MyLog.i();

        if(v.getTag().equals(TAG_FAB)) {
            startActivity(RecruitPostRegistActivity.class);
        } else if(v.getTag().equals(TAG_LIST_ROW)) {
            int position = mRecyclerView.getChildAdapterPosition(v);
            long id = mRecyclerViewAdapter.getItemId(position);

            goDetail(position, id);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        MyLog.i();
        Intent intent = null;
        switch(menuItem.getItemId()) {
            case R.id.navigation_item_1:
//                intent = new Intent(this, MyPlaybookPostListActivity.class);
//                intent.putExtra(KeySet.KEY_TITLE_BASE_LIST_ACTIVITY, getResources().getString(R.string.my_playbook));
//                startActivity(intent);

                break;
            case R.id.navigation_item_2:
//                intent = new Intent(this, FavoritePlaybookPostListActivity.class);
//                intent.putExtra(KeySet.KEY_TITLE_BASE_LIST_ACTIVITY, getResources().getString(R.string.like_playbook));
//                startActivity(intent);
                break;

            case R.id.navigation_item_3:
                //로그아웃
//                LoginUtil.logOut(LoginUtil.REQUEST_COMMON_LOGIN, MainShareActivity.this);
                break;

        }
        return false;
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

    @Override
    public RecyclerViewColletionData provideData() {
        return new JSONRecyclerViewCollectionData();
    }

    @Override
    public AbsRecyclerViewHolder createViewHolder(ViewGroup parent) {
        View v = getLayoutInflater().inflate(R.layout.adapter_recruit_post_list, parent, false);
        v.setTag(TAG_LIST_ROW);
        TextView postTitle = (TextView) v.findViewById(R.id.post_title);
        TextView postContent = (TextView) v.findViewById(R.id.post_content);
        TextView postAuthor = (TextView) v.findViewById(R.id.post_author);
        ImageView postAuthorImage = (ImageView) v.findViewById(R.id.post_author_image);

        v.setOnClickListener(this);

        return new ViewHolder(v, postTitle, postContent, postAuthor, postAuthorImage);
    }

    @Override
    public void onBindViewHolder(AbsRecyclerViewHolder holder, int position, Object data) {
        JSONObject rowData = (JSONObject) data;

        ((ViewHolder) holder).mPostTitle.setText(rowData.optString("title"));
        ((ViewHolder) holder).mPostContent.setText(rowData.optString("content"));
        ((ViewHolder) holder).mPostAuthor.setText(rowData.optString("author_name"));
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

    private JSONArray testResponse(int start) {
        JSONArray jsonArray = new JSONArray();

        for(int i=start; i<=start+20; i++) {
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
    protected void requestList() {
        boolean showLoading = true;
        if(mTotalItemCount > 0) showLoading = false;

        new HttpAPIRequester(this, showLoading, ServerURLInfo.API_RECRUIT_POSTS_GET, "GET", new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                MyLog.i("retCode=" + retCode + ", response=" + response);
                populatePostList(response.getJSONArray("results"));
                refreshComplete(true);
            }

            @Override
            public void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException {
                MyLog.i("retCode=" + retCode + ", response=" + response);
                populatePostList(response);
                refreshComplete(true);
            }

            @Override
            public void onErrorResponse(String APIUrl, String message, Throwable cause) {
                //TODO 테스트 용
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        populatePostList(testResponse(mTotalItemCount));
                        isCanLoadMore = true;
                    }
                }, 1000);
                refreshComplete(false);
            }
        }).execute((JSONObject) null);
    }

    private void populatePostList(final JSONArray response) {
        MyLog.d("populatePostList >> response=" + response);
        if(mRecyclerViewAdapter == null) {
            mRecyclerViewAdapter = new MyRecyclerViewAdapter(this);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        } else {
            if(mCurPage > 1) {
                mRecyclerViewAdapter.addDataset(response);
            } else {
                mRecyclerViewAdapter.setDataset(response);
            }
        }

        mTotalItemCount += response.length();
    }

    private void goDetail(int position, long id) {
        MyLog.d("position=" + position + ", id=" + id);
        Intent intent = new Intent(this, RecruitPostDetailActivity.class);
        intent.putExtra(IntentConst.KEY_EXTRA_POST_ID, id);
        startActivity(intent);
    }


}
