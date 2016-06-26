package com.angel.black.baskettogether.core.network;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public class ServerURLInfo {
//    public static final String DEV_SERVER_URL = "http://124.58.75.7:10/";     // 태우 집
//    public static final String DEV_SERVER_URL = "http://172.30.60.41:8000/";    // 태우 로컬
    public static final String DEV_SERVER_URL = "http://5d420d31.ngrok.io/";    // 태우 로컬2
//    public static final String DEV_SERVER_URL = "http://192.168.0.23:8000/";
    public static final String PRODUCT_SERVER_URL = "";

    public static final String API_USER_REGIST = "signup/";
    public static final String API_USER_LOGIN = "rest-auth/login/";

    public static final String API_RECRUIT_POST_REGIST = "recruit/post/add/";
    public static final String API_RECRUIT_POSTS_GET = "recruit/posts/";
    public static final String API_GET_RECRUIT_POST_DETAIL = "recruit/post/";       // 뒤에 글 id 붙여야함
}
