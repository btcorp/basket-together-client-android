package com.angel.black.baframework.media.image;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.media.image.fragment.GalleryFragment;

import java.util.ArrayList;

/**
 * Created by KimJeongHun on 2016-09-23.
 */
public class ImagesPickerActivity extends BaseActivity implements GalleryBuilder.GalleryBuildListener,
        GalleryFragment.GalleryImageDisplayer, GalleryFragment.GalleryImagePickListener {
    private GalleryFragment mGalleryFragment;
    private GalleryFragment.GalleryAlbumAdapter mGalleryAlbumAdatper;
    private int mMode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_gallery);

        mGalleryFragment = (GalleryFragment) getSupportFragmentManager().findFragmentById(R.id.gallery_fragment);
//        mGalleryFragment.setCanSelectCount(6);
//        mGalleryFragment.setGalleryBucketIdAndDisplayImages(GalleryBuilder.FULL_GALLERY_BUCKET_ID);
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }


    @Override
    public void onBuildGalleryAlbumList(ArrayList<GalleryBuilder.GalleryBucketItemInfo> albumList) {
//        mGalleryAlbumAdatper = new GalleryAlbumAdapter(this, R.layout.spinner_item_gallery_album, R.id.tv_spitem, albumList);
//        mGalleryAlbumAdatper.setDropDownViewResource(R.layout.spinner_dropdown_gallery_album_item);
//        mSpinAlbum.setAdapter(mGalleryAlbumAdatper);
//        mSpinAlbum.setSelection(findIndexGalleryAlbum(mCurrentGalleryAlbumId));
    }

    @Override
    public void onBuildGalleryImageListInAlbum(ArrayList<GalleryBuilder.GalleryBucketItemInfo> imageList) {

    }

    @Override
    public void onDisplayImage(String uri, ImageView imgView, Object... extras) {
        imgView.setImageURI(Uri.parse(uri));
    }

    @Override
    public void onPickGalleryImage(GalleryBuilder.GalleryBucketItemInfo item) {

    }

    @Override
    public void onUnPickGalleryImage(GalleryBuilder.GalleryBucketItemInfo item) {

    }


}
