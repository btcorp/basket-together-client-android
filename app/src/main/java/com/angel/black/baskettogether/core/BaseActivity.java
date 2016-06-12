package com.angel.black.baskettogether.core;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.angel.black.baskettogether.R;
import com.angel.black.baskettogether.util.MyLog;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public class BaseActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    protected final String TAG = this.getClass().getSimpleName();

    protected Toolbar mToolbar;
    protected ProgressBar mLoadingProgress;
    protected ViewGroup mContentsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyLog.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        initToolbar();

        mContentsLayout = (ViewGroup) findViewById(R.id.layout_activity_contents);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getLayoutInflater().inflate(layoutResID, mContentsLayout);
    }

    protected void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);

            ActionBar actionBar = getSupportActionBar();

            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setOnMenuItemClickListener(this);
        }
    }

    protected void hideToolbar() {
        if(mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
    }

    protected void initToolbar(int naviDrawableResId, View.OnClickListener naviClick) {
        if(mToolbar != null) {
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
        if(mLoadingProgress == null) {
            mLoadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        }
        mLoadingProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        if(mLoadingProgress != null && mLoadingProgress.isShown()) {
            mLoadingProgress.setVisibility(View.GONE);
        }
    }

    public void showOkDialog(int strResId) {
        this.showOkDialog(getString(strResId));
    }

    protected void showOkDialog(String message) {
        AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(null, message);
        dialogFragment.show(getSupportFragmentManager(), "okDialog");
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    protected void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public static class AlertDialogFragment extends DialogFragment {
        private static final String ARG_TITLE = "title";
        private static final String ARG_MESSAGE = "message";

        public static AlertDialogFragment newInstance(String title, String message) {
            AlertDialogFragment dialogFragment = new AlertDialogFragment();

            Bundle args = new Bundle();
            if(title != null) args.putString(ARG_TITLE, title);
            args.putString(ARG_MESSAGE, message);
            dialogFragment.setArguments(args);

            return dialogFragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            String title = getArguments().getString(ARG_TITLE);
            String message = getArguments().getString(ARG_MESSAGE);

            if(title != null)
                builder.setTitle(title);

            builder.setMessage(message)
                    .setPositiveButton(android.R.string.ok, null);
            return builder.create();
        }


    }
}
