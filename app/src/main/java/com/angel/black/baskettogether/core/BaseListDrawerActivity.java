package com.angel.black.baskettogether.core;

import android.os.Bundle;
import android.support.annotation.LayoutRes;

/**
 * Created by KimJeongHun on 2016-06-12.
 */
public class BaseListDrawerActivity extends BaseListActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    protected MyRecyclerViewAdapter createListAdapter() {
        return null;
    }

    @Override
    protected void requestList() {

    }
}
