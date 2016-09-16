package com.angel.black.baframework.ui.view.switchview;

import android.widget.LinearLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

/**
 * Created by KimJeongHun on 2016-08-18.
 */
public class SwitchView extends LinearLayout {
    protected Context mContext;
    protected SwitchViewCompat mSwitchCompat;

    protected SwitchView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        mSwitchCompat = SwitchViewCompat.createInstance(mContext);

        addView(mSwitchCompat.makeView());
    }

    public boolean isChecked() {
        return mSwitchCompat.isChecked();
    }

    public void setChecked(boolean check) {
        mSwitchCompat.setChecked(check);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener switchChangeListener) {
        mSwitchCompat.setOnCheckedChangeListener(switchChangeListener);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mSwitchCompat.setOnTouchListener(l);
    }

    public CompoundButton getView() {
        return mSwitchCompat.mView;
    }
}
