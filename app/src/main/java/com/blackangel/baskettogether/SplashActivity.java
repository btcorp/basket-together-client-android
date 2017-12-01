package com.blackangel.baskettogether;

import android.os.Bundle;

import com.blackangel.baframework.core.base.BaseSplashActivity;
import com.blackangel.baskettogether.intro.IntroActivity;

/**
 * Created by Finger-kjh on 2017-12-01.
 */

public class SplashActivity extends BaseSplashActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void goIntro() {
        startActivity(IntroActivity.class);
        finish();
    }

    @Override
    protected void configPush() {

    }

    @Override
    protected boolean isNeedShowIntro() {
        return true;
    }

    @Override
    protected void doAfterSplashSkipIntro() {

    }

}
