package com.blackangel.baskettogether.intro;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.blackangel.baframework.core.base.BaseIntroActivity;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.network.retrofit.BaseRetrofitRunner;
import com.blackangel.baskettogether.intro.fragment.IntroFragment;
import com.blackangel.baskettogether.intro.viewmodel.IntroViewModel;
import com.blackangel.baskettogether.user.loader.UserRepository;
import com.blackangel.baskettogether.user.service.UserService;
import com.facebook.CallbackManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IntroActivity extends BaseIntroActivity {

    private IntroViewModel mIntroViewModel;
    private IntroSessionListener mIntroSessionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideToolbar();

        mIntroSessionListener = new IntroSessionListener(getLifecycle());
        mIntroViewModel = ViewModelProviders.of(this).get(IntroViewModel.class);
        mIntroViewModel.setUserRepo(new UserRepository(BaseRetrofitRunner.getGlobalRetrofit().create(UserService.class)));

        Observer<CallbackManager> fbCallbackManagerObserver = new Observer<CallbackManager>() {
            @Override
            public void onChanged(@Nullable CallbackManager callbackManager) {
                MyLog.i("fb callbackManager = " + callbackManager);
            }
        };

        mIntroViewModel.getFbCallbackManager().observe(this, fbCallbackManagerObserver);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIntroViewModel.getFbCallbackManager().getValue().onActivityResult(requestCode, resultCode, data);
    }

    class IntroSessionListener implements LifecycleObserver {

        public IntroSessionListener(Lifecycle lifecycle) {
            lifecycle.addObserver(this);
        }

        // 애노테이션에 의해 자동으로 onCreate 에서 발생됨
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        void onCreate() {
            MyLog.i("IntroActivity observed onCreate");
            mIntroViewModel.getFbCallbackManager().setValue(CallbackManager.Factory.create());
            mIntroViewModel.getUserSession();
        }
    }
}
