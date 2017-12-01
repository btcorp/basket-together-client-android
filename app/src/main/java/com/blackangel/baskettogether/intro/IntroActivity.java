package com.blackangel.baskettogether.intro;

import android.os.Bundle;

import com.blackangel.baframework.core.base.BaseIntroActivity;
import com.blackangel.baskettogether.intro.fragment.IntroFragment;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IntroActivity extends BaseIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideToolbar();
    }

    @Override
    protected void goNext() {
        replaceFragment(mContentsLayout.getId(), IntroFragment.newInstance());
    }

    @Override
    protected boolean isHandleInnerViews() {
        return true;
    }

    @Override
    protected int[] getPageResIds() {
        return new int[] {
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_dark,
        };
    }
}
