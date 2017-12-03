//package com.blackangel.baskettogether.core.network;
//
//import com.blackangel.baframework.logger.MyLog;
//import com.blackangel.baframework.network.HttpAPIRequester;
//import com.blackangel.baframework.util.StringUtil;
//import com.blackangel.baskettogether.core.MyApplication;
//import com.blackangel.baskettogether.user.UserInfoManager;
//
//import java.net.HttpURLConnection;
//
///**
// * Created by KimJeongHun on 2016-09-11.
// */
//public class MyRequestStrategy implements HttpAPIRequester.HttpRequestStrategy {
//    @Override
//    public String getServerUrl() {
//        return MyApplication.serverUrl;
//    }
//
//    @Override
//    public void setHeader(HttpURLConnection conn, String method) {
//        if(method.equals("GET")) {
//            conn.setRequestProperty("Accept", "application/json");
//            MyLog.d("setHeader(Accept, application/json)");
//            conn.setRequestProperty("Content-type", "application/json");
//            MyLog.d("setHeader(Content-type, application/json)");
//
//        } else {
//            conn.setRequestProperty("Accept", "application/json");                          // 서버 응답 컨텐츠 타입
//            MyLog.d("setHeader(Accept, application/json)");
//            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");   // 요청 컨텐츠 타입. 데이터는 포스트데이터(폼) 에 넣음
//            MyLog.d("setHeader(Content-type, application/x-www-form-urlencoded)");
//        }
//
//        if (isNeedUserAuthToken()) {
//            conn.setRequestProperty("Token", UserInfoManager.userAccessToken);
//            MyLog.d("setHeader(Token, " + UserInfoManager.userAccessToken + ")");
//        }
//    }
//
//    private boolean isNeedUserAuthToken() {
//        return !StringUtil.isEmptyString(UserInfoManager.userAccessToken);
//    }
//
//}
