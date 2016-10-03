package com.angel.black.baframework.media.image.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.angel.black.baframework.intent.IntentConstants;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.util.ScreenUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by KimJeongHun on 2016-07-11.
 */
public class ImageOrderingFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String TAG = ImageOrderingFragment.class.getSimpleName();
    private GridView mGridView;
    private OrderingGridAdapter mGridAdapter;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;

    public static ImageOrderingFragment newInstance(ArrayList<String> imagePathList) {
        ImageOrderingFragment fragment = new ImageOrderingFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(IntentConstants.KEY_IMAGE_PATH_LIST, imagePathList);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoader = ImageLoader.getInstance();
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_camera_alt_white_24dp)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)       // 이미지 회전 알아서 해줌
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_ordering, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아무것도 안함. 하위 뷰의 클릭이벤트를 방지하기 위해 여기서 클릭이벤트 소비함
                BaLog.i();
            }
        });

        mGridView = (GridView) view.findViewById(R.id.ordering_gridview);
        mGridView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGridAdapter = new OrderingGridAdapter((BaseActivity) getActivity(), getArguments().getStringArrayList(IntentConstants.KEY_IMAGE_PATH_LIST));
        mGridView.setAdapter(mGridAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mGridAdapter.isSelected(position)) {
            mGridAdapter.setSelected(position, false);
        } else {
            mGridAdapter.setSelected(position, true);
        }
    }

    public boolean isSelectedAll() {
        return mGridAdapter.isSelectedAll();
    }

    public HashMap<String, Integer> getSequenceMap() {
        return mGridAdapter.getSequenceMap();
    }

    public boolean isSequenceChanged() {
        return mGridAdapter.isSequenceChanged();
    }

    public class OrderingGridAdapter extends BaseAdapter {
        private BaseActivity mActivity;

        private HashMap<String, Integer> mSequenceMap;            // 순서정보 맵

        public HashMap<String, Integer> getSequenceMap() {
            return mSequenceMap;
        }

        private ArrayList<String> mImagePathList;           // 패스명 리스트

        private int[] mSeqImgIds = {
                R.drawable.ic_camera_alt_white_24dp,
                R.drawable.ic_camera_alt_white_24dp,
                R.drawable.ic_camera_alt_white_24dp,
                R.drawable.ic_camera_alt_white_24dp,
                R.drawable.ic_camera_alt_white_24dp,
                R.drawable.ic_camera_alt_white_24dp};

        private boolean isSeqChanged;  // 순서가 변경되었는지 여부

        public OrderingGridAdapter(BaseActivity activity, ArrayList<String> imagePathList) {
            mActivity = activity;
            mImagePathList = imagePathList;
            mSequenceMap = new HashMap<>();

            for(int i=0; i < mImagePathList.size(); i++) {
                String path = mImagePathList.get(i);
                mSequenceMap.put(path, i);
            }
        }

        @Override
        public int getCount() {
            return mImagePathList.size();
        }

        @Override
        public Object getItem(int position) {
            return mImagePathList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mActivity.getLayoutInflater().inflate(R.layout.fragment_gallery_grid_adapter, null);

                holder = new ViewHolder();
                holder.mGridImg = (ImageView) convertView.findViewById(R.id.img_view);
                holder.mCheck = (CheckBox) convertView.findViewById(R.id.check_number);

                ViewGroup.LayoutParams params = holder.mGridImg.getLayoutParams();
                params.height = ScreenUtil.convertDpToPixel(mActivity, 130);
                holder.mGridImg.setLayoutParams(params);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String path = mImagePathList.get(position);

            try {
                String uri = Uri.decode(Uri.fromFile(new File(path)).toString());

                holder.mGridImg.setVisibility(View.VISIBLE);

                // 어댑터 갱신 시 이미 로딩되어 있는 이미지는 재 로딩 하지 않는다. (깜빡거림 방지)
//                BaLog.d("holder.mGridImg.getTag()=" +  holder.mGridImg.getTag() + ", uri=" + uri);
                if (holder.mGridImg.getTag() == null || !holder.mGridImg.getTag().equals(uri)) {
                    mImageLoader.displayImage(uri, holder.mGridImg, mDisplayImageOptions);

//                    holder.mGridImg.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
//                    ImageAware imageAware = new ImageViewAware(holder.mGridImg, true);
//
//                    mActivity.mImageLoader.displayImage(uri, imageAware, mDisplayImageOptions,
//                            new ImageSize(holder.mGridImg.getMeasuredWidth(), holder.mGridImg.getMeasuredHeight()),
//                            new SimpleImageLoadingListener(), null);

                    holder.mGridImg.setTag(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(mSequenceMap.containsKey(path)) {
                int seq = mSequenceMap.get(path);

                if (seq >= 0) {
                    holder.mCheck.setBackgroundResource(mSeqImgIds[seq]);
                }
            } else {
                holder.mCheck.setBackgroundResource(android.R.color.transparent);
            }

            return convertView;
        }

        public boolean isSelected(int position) {
            String path = mImagePathList.get(position);

            return mSequenceMap.containsKey(path);
        }

        public void setSelected(int position, boolean select) {
            String path = mImagePathList.get(position);

            if(select) {
                mSequenceMap.put(path, mSequenceMap.size());
            } else {
                int prevSeq = mSequenceMap.get(path);

                Set<String> seqKeys = mSequenceMap.keySet();
                Iterator<String> seqKeyIter = seqKeys.iterator();

                // 삭제전, 뒤 아이템들 순서 하나씩 앞으로 땡기기
                while(seqKeyIter.hasNext()) {
                    String key = seqKeyIter.next();
                    int seq = mSequenceMap.get(key);

                    if(seq > prevSeq) {
                        mSequenceMap.put(key, seq-1);
                    }
                }

                mSequenceMap.remove(path);
            }

            notifyDataSetChanged();
            isSeqChanged = true;
        }

        public boolean isSelectedAll() {
            return mSequenceMap.size() == mImagePathList.size();
        }

        public boolean isSequenceChanged() {
            return isSeqChanged;
        }

        public class ViewHolder {
            ImageView mGridImg;
            CheckBox mCheck;
        }
    }
}
