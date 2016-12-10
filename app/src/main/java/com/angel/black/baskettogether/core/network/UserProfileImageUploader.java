package com.angel.black.baskettogether.core.network;

import android.util.Log;

import com.angel.black.baframework.network.GeoPictureUploader;
import com.angel.black.baskettogether.user.UserInfoManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by KimJeongHun on 2016-09-18.
 */
public class UserProfileImageUploader extends GeoPictureUploader implements GeoPictureUploader.ImageUploadRequestParamsStrategy {

    private String nickname;
    private String phoneNum;
    private String imgFilename;

    public UserProfileImageUploader(String nickname, String phoneNum, String imgFilename) {
        this.nickname = nickname;
        this.phoneNum = phoneNum;
        this.imgFilename = imgFilename;

        setImageUploadRequestParamsStrategy(this);
    }

    @Override
    protected String getFileUploadUrl() {
        return ServerURLInfo.DEV_SERVER_URL + ServerURLInfo.API_USER_INFO;
    }

    @Override
    public void writeRequestParams(DataOutputStream outputStream) throws IOException {
        writeFormField(outputStream, "nickname", "" + nickname);
        writeFormField(outputStream, "phone_number", "" + phoneNum);

        File uploadFile = new File(imgFilename);
        FileInputStream fileInputStream = new FileInputStream(uploadFile);

        Log.d("KJH", "IMAGE TEST >> upload filename=" + uploadFile.getName() + ", file size=" + uploadFile.length());
        writeFileField(outputStream, "user_image", "profile.jpg", "image/jpg", fileInputStream);
    }

    @Override
    public void setExtraHeader(HttpURLConnection conn) {
        conn.setRequestProperty("Token", UserInfoManager.userAccessToken);
    }
}
