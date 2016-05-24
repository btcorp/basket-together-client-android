package com.angel.black.baskettogether.intro;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.BaseActivity;
import com.angel.black.baskettogether.login.LoginActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IntroActivity extends BaseActivity {

    private static final int INTRO_DELAY_MILLIS = 3000;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mIntroRunnable = new Runnable() {
        @Override
        public void run() {
            startActivity(LoginActivity.class);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        mContentView = findViewById(R.id.fullscreen_content);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedIntro(INTRO_DELAY_MILLIS);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedIntro(int delayMillis) {
        mHideHandler.removeCallbacks(mIntroRunnable);
        mHideHandler.postDelayed(mIntroRunnable, delayMillis);
    }
}
