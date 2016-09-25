package com.angel.black.baframework.core.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.angel.black.baframework.ui.dialog.AlertDialogFragment;
import com.angel.black.baframework.ui.dialog.PermissionConfirmationDialog;

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


    protected void showProgress() {
        getBaseActivity().showProgress();
    }

    protected void hideProgress() {
        getBaseActivity().hideProgress();
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

    /**
     * 퍼미션(권한) 이 있는지 체크한다.
     * @param permission Manifest.Permisson 클래스 상수
     * @return  권한 있으면 true
     */
    protected boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 퍼미션을 요청한다.
     * 처음 퍼미션을 요청하는 경우 퍼미션 요청 이유를 보여주는 팝업 띄운다.
     *
     * @param permission                    요청할 퍼미션(한개)
     * @param permissionReqReasonMsgId      퍼미션 요청 이유 문자 리소스 id
     * @param permissionRequestCode         퍼미션 요청 코드(PermissionConstants 에 정의)
     * @param finishIfCancel                퍼미션 요청 이유 보여주는 팝업에서 취소할 때 액티비티 종료할지 여부
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermission(String permission, int permissionReqReasonMsgId, int permissionRequestCode, boolean finishIfCancel) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
            PermissionConfirmationDialog.newInstance(getResources().getString(permissionReqReasonMsgId),
                    permission, permissionRequestCode, finishIfCancel)
                    .show(getActivity().getSupportFragmentManager(), PermissionConfirmationDialog.TAG);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, permissionRequestCode);
        }
    }
}
