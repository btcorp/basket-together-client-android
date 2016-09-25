package com.angel.black.baskettogether.image;

import android.os.Bundle;
import android.widget.ImageView;

import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.image.BaseImagePickActivity;
import com.angel.black.baskettogether.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by KimJeongHun on 2016-09-25.
 */
public class ImagePickActivity extends BaseImagePickActivity {
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDipslayImageOptions;

    private static final int MAX_IMAGE_THUMBNAIL_WIDTH = 300;
    private static final int MAX_IMAGE_THUMBNAIL_HEIGHT = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_image_pick);

        mImageLoader = ImageLoader.getInstance();

        mDipslayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(getResources().getDrawable(R.color.light_gray))
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    @Override
    public void onDisplayImage(String uri, final ImageView imgView, Object... extras) {
        BaLog.d("uri=" + uri + ", extras=" + extras);

        mImageLoader.displayImage(uri, imgView, mDipslayImageOptions);
    }
}
