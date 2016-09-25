package com.angel.black.baskettogether.core.network;

import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.network.HttpAPIRequester;
import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.core.MyApplication;
import com.angel.black.baskettogether.user.UserInfoManager;

import java.net.HttpURLConnection;

/**
 * Created by KimJeongHun on 2016-09-11.
 */
public class MyRequestStrategy implements HttpAPIRequester.HttpRequestStrategy {
    @Override
    public String getServerUrl() {
        return MyApplication.serverUrl;
    }

    @Override
    public void setHeader(HttpURLConnection conn, String method) {
        if(method.equals("GET")) {
            conn.setRequestProperty("Accept", "application/json");
            BaLog.d("setHeader(Accept, application/json)");
            conn.setRequestProperty("Content-type", "application/json");
            BaLog.d("setHeader(Content-type, application/json)");

        } else {
            conn.setRequestProperty("Accept", "application/json");                          // 서버 응답 컨텐츠 타입
            BaLog.d("setHeader(Accept, application/json)");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");   // 요청 컨텐츠 타입. 데이터는 포스트데이터(폼) 에 넣음
            BaLog.d("setHeader(Content-type, application/x-www-form-urlencoded)");
        }

        if (isNeedUserAuthToken()) {
            conn.setRequestProperty("Token", UserInfoManager.userAccessToken);
            BaLog.d("setHeader(Token, " + UserInfoManager.userAccessToken + ")");
        }
    }

    private boolean isNeedUserAuthToken() {
        return !StringUtil.isEmptyString(UserInfoManager.userAccessToken);
    }

}
