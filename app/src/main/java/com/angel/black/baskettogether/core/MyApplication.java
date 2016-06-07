package com.angel.black.baskettogether.core;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.angel.black.baskettogether.BuildConfig;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.image.LruBitmapCache;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public class MyApplication extends Application {
    public static boolean debug = BuildConfig.DEBUG;

    private static MyApplication sInstance;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static String serverUrl;

    @Override
    public void onCreate() {
        super.onCreate();

        serverUrl = debug ? ServerURLInfo.DEV_SERVER_URL : ServerURLInfo.PRODUCT_SERVER_URL;

        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(8388608));
        sInstance = this;
    }

    public synchronized static MyApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
