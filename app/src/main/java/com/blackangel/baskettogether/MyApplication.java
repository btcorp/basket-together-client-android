package com.blackangel.baskettogether;

import com.blackangel.baframework.BaApplication;
import com.blackangel.baframework.app.constants.ApiInfo;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.network.okhttp.OkHttpClientBuilder;
import com.blackangel.baframework.network.retrofit.BaseRetrofitRunner;

import okhttp3.OkHttpClient;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public class MyApplication extends BaApplication {

    public static OkHttpClient sOkHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.i();
        sDebug = BuildConfig.BUILD_TYPE.toLowerCase().contains("debug");
        sOkHttpClient = new OkHttpClientBuilder()
                .setTimeout(OkHttpClientBuilder.DEFAULT_TIMEOUT)
                .setLogging(BuildConfig.DEBUG)
                .build();

        BaseRetrofitRunner.initGlobalRetrofit(ApiInfo.APP_SERVER_URL, sOkHttpClient);

    }

    @Override
    protected String getAppServerUrl() {
        return BuildConfig.API_SVR_URL;
    }
}
