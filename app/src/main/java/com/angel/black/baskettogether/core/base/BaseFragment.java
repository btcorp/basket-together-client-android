package com.angel.black.baskettogether.core.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.angel.black.baskettogether.core.BaseActivity;

/**
 * Created by KimJeongHun on 2016-06-24.
 */
public class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();

    protected ViewGroup getActivityContentsLayout() {
        return getBaseActivity().getContentsLayout();
    }

    private BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected void startActivity(Class clazz) {
        this.startActivity(clazz, false);
    }

    protected void startActivity(Class clazz, boolean finish) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
        if(finish) getActivity().finish();
    }

    public void hideCurrentFocusKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public void showOkDialog(int strResId) {
        this.showOkDialog(getString(strResId));
    }

    protected void showOkDialog(String message) {
        AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(null, message);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "okDialog");
    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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
