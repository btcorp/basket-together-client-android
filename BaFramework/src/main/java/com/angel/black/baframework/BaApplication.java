package com.angel.black.baframework;

import android.app.Application;

import com.angel.black.baframework.network.HttpAPIRequester;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public abstract class BaApplication extends Application {
    public static boolean debug = true;

    private static BaApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public synchronized static BaApplication getInstance() {
        return instance;
    }

    public abstract HttpAPIRequester.HttpRequestStrategy getHttpRequestStrategy();
}
