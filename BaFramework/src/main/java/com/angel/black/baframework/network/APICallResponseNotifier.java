package com.angel.black.baframework.network;

import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-09-15.
 */
public interface APICallResponseNotifier {
    /**
     * API 요청에 성공 응답이 왔을 때 화면단으로 돌려줄 콜백
     */
    void onSuccess(String APIUrl, JSONObject response);
    void onFail(String APIUrl, String errCode, String errMessage);
    void onError(String apiUrl, int retCode, String message, Throwable cause);
}
