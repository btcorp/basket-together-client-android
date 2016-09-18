package com.angel.black.baframework.intent;

import android.content.Intent;

import com.angel.black.baframework.core.base.BaseActivity;

/**
 * Created by KimJeongHun on 2016-09-18.
 */
public class IntentExecutor {
    public static void executeGalleryPick(BaseActivity activity) {
        // 사진 선택
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), IntentConstants.REQUEST_PICK_GALLERY);
    }
}
