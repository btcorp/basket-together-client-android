package com.angel.black.baskettogether.core.network;

import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.network.HttpAPIRequester;
import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.core.MyApplication;
import com.angel.black.baskettogether.user.UserHelper;

import org.apache.http.client.methods.HttpRequestBase;

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

    private boolean isNeedUserAuthToken() {
        return !StringUtil.isEmptyString(UserHelper.userAccessToken);
    }

}
