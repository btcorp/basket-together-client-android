package com.angel.black.baskettogether.login;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.angel.black.baframework.core.base.BaseActivity;
import com.angel.black.baskettogether.R;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
    }
}

