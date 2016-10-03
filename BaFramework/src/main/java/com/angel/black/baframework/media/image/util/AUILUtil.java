package com.angel.black.baframework.media.image.util;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.angel.black.baframework.logger.BaLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.util.List;

/**
 * Created by KimJeongHun on 2016-10-04.
 */
public class AUILUtil {
    private static ImageLoader mImageLoader = ImageLoader.getInstance();

    /**
     * 이미지 캐시에 데이터가 있으면 지우고 로딩 후 새로 캐싱하는 이미지로딩 콜백
     */
    public static class ImageCacheRefreshLoadingListener extends SimpleImageLoadingListener {
        DisplayImageOptions mDisplayImageOptions;
        boolean cacheFound;

        public ImageCacheRefreshLoadingListener(DisplayImageOptions displayImageOptions) {
            mDisplayImageOptions = displayImageOptions;
        }

        @Override
        public void onLoadingStarted(String url, View view) {
            List<String> memCache = MemoryCacheUtils.findCacheKeysForImageUri(url, mImageLoader.getMemoryCache());
            cacheFound = !memCache.isEmpty();
            if (!cacheFound) {
                File discCache = DiskCacheUtils.findInCache(url, mImageLoader.getDiskCache());
                if (discCache != null) {
                    cacheFound = discCache.exists();
                }
            }
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            BaLog.e("ImageLoader", "onLoadingComplete(" + imageUri + ", cacheFound=" + cacheFound + ")");
            // 다시 로드
            if (cacheFound) {
                MemoryCacheUtils.removeFromCache(imageUri, mImageLoader.getMemoryCache());
                DiskCacheUtils.removeFromCache(imageUri, mImageLoader.getDiskCache());

                BaLog.w(ImageLoader.TAG, "캐시 삭제!! > " + imageUri);

                if(view != null) {
                    mImageLoader.displayImage(imageUri, (ImageView) view, mDisplayImageOptions);
                } else {
                    mImageLoader.loadImage(imageUri, mDisplayImageOptions, null);
                }
            }
        }
    }

    /**
     * 특정 이미지Uri 캐시를 삭제한다. (재로딩 위해)
     * @param imageUri
     */
    public static void deleteImageCache(String imageUri) {
        boolean cacheFound = false;

        List<String> memCache = MemoryCacheUtils.findCacheKeysForImageUri(imageUri, mImageLoader.getMemoryCache());
        cacheFound = !memCache.isEmpty();
        if (!cacheFound) {
            File discCache = DiskCacheUtils.findInCache(imageUri, mImageLoader.getDiskCache());
            if (discCache != null) {
                cacheFound = discCache.exists();
            }
        }

        if (cacheFound) {
            MemoryCacheUtils.removeFromCache(imageUri, mImageLoader.getMemoryCache());
            DiskCacheUtils.removeFromCache(imageUri, mImageLoader.getDiskCache());
        }
    }
}
