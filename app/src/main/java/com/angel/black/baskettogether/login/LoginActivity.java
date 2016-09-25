package com.angel.black.baskettogether.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.angel.black.baframework.logger.BaLog;
import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.core.base.BtBaseActivity;

public class LoginActivity extends BtBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    // 이미지 픽 테스트용
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BaLog.d("requestCode=" + requestCode + ", resultCode=" + resultCode, data);

    }
}

