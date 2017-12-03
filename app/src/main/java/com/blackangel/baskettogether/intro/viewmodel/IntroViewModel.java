package com.blackangel.baskettogether.intro.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.network.ApiProgressListener;
import com.blackangel.baframework.network.retrofit.BaseRetrofitRunner;
import com.blackangel.baframework.sns.SnsLoginResult;
import com.blackangel.baskettogether.user.domain.User;
import com.blackangel.baskettogether.user.loader.UserRepository;
import com.facebook.CallbackManager;

import okhttp3.ResponseBody;

/**
 * Created by kimjeonghun on 2017. 12. 3..
 */

public class IntroViewModel extends ViewModel {

    private MutableLiveData<CallbackManager> mFbCallbackManager;
    private MutableLiveData<User> mLiveDataUser;

    private UserRepository mUserRepo;

    public void setUserRepo(UserRepository userRepo) {
        mUserRepo = userRepo;
    }

    public UserRepository getUserRepo() {
        return mUserRepo;
    }

    public MutableLiveData<CallbackManager> getFbCallbackManager() {
        if(mFbCallbackManager == null) {
            mFbCallbackManager = new MutableLiveData<>();
        }
        return mFbCallbackManager;
    }

    public MutableLiveData<User> getLiveDataUser() {
        if(mLiveDataUser == null) {
            mLiveDataUser = new MutableLiveData<>();
        }
        return mLiveDataUser;
    }

    public void loginFromSns(final ApiProgressListener apiProgressListener, final SnsLoginResult snsLoginResult, final String password) {
        mUserRepo.loginFromSns(apiProgressListener, snsLoginResult, password);
    }

    public void signUpFromSns(ApiProgressListener apiProgressListener, SnsLoginResult snsLoginResult, String password) {
        mUserRepo.signUpFromSns(apiProgressListener, snsLoginResult.getUserKey(), password,
                snsLoginResult.getUserName(), snsLoginResult.getId());
    }

    public void getUserSession() {
        mUserRepo.getUserSession(new BaseRetrofitRunner.ApiModelResultListener<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                MyLog.i();
            }

            @Override
            public void onFail(String s, int i, String s1, Throwable throwable) {

            }
        });
    }
}
