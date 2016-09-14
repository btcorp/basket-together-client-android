package com.angel.black.baskettogether.core;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.angel.black.baframework.BaApplication;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.network.HttpAPIRequester;
import com.angel.black.baframework.preference.MyPreferenceManager;
import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.BuildConfig;
import com.angel.black.baskettogether.core.network.MyRequestStrategy;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.core.preference.KeyConst;
import com.angel.black.baskettogether.image.LruBitmapCache;
import com.angel.black.baskettogether.user.UserHelper;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public class MyApplication extends BaApplication {
    public static boolean debug = BuildConfig.DEBUG;

    private static MyApplication sInstance;

    //TODO 발리 안씀 현재
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static String serverUrl;

    public static HttpAPIRequester.HttpRequestStrategy mHttpRequestStrategy;

    @Override
    public void onCreate() {
        super.onCreate();
        BaLog.i();

        serverUrl = debug ? ServerURLInfo.DEV_SERVER_URL : ServerURLInfo.PRODUCT_SERVER_URL;

        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(8388608));
        sInstance = this;

        MyPreferenceManager pm = new MyPreferenceManager(this);
        String savedAccessToken = pm.loadString(KeyConst.SAVED_ACCESS_TOKEN);

        if(!StringUtil.isEmptyString(savedAccessToken)) {
            UserHelper.userAccessToken = savedAccessToken;
        }

        mHttpRequestStrategy = new MyRequestStrategy();
    }

    public synchronized static MyApplication getInstance() {
        return sInstance;
    }

    @Override
    public HttpAPIRequester.HttpRequestStrategy getHttpRequestStrategy() {
        if(mHttpRequestStrategy == null) {
            mHttpRequestStrategy = new MyRequestStrategy();
        }

        return mHttpRequestStrategy;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
