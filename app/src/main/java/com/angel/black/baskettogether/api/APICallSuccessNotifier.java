package com.angel.black.baskettogether.api;

import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-09-15.
 */
public interface APICallSuccessNotifier {
    /**
     * API 요청에 성공 응답이 왔을 때 화면단으로 돌려줄 콜백
     */
    void onSuccess(JSONObject response);
}
