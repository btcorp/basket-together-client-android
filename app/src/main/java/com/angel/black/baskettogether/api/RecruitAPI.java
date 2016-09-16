package com.angel.black.baskettogether.api;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.network.HttpAPIRequester;
import com.angel.black.baskettogether.core.network.ServerURLInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-09-15.
 */
public class RecruitAPI {
    public static void getRecruitPostDetail(BaseActivity activity, String postId, final APICallSuccessNotifier notifier) {
        new HttpAPIRequester(activity, true, ServerURLInfo.API_GET_RECRUIT_POST_DETAIL + postId + "/", "GET", new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                if(notifier != null) {
                    notifier.onSuccess(response);
                }
            }

            @Override
            public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {
                //TODO 테스트
//                try {
//                    setData(testResponse());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }).execute((JSONObject)null);
    }

    public static void cancelAttendToRecruit(BaseActivity baseActivity, long postId, final APICallSuccessNotifier notifier) {
        new HttpAPIRequester(baseActivity, true, String.format(ServerURLInfo.API_RECRUIT_REQUEST_ATTEND_CANCEL, postId), "GET",
                new HttpAPIRequester.OnAPIResponseListener() {
                    @Override
                    public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                        if(notifier != null) {
                            notifier.onSuccess(response);
                        }
                    }

                    @Override
                    public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {

                    }
                }).execute((JSONObject)null);
    }

    public static void requestAttendToRecruit(BaseActivity baseActivity, long postId, final APICallSuccessNotifier notifier) {
        new HttpAPIRequester(baseActivity, true, String.format(ServerURLInfo.API_RECRUIT_REQUEST_ATTEND, postId), "GET",
                new HttpAPIRequester.OnAPIResponseListener() {
                    @Override
                    public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                        if(notifier != null) {
                            notifier.onSuccess(response);
                        }
                    }

                    @Override
                    public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {

                    }
                }).execute((JSONObject)null);
    }

    public static void getRecruitPostComments(BaseActivity activity, long postId, final APICallSuccessNotifier notifier) {
        new HttpAPIRequester(activity, true, String.format(ServerURLInfo.API_GET_RECRUIT_POST_COMMENTS, postId), "GET",
                new HttpAPIRequester.OnAPIResponseListener() {
                    @Override
                    public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                        if(notifier != null) {
                            notifier.onSuccess(response);
                        }
                    }

                    @Override
                    public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {

                    }
                }).execute((JSONObject)null);
    }

    public static void registCommentToRecruit(BaseActivity activity, long postId, String content,
                                              final APICallSuccessNotifier notifier) throws JSONException{
        JSONObject commentData = buildRegistCommentData(content);

        new HttpAPIRequester(activity, true, String.format(ServerURLInfo.API_RECRUIT_POST_REGIST_COMMENT, postId), "POST",
                new HttpAPIRequester.OnAPIResponseListener() {
                    @Override
                    public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                        if(notifier != null) {
                            notifier.onSuccess(response);
                        }
                    }

                    @Override
                    public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {
                        //TODO 테스트

                    }
                }).execute(commentData);
    }

    private static JSONObject buildRegistCommentData(String content) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("content", content);

        return json;
    }

    public static void deleteRecruitPost(BaseActivity activity, long postId, final APICallSuccessNotifier notifier) {
        new HttpAPIRequester(activity, true, String.format(ServerURLInfo.API_DELETE_RECRUIT_POST_COMMENT, postId), "DELETE",
                new HttpAPIRequester.OnAPIResponseListener() {
                    @Override
                    public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                        if(notifier != null) {
                            notifier.onSuccess(response);
                        }
                    }

                    @Override
                    public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {

                    }
                }).execute((JSONObject) null);
    }
}
