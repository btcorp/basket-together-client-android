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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.angel.black.baframework.core.base.BaseListFragment;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.util.ScreenUtil;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.api.APICallSuccessNotifier;
import com.angel.black.baskettogether.api.UserAPI;
import com.angel.black.baskettogether.core.MyApplication;
import com.angel.black.baskettogether.core.base.BtBaseListActivity;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.angel.black.baskettogether.core.view.imageview.RoundedImageView;
import com.angel.black.baskettogether.login.LoginActivity;
import com.angel.black.baskettogether.recruit.fragment.RecruitPostListFragment;
import com.angel.black.baskettogether.user.UserInfoManager;
import com.angel.black.baskettogether.user.UserProfileEditActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-06-06.
 */
public class RecruitPostListActivity extends BtBaseListActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
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
        mNaviView.setItemBackgroundResource(R.drawable.base_list_item_selector);

        View headerView = mNaviView.getHeaderView(0);
        TextView txtNickName = (TextView) headerView.findViewById(R.id.user_nickname);
        txtNickName.setText(UserInfoManager.userNickName);

        Button btnEditUserInfo = (Button) headerView.findViewById(R.id.btn_edit_user_info);
        btnEditUserInfo.setOnClickListener(this);

        final RoundedImageView userImgView = (RoundedImageView) headerView.findViewById(R.id.user_profile_img);

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(MyApplication.serverUrl + UserInfoManager.userProfileImgUrl, userImgView,
                DisplayImageOptions.createSimple());

//                new ImageLoader.ImageListener() {
//                    @Override
//                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
//                        BaLog.i("uri=" + imageContainer.getRequestUrl() + ", bitmap=" + imageContainer.getBitmap());
//                        if(imageContainer.getBitmap() == null) {
//                            return;
//                        }
//                        userImgView.setImageBitmap(imageContainer.getBitmap());
//                    }
//
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//
//                    }
//                });



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
        int size = ScreenUtil.convertDpToPixel(this, 48);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size, Gravity.RIGHT|Gravity.BOTTOM);

        params.bottomMargin = size/3;
        params.rightMargin = size/3;

        mContentsLayout.addView(fab, params);
    }

    @Override
    public void onClick(View v) {
        BaLog.i("v.getId()=" + v.getId());

        if(v.getTag() != null && v.getTag().equals(TAG_FAB)) {
            startActivityForResult(RecruitPostRegistActivity.class, IntentConst.REQUEST_REGIST_RECRUIT_POST);
        } else if (v.getId() == R.id.btn_edit_user_info) {
            Intent intent = new Intent(this, UserProfileEditActivity.class);
            intent.putExtra(IntentConst.KEY_EXTRA_USER_NICKNAME, UserInfoManager.userNickName);
            startActivity(intent);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        BaLog.i();
        switch(item.getItemId()) {
            case R.id.navigation_item_1:
                showToast("네비아이템1 클릭");
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
//                LoginUtil.logOut(LoginUtil.REQUEST_COMMON_LOGIN, MainShareActivity.this);
                break;

            case R.id.navigation_item_4:

                break;

            case R.id.navigation_item_5:
                // 로그아웃

                try {
                    UserAPI.logout(RecruitPostListActivity.this, new APICallSuccessNotifier() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            startActivity(LoginActivity.class, true);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    showOkDialog("로그아웃이 되지 않았습니다.");
                }

                break;

        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        BaLog.i();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentConst.REQUEST_REGIST_RECRUIT_POST) {
            if (resultCode == RESULT_OK) {
                mBaseListFragment.requestList();
            }
        } else if (requestCode == IntentConst.REQUEST_VIEW_RECRUIT_POST_DETAIL) {
            if (resultCode == IntentConst.RESULT_DELETED) {

                BaLog.i("글 삭제후 돌아옴");
            }
        }
    }
}
