package com.angel.black.baskettogether.core;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by KimJeongHun on 2016-05-19.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected void startActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    protected void showOkDialog(int strResId) {
        new AlertDialog.Builder(this)
                .setMessage(strResId)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
