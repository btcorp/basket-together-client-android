package com.blackangel.baskettogether.user.loader;

import android.arch.lifecycle.MutableLiveData;

import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.network.ApiProgressListener;
import com.blackangel.baframework.network.retrofit.BaseRetrofitRunner;
import com.blackangel.baframework.sns.SnsLoginResult;
import com.blackangel.baskettogether.user.domain.User;
import com.blackangel.baskettogether.user.service.UserService;

import okhttp3.ResponseBody;

/**
 * Created by kimjeonghun on 2017. 12. 3..
 */

public class UserRepository {
    private UserService mUserService;

    public UserRepository(UserService userService) {
        mUserService = userService;
    }

    public void getUserSession(BaseRetrofitRunner.ApiModelResultListener<ResponseBody> apiModelResultListener) {


        BaseRetrofitRunner.executeAsync(null,false, mUserService.getUserSession(),
                apiModelResultListener);
    }

    public void loginFromSns(final ApiProgressListener apiProgressListener, final SnsLoginResult snsLoginResult, final String password) {
        BaseRetrofitRunner.executeAsync(apiProgressListener, true,
                mUserService.login(snsLoginResult.getUserKey(), password), new BaseRetrofitRunner.ApiModelResultListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        MutableLiveData<User> liveDataUser = new MutableLiveData<>();
                        liveDataUser.setValue(user);
                    }

                    @Override
                    public void onFail(String s, int i, String s1, Throwable throwable) {
                        MyLog.i("errMessage=" + s1);
                        signUpFromSns(apiProgressListener, snsLoginResult.getUserKey(), password,
                                snsLoginResult.getUserName(), snsLoginResult.getId());
                    }
                });
    }

    public void signUpFromSns(ApiProgressListener apiProgressListener, String userId, String password,
                              String nickname, String snsId) {
        BaseRetrofitRunner.executeAsync(apiProgressListener, true,
                mUserService.signUp(userId, password, nickname, User.UserRegType.TYPE_FACEBOOK.getValue()),
                new BaseRetrofitRunner.ApiModelResultListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        MutableLiveData<User> liveDataUser = new MutableLiveData<>();
                        liveDataUser.setValue(user);
                    }

                    @Override
                    public void onFail(String s, int i, String s1, Throwable throwable) {

                    }
                });
    }

}
