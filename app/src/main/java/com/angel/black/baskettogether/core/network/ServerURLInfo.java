package com.angel.black.baskettogether.core.network;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public class ServerURLInfo {
    public static final String DEV_SERVER_URL = "http://52.78.69.17:8000/";    // 공식 개발
//    public static final String DEV_SERVER_URL = "http://0f80a6f1.ngrok.io/";
    public static final String PRODUCT_SERVER_URL = "";

    public static final String API_USER_SIGNUP = "accounts/signup/";
    public static final String API_USER_LOGIN = "accounts/login/";
    public static final String API_USER_LOGOUT = "accounts/logout/";
    public static final String API_USER_INFO = "accounts/profile/";        // 유저정보 수정 멀티파트, 유저정보 가져오기 GET

    public static final String API_RECRUIT_POST_REGIST = "recruit/post/add/";
    public static final String API_RECRUIT_POSTS_GET = "recruit/posts/page-" + "%1$d" + "/";
    public static final String API_GET_RECRUIT_POST_DETAIL = "recruit/post/";       // 뒤에 글 id 붙여야함
    public static final String API_RECRUIT_REQUEST_ATTEND = "recruit/post/" + "%1$d" + "/participation/add/";
    public static final String API_RECRUIT_REQUEST_ATTEND_CANCEL = "recruit/post/" + "%1$d" + "/participation/remove/";
    public static final String API_DELETE_RECRUIT_POST_COMMENT = "recruit/post/" + "%1$d" + "/";               // 모집글삭제

    public static final String API_RECRUIT_POST_REGIST_COMMENT = "recruit/post/" + "%1$d" + "/comment/add/";    // 댓글등록
    public static final String API_GET_RECRUIT_POST_COMMENTS = "recruit/post/" + "%1$d" + "/comments/";         // 댓글조회

    public static final String API_GET_USER_PROFILE_IMAGE = "media/" + "%1$d" + "/";         // 유저 이미지 다운로드


}
