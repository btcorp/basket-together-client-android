package com.angel.black.baskettogether.core;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.view.recyclerview.AbsRecyclerViewHolder;
import com.angel.black.baskettogether.core.view.recyclerview.RecyclerViewAdapterData;
import com.angel.black.baskettogether.util.MyLog;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public abstract class BaseListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected MyRecyclerViewAdapter mRecyclerViewAdapter;
    protected LinearLayoutManager mLayoutManager;
    protected View mLoadingFooterView;

    protected int mCurPage = 1;
    protected int mTotalItemCount = 0;
    protected boolean isCanLoadMore = true;
    protected int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents_base_list);

        mSwipeRefreshLayout = (SwipeRefreshLayout) mContentsLayout.findViewById(R.id.layout_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) mContentsLayout.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerViewAdapter = createListAdapter();
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(new MyScrollListener());

        mLoadingFooterView = getLayoutInflater().inflate(R.layout.loading_progress, null);
    }

    protected abstract MyRecyclerViewAdapter createListAdapter();

    @Override
    public void onRefresh() {
        MyLog.i("mCurPage=" + mCurPage);
    }

    class MyScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            MyLog.i();
            if(dy > 0) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                if (isCanLoadMore) {
                    if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        isCanLoadMore = false;
                        MyLog.v("Last Item Wow !");
                        loadMore();
                    }
                }
            }
        }
    }

    public void showLoadingFooter() {
        mRecyclerViewAdapter.addData(null);
        mRecyclerViewAdapter.notifyItemInserted(mRecyclerViewAdapter.getDataset().length() - 1);
    }

    public void hideLoadingFooter() {
        mRecyclerViewAdapter.remove(mRecyclerViewAdapter.getDataset().length() - 1);
        mRecyclerViewAdapter.notifyItemRemoved(mRecyclerViewAdapter.getDataset().length());
    }

    protected void loadMore() {
        mCurPage++;
        requestList();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyLog.w("Load More");

            }
        }, 3000);

    }

    protected abstract void requestList();

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<AbsRecyclerViewHolder> {
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private RecyclerViewAdapterData.RecyclerViewColletionData mDataset;
        private RecyclerViewAdapterData mRecyclerViewAdapterData;

        public MyRecyclerViewAdapter(RecyclerViewAdapterData recyclerViewAdapterData) {
            this.mRecyclerViewAdapterData = recyclerViewAdapterData;
            this.mDataset = mRecyclerViewAdapterData.provideData();
        }

        public RecyclerViewAdapterData.RecyclerViewColletionData getDataset() {
            return mDataset;
        }

        public class ProgressViewHolder extends AbsRecyclerViewHolder {
            public ProgressBar progressBar;

            public ProgressViewHolder(View v) {
                super(v);
                progressBar = (ProgressBar) v.findViewById(R.id.loading_progress);
            }
        }

        public void addDataset(Object dataset) {
            MyLog.i();
            int itemCount = mDataset.addDataset(dataset);
            notifyItemRangeInserted(mTotalItemCount, itemCount);
        }

        public void addData(Object data) {
            mDataset.addData(data);
            notifyItemInserted(mDataset.length() - 1);
        }

        public void remove(int position) {
            mDataset.removeData(position);
        }

        @Override
        public int getItemViewType(int position) {
            return mDataset.getData(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
        }

        @Override
        public AbsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyLog.i();
            AbsRecyclerViewHolder vh;
            if(viewType == VIEW_TYPE_ITEM) {
                vh = mRecyclerViewAdapterData.createViewHolder(parent);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_footer, parent, false);
                vh = new ProgressViewHolder(view);
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(AbsRecyclerViewHolder holder, int position) {
            if(holder instanceof ProgressViewHolder) {
                ProgressViewHolder loadingViewHolder = (ProgressViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            } else {
                mRecyclerViewAdapterData.onBindViewHolder(holder, position, mDataset.getData(position));
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.length();
        }

        @Override
        public long getItemId(int position) {
            return mDataset.getItemId(position);
        }

    }
}
