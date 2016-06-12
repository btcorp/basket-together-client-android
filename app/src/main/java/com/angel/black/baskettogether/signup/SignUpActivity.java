package com.angel.black.baskettogether.signup;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.BaseActivity;
import com.angel.black.baskettogether.core.network.HttpAPIRequester;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.recruit.RecruitPostRegistActivity;
import com.angel.black.baskettogether.user.UserHelper;
import com.angel.black.baskettogether.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends BaseActivity {
    private EditText mEditId;
    private EditText mEditPw;
    private EditText mEditPwRe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sign_up);

        mEditId = (EditText) findViewById(R.id.user_id);
        mEditPw = (EditText) findViewById(R.id.password);
        mEditPwRe = (EditText) findViewById(R.id.password_re);
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_sign_up) {
            mEditId.setError(null);
            mEditPw.setError(null);
            mEditPwRe.setError(null);

            String id = mEditId.getText().toString().trim();
            String pwd = mEditPw.getText().toString().trim();
            String pwdRe = mEditPwRe.getText().toString().trim();

            if(isValidateForm(id, pwd, pwdRe)) {
                try {
                    requestSignUp(id, pwd);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void requestSignUp(final String id, final String pwd) throws JSONException{
        JSONObject joinData = buildRequestJoinData(id, pwd);
        new HttpAPIRequester(this, true, ServerURLInfo.API_USER_REGIST, "POST", new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) {
                try {
                    String key = response.getString("key");
                    UserHelper.userAccessToken = key;

                    requestLogin(id, pwd);
                } catch (JSONException e) {
                    showOkDialog(response.toString());
                }
            }

            @Override
            public void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException {

            }

            @Override
            public void onErrorResponse(String APIUrl, String message, Throwable cause) {

            }
        }).execute(joinData);
    }

    private void requestLogin(String id, String pwd) throws JSONException{
        JSONObject loginData = buildRequestLoginData(id, pwd);
        new HttpAPIRequester(this, true, ServerURLInfo.API_USER_LOGIN, "POST", new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                showToast("로그인 성공");
                startActivity(RecruitPostRegistActivity.class);
            }

            @Override
            public void onResponse(String APIUrl, int retCode, JSONArray response) throws JSONException {

            }

            @Override
            public void onErrorResponse(String APIUrl, String message, Throwable cause) {

            }
        }).execute(loginData);
    }

    private JSONObject buildRequestJoinData(String id, String pwd) throws JSONException{
        JSONObject json = new JSONObject();
        json.put("username", id);
        json.put("password1", pwd);
        json.put("password2", pwd);

        return json;
    }

    private JSONObject buildRequestLoginData(String id, String pwd) throws JSONException{
        JSONObject json = new JSONObject();
        json.put("username", id);
        json.put("password", pwd);

        return json;
    }

    private boolean isValidateForm(String id, String pwd, String pwdRe) {
        if(StringUtil.isEmptyString(id)) {
            mEditId.setError(getString(R.string.error_not_input_id));
            return false;
        } else if(StringUtil.isEmptyString(pwd)) {
            mEditPw.setError(getString(R.string.error_not_input_pwd));
            return false;
        } else if(StringUtil.isEmptyString(pwdRe)) {
            mEditPwRe.setError(getString(R.string.error_not_input_pwd_re));
            return false;
        } else if(!pwd.equals(pwdRe)) {
            showOkDialog(R.string.error_not_equal_pw_re);
            return false;
        } else if(pwd.length() < 6 || pwdRe.length() < 6) {
            mEditPw.setError(getString(R.string.error_pwd_minimun_6));
            return false;
        }

        return true;
    }
}
