package com.angel.black.baskettogether.user;

import com.angel.black.baskettogether.core.BaseActivity;
import com.angel.black.baskettogether.core.preference.KeyConst;
import com.angel.black.baskettogether.core.preference.MyPreferenceManager;
import com.angel.black.baskettogether.util.MyLog;
import com.angel.black.baskettogether.util.StringUtil;

/**
 * Created by KimJeongHun on 2016-05-24.
 */
public class UserHelper {
    /**
     * 로그인 액세스 토큰 : 앱 전역으로 유일한 값
     */
    public static String userAccessToken;


    public static void saveUserInfo(BaseActivity activity, String token, String id, String pwd) {
        userAccessToken = token;
        MyLog.e("set userAcessToken=" + userAccessToken + ", saved id=" + id + ", pwd=" + pwd);

        MyPreferenceManager pm = activity.getPreferenceManager();
        pm.saveString(KeyConst.SAVED_ACCESS_TOKEN, token);
        pm.saveString(KeyConst.SAVED_USER_ID, id);
        pm.saveString(KeyConst.SAVED_USER_PWD, pwd);
    }

    /**
     * 자동 로그인 설정 되있는지 여부 반환
     * @return
     */
    public static boolean isValidUserAccessToken() {
        return !StringUtil.isEmptyString(userAccessToken);
    }
}
