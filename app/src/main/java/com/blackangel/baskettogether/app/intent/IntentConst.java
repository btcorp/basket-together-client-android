package com.blackangel.baskettogether.app.intent;

/**
 * Created by KimJeongHun on 2016-06-06.
 */
public class IntentConst {
    public static final int REQUEST_REGIST_RECRUIT_POST = 1;        // 모집글 등록
    public static final int REQUEST_VIEW_RECRUIT_POST_DETAIL = 2;   // 모집글 상세보기
    public static final int REQUEST_MAP_LOCATION_SELECT = 3;        // 구글맵 위치 선택

    // ResultCode 정의 RESULT_OK(-1), RESULT_CANCELED(0), RESULT_FIRST_USER(1) 을 피해서 정의
    public static final int RESULT_DELETED = 2;

    public static final String KEY_EXTRA_POST_ID = "postId";
    public static final String KEY_EXTRA_MAP_LATITUDE = "latitude";
    public static final String KEY_EXTRA_MAP_LONGITUDE = "longitude";
    public static final String KEY_EXTRA_MAP_ADDRESS = "address";
    public static final String KEY_EXTRA_MAP_MODE = "mapMode";
    public static final String KEY_EXTRA_USER_NICKNAME = "userNickname";
}
