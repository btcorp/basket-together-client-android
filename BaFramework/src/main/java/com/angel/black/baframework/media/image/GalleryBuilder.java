package com.angel.black.baframework.media.image;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by KimJeongHun on 2016-07-02.
 */
public class GalleryBuilder {
    public interface GalleryBuildListener {
        /**
         * 갤러리 앨범 리스트 데이터를 만들어서 반환한다.
         * @param albumList
         */
        void onBuildGalleryAlbumList(ArrayList<GalleryBucketItemInfo> albumList);

        /**
         * 특정 앨범안에 속해있는 이미지 리스트 데이터를 만들어서 반환한다.
         * @param imageList
         */
        void onBuildGalleryImageListInAlbum(ArrayList<GalleryBucketItemInfo> imageList);
    }

    public static final int TYPE_ALBUM_LIST = 0;
    public static final int TYPE_ALBUM_DETAIL = 1;

    public static final long FULL_GALLERY_BUCKET_ID = 0;

    private Activity mActivity;
    private GalleryBuildListener mGalleryBuildListener;
    private int mBuildType;
    private long mBucketId;

    public GalleryBuilder(Activity activity, GalleryBuildListener listener) {
        this.mActivity = activity;
        this.mGalleryBuildListener = listener;
    }

    public GalleryBuilder setType(int type) {
        this.mBuildType = type;
        return this;
    }

    /**
     * 갤러리 폴더의 이미지를 가져올때 가져올 폴더의 bucketId 셋팅
     * @param bucketId
     * @return
     */
    public GalleryBuilder setBucketId(long bucketId) {
        this.mBucketId = bucketId;
        return this;
    }

    public void build() {
        if(mBuildType == TYPE_ALBUM_LIST) {
            new GalleryAlbumLoader().execute();
        } else {
            new GalleryImageLoader().execute(mBucketId);
        }
    }

    private class GalleryAlbumLoader extends AsyncTask {
        private ArrayList<GalleryBucketItemInfo> mList;

        @Override
        protected void onPreExecute() {
            mList = new ArrayList<>();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            totalGalleryList();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (mGalleryBuildListener != null) {
                mGalleryBuildListener.onBuildGalleryAlbumList(mList);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        /**
         * 전체 사진 폴더
         */
        public void totalGalleryList() {
            String[] projection = { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
            String BUCKET_THUMBNAIL_SORT_ORDER = MediaStore.Images.Media.DATE_TAKEN + " DESC";

            Cursor c = mActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, BUCKET_THUMBNAIL_SORT_ORDER);

            if (c.moveToFirst()) {
                GalleryBucketItemInfo item = new GalleryBucketItemInfo();
                item.id = 0;
                item.name = "전체 앨범";
                item.count = c.getCount();
                item.path = c.getString(1);
                item.orientation = c.getInt(2);
                mList.add(item);
            }
            c.close();

            getGalleryList();
        }

        /**
         * 각 갤러리 리스트 폴더
         */
        public void getGalleryList() {

            String[] PROJECTION_BUCKET = {MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
            String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
            String BUCKET_ORDER_BY = "MAX(datetaken) DESC";

            Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            Cursor c = mActivity.getContentResolver().query(images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);

            while (c.moveToNext()) {
                GalleryBucketItemInfo item = new GalleryBucketItemInfo();
                item.id = c.getLong(0);
                item.name = c.getString(1);
                populateGroupSubInfo(item);
                mList.add(item);
            }
            c.close();
        }

        /**
         * 갤러리 리스트 폴더 별 첫 사진
         * @param item
         */
        public void populateGroupSubInfo(GalleryBucketItemInfo item) {
            String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
            String selection = MediaStore.Images.Media.BUCKET_ID + "=?";
            String[] selectionArgs = new String[] {	"" + item.id };
            String BUCKET_THUMBNAIL_SORT_ORDER = MediaStore.Images.Media.DATE_TAKEN + " DESC";

            Cursor c = mActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, BUCKET_THUMBNAIL_SORT_ORDER);
            item.count = c.getCount();

            if (c.moveToFirst()) {
                item.path = c.getString(1);
                item.orientation = c.getInt(2);
            }

            c.close();
        }
    }

    private class GalleryImageLoader extends AsyncTask<Long, Void, Void> {
        private ArrayList<GalleryBucketItemInfo> mList;

        @Override
        protected void onPreExecute() {
            mList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Long... params) {
            if (params[0] == FULL_GALLERY_BUCKET_ID) {
                getTotalGalleryImages();
            } else {
                getGalleryImages();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mGalleryBuildListener != null) {
                mGalleryBuildListener.onBuildGalleryImageListInAlbum(mList);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        private void getTotalGalleryImages() {
            String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
            String BUCKET_THUMBNAIL_SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";

            Cursor c = mActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, BUCKET_THUMBNAIL_SORT_ORDER);

            if(c != null) {
                while (c.moveToNext()) {
                    GalleryBucketItemInfo item = new GalleryBucketItemInfo();
                    item.id = c.getLong(0);
                    item.path = c.getString(1);
                    mList.add(item);
                }

                c.close();
            }
        }

        private void getGalleryImages() {
            String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
            String selection = MediaStore.Images.Media.BUCKET_ID + "=?";
            String[] selectionArgs = new String[] {"" + mBucketId};

            String BUCKET_THUMBNAIL_SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";

            Cursor c = mActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, BUCKET_THUMBNAIL_SORT_ORDER);

            if(c != null) {
                while (c.moveToNext()) {
                    GalleryBucketItemInfo item = new GalleryBucketItemInfo();
                    item.id = c.getLong(0);
                    item.path = c.getString(1);

                    mList.add(item);
                }

                c.close();
            }
        }
    }

    private class GalleryThumbnailLoader extends AsyncTask<String, Void, Void> {
        private ArrayList<GalleryThumbnailItemInfo> mList;

        @Override
        protected void onPreExecute() {
            mList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(String... params) {
            if (params[0].equals(FULL_GALLERY_BUCKET_ID)) {
                getTotalGalleryImages();
            } else {
                getGalleryThumbnails();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        private void getTotalGalleryImages() {
            String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
            String BUCKET_THUMBNAIL_SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";

            Cursor c = mActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, BUCKET_THUMBNAIL_SORT_ORDER);

            if(c != null) {
                while (c.moveToNext()) {
                    GalleryThumbnailItemInfo item = new GalleryThumbnailItemInfo();
                    item.id = c.getString(0);
//                    item.path = c.getString(1);
                    mList.add(item);
                }

                c.close();
            }
        }

        private void getGalleryThumbnails() {
            String[] projection = { MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA};

            String BUCKET_THUMBNAIL_SORT_ORDER = MediaStore.Images.Thumbnails.IMAGE_ID + " DESC";

            Cursor c = mActivity.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    projection, null, null, BUCKET_THUMBNAIL_SORT_ORDER);

            if(c != null) {
                while (c.moveToNext()) {
                    GalleryThumbnailItemInfo item = new GalleryThumbnailItemInfo();
                    item.id = c.getString(0);
                    item.imageId = c.getString(1);

                    mList.add(item);
                }

                c.close();
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    public static class GalleryBucketItemInfo {
        public long id;
        public String name;
        public int count;
        public String path;
        public int orientation;		// 회전각도

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return "id=" + id + ", name=" + name + ", path=" + path;
        }
    }

    public class GalleryThumbnailItemInfo {
        public String id;
        public String imageId;
        public Bitmap bitmap;
    }
}
