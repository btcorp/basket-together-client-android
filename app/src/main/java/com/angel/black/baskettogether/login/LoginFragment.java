package com.angel.black.baskettogether.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.base.BaseFragment;
import com.angel.black.baskettogether.core.network.HttpAPIRequester;
import com.angel.black.baskettogether.core.network.ServerURLInfo;
import com.angel.black.baskettogether.recruit.RecruitPostListActivity;
import com.angel.black.baskettogether.signup.SignUpActivity;
import com.angel.black.baskettogether.user.UserHelper;
import com.angel.black.baskettogether.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-06-24.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private AutoCompleteTextView mIdView;
    private EditText mPasswordView;
    private Button mBtnLogin;
    private Button mBtnSignUp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mIdView = (AutoCompleteTextView) view.findViewById(R.id.id);

        mPasswordView = (EditText) view.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mBtnLogin = (Button) view.findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);

        mBtnSignUp = (Button) view.findViewById(R.id.btn_sign_up_at_login);
        mBtnSignUp.setOnClickListener(this);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void attemptLogin() {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_login:

                mIdView.setError(null);
                mPasswordView.setError(null);

                String id = mIdView.getText().toString();
                String password = mPasswordView.getText().toString();

                if (isValidateForm(id, password)) {
                    hideCurrentFocusKeyboard();
                    try {
                        if(false) {  //TODO 테스트
                            startActivity(RecruitPostListActivity.class, true);
                        } else {
                            requestLogin(id, password);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showOkDialog("로그인 실패");
                    }

                }
                break;

            case R.id.btn_sign_up_at_login:
                startActivity(SignUpActivity.class);
//                startActivity(RecruitPostRegistActivity.class);
                break;
        }
    }

    private boolean isValidateForm(String id, String pwd) {
        if(StringUtil.isEmptyString(id)) {
            mIdView.setError(getString(R.string.error_not_input_id));
            return false;
        } else if(StringUtil.isEmptyString(pwd)) {
            mPasswordView.setError(getString(R.string.error_not_input_pwd));
            return false;
        } else if(pwd.length() < 6) {
            mPasswordView.setError(getString(R.string.error_pwd_minimum_char));
            return false;
        }

        return true;
    }

    private void requestLogin(String id, String pwd) throws JSONException{
        JSONObject loginData = buildRequestLoginData(id, pwd);
        new HttpAPIRequester(this, true, ServerURLInfo.API_USER_LOGIN, "POST", new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) throws JSONException {
                try {
                    String token = response.getString("token");
                    UserHelper.saveUserAccessToken(getBaseActivity(), token);
//                    UserHelper.userAccessToken = token;

                    showToast("로그인 성공");
                    startActivity(RecruitPostListActivity.class, true);
                } catch (JSONException e) {
                    showOkDialog(response.toString());
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
