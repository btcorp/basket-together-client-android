package com.angel.black.baframework.core.base;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.angel.black.baframework.ui.dialog.AlertDialogFragment;

/**
 * Created by KimJeongHun on 2016-06-24.
 */
public class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();

    protected ViewGroup getActivityContentsLayout() {
        return getBaseActivity().getContentsLayout();
    }

    protected BaseActivity getBaseActivity() {
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

    public void showToast(int msgResId) {
        Toast.makeText(getActivity(), msgResId, Toast.LENGTH_LONG).show();
    }
    protected void addChildFragment(int containerResId, Fragment fragment, int enterAnim, int exitAnim) {
        FragmentManager fm = getChildFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(enterAnim, exitAnim);
        ft.add(containerResId, fragment);
        ft.commit();
    }

    protected void addChildFragment(int containerResId, Fragment fragment) {
        addChildFragment(containerResId, fragment, 0, 0);
    }

    protected void removeChildFragment(Fragment fragment, int enterAnim, int exitAnim) {
        FragmentManager fm = getChildFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(enterAnim, exitAnim);
        ft.remove(fragment);
        ft.commit();
    }

    protected void removeChildFragment(Fragment fragment) {
        removeChildFragment(fragment, 0, 0);
    }
}
