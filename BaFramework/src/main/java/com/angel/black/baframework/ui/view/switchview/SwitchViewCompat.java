package com.angel.black.baframework.ui.view.switchview;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import com.angel.black.baframework.util.BuildUtil;

/**
 * Created by KimJeongHun on 2016-08-18.
 */
public abstract class SwitchViewCompat {
    protected Context mContext;
    protected CompoundButton mView;

    protected SwitchViewCompat(Context context) {
        this.mContext = context;
    }

    public static SwitchViewCompat createInstance(Context context) {
        if (BuildUtil.isAboveIcecreamSandwich()) {
            return new SwitchCompatIcecreamSandwich(context);
        } else {
            return new SwitchCompatHoneyCome(context);
        }
    }

    protected abstract View makeView();

    public boolean isChecked() {
        return mView.isChecked();
    }

    public void setChecked(boolean check) {
        mView.setChecked(check);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener switchChangeListener) {
        mView.setOnCheckedChangeListener(switchChangeListener);
    }

    public void setOnTouchListener(View.OnTouchListener onTouchListener) {
        mView.setOnTouchListener(onTouchListener);
    }
}
