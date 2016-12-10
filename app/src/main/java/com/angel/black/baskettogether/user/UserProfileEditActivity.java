package com.angel.black.baskettogether.user;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.angel.black.baframework.intent.IntentConstants;
import com.angel.black.baframework.intent.IntentExecutor;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.media.image.BaseImagePickActivity;
import com.angel.black.baframework.media.image.util.AUILUtil;
import com.angel.black.baframework.network.APICallResponseNotifier;
import com.angel.black.baframework.network.GeoPictureUploader;
import com.angel.black.baframework.network.ImageUploaderTask;
import com.angel.black.baframework.security.PermissionConstants;
import com.angel.black.baframework.ui.dialog.DialogClickListener;
import com.angel.black.baframework.ui.dialog.PermissionConfirmationDialog;
import com.angel.black.baframework.util.BaPackageManager;
import com.angel.black.baframework.util.FileUtil;
import com.angel.black.baframework.util.UriUtil;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.api.UserAPI;
import com.angel.black.baskettogether.core.MyApplication;
import com.angel.black.baskettogether.core.base.BtBaseActivity;
import com.angel.black.baskettogether.core.intent.IntentConst;
import com.angel.black.baskettogether.core.view.imageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KimJeongHun on 2016-09-18.
 */
public class UserProfileEditActivity extends BtBaseActivity implements View.OnClickListener, PermissionConfirmationDialog.OnPermissionConfirmationDialogListener {
    private RoundedImageView mProfileImgView;
    private EditText mEditNickName;
    private EditText mEditPhoneNum;
    private String mSelectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        mProfileImgView = (RoundedImageView) findViewById(R.id.user_profile_img);
        mProfileImgView.setOnClickListener(this);

        mEditNickName = (EditText) findViewById(R.id.edit_nickname);
        mEditPhoneNum = (EditText) findViewById(R.id.edit_phone_num);

        String nickname = getIntent().getStringExtra(IntentConst.KEY_EXTRA_USER_NICKNAME);
        mEditNickName.setText(nickname);

        requestUserInfo();
    }

    private void requestUserInfo() {
        UserAPI.requestUserInfo(this, new APICallResponseNotifier() {
            @Override
            public void onSuccess(String APIUrl, JSONObject response) {
                BaLog.i("response=" + response);

                String pictureUrl = response.optString("picture_url");
                String nickName = response.optString("nickname");
                UserInfoManager.saveUserInfo(UserProfileEditActivity.this,
                        nickName,
                        pictureUrl);

                displayProfileImage(MyApplication.serverUrl + pictureUrl);
                String phoneNo = response.optString("phone_no");

                mEditNickName.setText(nickName);
                mEditPhoneNum.setText(phoneNo);
            }

            @Override
            public void onFail(String APIUrl, String errCode, String errMessage) {

            }

            @Override
            public void onError(String apiUrl, int retCode, String message, Throwable cause) {

            }
        });
    }

    private void displayProfileImage(String imageUrl) {
        ImageLoader.getInstance().displayImage(imageUrl, mProfileImgView,
                MyApplication.mDefaultDisplayImgOpts, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        BaLog.d(imageUri + " is loaded!");
                        FileUtil.saveImageFile(UserProfileEditActivity.this, loadedImage,
                                FileUtil.createTempImageFile(
                                        BaPackageManager.getTempImagePath(UserProfileEditActivity.this)),
                                new FileUtil.ImageFileBuildListener() {
                                    @Override
                                    public void onSuccessImageFileBuild(String filepath) {
                                        BaLog.i("filePath=" + filepath);

                                        mSelectedImagePath = filepath;
                                    }

                                    @Override
                                    public void onFailImageFileBuild(String filepath, String errMsg) {
                                        BaLog.i("filePath=" + filepath + ", errMsg=" + errMsg);
                                    }
                                });
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.menu_complete) {
            BaLog.d("완료 버튼 클릭!");

            requestUserInfoEdit();
        }

        return false;
    }

    private void requestUserInfoEdit() {
        String nickname = mEditNickName.getText().toString().trim();
        String phoneNum = mEditPhoneNum.getText().toString().trim();

        if (!isValidateFormData(nickname, phoneNum)) {
            return;
        }

        UserAPI.editUserInfo(this, nickname, phoneNum, mSelectedImagePath, new ImageUploaderTask.ImageUploadListener() {
            @Override
            public void onUploadComplete(GeoPictureUploader.ReturnCode result) {
                BaLog.i("result=" + result);

                if(result.equals(GeoPictureUploader.ReturnCode.succ)) {
                    AUILUtil.deleteImageCache(mSelectedImagePath);
                }

                requestUserInfo();
            }
        });

    }

    private boolean isValidateFormData(String nickname, String phoneNum) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.user_profile_img) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        PermissionConstants.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION,
                        R.string.request_write_storage_permission, false);
                return;
            } else {
                IntentExecutor.executeCustomGalleryPick(this, BaseImagePickActivity.class, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IntentConstants.REQUEST_PICK_GALLERY) {
                ArrayList<String> imagePathList = data.getStringArrayListExtra(IntentConstants.KEY_IMAGE_PATH_LIST);

                mSelectedImagePath = imagePathList.get(0);

                displayProfileImage(UriUtil.filePath2Uri(mSelectedImagePath));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        BaLog.i("requestCode=" + requestCode + ", pemissions=" + permissions.length + ", grantResults=" + grantResults.length);
        if (requestCode == PermissionConstants.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 퍼미션 거부
                showAlertDialogNotCancelable(R.string.error_require_write_storage_permission, new DialogClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    IntentExecutor.executeCustomGalleryPick(this, BaseImagePickActivity.class, 1);
                }
            }
        }
    }

    @Override
    public void onAllowedPermissionConfirm(int permissionRequestCode) {
        BaLog.d("permissionRequestCode=" + permissionRequestCode);
        if (permissionRequestCode == PermissionConstants.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {

        }
    }

    @Override
    public void onDenyedPermissionConfirm(int permissionRequestCode) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        FileUtil.deleteTempImageFiles(this);
    }
}
