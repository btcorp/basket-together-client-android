package com.blackangel.baskettogether.intro.fragment;

/**
 * Created by Finger-kjh on 2017-12-01.
 */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackangel.baframework.core.base.BaseFragment;
import com.blackangel.baframework.logger.MyLog;
import com.blackangel.baframework.sns.FacebookUtil;
import com.blackangel.baframework.sns.ISnsLoginParam;
import com.blackangel.baframework.sns.OnSnsLoginListener;
import com.blackangel.baframework.sns.SnsLoginResult;
import com.blackangel.baskettogether.MyApplication;
import com.blackangel.baskettogether.R;
import com.blackangel.baskettogether.app.security.EncryptUtil;
import com.blackangel.baskettogether.intro.viewmodel.IntroViewModel;
import com.blackangel.baskettogether.user.domain.User;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

import java.net.HttpCookie;
import java.security.PublicKey;
import java.util.List;

public class IntroFragment extends BaseFragment implements OnSnsLoginListener {

    public static IntroFragment newInstance() {
        IntroFragment fragment = new IntroFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntroViewModel model = ViewModelProviders.of(getActivity()).get(IntroViewModel.class);
        model.getFbCallbackManager().observe(this, new Observer<CallbackManager>() {
            @Override
            public void onChanged(@Nullable CallbackManager callbackManager) {
                MyLog.i("IntroFragment", "observe onChanged");
            }
        });

        Observer<User> userObserver = new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                MyLog.i("user = " + user);
                if(user != null) {

                }
            }
        };

        model.getLiveDataUser().observe(this, userObserver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro, null);

        final LoginButton btnFbLogin = view.findViewById(R.id.btn_fb_login);
        btnFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntroViewModel model = ViewModelProviders.of(getActivity()).get(IntroViewModel.class);
                CallbackManager fbCallbackManager = model.getFbCallbackManager().getValue();
                FacebookUtil.loginFacebookViaFacebookButton(getBaseActivity(),
                        fbCallbackManager, btnFbLogin, IntroFragment.this);
            }
        });

        return view;
    }

    @Override
    public void onLoginCompleted(SnsLoginResult snsLoginResult, ISnsLoginParam... iSnsLoginParams) {

        List<HttpCookie> cookies = MyApplication.sCookieManager.getCookieStore().getCookies();

        String rsaPbkModulus = null;
        String rsaPbkExponent = null;
        for (HttpCookie c : cookies) {
            if(c.getName().equals("RSAPublicKeyModulus")) {
                rsaPbkModulus = c.getValue();
            } else if(c.getName().equals("RSAPublicKeyExponent")) {
                rsaPbkExponent = c.getValue();
            }
        }

        if(rsaPbkModulus != null && rsaPbkExponent != null) {
            MyLog.i("rsaPbkModulus=" + rsaPbkModulus + ", rsaPbkExponent=" + rsaPbkExponent);

            try {
                PublicKey publicKey = EncryptUtil.makeRSAPublicKey(rsaPbkModulus, rsaPbkExponent);
                String encryptedPw = EncryptUtil.encryptRSA(publicKey, "1234");

                MyLog.i("encryptedPw=" + encryptedPw);

                tryLogin(snsLoginResult, encryptedPw);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tryLogin(SnsLoginResult snsLoginResult, String encryptedPw) {
        MyLog.i("snsLoginResult=" + snsLoginResult + ", pw=" + encryptedPw);
        IntroViewModel model = ViewModelProviders.of(getActivity()).get(IntroViewModel.class);
        model.loginFromSns(getBaseActivity(), snsLoginResult, encryptedPw);
    }

    @Override
    public void onLoginFailed(String s) {

    }
}