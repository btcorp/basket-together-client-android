package com.angel.black.baframework.core.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.angel.black.baframework.R;

/**
 * 리스트뷰 하나가 화면 전체를 덮는 액티비티 정의
 *
 * Created by KimJeongHun on 2016-05-19.
 */
public abstract class BaseListActivity extends BaseActivity {
    protected BaseListFragment mBaseListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents_base_list);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.container_list_fragment, getListFragment());
        ft.commit();
    }

    protected abstract BaseListFragment getListFragmentInstance();

    protected Fragment getListFragment() {
        mBaseListFragment = getListFragmentInstance();
        return mBaseListFragment;
    }


    public void showLoadingFooter() {
        mBaseListFragment.showLoadingFooter();
    }

    public void hideLoadingFooter() {
        mBaseListFragment.hideLoadingFooter();
    }
}
