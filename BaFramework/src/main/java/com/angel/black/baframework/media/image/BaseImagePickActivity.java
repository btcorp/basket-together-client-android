package com.angel.black.baframework.media.image;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.intent.IntentConstants;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.image.fragment.GalleryFragment;
import com.angel.black.baframework.util.BaPackageManager;
import com.angel.black.baframework.util.ScreenUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KimJeongHun on 2016-09-23.
 */
public class BaseImagePickActivity extends BaseActivity implements
        GalleryFragment.ImageDisplayer, GalleryFragment.GalleryImagePickListener, AdapterView.OnItemSelectedListener {
    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mDipslayImageOptions;

    protected GalleryFragment mGalleryFragment;
    protected Mode mMode;

    protected Spinner mSpinnerAlbum;
    protected GalleryAlbumAdapter mGalleryAlbumAdapter;
    protected SelectedImageInfo mSelectedImageInfo;

    /**
     * 현재 등록(선택)된 이미지(아이템) 수
     */
    protected int mCurRegisteredItemCount;

    public enum Mode {
        GALLERY, CAMERA
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();

        mImageLoader = ImageLoader.getInstance();

        mDipslayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(getResources().getDrawable(R.color.light_gray))
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        mSelectedImageInfo = new SelectedImageInfo();

        initGalleryFragment();
    }

    protected void initGalleryFragment() {
        mGalleryFragment = (GalleryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_gallery);
        initGalleryPickOptions();
    }

    protected void setContentView() {
        setContentView(R.layout.activity_image_pick);
    }

    protected void initGalleryPickOptions() {
        int pickCount = getIntent().getIntExtra(IntentConstants.KEY_IMAGE_PICK_COUNT, 1);
        mGalleryFragment.setCanSelectCount(pickCount);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();

        // 타이틀을 숨기고 앨범 Spinner 를 보여줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    protected void onClickComplete() {
        finishWithReturnData(mSelectedImageInfo.getSelectedImagePathList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_base_image_pick, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_gallery_camera);
        mSpinnerAlbum = (Spinner) MenuItemCompat.getActionView(menuItem);

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.menu_complete) {
            onClickComplete();
            return true;
        }

        return false;
    }

    public void setMode(Mode mode) {
        this.mMode = mode;
        onModeChanged(mode);
    }

    protected void onModeChanged(Mode mode) {

    }

    /**
     * 테스트용. 실제 상속하는 액티비티단에서 오버라이드 해서 이미지 표시 방식을 바꾼다.
     */
    @Override
    public void displayImage(String uri, ImageView imgView, Object... extras) {
        mImageLoader.displayImage(uri, imgView, mDipslayImageOptions);
    }

    @Override
    public void onPickGalleryImage(GalleryBuilder.GalleryBucketItemInfo item) {
        if(mGalleryFragment.isCanSelectOneImage()) {
            mSelectedImageInfo.clear();
        }
        mSelectedImageInfo.put(item.path);
    }

    @Override
    public void onUnPickGalleryImage(GalleryBuilder.GalleryBucketItemInfo item) {
        mSelectedImageInfo.remove(item.path);
    }

    public void makeGalleryAlbumView(ArrayList<GalleryBuilder.GalleryBucketItemInfo> albumList) {
        ViewGroup.LayoutParams layoutParams = mSpinnerAlbum.getLayoutParams();
        layoutParams.width = ScreenUtil.convertDpToPixel(this, 200);
        mSpinnerAlbum.setLayoutParams(layoutParams);

        if(mGalleryAlbumAdapter == null) {
            mGalleryAlbumAdapter = new GalleryAlbumAdapter(this, R.layout.spinner_item_gallery_album, R.id.tv_spitem, albumList);
            mGalleryAlbumAdapter.setDropDownViewResource(R.layout.spinner_dropdown_gallery_album_item);
            mSpinnerAlbum.setAdapter(mGalleryAlbumAdapter);
            mSpinnerAlbum.setOnItemSelectedListener(this);
        } else {
            mGalleryAlbumAdapter.notifyDataSetChanged();
        }

//        spinner.setSelection(findIndexGalleryAlbum(mCurrentGalleryAlbumId));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        BaLog.d("position=" + position);
        if (parent.getId() == R.id.spinner_album) {
            // 스피너 앨범 선택
            GalleryBuilder.GalleryBucketItemInfo item = (GalleryBuilder.GalleryBucketItemInfo) mSpinnerAlbum.getAdapter().getItem(position);
            mGalleryFragment.setGalleryBucketIdAndDisplayImages(item.id);
            mGalleryFragment.setGalleryBucketName(item.name);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 퍼미션 요청 이유 팝업의 취소 버튼 눌렀을 때
     */
    public void onDenyedPermissionConfirmDialog() {
        BaLog.i();
    }

    /**
     * 등록대상 이미지들의 패스 리스트를 호출액티비티에 반환하고 종료한다.
     * @param imagePathList
     */
    protected void finishWithReturnData(ArrayList<String> imagePathList) {
        Intent returnData = new Intent();
        returnData.putStringArrayListExtra(IntentConstants.KEY_IMAGE_PATH_LIST, imagePathList);
//        if(mOneImageChangeIndex >= 0) {
//            // 이미지 변경으로 호출되어 1장만 선택했을 때 변경 대상 인덱스 알려줌
//            returnData.putExtra(IntentConstants.KEY_IMAGE_CHANGE_INDEX, mOneImageChangeIndex);
//        }
        setResult(RESULT_OK, returnData);
        finish();
    }

    public class GalleryAlbumAdapter extends ArrayAdapter<GalleryBuilder.GalleryBucketItemInfo> {
        public GalleryAlbumAdapter(Context context, int resource, int textViewResourceId, List<GalleryBuilder.GalleryBucketItemInfo> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            TextView text;

            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.spinner_item_gallery_album, parent, false);
            } else {
                view = convertView;
            }

            text = (TextView) view.findViewById(R.id.tv_spitem);

            GalleryBuilder.GalleryBucketItemInfo item = getItem(position);
            text.setText(item.name);

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.spinner_dropdown_gallery_album_item, null);

                holder = new ViewHolder();
                holder.mThumbnail = (ImageView) convertView.findViewById(R.id.gallery_album_thumbnail);
                holder.mAlbumName = (TextView) convertView.findViewById(R.id.gallery_album_name);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            GalleryBuilder.GalleryBucketItemInfo item = getItem(position);
            String uri = Uri.decode(Uri.fromFile(new File(item.path)).toString());

//            mImageLoader.displayImage(uri, holder.mThumbnail, mOptions);
            displayImage(uri, holder.mThumbnail);

            holder.mAlbumName.setText(item.name + " ( " + item.count + " )");

            return convertView;
        }

        /**
         * 현재 갤러리 앨범 리스트 중에 "프리마켓" 앨범이 있는지 조사한다.
         * @return
         */
        public boolean containPublicAppAlbum() {
            String appAlbumPath = BaPackageManager.getPublicAppAlbumPath(getContext());

            int count = getCount();
            for(int i = 0; i < count; i++) {
                GalleryBuilder.GalleryBucketItemInfo item = getItem(i);

                if(item.path.contains(appAlbumPath)) {
                    BaLog.d("Contain 프리마켓 앨범!!");
                    return true;
                }
            }

            BaLog.d("not contain 프리마켓 앨범!!");
            return false;
        }

        class ViewHolder {
            ImageView mThumbnail;
            TextView mAlbumName;
        }
    }

    private static class SelectedImageInfo {
        private ArrayList<String> selectedImagePathList;

        SelectedImageInfo() {
            selectedImagePathList = new ArrayList<>();
        }

        public void put(String imagePath) {
            if(selectedImagePathList.contains(imagePath)) {
                return;
            }

            selectedImagePathList.add(imagePath);
        }

        public ArrayList<String> getSelectedImagePathList() {
            return selectedImagePathList;
        }

        public void remove(String imagePath) {
            selectedImagePathList.remove(selectedImagePathList.indexOf(imagePath));
        }

        public void clear() {
            selectedImagePathList.clear();
        }
    }
}
