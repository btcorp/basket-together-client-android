package com.angel.black.baskettogether.common.view;

/**
 * Created by KimJeongHun on 2016-12-10.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.util.ScreenUtil;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserImageNameView extends RelativeLayout implements View.OnClickListener {
    private BaseActivity mActivity;
    private ViewGroup mUserImageLayout;
    private ImageView mUserImage;
    private TextView mUserNameTxtView;

    private String mUserImageUrl;
    private String mUserName;

    public UserImageNameView(Context context) {
        this(context, null);
    }

    public UserImageNameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mActivity = (BaseActivity) context;
        inflate(context, R.layout.user_image_name_view, this);

        mUserImageLayout = (ViewGroup) findViewById(R.id.layout_user_image);
        mUserImage = (ImageView) findViewById(R.id.user_image);
        mUserImage.setOnClickListener(this);
        mUserNameTxtView = (TextView) findViewById(R.id.user_name);
    }

    public void setUserImageAndName(String userImageUrl, String userName, boolean nameSingleLine) {
        this.mUserImageUrl = userImageUrl;
        this.mUserName = userName;

        ImageLoader.getInstance().displayImage(userImageUrl, mUserImage, MyApplication.mDefaultDisplayImgOpts);
        mUserNameTxtView.setText(userName);

        if(nameSingleLine) {
            mUserNameTxtView.setSingleLine();
        }
    }

    public void setUserImageSize(int dp) {
        ViewGroup.LayoutParams layoutParams = mUserImageLayout.getLayoutParams();
        int size = ScreenUtil.convertDpToPixel(mActivity, dp);
        layoutParams.width = layoutParams.height = size;
        mUserImageLayout.setLayoutParams(layoutParams);
    }

    /**
     * 이미지와 이름을 가로로 나열한다. (디폴트는 세로)
     */
    private void setHorizontal(int nameDirection) {
        if(!(nameDirection == Gravity.LEFT || nameDirection == Gravity.RIGHT)) {
            throw new RuntimeException("nameDirection is must Gravity.LEFT or Gravity.RIGHT");
        }

        RelativeLayout.LayoutParams userImgLayoutParams = (RelativeLayout.LayoutParams) mUserImageLayout.getLayoutParams();
        userImgLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);

        if(nameDirection == Gravity.LEFT) {
            userImgLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            userImgLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        }

        mUserImageLayout.setLayoutParams(userImgLayoutParams);

        RelativeLayout.LayoutParams userNameLayoutParams = (RelativeLayout.LayoutParams) mUserNameTxtView.getLayoutParams();

        userNameLayoutParams.addRule(RelativeLayout.BELOW, 0);

        if(nameDirection == Gravity.LEFT) {
            userNameLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.layout_user_image);
            userNameLayoutParams.rightMargin = ScreenUtil.convertDpToPixel(mActivity, 4);
        } else {
            userNameLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.layout_user_image);
            userNameLayoutParams.leftMargin = ScreenUtil.convertDpToPixel(mActivity, 4);
        }

        userNameLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        userNameLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);

        userNameLayoutParams.topMargin = 0;
        userNameLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        mUserNameTxtView.setLayoutParams(userNameLayoutParams);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.user_image) {
//            IntentResolver.goProfileActivity(mActivity, mUserId);
        }
    }

    public void setUserNameAlign(int direction) {
        setHorizontal(direction);
    }

    public void setUserNameTextSize(int sp) {
        mUserNameTxtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }

    public void setUserNameTextBold() {
        mUserNameTxtView.setTypeface(null, Typeface.BOLD);
    }
}
