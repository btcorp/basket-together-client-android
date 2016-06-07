package com.angel.black.baskettogether.post.get;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.BaseActivity;
import com.angel.black.baskettogether.core.MyApplication;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.angel.black.baskettogether.core.network.HttpAPIRequester;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.util.MyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-06-06.
 */
public class RecruitPostListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListViewCompat mPostList;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_post_list);
        initToolbar();

        mImageLoader = MyApplication.getInstance().getImageLoader();
        mPostList = (ListViewCompat) findViewById(R.id.post_list);

        requestPostList();
    }

    private void requestPostList() {
        new HttpAPIRequester(this, ServerURLInfo.API_RECRUIT_POSTS_GET, "GET", new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                populatePostList(response.getJSONArray("results"));
            }

            @Override
            public void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException {
                populatePostList(response);
            }

            @Override
            public void onErrorResponse(String APIUrl, String message, Throwable cause) {

            }
        }).execute((JSONObject) null);
    }

    private void populatePostList(JSONArray response) {
        PostListAdapter listAdapter = new PostListAdapter(response);
        mPostList.setAdapter(listAdapter);
        mPostList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyLog.d("position=" + position + ", id=" + id);
        Intent intent = new Intent(this, RecruitPostDetailActivity.class);
        intent.putExtra(IntentConst.KEY_EXTRA_POST_ID, id);
        startActivity(intent);
    }

    class PostListAdapter extends BaseAdapter {
        private JSONArray datas;

        protected PostListAdapter(JSONArray data) {
            this.datas = data;
        }

        @Override
        public int getCount() {
            return datas.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return datas.get(position);
            } catch(JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            try {
                return ((JSONObject)datas.get(position)).optInt("id");
            } catch (JSONException e) {
                return 0;
            }
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = View.inflate(RecruitPostListActivity.this, R.layout.adapter_recruit_post_list, null);

                holder = new ViewHolder();
                holder.postTitle = (TextView) convertView.findViewById(R.id.post_title);
                holder.postContent = (TextView) convertView.findViewById(R.id.post_content);
                holder.postAuthor = (TextView) convertView.findViewById(R.id.post_author);
                holder.postAuthorImage = (ImageView) convertView.findViewById(R.id.post_author_image);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            try {
                JSONObject rowData = datas.getJSONObject(position);
                holder.postTitle.setText(rowData.optString("title"));
                holder.postContent.setText(rowData.optString("content"));
                holder.postAuthor.setText(rowData.optString("author"));
                holder.postAuthorImage.setImageResource(R.drawable.ic_person_white_24dp);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }

        public class ViewHolder {
            TextView postTitle;
            TextView postContent;
            TextView postAuthor;
            ImageView postAuthorImage;
        }
    }
}
