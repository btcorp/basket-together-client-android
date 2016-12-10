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

import com.angel.black.baframework.core.base.BaseFragment;
import com.angel.black.baframework.util.StringUtil;
import com.angel.black.baskettogether.R;
import com.angel.black.baframework.network.APICallResponseNotifier;
import com.angel.black.baskettogether.api.UserAPI;
import com.angel.black.baskettogether.recruit.RecruitPostListMainActivity;
import com.angel.black.baskettogether.signup.SignUpActivity;

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
        mIdView.setError(null);
        mPasswordView.setError(null);

        String id = mIdView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (isValidateForm(id, password)) {
            hideCurrentFocusKeyboard();
            try {
                if(false) {  //TODO 테스트
                    startActivity(RecruitPostListMainActivity.class, true);
                } else {
                    requestLogin(id, password);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showOkDialog("로그인 실패");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_login:
                attemptLogin();
                break;

            case R.id.btn_sign_up_at_login:
                startActivity(SignUpActivity.class);

                //TEST
//                startActivity(RecruitPostRegistActivity.class);
//                startActivity(CameraTestActivity.class);
//                startActivity(BaseImagePickWithCameraActivity.class);
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

    private void requestLogin(final String id, final String pwd) throws JSONException{
        UserAPI.login(getBaseActivity(), id, pwd, new APICallResponseNotifier() {
            @Override
            public void onSuccess(String APIUrl, JSONObject response) {
                showToast("로그인 성공");
                startActivity(RecruitPostListMainActivity.class, true);
            }

            @Override
            public void onFail(String APIUrl, String errCode, String errMessage) {

            }

            @Override
            public void onError(String apiUrl, int retCode, String message, Throwable cause) {

            }
        });
    }
}
