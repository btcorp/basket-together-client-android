package com.angel.black.baskettogether.core;

import com.angel.black.baframework.BaApplication;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.network.HttpAPIRequester;
import com.angel.black.baframework.preference.MyPreferenceManager;
import com.angel.black.baframework.util.BuildUtil;
import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.BuildConfig;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.network.MyRequestStrategy;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.core.preference.KeyConst;
import com.angel.black.baskettogether.user.UserInfoManager;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public class MyApplication extends BaApplication {
    public static boolean debug = BuildConfig.DEBUG;

    private static MyApplication sInstance;

    public static String serverUrl;

    public static HttpAPIRequester.HttpRequestStrategy mHttpRequestStrategy;
    public static DisplayImageOptions mDefaultDisplayImgOpts;

    @Override
    public void onCreate() {
        super.onCreate();
        BaLog.i();

        serverUrl = debug ? ServerURLInfo.DEV_SERVER_URL : ServerURLInfo.PRODUCT_SERVER_URL;

        sInstance = this;

        MyPreferenceManager pm = new MyPreferenceManager(this);
        String savedAccessToken = pm.loadString(KeyConst.SAVED_ACCESS_TOKEN);

        if(!StringUtil.isEmptyString(savedAccessToken)) {
            UserInfoManager.userAccessToken = savedAccessToken;
        }

        mHttpRequestStrategy = new MyRequestStrategy();

        initImageLoader();
    }

    /**
     * Universal Image Loader 의 환경설정을 초기화한다. (최초 1회)
     */
    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(BuildUtil.isLowDevice() ? 2 : 5)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(20 * 1024 * 1024))
                .diskCache(new LimitedAgeDiskCache(StorageUtils.getCacheDirectory(this), 30 * 24 * 60 * 60))	// 캐시유효 기간 한달
                //TODO 상용빌드시 주석
				.writeDebugLogs()
                .build();

        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        mDefaultDisplayImgOpts = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_person_white_24dp)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)    // 기본적으로 2의 배수 샘플링
                .considerExifParams(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
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
}
