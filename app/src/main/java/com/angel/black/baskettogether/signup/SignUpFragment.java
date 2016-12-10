package com.angel.black.baskettogether.signup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.angel.black.baframework.core.base.BaseFragment;
import com.angel.black.baframework.network.HttpAPIRequester;
import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.R;
import com.angel.black.baframework.network.APICallResponseNotifier;
import com.angel.black.baskettogether.api.UserAPI;
import com.angel.black.baskettogether.recruit.RecruitPostListMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-06-26.
 */
public class SignUpFragment extends BaseFragment implements View.OnClickListener {
    private EditText mEditId;
    private EditText mEditPw;
    private EditText mEditPwRe;
    private EditText mEditNickName;
    private Button mBtnSignUp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mEditId = (EditText) view.findViewById(R.id.user_id);
        mEditPw = (EditText) view.findViewById(R.id.password);
        mEditPwRe = (EditText) view.findViewById(R.id.password_re);
        mEditNickName = (EditText) view.findViewById(R.id.user_nickname);
        mBtnSignUp = (Button) view.findViewById(R.id.btn_sign_up);
        mBtnSignUp.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_sign_up) {
            mEditId.setError(null);
            mEditPw.setError(null);
            mEditPwRe.setError(null);
            mEditNickName.setError(null);

            String id = mEditId.getText().toString().trim();
            String pwd = mEditPw.getText().toString().trim();
            String pwdRe = mEditPwRe.getText().toString().trim();
            String nickname = mEditNickName.getText().toString().trim();

            if(isValidateForm(id, pwd, pwdRe, nickname)) {
                try {
                    requestSignUp(id, pwd, nickname);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void requestSignUp(final String id, final String pwd, final String nickname) throws JSONException{
        UserAPI.signUp(getBaseActivity(), id, pwd, nickname, new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onSuccessResponse(String APIUrl, JSONObject response) {
                try {
                    requestLogin(id, pwd);
                } catch (JSONException e) {
                    showOkDialog(response.toString());
                }
            }

            @Override
            public void onErrorResponse(String APIUrl, String errCode, String errMessage) {

            }

            @Override
            public void onError(String APIUrl, int retCode, String message, Throwable cause) {

            }
        });
    }

    private void requestLogin(String id, String pwd) throws JSONException {
        UserAPI.login(getBaseActivity(), id, pwd, new APICallResponseNotifier() {
            @Override
            public void onSuccess(String APIUrl, JSONObject response) {
                showToast("로그인 성공");
                startActivity(RecruitPostListMainActivity.class);
            }

            @Override
            public void onFail(String APIUrl, String errCode, String errMessage) {

            }

            @Override
            public void onError(String apiUrl, int retCode, String message, Throwable cause) {

            }
        });
    }

    private boolean isValidateForm(String id, String pwd, String pwdRe, String nickname) {
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
        } else if(pwd.length() < 8 || pwdRe.length() < 8) {
            mEditPw.setError(getString(R.string.error_pwd_minimum_char));
            return false;
        } else if(StringUtil.isEmptyString(nickname)) {
            mEditNickName.setError(getString(R.string.error_not_input_nickname));
            return false;
        }

        return true;
    }
}
