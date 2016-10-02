package com.angel.black.baframework.media.image.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.angel.black.baframework.R;

/**
 * Created by KimJeongHun on 2016-10-02.
 */
public class ImagePickWithCamTitleView extends FrameLayout {
    private Spinner mSpinnerAlbum;
    private TextView mTxtCameraTitle;

    public ImagePickWithCamTitleView(Context context) {
        this(context, null);
    }

    public ImagePickWithCamTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(getContext(), R.layout.image_pick_with_cam_title_view, this);

        mSpinnerAlbum = (Spinner) view.findViewById(R.id.spinner_album);
        mTxtCameraTitle = (TextView) view.findViewById(R.id.textview_camera_title);
    }


    public Spinner getSpinnerAlbum() {
        return mSpinnerAlbum;
    }

    public TextView getTextViewCameraTitle() {
        return mTxtCameraTitle;
    }
}
