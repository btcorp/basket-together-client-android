package com.angel.black.baskettogether.core;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.angel.black.baskettogether.R;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public class BaseActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    protected Toolbar mToolbar;
    protected ContentLoadingProgressBar mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected void initToolbar() {
        this.initToolbar(R.drawable.ic_arrow_back_white_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void initToolbar(int naviDrawableResId, View.OnClickListener naviClick) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(naviDrawableResId);
            mToolbar.setNavigationOnClickListener(naviClick);
            mToolbar.setOnMenuItemClickListener(this);
        }

    }

    protected void startActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    public void showProgress() {
        if(mProgress == null) {
            mProgress = new ContentLoadingProgressBar(this);
        }
        mProgress.show();
    }

    public void hideProgress() {
        if(mProgress != null && mProgress.isShown()) {
            mProgress.hide();
        }
    }

    public void showOkDialog(int strResId) {
        new AlertDialog.Builder(this)
                .setMessage(strResId)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    protected void showOkDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

}
