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
import com.angel.black.baskettogether.api.APICallSuccessNotifier;
import com.angel.black.baskettogether.api.UserAPI;
import com.angel.black.baskettogether.recruit.RecruitPostListActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KimJeongHun on 2016-06-26.
 */
public class SignUpFragment extends BaseFragment implements View.OnClickListener {
    private EditText mEditId;
    private EditText mEditPw;
    private EditText mEditPwRe;
    private Button mBtnSignUp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mEditId = (EditText) view.findViewById(R.id.user_id);
        mEditPw = (EditText) view.findViewById(R.id.password);
        mEditPwRe = (EditText) view.findViewById(R.id.password_re);
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
        UserAPI.signUp(getBaseActivity(), id, pwd, new HttpAPIRequester.OnAPIResponseListener() {
            @Override
            public void onResponse(String APIUrl, int retCode, JSONObject response) {
                try {
                    requestLogin(id, pwd);
                } catch (JSONException e) {
                    showOkDialog(response.toString());
                }
            }

            @Override
            public void onErrorResponse(String APIUrl, int retCode, String message, Throwable cause) {

            }
        });
    }

    private void requestLogin(String id, String pwd) throws JSONException{
        UserAPI.login(getBaseActivity(), id, pwd, new APICallSuccessNotifier() {
            @Override
            public void onSuccess(JSONObject response) {
                showToast("로그인 성공");
                startActivity(RecruitPostListActivity.class);
            }
        });
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
        } else if(pwd.length() < 8 || pwdRe.length() < 8) {
            mEditPw.setError(getString(R.string.error_pwd_minimum_char));
            return false;
        }

        return true;
    }
}
