package com.angel.black.baskettogether.user;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.preference.MyPreferenceManager;
import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.core.preference.KeyConst;

/**
 * Created by KimJeongHun on 2016-05-24.
 */
public class UserInfoManager {
    /**
     * 로그인 액세스 토큰 : 앱 전역으로 유일한 값
     */
    public static String userAccessToken;

    /**
     * 로그인한 유저의 유니크한 id 값 (서버 DB상의 회원 구분값)
     */
    public static long userUid;

    /**
     * 로그인한 유저의 닉네임
     */
    public static String userNickName;

    // TODO 서버에서 "/media/어쩌구 줬는데, 앞에 / 빼야함"
    public static String userProfileImgUrl;


    public static void saveUserInfo(BaseActivity activity, String token, String id, String pwd, String nickname, String profileImgUrl, long uid) {
        userAccessToken = token;
        userUid = uid;
        userNickName = nickname;
        userProfileImgUrl = profileImgUrl;

        BaLog.e("set userAcessToken=" + userAccessToken + ", saved id=" + id + ", pwd=" + pwd + ", nickname=" + nickname + ", uid=" + uid);

        MyPreferenceManager pm = activity.getPreferenceManager();
        pm.saveString(KeyConst.SAVED_ACCESS_TOKEN, token);
        pm.saveString(KeyConst.SAVED_USER_ID, id);
        pm.saveString(KeyConst.SAVED_USER_PWD, pwd);
        pm.saveString(KeyConst.SAVED_USER_NICKNAME, nickname);
    }

    public static void saveUserInfo(BaseActivity activity, String nickname, String profileImgUrl) {
        userNickName = nickname;
        userProfileImgUrl = profileImgUrl;

        BaLog.e("saved nickname=" + nickname + ", profileImgUrl=" + profileImgUrl);

        MyPreferenceManager pm = activity.getPreferenceManager();
        pm.saveString(KeyConst.SAVED_USER_NICKNAME, nickname);
    }

    /**
     * 자동 로그인 설정 되있는지 여부 반환
     * @return
     */
    public static boolean isValidUserAccessToken() {
        return !StringUtil.isEmptyString(userAccessToken);
    }

    public static void removeUserInfo(BaseActivity activity) {
        userAccessToken = null;
        userNickName = null;
        userUid = -1;

        MyPreferenceManager pm = activity.getPreferenceManager();
        pm.saveString(KeyConst.SAVED_ACCESS_TOKEN, "");
        pm.saveString(KeyConst.SAVED_USER_ID, "");
        pm.saveString(KeyConst.SAVED_USER_PWD, "");
        pm.saveString(KeyConst.SAVED_USER_NICKNAME, "");
    }
}
