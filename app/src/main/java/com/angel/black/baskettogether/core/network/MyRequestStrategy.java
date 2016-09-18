package com.angel.black.baskettogether.core.network;

import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.network.HttpAPIRequester;
import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.core.MyApplication;
import com.angel.black.baskettogether.user.UserHelper;

import org.apache.http.client.methods.HttpRequestBase;

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
    public void setHeader(HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader("Accept", "application/json");
        BaLog.d("setHeader(Accept, application/json)");
        httpRequestBase.setHeader("Content-type", "application/json");
        BaLog.d("setHeader(Content-type, application/json)");

        if (isNeedUserAuthToken()) {
            httpRequestBase.setHeader("Token", UserHelper.userAccessToken);
            BaLog.d("setHeader(Token, " + UserHelper.userAccessToken + ")");
        }
    }

    @Override
    public void setHeader(HttpURLConnection conn) {
        conn.setRequestProperty("Accept", "application/json");                          // 서버 응답 컨텐츠 타입
        BaLog.d("setHeader(Accept, application/json)");
        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");   // 요청 컨텐츠 타입
        BaLog.d("setHeader(Content-type, application/x-www-form-urlencoded)");

        if (isNeedUserAuthToken()) {
            conn.setRequestProperty("Token", UserHelper.userAccessToken);
            BaLog.d("setHeader(Token, " + UserHelper.userAccessToken + ")");
        }
    }

    private boolean isNeedUserAuthToken() {
        return !StringUtil.isEmptyString(UserHelper.userAccessToken);
    }

}
