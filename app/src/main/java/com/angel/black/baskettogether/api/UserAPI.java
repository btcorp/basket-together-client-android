package com.angel.black.baskettogether.api;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.network.HttpAPIRequester;
import com.angel.black.baframework.network.ImageUploaderTask;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.core.network.UserProfileImageUploader;
import com.angel.black.baskettogether.user.UserInfoManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-09-15.
 */
public class UserAPI {

    public static void signUp(BaseActivity activity, String id, String pwd, String nickname, HttpAPIRequester.OnAPIResponseListener responseListener) throws JSONException {
        JSONObject joinData = buildRequestJoinData(id, pwd, nickname, "0");

        new HttpAPIRequester(activity, true, ServerURLInfo.API_USER_SIGNUP, "POST", responseListener).execute(joinData);
    }

    public static void logout(final BaseActivity activity, final APICallSuccessNotifier notifier) throws JSONException {
        new HttpAPIRequester(activity, true, ServerURLInfo.API_USER_LOGOUT, "GET", new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                UserInfoManager.removeUserInfo(activity);
            }

            @Override
            public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {

            }
        }).execute((JSONObject) null);
    }

    public static void login(final BaseActivity activity, final String id, final String pwd, final APICallSuccessNotifier notifier) throws JSONException {
        JSONObject loginData = buildRequestLoginData(id, pwd);

        new HttpAPIRequester(activity, true, ServerURLInfo.API_USER_LOGIN, "POST", new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                try {
                    String token = response.getString("token");
                    long uid = response.getLong("user_id");
                    String nickname = response.getString("nickname");
                    String imgUrl = response.getString("picture_url");
                    UserInfoManager.saveUserInfo(activity, token, id, pwd, nickname, imgUrl, uid);

                    if(notifier != null) {
                        notifier.onSuccess(response);
                    }
                } catch (JSONException e) {
                    activity.showOkDialog(response.toString());
                }
            }

            @Override
            public void onErrorResponse(String APIUrl, int retCode, String message, Throwable throwable) {

            }
        }).execute(loginData);
    }

    private static JSONObject buildRequestJoinData(String id, String pwd, String nickname, String joinPath) throws JSONException{
        JSONObject json = new JSONObject();
        json.put("username", id);
        json.put("password1", pwd);
        json.put("password2", pwd);
        json.put("nickname", nickname);
//        json.put("join_path", joinPath);

        return json;
    }

    private static JSONObject buildRequestLoginData(String id, String pwd) throws JSONException{
        JSONObject json = new JSONObject();
        json.put("username", id);
        json.put("password", pwd);

        return json;
    }

    public static void editUserInfo(BaseActivity activity, String nickname, String phoneNum, String selectedImagePath, final ImageUploaderTask.ImageUploadListener imageUploadListener) {
        ImageUploaderTask imageUploaderTask = new ImageUploaderTask(activity,
                new UserProfileImageUploader(UserInfoManager.userUid, nickname, phoneNum, selectedImagePath));
        imageUploaderTask.setImageUploadListener(imageUploadListener);
        imageUploaderTask.uploadImage();
    }
}
