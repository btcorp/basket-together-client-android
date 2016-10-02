package com.angel.black.baframework.media.image.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import com.angel.black.baframework.R;
import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.core.base.BaseFragment;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.image.BaseImagePickActivity;
import com.angel.black.baframework.media.image.GalleryBuilder;
import com.angel.black.baframework.security.PermissionConstants;
import com.angel.black.baframework.ui.dialog.PermissionConfirmationDialog;
import com.angel.black.baframework.util.ScreenUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by KimJeongHun on 2016-07-01.
 */
public class GalleryFragment extends BaseFragment implements AdapterView.OnItemClickListener, GalleryBuilder.GalleryBuildListener,
                    PermissionConfirmationDialog.OnPermissionConfirmationDialogListener {
    private static final String ARG_GALLERY_BUCKET_ID = "galleryBucketId";
    private static final String ARG_CAN_SELECT_COUNT = "canSelectCount";

    public static final int MODE_CAMERA = 0;
    public static final int MODE_GALLERY = 1;

    private GridView mPhotoGridview;
    private GalleryGridAdapter mGridAdapter;
    private GalleryImagePickListener mGalleryImagePickListener;
    private ImageDisplayer mImageDisplayer;
    private View mEmptyView;

    /** 현재 선택된 갤러리 폴더 id. 디폴트 - 전체앨범*/
    private long mGallaryBucketId;

    /** 현재 선택된 갤러리 폴더명 */
    private String mGalleryBucketName;

    private boolean mInitialLoadedGallery;

    /** 선택할 수 있는 숫자 */
    private int mCanSelectCount;

    /** 갤러리 아이템 클릭 락 */
    private boolean mLockItemClick;

//    /** 한개 이미지를 바꾸는 모드 여부 */
//    private boolean mOneImageChange;

    public void setLockItemClick(boolean lockItemClick) {
        this.mLockItemClick = lockItemClick;
    }

    public void setGalleryBucketName(String galleryBucketName) {
        this.mGalleryBucketName = galleryBucketName;
    }

    public String getGalleryBucketName() {
        BaLog.d("mGalleryBucketName=" + mGalleryBucketName);
        return mGalleryBucketName;
    }

    public long getCurrentGalleryBucketId() {
        return mGallaryBucketId;
    }

    public void setCanSelectCount(int canSelectCount) {
        this.mCanSelectCount = canSelectCount;
    }

    public boolean isLockItemClick() {
        return mLockItemClick;
    }

    public static GalleryFragment newInstance(long galleryBucketId, int canSelectCount) {
        BaLog.d("galleyBucketId=" + galleryBucketId + ", canSelectCount=" + canSelectCount);
        GalleryFragment fragment = new GalleryFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_GALLERY_BUCKET_ID, galleryBucketId);
        args.putInt(ARG_CAN_SELECT_COUNT, canSelectCount);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BaLog.i();

        showProgress();
        mGalleryImagePickListener = (GalleryImagePickListener) getActivity();
        mImageDisplayer = (ImageDisplayer) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaLog.i("savedInstanceState=" + savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
            mCanSelectCount = args.getInt(ARG_CAN_SELECT_COUNT, 6);
            mGallaryBucketId = args.getLong(ARG_GALLERY_BUCKET_ID, GalleryBuilder.FULL_GALLERY_BUCKET_ID);
        } else {
            mCanSelectCount = 6;
        }

        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.request_write_storage_permission_for_gallery,
                    PermissionConstants.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION, true);
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BaLog.i("savedInstanceState=" + savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        mPhotoGridview = (GridView) view.findViewById(R.id.photo_gridview);
        mEmptyView = view.findViewById(R.id.txt_empty_gallery);

        if(mGridAdapter == null) {
            mGridAdapter = new GalleryGridAdapter((BaseActivity) getActivity(), mGallaryBucketId);
        }

        mPhotoGridview.setAdapter(mGridAdapter);
        mPhotoGridview.setOnItemClickListener(this);

        BaLog.i("mInitialLoadedGallery=" + mInitialLoadedGallery);

        if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadGalleryAlbums();
            initGallery();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAllowedPermissionConfirm(int permissionRequestCode) {

    }

    @Override
    public void onDenyedPermissionConfirm(int permissionRequestCode) {
        if(permissionRequestCode == PermissionConstants.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
//            getActivity().finish();
            ((BaseImagePickActivity) getActivity()).onDenyedPermissionConfirmDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        BaLog.i("pemissions=" + permissions.length + ", grantResults=" + grantResults.length);
        if (requestCode == PermissionConstants.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 퍼미션 거부
                if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    showOkDialogNotCancelable(0, R.string.error_write_storage_not_granted_permission, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    });
                } else if (permissions[0].equals(Manifest.permission.CAMERA)) {
//                    showGallery();
                }
            } else {
                // 퍼미션 허용
                if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // 저장공간 접근 허용 후에 파일접근을 테스트로 해보아서 에러나는 경우 앱 재시작 시킴

//                    if(FileUtil.testFileAccess(this)) {
//                        loadGalleryAlbums();
//                        mGalleryFragment.loadGalleryImages();
//                    } else {
//                        showDialogAppRestart(R.string.app_restart_for_permission_on_save_image_file);
//                    }
                }
//                  else if (permissions[0].equals(Manifest.permission.CAMERA)) {
//                    mCameraFragment.openCamera();
//                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void initGallery() {
        BaLog.i();
        ((BaseImagePickActivity) getActivity()).setMode(BaseImagePickActivity.Mode.GALLERY);

        if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadGalleryImages();
        }
    }

    public void loadGalleryAlbums() {
        BaLog.i();
        new GalleryBuilder(getActivity(), this).setType(GalleryBuilder.TYPE_ALBUM_LIST).build();
    }

    @Override
    public void onResume() {
        super.onResume();
        BaLog.i();
    }

    /**
     * 특정 갤러리 폴더로 셋팅한다. 그리고 갤러리 이미지 로드한다.
     * @param galleryBucketId
     */
    public void setGalleryBucketIdAndDisplayImages(long galleryBucketId) {
        if(mGallaryBucketId >= 0 && mGallaryBucketId == galleryBucketId) {
            return;
        }

        this.mGallaryBucketId = galleryBucketId;
        BaLog.d("mGalleryBucketId=" + mGallaryBucketId);

        loadGalleryImages();
    }

    public void loadGalleryImages() {
        BaLog.d("mGalleryBucketId=" + mGallaryBucketId + ", mGalleryBucketName=" + mGalleryBucketName);
        mGridAdapter.setBucketId(mGallaryBucketId);
        new GalleryBuilder(getActivity(), this).setType(GalleryBuilder.TYPE_ALBUM_DETAIL).setBucketId(mGallaryBucketId).build();
    }

    /**
     * 현재 갤러리 폴더안의 아이템들을 갱신한다.
     */
    public void refreshCurrentBucket() {
        BaLog.d();
        loadGalleryImages();
    }

    public void updateDeletedImages(ArrayList<String> delImagePathList) {
        mGridAdapter.updateSelected(delImagePathList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
        BaLog.d("mLockItemClick=" + mLockItemClick + ", position=" + position + ", mCanSelectCount=" + mCanSelectCount);
        if(mLockItemClick) return;

        if (mGridAdapter.preTrySelected(position) > mCanSelectCount) {
            if(mCanSelectCount == 1) {
                // 한장만 선택가능한 모드일때
                mGridAdapter.clearSelectedInfo();
            } else {
                String msg = String.format(getResources().getString(R.string.overflow_img_cnt), Integer.toString(mCanSelectCount));
                ((BaseActivity) getActivity()).showToast(msg);
                return;
            }
        }

        if(mGridAdapter.setSelected(position)) {
            if(mGalleryImagePickListener != null) {
                mGalleryImagePickListener.onPickGalleryImage((GalleryBuilder.GalleryBucketItemInfo) mGridAdapter.getItem(position));
            }
        } else {
            if(mGalleryImagePickListener != null) {
                mGalleryImagePickListener.onUnPickGalleryImage((GalleryBuilder.GalleryBucketItemInfo) mGridAdapter.getItem(position));
            }
        }

        scrollGridViewSeletedPosition(position);
    }

    /**
     * 선택한 갤러리 아이템이 화면 스크롤 중앙에 오도록 자동스크롤한다.
     * @param position
     */
    private void scrollGridViewSeletedPosition(int position) {
        int middlePosition = (mPhotoGridview.getFirstVisiblePosition() + mPhotoGridview.getLastVisiblePosition()) / 2;

        BaLog.d("click Position=" + position + ", firstPosition=" + mPhotoGridview.getFirstVisiblePosition() + ", lastPosition=" + mPhotoGridview.getLastVisiblePosition()
                + ", middlePosition=" + middlePosition);

        int childHeight = mPhotoGridview.getChildAt(0).getHeight();
        int subtract = position - middlePosition;

        if(subtract >= 0) {
            // 중앙 포지션보다 아래방향 클릭
            middlePosition--;
            int diffRowNum = (position - middlePosition) / 3;

            mPhotoGridview.smoothScrollBy(childHeight * diffRowNum, 300);
        } else {
            // 중앙 포지션보다 윗방향 클릭
            middlePosition++;
            int diffRowNum = (middlePosition - position) / 3;

            mPhotoGridview.smoothScrollBy(-(childHeight * diffRowNum), 300);
        }
    }

    @Override
    public void onBuildGalleryAlbumList(ArrayList<GalleryBuilder.GalleryBucketItemInfo> albumList) {
        ((BaseImagePickActivity) getActivity()).makeGalleryAlbumView(albumList);
        BaLog.i();

    }

    @Override
    public void onBuildGalleryImageListInAlbum(ArrayList<GalleryBuilder.GalleryBucketItemInfo> imageList) {
        BaLog.d("imageList.size=" + imageList.size());

        mGridAdapter.setList(imageList);

        if (imageList.size() <= 0) {
            mPhotoGridview.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        } else {
            if(mPhotoGridview.getVisibility() != View.VISIBLE) {
                mEmptyView.setVisibility(View.INVISIBLE);
                mPhotoGridview.setVisibility(View.VISIBLE);
            }
        }

        hideProgress();

        // 최초 갤러리 데이터 표시는 한번 갤러리 표시가 초기화 되면 함
        // 왜냐하면 최초 액티비티 초기화 시에는 갤러리 아이템 뷰의 사이즈가 0으로 리턴돼서
        // 썸네일 사이즈로 캐시가 안됨
        // 따라서 액티비티가 초기화 되고 onWindowFocusChanged 에서 최초 갤러리 표시함
        if(mInitialLoadedGallery) {     // 이미 한번 갤러리 표시 초기화된 상태에서 다시 갤러리 앨범 불러왔을 때
            mGridAdapter.notifyDataSetChanged();
            scrollToTop();      // 갤러리 폴더가 바뀌었으므로 제일 상위로 스크롤
        } else {
            mGridAdapter.notifyDataSetChanged();
        }
    }

    private void scrollToTop() {
        mPhotoGridview.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPhotoGridview.setSelection(0);
            }
        }, 100);
    }

    public void addSelectedListFromCamera(String filepath) {
        mGridAdapter.addSelectedListFromCamera(filepath);
    }

    public void setDeselected(GalleryBuilder.GalleryBucketItemInfo item) {
        mGridAdapter.setDeselected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        BaLog.i();
    }

    @Override
    public void onStop() {
        super.onStop();
        BaLog.i();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaLog.i();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BaLog.i();
    }

    /**
     * 최초 한번 갤러리가 로드 되었는지 여부
     * @return
     */
    public boolean isInitialLoadedGallery() {
        BaLog.d("mGridAdapter=" + mGridAdapter + ", mInitialLoadedGallery=" + mInitialLoadedGallery);
        return mInitialLoadedGallery;
    }

    public void initGalleryAdapter() {
        BaLog.d("mGridAdapter=" + mGridAdapter + ", mInitialLoadedGallery=" + mInitialLoadedGallery);

        mGalleryHandler.sendEmptyMessage(0);
    }

    private Handler mGalleryHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(mGridAdapter.getCount() <= 0) {
                mGalleryHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
//                mLoadingProgress.setVisibility(View.GONE);
                hideProgress();
                mGridAdapter.notifyDataSetChanged();
                mInitialLoadedGallery = true;
            }
        }
    };

    public boolean isCanSelectOneImage() {
        return mCanSelectCount == 1;
    }

    public interface GalleryImagePickListener {
        void onPickGalleryImage(GalleryBuilder.GalleryBucketItemInfo item);

        void onUnPickGalleryImage(GalleryBuilder.GalleryBucketItemInfo item);
    }

    /**
     * 갤러리 이미지 표시 인터페이스
     */
    public interface ImageDisplayer {
        void displayImage(String uri, ImageView imgView, Object... extras);
    }

    public class GalleryGridAdapter extends BaseAdapter {
        private BaseActivity mActivity;

        private ArrayList<GalleryBuilder.GalleryBucketItemInfo> mItemList = new ArrayList<>();
        private HashMap<Long, ItemSelectedInfoInBucket> mSelectedMap;     // 갤러리앨범ID 로 담는 앨범당 선택정보 맵
        private ArrayList<String> mSeqList;  // 패스명으로 담는 선택한 순서 리스트
        private int mItemWidth;

        private long mBucketId;   // 상위 폴더 id

//        private int[] mSeqImgIds = {
//                R.drawable.icobtn2_check_num1,
//                R.drawable.icobtn2_check_num2,
//                R.drawable.icobtn2_check_num3,
//                R.drawable.icobtn2_check_num4,
//                R.drawable.icobtn2_check_num5,
//                R.drawable.icobtn2_check_num6 };

        public GalleryGridAdapter(BaseActivity activity, long bucketId) {
            mActivity = activity;
            mBucketId = bucketId;
            mSelectedMap = new HashMap<>();
            mSeqList = new ArrayList<>();
            mItemWidth = (ScreenUtil.getScreenWidth(mActivity) - ScreenUtil.convertDpToPixel(mActivity, 8)) / 3;
            BaLog.d("mItemWidth=" + mItemWidth);
        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = mActivity.getLayoutInflater().inflate(R.layout.fragment_gallery_grid_adapter, null);

                holder = new ViewHolder();
                holder.mGridImg = (ImageView) convertView.findViewById(R.id.img_view);
                holder.mCheck = (CheckBox) convertView.findViewById(R.id.check_number);

                ViewGroup.LayoutParams params = holder.mGridImg.getLayoutParams();
                params.width = mItemWidth;
                holder.mGridImg.setLayoutParams(params);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            GalleryBuilder.GalleryBucketItemInfo item = mItemList.get(position);
            String path = item.path;

            try {
                final String uri = Uri.decode(Uri.fromFile(new File(path)).toString());
                final ImageView imgView = holder.mGridImg;
                imgView.setVisibility(View.VISIBLE);

                imgView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

                final int width = imgView.getMeasuredWidth();
                final int height = imgView.getMeasuredHeight();

                // 어댑터 갱신 시 이미 로딩되어 있는 이미지는 재 로딩 하지 않는다. (깜빡거림 방지)
//                BaLog.d("holder.mGridImg.getTag()=" +  holder.mGridImg.getTag() + ", uri=" + uri);

                if (imgView.getTag() == null || !imgView.getTag().equals(uri)) {
                    BaLog.d("mGridImg width=" + width + ", height=" + height);

                    //TODO 이미지 로더 실제로 표시
                    mImageDisplayer.displayImage(uri, imgView);

                    imgView.setTag(uri);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            ItemSelectedInfoInBucket selectedCollection = mSelectedMap.get(mBucketId);
            ItemSelectedInfoItem selectedInfo = selectedCollection.map.get(position);

            try {
                if (selectedInfo != null && selectedInfo.selected) {
                    if(!selectedInfo.path.equals(path)) {
                        // 기존에 선택했던 선택정보의 패스가 현재 인덱스의 갤러리 아이템 패스와 같지 않으면
                        // 사진이 추가됨으로 인해 인덱스가 바뀐것
                        selectedInfo.selected = false;
                        selectedInfo.path = path;
                        selectedCollection.map.put(position, selectedInfo);
                        selectImage(holder.mGridImg, holder.mCheck, false, 0);

                    } else {
                        selectImage(holder.mGridImg, holder.mCheck, true, findSelectedSequenceIndex(selectedInfo.path));
                    }
                } else if (findSelectedSequenceIndex(path) >= 0) {
                    selectedInfo = new ItemSelectedInfoItem(true, path);
                    selectedCollection.map.put(position, selectedInfo);
                    selectImage(holder.mGridImg, holder.mCheck, true, findSelectedSequenceIndex(path));
                } else {
                    selectImage(holder.mGridImg, holder.mCheck, false, 0);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                selectedInfo.selected = false;
                selectedInfo.path = null;
                selectedCollection.map.put(position, selectedInfo);
                selectImage(holder.mGridImg, holder.mCheck, false, 0);
            }

            return convertView;
        }

        private void selectImage(ImageView imgView, CheckBox check, boolean select, int selectSequenceIndex) {
            if(select) {
                if(mCanSelectCount == 1) {
                    check.setBackgroundResource(android.R.drawable.checkbox_on_background);
                } else {
//                    check.setBackgroundResource(mSeqImgIds[selectSequenceIndex]);
                    //TODO 이미지 순서 뱃지
                }
                imgView.setBackgroundResource(R.drawable.bg_border_main_color);
            } else {
                check.setBackgroundResource(android.R.color.transparent);
                imgView.setBackgroundResource(android.R.color.transparent);
                imgView.setPadding(0, 0, 0, 0);
            }
        }

        private int findSelectedSequenceIndex(String path) {
            for(int i = 0; i < mSeqList.size(); i++) {
                if(mSeqList.get(i).equals(path)) {
                    return i;
                }
            }

            return -1;
        }

        public void setList(ArrayList<GalleryBuilder.GalleryBucketItemInfo> list) {
            mItemList = list;
        }

        public void setBucketId(long bucketId) {
            mBucketId = bucketId;
            initSelectedInfoInBucket();
        }

        public void initSelectedInfoInBucket() {
            BaLog.i();
            if(mSelectedMap.get(mBucketId) == null) {
                mSelectedMap.put(mBucketId, new ItemSelectedInfoInBucket());
            }
        }

        /**
         * 미리 선택/선택해제를 가상으로 해보고 선택된 가상 결과 카운트를 반환
         * @param position
         * @return
         */
        public int preTrySelected(int position) {
            ItemSelectedInfoItem selectedInfo = mSelectedMap.get(mBucketId).map.get(position);

            if (selectedInfo != null && selectedInfo.selected) {
                // 선택되어 있음
                BaLog.d("선택되있음 getSelectedCount - 1=" + (getSelectedCount() - 1));
                return getSelectedCount() - 1;
            } else {
                // 선택안되어 있음
                BaLog.d("선택 안되있음 getSelectedCount + 1=" + (getSelectedCount() + 1));
                return getSelectedCount() + 1;
            }
        }

        private int getSelectedCount() {
            return mSeqList.size();
        }
        /**
         * 특정 갤러리 아이템을 선택 해제 처리한다. 모든 버킷아이디를 다 검색해야 함
         * @param item
         */
        public void setDeselected(GalleryBuilder.GalleryBucketItemInfo item) {
            Set<Long> keySet = mSelectedMap.keySet();

            Iterator<Long> iter = keySet.iterator();

            while(iter.hasNext()) {
                long bucketId = iter.next();

                ItemSelectedInfoInBucket selectedCollection = mSelectedMap.get(bucketId);
                Set<Integer> keyset2 = selectedCollection.map.keySet();

                Iterator<Integer> iter2 = keyset2.iterator();
                while(iter2.hasNext()) {
                    int position = iter2.next();

                    ItemSelectedInfoItem selectedInfo = selectedCollection.map.get(position);

                    if(item.path.equals(selectedInfo.path) && selectedInfo.selected) {
                        setDeselected(position, selectedCollection, selectedInfo);
                        return;
                    }
                }
            }
        }

        /**
         * 아이템 선택 처리
         * @param position
         * @return 선택되는지 선택해제되는지 여부
         */
        public boolean setSelected(int position) {
            ItemSelectedInfoInBucket selectedCollection = mSelectedMap.get(mBucketId);
            ItemSelectedInfoItem selectedInfo = selectedCollection.map.get(position);

            if(selectedInfo == null) {
                selectedInfo = new ItemSelectedInfoItem();
            }

            if(!selectedInfo.selected) {
                // 선택
                selectedInfo.selected = true;
                selectedInfo.path = mItemList.get(position).path;
                selectedCollection.map.put(position, selectedInfo);

                addSelectedList(selectedInfo.path);

                notifyDataSetChanged();
                return true;
            } else {
                // 선택해제
                setDeselected(position, selectedCollection, selectedInfo);

                notifyDataSetChanged();
                return false;
            }
        }

        private void setDeselected(int position, ItemSelectedInfoInBucket selectedCollection, ItemSelectedInfoItem selectedInfo) {
            mSeqList.remove(selectedInfo.path);

            selectedInfo.selected = false;
            selectedInfo.path = null;
            selectedCollection.map.put(position, selectedInfo);

            notifyDataSetChanged();
        }

        /**
         * 선택된 패스명 리스트에 추가한다.
         * 이렇게 추가를 해주어야 선택된 아이템 처리 및 숫자표시가 제대로 된다.
         *
         * @param filepath
         */
        public void addSelectedList(String filepath) {
            mSeqList.add(filepath);
        }

        /**
         * 선택된 패스명 리스트에 추가한다. (카메라로부터)
         * 이렇게 추가를 해주어야 선택된 아이템 처리 및 숫자표시가 제대로 된다.
         *
         * @param filepath
         */
        public void addSelectedListFromCamera(String filepath) {
            mSeqList.add(filepath);

            // 전체앨범에 넣어놓는다.?? - 음수 인덱스로..
            Map fullAlbumMap = mSelectedMap.get(GalleryBuilder.FULL_GALLERY_BUCKET_ID).map;
            fullAlbumMap.put(-fullAlbumMap.size(), new ItemSelectedInfoItem(true, filepath));
        }

        public void updateSelected(ArrayList<String> delImagePathList) {
            Set<Long> bucketIdSet = mSelectedMap.keySet();

            Iterator<Long> bucketIdIter = bucketIdSet.iterator();

            while(bucketIdIter.hasNext()) {
                long bucketId = bucketIdIter.next();

                ItemSelectedInfoInBucket selectedCollection = mSelectedMap.get(bucketId);
                Set<Integer> positionSetInBucket = selectedCollection.map.keySet();

                Iterator<Integer> positionIterInBucket = positionSetInBucket.iterator();

                while(positionIterInBucket.hasNext()) {
                    int positionInBucket = positionIterInBucket.next();

                    ItemSelectedInfoItem selectedInfo = selectedCollection.map.get(positionInBucket);

                    // 파라미터로 주어진 패스명을 반복해 돌며 삭제된 것은 해제한다.
                    for(String updatedImagePath : delImagePathList) {
                        if(updatedImagePath.equals(selectedInfo.path) && selectedInfo.selected) {
                            setDeselected(positionInBucket, selectedCollection, selectedInfo);
                            break;
                        }
                    }
                }
            }
        }

        public void clearSelectedInfo() {
            mSelectedMap.clear();
            initSelectedInfoInBucket();
            mSeqList.clear();
        }

        public class ViewHolder {
            ImageView mGridImg;
            CheckBox mCheck;
        }

        /**
         * 특정 갤러리 폴더 안에서의 선택 정보
         */
        class ItemSelectedInfoInBucket {
            Map<Integer, ItemSelectedInfoItem> map;

            ItemSelectedInfoInBucket() {
                map = new HashMap<>();
            }
        }

        class ItemSelectedInfoItem {
            public boolean selected;
            public String path;

            public ItemSelectedInfoItem() {

            }

            public ItemSelectedInfoItem(boolean selected, String path) {
                this.selected = selected;
                this.path = path;
            }
        }
    }
}
