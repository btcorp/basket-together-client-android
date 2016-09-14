package com.angel.black.baskettogether.recruit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.view.imageview.RoundedImageView;

/**
 * Created by KimJeongHun on 2016-09-04.
 */
public class RecruitAttendeeView extends LinearLayout {
    private BaseActivity mActivity;

    private RoundedImageView mAttendeeImage;
    private TextView mAttendeeName;

    public RecruitAttendeeView(Context context) {
        this(context, null);
    }

    public RecruitAttendeeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mActivity = (BaseActivity) context;

        inflate(mActivity, R.layout.recruit_atendee_view, this);

        mAttendeeImage = (RoundedImageView) findViewById(R.id.attendee_image);
        mAttendeeName = (TextView) findViewById(R.id.attendee_name);
    }

    public void setName(String name) {
        this.mAttendeeName.setText(name);
    }
}
