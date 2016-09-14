package com.angel.black.baskettogether.intro;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baframework.network.HttpAPIRequester;
import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.core.preference.KeyConst;
import com.angel.black.baskettogether.login.LoginActivity;
import com.angel.black.baskettogether.recruit.RecruitPostListActivity;
import com.angel.black.baskettogether.user.UserHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IntroActivity extends BaseActivity {

    private static final int INTRO_DELAY_MILLIS = 1000;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mIntroRunnable = new Runnable() {
        @Override
        public void run() {
            String savedId = getPreferenceManager().loadString(KeyConst.SAVED_USER_ID);
            //TODO 추후 암호화
            String savedPwd = getPreferenceManager().loadString(KeyConst.SAVED_USER_PWD);

            BaLog.d("savedId=" + savedId + ", savedPwd=" + savedPwd);

            if (StringUtil.isEmptyString(savedId) && StringUtil.isEmptyString(savedPwd)) {
                startActivity(LoginActivity.class);
            } else {
                try {
                    requestLogin(savedId, savedPwd);
                } catch (JSONException e) {
                    showOkDialog(R.string.error_login);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        hideToolbar();

        mContentView = findViewById(R.id.fullscreen_content);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedIntro(INTRO_DELAY_MILLIS);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedIntro(int delayMillis) {
        mHideHandler.removeCallbacks(mIntroRunnable);
        mHideHandler.postDelayed(mIntroRunnable, delayMillis);
    }

    private void requestLogin(final String id, final String pwd) throws JSONException {
        JSONObject loginData = buildRequestLoginData(id, pwd);
        new HttpAPIRequester(this, true, ServerURLInfo.API_USER_LOGIN, "POST",
                new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                try {
                    String token = response.getString("token");
                    UserHelper.saveUserInfo(IntroActivity.this, token, id, pwd);

                    showToast("로그인 성공");
                    startActivity(RecruitPostListActivity.class, true);
                } catch (JSONException e) {
                    showOkDialog(response.toString());
                    startActivity(LoginActivity.class);
                }
            }

            @Override
            public void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException {

            }

            @Override
            public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {

            }
        }).execute(loginData);
    }

    private JSONObject buildRequestLoginData(String id, String pwd) throws JSONException{
        JSONObject json = new JSONObject();
        json.put("username", id);
        json.put("password", pwd);

        return json;
    }
}
