package com.angel.black.baframework.ui.view.switchview;

import android.content.Context;
import android.view.View;
import android.widget.ToggleButton;

import com.angel.black.baframework.R;

/**
 * Created by KimJeongHun on 2016-08-18.
 */
public class SwitchCompatHoneyCome extends SwitchViewCompat {

    public SwitchCompatHoneyCome(Context context) {
        super(context);
    }

    @Override
    protected View makeView() {
        mView = (ToggleButton) View.inflate(mContext, R.layout.toggle_button_view, null);
        return mView;
    }
}
