package com.angel.black.baframework.ui.dialog;

/**
 * Created by KimJeongHun on 2016-06-15.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;

import com.angel.black.baframework.logger.BaLog;

/**
 * 퍼미션 요청 이유 설명하는 다이얼로그 (마시멜로 이후)
 */
@TargetApi(Build.VERSION_CODES.M)
public class PermissionConfirmationDialog extends DialogFragment {
    public static final String TAG = "permissionDlg";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_PERMISSION = "permission";
    private static final String ARG_REQUEST_CODE = "requestCode";
    private static final String ARG_IS_ACTIVITY_FINISH_ON_CANCEL = "isActFinish";

    public static PermissionConfirmationDialog newInstance(String message, String permission, int requestCode, boolean isActFinish) {
        PermissionConfirmationDialog dialog = new PermissionConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_PERMISSION, permission);
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putBoolean(ARG_IS_ACTIVITY_FINISH_ON_CANCEL, isActFinish);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert)
                .setMessage(getArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OnPermissionConfirmationDialogListener listener = null;
                        if(getActivity() instanceof OnPermissionConfirmationDialogListener) {
                            listener = (OnPermissionConfirmationDialogListener) getActivity();
                        }

                        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{getArguments().getString(ARG_PERMISSION)}, getArguments().getInt(ARG_REQUEST_CODE));

                        if(listener != null) {
                            listener.onAllowedPermissionConfirm(getArguments().getInt(ARG_REQUEST_CODE));
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BaLog.e("퍼미션 접근 요청 거부함");
                        OnPermissionConfirmationDialogListener listener = null;
                        if(getActivity() instanceof OnPermissionConfirmationDialogListener) {
                            listener = (OnPermissionConfirmationDialogListener) getActivity();
                        }

                        if(listener != null) {
                            listener.onDenyedPermissionConfirm(getArguments().getInt(ARG_REQUEST_CODE));
                        }

                        if(getArguments().getBoolean(ARG_IS_ACTIVITY_FINISH_ON_CANCEL)) {
                            getActivity().finish();
                        }
                    }
                })
                .create();
    }

    /**
     * 퍼미션 요청 이유 설명하는 팝업의 허용/거부 리스너
     */
    public interface OnPermissionConfirmationDialogListener {
        void onAllowedPermissionConfirm(int permissionRequestCode);
        void onDenyedPermissionConfirm(int permissionRequestCode);
    }
}