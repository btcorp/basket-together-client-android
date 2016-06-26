package com.angel.black.baskettogether.recruit;

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

import com.android.volley.toolbox.ImageLoader;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.BaseListActivity;
import com.angel.black.baskettogether.core.base.BaseListFragment;
import com.angel.black.baskettogether.recruit.fragment.RecruitPostListFragment;
import com.angel.black.baskettogether.util.MyLog;
import com.angel.black.baskettogether.util.ScreenUtil;

/**
 * Created by KimJeongHun on 2016-06-06.
 */
public class RecruitPostListActivity extends BaseListActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG_FAB = "fab";

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
    }

    @Override
    protected BaseListFragment getListFragmentInstance() {
        return RecruitPostListFragment.newInstance();
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


}
