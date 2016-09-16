package com.angel.black.baframework.ui.view.switchview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Switch;

import com.angel.black.baframework.R;

/**
 * Created by KimJeongHun on 2016-08-18.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SwitchCompatIcecreamSandwich extends SwitchViewCompat {

    public SwitchCompatIcecreamSandwich(Context context) {
        super(context);
    }

    @Override
    protected View makeView() {
        mView = (Switch) View.inflate(mContext, R.layout.switch_view, null);
        return mView;
    }
}
