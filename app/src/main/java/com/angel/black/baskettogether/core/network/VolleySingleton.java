package com.angel.black.baskettogether.core.network;

/**
 * Created by KimJeongHun on 2016-09-25.
 */
//public class VolleySingleton {
//    private static VolleySingleton mInstance = null;
//    private RequestQueue mRequestQueue;
//    private ImageLoader mImageLoader;
//
//    private VolleySingleton(Context context){
//        mRequestQueue = Volley.newRequestQueue(context);
//        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
//            private final LruCache mCache = new LruCache(10);
//            public void putBitmap(String url, Bitmap bitmap) {
//                mCache.put(url, bitmap);
//            }
//            public Bitmap getBitmap(String url) {
//                return (Bitmap) mCache.get(url);
//            }
//        });
//    }
//
//    public static VolleySingleton getInstance(Context context){
//        if(mInstance == null){
//            mInstance = new VolleySingleton(context);
//        }
//        return mInstance;
//    }
//
//    public RequestQueue getRequestQueue(){
//        return this.mRequestQueue;
//    }
//
//    public ImageLoader getImageLoader(){
//        return this.mImageLoader;
//    }
//}
